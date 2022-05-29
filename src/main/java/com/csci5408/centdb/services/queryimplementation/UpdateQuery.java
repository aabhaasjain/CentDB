package com.csci5408.centdb.services.queryimplementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.csci5408.centdb.logging.QueryLogs;
import com.csci5408.centdb.services.UserService;

public class UpdateQuery {
	public static Object updateQuery(String query, String databaseName, boolean persistentFileUpdate)
			throws IOException {
		ArrayList<String> columns = new ArrayList<>();
		ArrayList<String> data = new ArrayList<>();
		String line;
		String[] columnSplit = null;
		String tableName = "";
		String constraint = "";
		String whereCondition = "";
		String tableNameLog = "";
		int count = 0;
		int position = 0;
		int positionWhere = 0;

		if (query.split(" ")[0].equalsIgnoreCase("update")) {
			System.out.println("An update query identified!");

			try {

				String regex = "update(.*?)set(.*?)";
				Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(query);
				while (matcher.find()) {
					tableName = (matcher.group(1).trim());
					tableNameLog = (matcher.group(1).trim());
					tableName = databaseName + "\\" + tableName + ".txt";
				}

				String regex1 = "set(.*?)where(.*?)";
				Pattern pattern1 = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
				Matcher matcher1 = pattern1.matcher(query);
				while (matcher1.find()) {
					constraint = (matcher1.group(1).trim());
				}

				String[] string_where = query.split("where");
				for (int i = 0; i < string_where.length; i++) {
				}
				whereCondition = string_where[1].trim();

				File file = new File(tableName);
				if (file.exists()) {
					BufferedReader br = new BufferedReader(new FileReader(file));
					StringTokenizer st1 = new StringTokenizer(br.readLine(), "\t");
					while (st1.hasMoreTokens()) {
						columns.add(st1.nextToken());
						count++;
					}

					if (columns.size() > 0) {
						for (String column : columns) {
							columnSplit = column.split("\\|");
						}
						for (int i = 0; i < columnSplit.length; i++) {
							if (columnSplit[i].trim().equals(constraint.split("=")[0].trim())) {
								position = i;
							}
							if (columnSplit[i].trim().equals(whereCondition.split("=")[0].trim())) {
								positionWhere = i;
							}
						}

						System.out.println(position);
						System.out.println(positionWhere);
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

						String where_value = whereCondition.split("=")[1].trim();
						String set_value = constraint.split("=")[1].trim();
						if (persistentFileUpdate) {
							for (int i = 0; i < data.size(); i++) {
								if (data.get(i).split("\\|")[positionWhere].trim().equals(where_value)) {
									data.set(i, data.get(i).replaceAll(data.get(i).split("\\|")[position], set_value));
								}
							}

							FileWriter writer = new FileWriter(tableName);
							writer.write(columns.remove(0) + "\n");
							for (String datum : data) {
								writer.write(datum + "\n");
							}
							writer.close();
						} else {
							List<String[]> columnValuesList = new ArrayList<>();
							String before_value = "";
							Integer rowId = -1;
							System.out.println(data.size());
							for (int i = 0; i < data.size(); i++) {
								if (data.get(i).split("\\|")[positionWhere].trim().equals(where_value)) {
									System.out.println("update if");
									before_value = data.get(i).split("\\|")[position];
									rowId = i + 1;
									data.set(i, data.get(i).replaceAll(data.get(i).split("\\|")[position], set_value));
									String[] columnValues = data.get(i).split("\\|");
									columnValuesList.add(columnValues);
								}
							}
							List<Map<String, String>> bufferPersistence = new ArrayList<>();
							Map<String, String> columnData = new HashMap<>();
							columnData.put("database", databaseName);
							columnData.put("table", tableNameLog);
							columnData.put("condition_column_name", whereCondition.split("=")[0]);
							columnData.put("condition_column_value", whereCondition.split("=")[1]);
							columnData.put("set_column_name", constraint.split("=")[0].trim());
							columnData.put("after_value", constraint.split("=")[1].trim());
							columnData.put("before_value", before_value);
							columnData.put("row_id", rowId.toString());
							String[] columnNames = columns.get(0).split("\\|");
							for (int j = 0; j < columnValuesList.size(); j++) {
								String[] columnValues = columnValuesList.get(j);
								for (int k = 0; k < columnValues.length; k++) {
									columnData.put(columnNames[k].trim(), columnValues[k].trim());
								}
							}
							System.out.println(columnData.size());
							bufferPersistence.add(columnData);
							System.out.println(bufferPersistence);
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
				queryLogs.createQueryLog(UserService.getUserName(), "Update", "Success", UseDatabase.getDatabaseName(),
						tableNameLog, constraint.split("=")[0], constraint, "where " + whereCondition);

			} catch (Exception e) {
				QueryLogs queryLogs = new QueryLogs();
				queryLogs.createQueryLog(UserService.getUserName(), "Update", "Failure", UseDatabase.getDatabaseName(), tableNameLog,
						constraint.split("=")[0], constraint, "where " + whereCondition);
				System.out.println(e);
			}
		} else {
			System.out.println("An update query unidentified!");
		}
		System.out.println("Update Query Completed!");
		return "Update Query Completed!";
	}
}
