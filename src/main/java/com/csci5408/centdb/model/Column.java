package com.csci5408.centdb.model;

public class Column {
    private String name;
    private String type;
    private String constraint;

    public Column() {
    }

    public Column(String name, String type, String constraint) {
        this.name = name;
        this.type = type;
        this.constraint = constraint;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }
}
