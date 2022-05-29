package com.csci5408.centdb.services.queryimplementation;
import com.csci5408.centdb.logging.QueryLogs;
import com.csci5408.centdb.services.UserService;
import com.csci5408.enums.ColumnConstraints;
import com.csci5408.enums.ColumnDataTypes;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CreateTable {
    static  String CREATE_TABLE_COMMAND = "Create table ";
    static  String SPACE = " ";
    static  String DELIMITER = "|";
    static String REFERENCES = "REFERENCES";

    ArrayList<String> columnNames = new ArrayList<>();
    ArrayList<String[]> columns = new ArrayList<>();


    public void create(String currentDatabase, String tableName, String[] allInputWords,
                       String inputString) throws IOException {
        // 1. Create table file with name array[2]. throw error if it exists
        String databaseMetaPath = "resources//Databases" + "//" + currentDatabase + "//" + currentDatabase + "-meta.txt";
        String tableFilePath = "resources//Databases" + "//" + currentDatabase + "//" + tableName + ".txt";
        File tableFile = new File(tableFilePath);
        //boolean isFileCreated = tableFile.createNewFile();
        if(tableFile.exists()) {
            System.out.println("This table already exists\n");
        }
        else {
            System.out.println(inputString);
            boolean success = validateColumns(inputString, tableName, databaseMetaPath);
            if(success) {
                FileWriter databaseMetaFileWriter = new FileWriter(databaseMetaPath, true);
                FileWriter tableFileWriter = new FileWriter(tableFilePath, true);
                databaseMetaFileWriter.write("Table" + DELIMITER + tableName + "\n" );
                databaseMetaFileWriter.flush();
                for(int i = 0; i< columns.size(); i++) {
                    String[] column = columns.get(i);
                    tableFileWriter.write(column[0] + "|");
                    if(column.length == 3) {
                        databaseMetaFileWriter.write(column[0] + "|" + column[1] + "|" + column[2] + "\n");
                    }
                    else {
                        databaseMetaFileWriter.write(column[0] + "|" + column[1] + "|" + column[2] +  "|" + column[3] + "|" + column[4] +"\n");
                    }
                }
                tableFileWriter.close();
                databaseMetaFileWriter.close();

                QueryLogs queryLogs = new QueryLogs();
                queryLogs.createQueryLog(UserService.getUserName(), "Create", "Success", currentDatabase, tableName, "0",
                        "0", "Create table success");
            }
             else {
                System.out.println("Create table syntax error");
                QueryLogs queryLogs = new QueryLogs();
                queryLogs.createQueryLog(UserService.getUserName(), "Create", "Failure", currentDatabase, tableName, "0",
                        "0", "Create table syntax error");
            }

        }

    }

    public boolean validateColumns(String inputString, String table, String databaseMetaPath)
            throws FileNotFoundException {

        int startParanthesisIndex = inputString.indexOf('(');
        int endParanthesisIndex = inputString.lastIndexOf(')');
        String columnData = inputString.substring(startParanthesisIndex + 1, endParanthesisIndex).trim();
        System.out.println(columnData);
        String[] individualColumns = columnData.split(",");
        ArrayList<String> individualColumnArray = new ArrayList<>(Arrays.asList(individualColumns));
        for(int i = 0; i < individualColumnArray.size(); i++) {
            String test = individualColumnArray.get(i).trim();
            String[] columnWords = test.split(" ");
            if(columnWords.length < 2) {
                System.out.println("Column syntax is incorrect");
                return false;
            } else {
                boolean isColumnNameCorrect = false;
                boolean isColumnTypeCorrect = false;
                boolean isColumnConstraintCorrect = true;
                String columnConstraint = "";

                String columnName = columnWords[0];
                //write columnwords[0] to table file
                if(columnNames.contains(columnName)) {
                    System.out.println("This column name already exists");
                    return false;
                } else {
                    columnNames.add(columnName);
                    isColumnNameCorrect = true;
                }

                String columnDataType = columnWords[1];
                //validate columnwords[1]
                if(!columnDataType.contains("(") && !columnDataType.contains(")")) {
                    boolean isTypeMatched = false;
                    for (ColumnDataTypes dataType : ColumnDataTypes.values()) {
                        if(columnDataType.equalsIgnoreCase(dataType.toString())) {
                            isTypeMatched = true;
                        }
                    }
                    if(isTypeMatched) {
                        //store type data in database-meta file
                        isColumnTypeCorrect = true;
                    } else {
                        System.out.println("Data type is wrong");
                        return false;
                    }
                } else if(columnDataType.contains("(") && columnDataType.indexOf(")") == columnDataType.length() - 1) {
                    //validate varchar, varbinary, enum, set
                    String VARCHAR = "varchar";
                    String VARBINARY = "varbinary";

                    int startParanthesis = columnDataType.indexOf("(");
                    String type = columnDataType.substring(0,startParanthesis);
                    String size = columnDataType.substring(startParanthesis + 1, columnDataType.length() - 1);
                    if(type.equalsIgnoreCase(VARBINARY) || type.equalsIgnoreCase(VARCHAR)) {
                        int integerSize = Integer.parseInt(size);
                        if(integerSize >= 0 && integerSize <= 255) {
                            //store type data in database-meta file
                            isColumnTypeCorrect = true;
                        } else {
                            System.out.println("error");
                            return false;
                        }
                    }
                } else {
                    System.out.println("error");
                    return false;
                }

                String foreignTableColumn = "";
                String foreignTableName = "";


                if(columnWords.length >= 3)  {
                    columnConstraint = columnWords[2];
                    boolean isConstraintMatched = false;
                    if(columnWords.length == 3) {
                        for (ColumnConstraints columnConstraintEnum : ColumnConstraints.values()) {
                            if(columnConstraint.equalsIgnoreCase(columnConstraintEnum.toString())) {
                                isConstraintMatched = true;
                            }
                        }
                    }
                    if(columnWords.length > 3) {
                        int inputLength = columnWords.length;
                        if(inputLength == 5 &&
                                columnWords[2].equalsIgnoreCase(ColumnConstraints.FOREIGN_KEY.toString()) &&
                                columnWords[3].equalsIgnoreCase(REFERENCES)) {
                            String foreignTableData = columnWords[4];
                            int startIndex = foreignTableData.indexOf('(');
                            int endIndex = foreignTableData.lastIndexOf(')');
                            if(endIndex != foreignTableData.length() - 1) {
                                isConstraintMatched = false;
                                return false;
                            } else {
                                foreignTableColumn  = foreignTableData.substring(startIndex + 1, endIndex).trim();
                                foreignTableName = foreignTableData.substring(0, startIndex);
                                boolean columnFound = isForeignKeyValid(databaseMetaPath, foreignTableName, foreignTableColumn, columnDataType);
                                System.out.println(columnFound);
                                if(columnFound) {
                                    isConstraintMatched = true;
                                }
                            }
                        }
                    }

                    if (!isConstraintMatched) {
                        isColumnConstraintCorrect = false;
                        return isColumnConstraintCorrect;
                    }
                    //validate columnwords[2]
                }
                if(isColumnNameCorrect && isColumnTypeCorrect && isColumnConstraintCorrect) {
                    if(columnWords.length <= 3) {
                        String[] column = {columnName, columnDataType, columnConstraint};
                        columns.add(column);
                    }

                    if(columnWords.length > 3) {
                        String[] column = {columnName, columnDataType, columnConstraint, foreignTableName, foreignTableColumn};
                        columns.add(column);
                    }
                }


            }
        }
        return individualColumnArray.size() == columns.size();

    }

    public boolean isForeignKeyValid(String databaseMetaPath, String foreignTableName, String foreignTableColumn, String columnDataType) throws FileNotFoundException {
        boolean isValid = false;
        File f = new File(databaseMetaPath);
        try(BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            String table = "";
            String tableKey = "";
            String keyDataType = "";

            READFILE: while ((line = br.readLine()) != null) {
                int i = 0;
                String[] s = line.split("\\|");
                if (s[0].equals("Table")) {
                    table = s[1];
                } else {
                    int length = s.length;
                    String column = "";
                    while (i < length) {
                        if (s[i].equalsIgnoreCase("primary_key")) {
                            tableKey = s[0];
                            keyDataType = s[1];
                            if(Objects.equals(table, foreignTableName) &&
                                    tableKey.equals(foreignTableColumn) &&
                                    Objects.equals(keyDataType, columnDataType)) {
                                isValid = true;
                                break;
                            }
                            continue READFILE;
                        } else {
                            column += s[i];
                        }
                        if (i != (length - 1)) {
                            column += " | ";
                        }
                        i++;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return isValid;
    }

    public void createTable(String input) throws IOException {
        String[] inputWords = input.split(" ");
        String createSyntax = inputWords[0] + SPACE + inputWords[1] + SPACE;
        //  CreateDatabase db = new CreateDatabase();
        String currentDatabase = UseDatabase.getDatabaseName();
        if(Objects.nonNull(currentDatabase)) {
            if(createSyntax.equalsIgnoreCase(CREATE_TABLE_COMMAND) &&
                    input.lastIndexOf(')') == input.length() - 1) {
                CreateTable createTable = new CreateTable();
                String tableName = inputWords[2];
                if(tableName.indexOf('(') != -1) {
                    tableName = tableName.substring(0, tableName.indexOf('('));
                }
                createTable.create(currentDatabase, tableName, inputWords, input);
            }  else  {
                System.out.println("Wrong Syntax");
            }
        }
    }
}
