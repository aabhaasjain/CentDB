package com.csci5408.centdb.model;

public class LogDetails {
    private String tableName;
    private String databaseName;
    private String operationName;
    private String rowsAffected;

    public String getRowsAffected() {
        return rowsAffected;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private String errorMessage;
    public String getTableName() {
        return tableName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getOperationName() {
        return operationName;
    }


    public LogDetails(LogBuilder logBuilder) {
        this.tableName = logBuilder.tableName;
        this.databaseName = logBuilder.databaseName;
        this.operationName = logBuilder.operationName;
        this.rowsAffected = logBuilder.rowsAffected;
        this.errorMessage = logBuilder.errorMessage;
    }
    public static class LogBuilder{
        private String tableName;
        private String databaseName;
        private String operationName;
        private String rowsAffected;
        private String errorMessage;

        public LogDetails build() {
            LogDetails logDetails =  new LogDetails(this);
            return logDetails;
        }

        public LogBuilder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }
        public LogBuilder databaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }
        public LogBuilder operationName(String operationName) {
            this.operationName = operationName;
            return this;
        }
        public LogBuilder rowsAffected(String rowsAffected) {
            this.rowsAffected = rowsAffected;
            return this;
        }
        public LogBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }
    }
}
