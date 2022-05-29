package com.csci5408.centdb.services.queryimplementation;

import com.csci5408.centdb.logging.QueryLogs;
import com.csci5408.centdb.services.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UseDatabase {
    static final String USE_DATABASE_COMMAND = "use";

    private static String name;
    public static String getDatabaseName() {
        return name;
    }
    public static void setDatabaseName(String newName) {
        name = newName;
    }
    public static boolean isDatabaseSet(){
        String currentDatabase = UseDatabase.getDatabaseName();
        if(currentDatabase == null) {
            return false;
        }
        return true;
    }
    public void use(String input) throws IOException {
        String[] inputWords = input.split(" ");
        // use database syntax will have length 2
        String initializerString = inputWords[0];
        System.out.println(initializerString);
        if(initializerString.equalsIgnoreCase(USE_DATABASE_COMMAND)) {
            // set path of the db with name array[1]. if it doesn't exist, throw error
            Path path = Paths.get("resources/Databases/" + inputWords[1]);
            if(!Files.exists(path)) {
                System.out.println("This database does not exist");
                QueryLogs queryLogs = new QueryLogs();
                queryLogs.createQueryLog(UserService.getUserName(), "Use", "Failure", inputWords[1], "", "0",
                        "0", "Trying to use a non-existing database");
            }
            else {
                System.out.println("Setting database name");
                System.out.println(inputWords[1]);
                QueryLogs queryLogs = new QueryLogs();
                queryLogs.createQueryLog(UserService.getUserName(), "Use", "Success", inputWords[1], "", "0",
                        "0", "Database name set");
                setDatabaseName(inputWords[1]);
            }
        }  else  {
            System.out.println("Wrong Syntax");
            QueryLogs queryLogs = new QueryLogs();
            queryLogs.createQueryLog(UserService.getUserName(), "Use", "Failure", inputWords[1], "", "0",
                    "0", "use database syntax error");
        }
    }
}
