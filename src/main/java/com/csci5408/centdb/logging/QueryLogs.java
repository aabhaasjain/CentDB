package com.csci5408.centdb.logging;
import java.io.*;
import java.util.Date;

public class QueryLogs {
    public void createQueryLog(String userId, String status, String query_type, String database_name,
                               String table_name, String column_affected, String row_affected,
                               String constraint) throws IOException {
        String query_log="";
        File f = new File("resources\\Databases\\"+database_name+"\\"+database_name+"_QueryLogs.txt");
        if(f.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(f));
            if (br.readLine() == null) {
                query_log = "User|Query type|Status|Database Name|Table Name|Column Affected|Row Affected|Condition|TimeStamp\n";
            }
        }
        else{
            query_log = "User|Query type|Status|Database Name|Table Name|Column Affected|Row Affected|Condition|TimeStamp\n";
        }
        query_log = query_log+userId+"|"+query_type+"|"+status+"|"+database_name+"|"+table_name+"|"+column_affected+"|"
                +row_affected+"|"+constraint+"|"+(new Date());
        FileWriter writer = new FileWriter("resources\\Databases\\"+database_name+"\\"+database_name+"_QueryLogs.txt",true);
        writer.write(query_log+"\n");
        writer.close();
    }
}
