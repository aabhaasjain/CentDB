package com.csci5408.centdb.services.queryimplementation;

import com.csci5408.centdb.logging.QueryLogs;
import com.csci5408.centdb.services.UserService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CreateDatabase {

    static final String CREATE_DATABASE_COMMAND = "Create database ";
    static final String DATABASES = "resources\\Databases";
    private static String name;

    public static String getDatabaseName() {
        return name;
    }
    public void setDatabaseName(String newName) {
        name = newName;
    }

    public void createDb(String inputString) throws IOException {
        String[] inputWords = inputString.split(" ");

        String createCommandString = inputWords[0] + " " + inputWords[1] + " ";
        if(createCommandString.equalsIgnoreCase(CREATE_DATABASE_COMMAND)) {
            // create databases folder if it doesn't exist
            File databasesDirectory = new File(DATABASES);
            if(!databasesDirectory.exists())
            {
                databasesDirectory.mkdir();
            }

            // create database.txt if it doesn't exist
            File databasesMeta = new File("resources\\databases.txt");
            boolean isNewlyCreated = databasesMeta.createNewFile();
            FileWriter metaFileWriter = new FileWriter(databasesMeta, true);
            if(isNewlyCreated) {
                metaFileWriter.write("Databases\n");
                metaFileWriter.flush();
            }

            //create folder with array[2] as name. if it exists, throw error
            String databasePathDir =  DATABASES + "//" + inputWords[2];
            System.out.println(databasePathDir);
            File databaseDirectory = new File(databasePathDir);
            boolean folderCreated = databaseDirectory.mkdir();
            if(!folderCreated) {
                System.out.println("This database already exists");
                QueryLogs queryLogs = new QueryLogs();
                queryLogs.createQueryLog(UserService.getUserName(), "Create", "Failure", inputWords[2], "", "0",
                        "0", "Trying to create an existing database");
            } else {
                metaFileWriter.write(inputWords[2] + "\n");
                metaFileWriter.close();
                // create db metadata file
                File databaseMetaFile = new File(databasePathDir + "//" + inputWords[2] + "-meta.txt");
                databaseMetaFile.createNewFile();
                name = inputWords[2];
                QueryLogs queryLogs = new QueryLogs();
                queryLogs.createQueryLog(UserService.getUserName(), "Create", "success", inputWords[2], "", "0",
                        "0", "Create new database");
            }
        } else  {
            System.out.println("Wrong Syntax");
            QueryLogs queryLogs = new QueryLogs();
            queryLogs.createQueryLog(UserService.getUserName(), "Create", "Failure", inputWords[2], "", "0",
                    "0", "create database syntax error");
        }
    }
}
