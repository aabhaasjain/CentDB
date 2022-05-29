package com.csci5408.centdb.persistence;

import com.csci5408.centdb.model.Metadata;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IFileReader {
    List<Metadata> getMetadata() throws IOException;
    Set<Map.Entry<String,String>> getDatabaseNames() throws IOException;
    List<String> getColumnValues(String tablePath) throws IOException;
    boolean createFile(String path) throws IOException;
    boolean checkFileExists(String path);

}
