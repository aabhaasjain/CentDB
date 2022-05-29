package com.csci5408.centdb.services.queryimplementation;

import com.csci5408.centdb.model.Column;
import com.csci5408.centdb.model.Metadata;
import com.csci5408.centdb.model.Query;
import com.csci5408.centdb.persistence.IFileReader;
import com.csci5408.centdb.persistence.IQueryDao;
import com.csci5408.centdb.persistence.impl.FileReader;
import com.csci5408.centdb.persistence.impl.QueryDao;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.csci5408.centdb.model.util.Constants.METADATA_PATH;


public class DropTable {
    public static boolean dropTable(String query) throws IOException {
        IFileReader fileReader = new FileReader();
        IQueryDao queryDao = new QueryDao();
        Pattern pattern = Pattern.compile("drop\\ *table\\s.*");
        Matcher matcher = pattern.matcher(query);
        if(matcher.matches()){
            String[] querySplit = query.split("table");
            if(querySplit.length >1){
                Query dropQuery = new Query.QueryBuilder()
                        .tableName(querySplit[1].trim())
                        .build();
                //check if table exists
                List<Metadata> metadataList = fileReader.getMetadata();
                List<Metadata> metadataTempList = fileReader.getMetadata();
                for (Metadata metadata: metadataList) {
                    if(metadata.getTableName().equals(dropQuery.getTableName())){
                         if(queryDao.dropTable(dropQuery)){
                             // remove from meta file
                             if(metadataTempList.removeIf(x->x.getTableName().equals(dropQuery.getTableName())))
                                removeTableMeta(metadataTempList);
                             return true;
                         }
                    }
                }
                System.out.println("Table not found!");
                return false;
            }
        }
        return false;
    }
    private static void removeTableMeta(List<Metadata> metadataList) throws IOException {
        IFileReader fileReader = new FileReader();
        StringBuilder dropFileUpdate = new StringBuilder();
        String filePath = String.format(METADATA_PATH,UseDatabase.getDatabaseName(),UseDatabase.getDatabaseName());
        if(fileReader.checkFileExists(filePath)){
            FileWriter fileWriter = new FileWriter(filePath,false);
            for (Metadata metadata:metadataList) {
                dropFileUpdate.append(String.format("Table|%s",metadata.getTableName()));
                for (Column column:metadata.getColumns()) {
                    dropFileUpdate.append(String.format("\n%s|%s|",column.getName(),column.getType()));
                    if(Objects.nonNull(column.getConstraint())){
                        dropFileUpdate.append(String.format("%s",column.getConstraint()));
                    }
                }
                dropFileUpdate.append(String.format("\n"));
            }
            fileWriter.write(dropFileUpdate.toString());
            fileWriter.close();
        }
    }

}
