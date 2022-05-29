package com.csci5408.centdb.persistence;

import com.csci5408.centdb.model.Metadata;
import com.csci5408.centdb.model.Query;

import java.io.IOException;
import java.util.List;

public interface IQueryDao {
    boolean dropTable(Query query);
}
