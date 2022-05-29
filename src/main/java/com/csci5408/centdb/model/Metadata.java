package com.csci5408.centdb.model;

import java.util.List;

public class Metadata {
    private String tableName;
    private List<Column> columns;

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public String getTableName() {
        return tableName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Metadata() {
    }

    public Metadata(String tableName, List<Column> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }
}
