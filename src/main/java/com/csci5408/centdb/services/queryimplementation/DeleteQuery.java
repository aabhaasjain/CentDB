package com.csci5408.centdb.services.queryimplementation;

import com.csci5408.centdb.logging.QueryLogs;
import com.csci5408.centdb.model.User;
import com.csci5408.centdb.services.UserService;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class DeleteQuery {
	public static Object deleteQuery(String query, String databaseName, boolean persistentFileUpdate)
			throws IOException {
		ArrayList<String> columns = new ArrayList<>();
		ArrayList<String> data = new ArrayList<>();
		String tableName = "";
		String whereCondition = "";
		String[] columnSplit = null;
		String line;
		String tableNameLog = "";
		int position = 0;
		int count = 0;

		if (query.toLowerCase().contains("delete from")) {
			System.out.println("Delete from table query identified!");

			try {
				String regex = "delete from(.*?)where(.*?)";
				Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(query);
				while (matcher.find()) {
					tableName = (matcher.group(1).trim());
					tableNameLog = (matcher.group(1).trim());
					tableName = databaseName + "\\" + tableName + ".txt";
				}

				String[] string_where = query.split("where");
				whereCondition = string_where[1].trim();
				String where_value = whereCondition.split("=")[1].trim();
				String where_column = whereCondition.split("=")[0].trim();

				File file = new File(tableName);
				if (file.exists()) {
					BufferedReader br = new BufferedReader(new FileReader(tableName));
					StringTokenizer st1 = new StringTokenizer(br.readLine(), "\t");
					while (st1.hasMoreTokens()) {
						columns.add(st1.nextToken());
						count++;
					}

					if (columns.size() > 0) {
						for (String column : columns) {
							columnSplit = column.split("\\|");
						}
						for (int i = 0; i < Objects.requireNonNull(columnSplit).length; i++) {
							if (columnSplit[i].trim().equals(where_column)) {
								position = i;
							}
						}

						while ((line = br.readLine()) != null) {
							StringTokenizer st2 = new StringTokenizer(line, "\t");
							for (int i = 0; i < count; i++) {
								if (st2.hasMoreTokens()) {
									data.add(st2.nextToken());
								} else {
									data.add("");
								}
							}
						}

						if (persistentFileUpdate) {
							for (int i = 0; i < data.size(); i++) {
								if (data.get(i).split("\\|")[position].trim().equals(where_value)) {
									data.remove(i);
								}
							}
							FileWriter writer = new FileWriter(tableName);
							writer.write(columns.remove(0) + "\n");
							for (String datum : data) {
								writer.write(datum + "\n");
							}
							writer.close();
						} else {
							Integer rowId = -1;
							for (int i = 0; i < data.size(); i++) {
								if (data.get(i).split("\\|")[position].trim().equals(where_value)) {
									rowId = i;
								}
							}
							List<Map<String, String>> bufferPersistence = new ArrayList<>();
							Map<String, String> columnData = new HashMap<>();
							columnData.put("database", databaseName);
							columnData.put("table", tableNameLog);
							columnData.put("rowId", rowId.toString());
							columnData.put("where_condition", whereCondition);
							bufferPersistence.add(columnData);
							return bufferPersistence;
						}
					} else {
						System.out.println("No data in Table: " + tableName);
					}
					br.close();
				} else {
					System.out.println(tableName + ": Table doesn't exist");
				}

				QueryLogs queryLogs = new QueryLogs();
				queryLogs.createQueryLog(UserService.getUserName(), "Delete Row", "Success",
						UseDatabase.getDatabaseName(), tableNameLog, "NA", "NA", "where " + whereCondition);

			} catch (Exception e) {
				QueryLogs queryLogs = new QueryLogs();
				queryLogs.createQueryLog(UserService.getUserName(), "Delete Table Row", "Failure",
						UseDatabase.getDatabaseName(), tableNameLog, "NA", "NA", "where " + whereCondition);
				System.out.println(e);
			}
		} else {
			System.out.println("Delete query unidentified!");
		}
		System.out.println("Delete Query Completed!");
		return "Delete Query Completed!";
	}
}
