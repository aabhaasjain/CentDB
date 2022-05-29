package com.csci5408.centdb.model;

import java.util.List;

public class Query {
    private String tableName;
    private List<String> columns;
    private String whereCondition;

    public Query(QueryBuilder queryBuilder) {
        this.tableName = queryBuilder.tableName;
        this.columns = queryBuilder.columns;
        this.whereCondition = queryBuilder.whereCondition;
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public String getWhereCondition() {
        return whereCondition;
    }

    public static class QueryBuilder{
        private String tableName;
        private List<String> columns;
        private String whereCondition;

        public QueryBuilder tableName(String tableName){
            this.tableName = tableName;
            return this;
        }
        public QueryBuilder columns(List<String> columns){
            this.columns = columns;
            return this;
        }
        public QueryBuilder whereCondition(String whereCondition){
            this.whereCondition = whereCondition;
            return this;
        }
        public Query build(){
            Query query = new Query(this);
            return query;
        }
    }

}
