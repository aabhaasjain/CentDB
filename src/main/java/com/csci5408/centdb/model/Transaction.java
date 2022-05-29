package com.csci5408.centdb.model;

public class Transaction {
    private String transactionId;
    private String transactionNumber;
    private String previousPtr;
    private String nextPtr;
    private String operation;
    private String tableName;
    private String rowId;
    private String columnName;
    private String beforeVal;
    private String afterVal;

    public String getTransactionId() {
        return transactionId;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public String getPreviousPtr() {
        return previousPtr;
    }

    public String getNextPtr() {
        return nextPtr;
    }

    public String getOperation() {
        return operation;
    }

    public String getTableName() {
        return tableName;
    }

    public String getRowId() {
        return rowId;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getBeforeVal() {
        return beforeVal;
    }

    public String getAfterVal() {
        return afterVal;
    }

    public Transaction(String transactionId, String transactionNumber, String previousPtr, String nextPtr, String operation, String tableName, String rowId, String columnName, String beforeVal, String afterVal) {
        this.transactionId = transactionId;
        this.transactionNumber = transactionNumber;
        this.previousPtr = previousPtr;
        this.nextPtr = nextPtr;
        this.operation = operation;
        this.tableName = tableName;
        this.rowId = rowId;
        this.columnName = columnName;
        this.beforeVal = beforeVal;
        this.afterVal = afterVal;
    }
}
