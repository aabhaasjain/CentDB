package com.csci5408.centdb.model.util;

public class Database {
    private static String databaseName;

    public static String getDatabaseName() {
        return databaseName;
    }

    public static void setDatabaseName(String databaseName) {
        Database.databaseName = databaseName;
    }
}
