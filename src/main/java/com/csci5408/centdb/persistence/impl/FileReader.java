package com.csci5408.centdb.persistence.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import com.csci5408.centdb.model.Column;
import com.csci5408.centdb.model.Metadata;
import com.csci5408.centdb.persistence.IFileReader;
import com.csci5408.centdb.services.queryimplementation.UseDatabase;

import static com.csci5408.centdb.model.util.Constants.*;

public class FileReader implements IFileReader {
	@Override
	public List<Metadata> getMetadata() throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new java.io.FileReader(
				String.format(METADATA_PATH, UseDatabase.getDatabaseName(), UseDatabase.getDatabaseName())));

		List<Metadata> metadataList = new ArrayList<>();
		List<Column> columns = new ArrayList<>();
		String tableName = null;

		for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
			List<String> strings = Arrays.asList(line.split(Pattern.quote(DELIMITER)));
			if (strings.get(0).equalsIgnoreCase("table")) {
				if (Objects.nonNull(tableName) && columns.size() > 0) {
					add(metadataList, tableName, columns);
					columns = new ArrayList<>();
				}
				tableName = strings.get(1);
			} else {
				Column column = new Column();
				column.setName(strings.get(0));
				column.setType(strings.get(1));
				if (strings.size() > 2) {
					column.setConstraint(strings.get(2));
				}
				columns.add(column);
			}
		}
		add(metadataList, tableName, columns);
		return metadataList;
	}

	@Override
	public Set<Map.Entry<String, String>> getDatabaseNames() throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new java.io.FileReader(String.format(DB_METADATA_PATH)));
		Map<String,String> databaseNames = new HashMap<>();
		int ctr = 1;
		for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
			databaseNames.put(Integer.toString(ctr),line.trim());
			ctr++;
		}
		return databaseNames.entrySet();
	}

	@Override
	public List<String> getColumnValues(String tablePath) throws IOException {
		List<String> tableList = new ArrayList<>();
		if (checkFileExists(tablePath)) {
			BufferedReader bufferedReader = new BufferedReader(new java.io.FileReader(tablePath));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				tableList.add(line);
			}
		}
		return tableList;
	}

	public boolean checkFileExists(String path) {
		File file = new File(path);
		return file.exists();
	}

	public boolean createFile(String path) throws IOException {
		File file = new File(path);
		return file.createNewFile();
	}

	private void add(List<Metadata> metadataList, String tableName, List<Column> columns) {
		Metadata tableMetadata = new Metadata();
		tableMetadata.setTableName(tableName);
		tableMetadata.setColumns(columns);
		metadataList.add(tableMetadata);
	}
}
