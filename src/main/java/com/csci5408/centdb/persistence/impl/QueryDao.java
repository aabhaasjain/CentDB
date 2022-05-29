package com.csci5408.centdb.persistence.impl;

import com.csci5408.centdb.model.Column;
import com.csci5408.centdb.model.Metadata;
import com.csci5408.centdb.model.Query;
import com.csci5408.centdb.persistence.IQueryDao;
import com.csci5408.centdb.services.queryimplementation.UseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static com.csci5408.centdb.model.util.Constants.DELIMITER;
import static com.csci5408.centdb.model.util.Constants.METADATA_PATH;

public class QueryDao implements IQueryDao {

    @Override
    public boolean dropTable(Query query) {
        try{
            File file = new File(String.format("resources/Databases/%s/%s.txt",UseDatabase.getDatabaseName(),query.getTableName()));
            if(file.delete()){
                System.out.println(String.format("Table: %s has been removed from %s",query.getTableName(),UseDatabase.getDatabaseName()));
                return true;
            }
            else{
                System.out.println("Failed to drop the table.");
                return false;
            }
        }
        catch (Exception exception){
            throw exception;
        }
    }
}
