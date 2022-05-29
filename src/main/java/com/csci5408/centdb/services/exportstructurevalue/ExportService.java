package com.csci5408.centdb.services.exportstructurevalue;

import static com.csci5408.centdb.model.util.Constants.DELIMITER;
import static com.csci5408.centdb.model.util.Constants.EXPORT_PATH;
import static com.csci5408.centdb.model.util.Constants.TABLE_PATH;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.csci5408.centdb.model.Column;
import com.csci5408.centdb.model.Metadata;
import com.csci5408.centdb.persistence.IFileReader;
import com.csci5408.centdb.persistence.impl.FileReader;
import com.csci5408.centdb.services.UserService;
import com.csci5408.centdb.services.queryimplementation.UseDatabase;

public class ExportService {
	IFileReader fileReader;

	public ExportService() {
		this.fileReader = new FileReader();
	}


	private String buildCreateQuery(Metadata tableInfo) {
		StringBuilder createQuery = new StringBuilder(String.format(
				"--\n" + "-- Table structure for table %s\n" + "--\n" + "DROP TABLE IF EXISTS %s;\n"
						+ "CREATE TABLE %s (",
				tableInfo.getTableName(), tableInfo.getTableName(), tableInfo.getTableName()));
		List<Column> columns = tableInfo.getColumns();
		for (int i = 0; i < columns.size(); i++) {
			createQuery.append("\n" + columns.get(i).getName() + " " + columns.get(i).getType());
			if (Objects.nonNull(columns.get(i).getConstraint())) {
				createQuery.append(" " + columns.get(i).getConstraint());
			}
			if (i != columns.size() - 1) {
				createQuery.append(String.format(","));
			}
		}
		createQuery.append(String.format(");\n"));
		return createQuery.toString();
	}

	private String buildInsertQuery(String tableName) throws IOException {
		String tablePath = String.format(TABLE_PATH, UseDatabase.getDatabaseName(), tableName);

		StringBuilder insertQuery = new StringBuilder(String.format("--\n" + "-- Dumping data for table %s\n" + "--\n"
				+ "LOCK TABLES %s WRITE;\n" + "INSERT INTO %s VALUES ", tableName, tableName, tableName));
		List<String> columnValues = fileReader.getColumnValues(tablePath);
		String insertColumn = columnValues.get(0).replace(DELIMITER, ",");
		insertQuery.append(String.format("(%s)\nVALUES ", insertColumn));
		for (int i = 1; i < columnValues.size(); i++) {
			insertColumn = columnValues.get(i).replace(DELIMITER, ",");
			insertQuery.append(String.format("(%s)", insertColumn));
			if (i != columnValues.size() - 1) {
				insertQuery.append(String.format(","));
			}
		}
		insertQuery.append(String.format(";\n" + "UNLOCK TABLES;"));
		return insertQuery.toString();
	}
	private String selectDatabase() throws IOException {
		Scanner sc = new Scanner(System.in);
		Set<Map.Entry<String,String>> databaseNames = fileReader.getDatabaseNames();
		if(databaseNames.size()>0){
			for (Map.Entry<String,String> databaseName: databaseNames) {
				System.out.println(databaseName.getKey()+". "+databaseName.getValue());
			}
		}
		System.out.println("Please select a database: ");

		String choice = sc.nextLine();
		for (Map.Entry<String,String> databaseName: databaseNames) {
			if(databaseName.getKey().equals(choice.trim()))
			{
				return databaseName.getValue();
			}
		}
		return null;
	}
	public void createExport() throws IOException {
		String dbOption = selectDatabase();
		if(Objects.nonNull(dbOption)){
			UseDatabase.setDatabaseName(dbOption);
		}
		else{
			System.out.println("Incorrect option selected!");
			return;
		}
		String exportPath = String.format(EXPORT_PATH, UseDatabase.getDatabaseName());
		fileReader.createFile(exportPath);
		FileWriter fileWriter = new FileWriter(exportPath, false);
		List<Metadata> metadataList = fileReader.getMetadata();
		StringBuilder exportText = new StringBuilder(String.format("-- Host: localhost    Database: %s\n"
				+ "---------------------------------------------------------" + "", UseDatabase.getDatabaseName()));
		for (Metadata metadata : metadataList) {
			exportText.append(buildCreateQuery(metadata));
			exportText.append(buildInsertQuery(metadata.getTableName()));
		}
		fileWriter.write(exportText.toString());
		fileWriter.close();
		System.out.println("Export file generated in location: " + exportPath);
	}
}
