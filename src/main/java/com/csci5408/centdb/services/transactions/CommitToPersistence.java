package com.csci5408.centdb.services.transactions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.csci5408.centdb.logging.EventLogs;
import com.csci5408.centdb.logging.QueryLogs;
import com.csci5408.centdb.model.LogDetails;
import com.csci5408.centdb.model.LogDetails.LogBuilder;
import com.csci5408.centdb.model.User;
import com.csci5408.centdb.persistence.UserDao;
import com.csci5408.centdb.services.UserService;

public class CommitToPersistence {

	public static void commitToPersistenceFile(List<Map<String, String>> bufferPersistence,
			Map<String, Map<String, String>> tableData) throws IOException {
		TRANSACTION: for (Map<String, String> buffer : bufferPersistence) {
			String fileName = buffer.get("database") + "\\" + buffer.get("table") + ".txt";
			File file = new File(fileName);
			if (file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
				if (tableData.containsKey(buffer.get("database")))
					tableData.get(buffer.get("database")).putIfAbsent(buffer.get("table"), content);
				else {
					HashMap<String, String> tableMap = new HashMap<>();
					tableMap.put(buffer.get("table"), content);
					tableData.put(buffer.get("database"), tableMap);
				}
				br.close();
				if (buffer.get("queryType").equalsIgnoreCase("insert")) {
					try {
						commitInsertedData(buffer, content, file);
					} catch (Exception e) {
						System.out.println(
								"An exception occured while performing transaction.. rolling back the database changes");
						rollback(tableData);
						break TRANSACTION;
					}
				}
				if (buffer.get("queryType").equalsIgnoreCase("update")) {
					try {
						commitUpdatedData(buffer, content, file);
					} catch (Exception e) {
						System.out.println(
								"An exception occured while performing transaction.. rolling back the database changes");
						rollback(tableData);
						break TRANSACTION;
					}
				}
				if (buffer.get("queryType").equalsIgnoreCase("delete")) {
					try {
						commitDeletedData(buffer, content, file);
					} catch (Exception e) {
						System.out.println(
								"An exception occured while performing transaction.. rolling back the database changes");
						rollback(tableData);
						break TRANSACTION;
					}
				}
			}

		}
	}

	private static void commitInsertedData(Map<String, String> buffer, String content, File file) throws IOException {
		Integer rowsEffected = 0;
		String[] columnNames = null;
		BufferedReader br = new BufferedReader(
				new FileReader(buffer.get("database") + "\\" + buffer.get("table") + ".txt"));
		String columnName = br.readLine();
		if (columnName != null) {
			columnNames = columnName.split("\\|");
		}
		try {
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(buffer.get("database") + "\\" + buffer.get("table") + ".txt", true));
			bw.append(System.lineSeparator());
			String newRow = "";
			for (String col : columnNames) {
				newRow = newRow + buffer.get(col) + "|";
			}
			rowsEffected += 1;
			newRow = newRow.substring(0, newRow.length() - 1);
			bw.append(newRow);
			bw.close();
			QueryLogs queryLogs = new QueryLogs();
			queryLogs.createQueryLog(UserService.getUserName(), "Insert Row", "Success", buffer.get("database"),
					buffer.get("table"), "NA", "NA", "NA");
			generateEventLogs(buffer, "insert", rowsEffected.toString());
		} catch (Exception e) {
			QueryLogs queryLogs = new QueryLogs();
			queryLogs.createQueryLog(UserService.getUserName(), "Insert Row", "Failure", buffer.get("database"),
					buffer.get("table"), "NA", "NA", "NA");
			throw e;
		}
	}

	private static void commitDeletedData(Map<String, String> buffer, String content, File file) throws IOException {
		Integer rowsEffected = 0;
		try {
			BufferedWriter wr = new BufferedWriter(new FileWriter(file));
			String rows[] = content.split("\\n");
			int id = Integer.parseInt(buffer.get("rowId")) + 1;
			for (int i = 0; i < rows.length; i++) {
				if (i == id) {
					rowsEffected += 1;
					continue;
				} else
					wr.append(rows[i]);
			}
			wr.close();
			QueryLogs queryLogs = new QueryLogs();
			queryLogs.createQueryLog(UserService.getUserName(), "Delete Row", "Success", buffer.get("database"),
					buffer.get("table"), "NA", "NA", "where " + buffer.get("where_condition"));
			generateEventLogs(buffer, "delete", rowsEffected.toString());
		} catch (Exception e) {
			QueryLogs queryLogs = new QueryLogs();
			queryLogs.createQueryLog(UserService.getUserName(), "Delete Row", "Failure", buffer.get("database"),
					buffer.get("table"), "NA", "NA", "where " + buffer.get("where_condition"));
			throw e;
		}
	}

	private static void commitUpdatedData(Map<String, String> buffer, String content, File file) throws Exception {
		String st;
		int conditionindex = -1;
		int setIndex = -1;
		String conditionColumn = buffer.get("condition_column_name").trim();
		String setColumn = buffer.get("set_column_name").trim();
		String rows[] = content.split("\\n");
		BufferedWriter wr = new BufferedWriter(new FileWriter(file));
		String columnNames = rows[0];
		String[] columns = columnNames.split("\\|");
		Integer rowsEffected = 0;
		for (int j = 0; j < columns.length; j++) {
			String c = columns[j];
			if (c.trim().replaceAll("\\r", "").equals(conditionColumn.trim().replaceAll("\\r", ""))) {
				conditionindex = j;
			}
			if (c.trim().replaceAll("\\r", "").equals(setColumn.trim().replaceAll("\\r", ""))) {
				setIndex = j;
				rowsEffected += 1;
			}
		}
		String columnsString = createString(columns);
		wr.write(columnsString + "\n");
		int flag = 0;
		// writing to persistence
		try {
			ROW: for (int row = 1; row < rows.length; row++) {
				String[] values = rows[row].split("\\|");
				for (int k = 0; k < values.length; k++) {
					String value = values[k].replaceAll("\\r", "");
					if (k == conditionindex && flag == 0) {
						if (buffer.get(conditionColumn).equals(value.trim())) {
							flag = 1;
							values[setIndex] = buffer.get(setColumn);
							st = createString(values);
							wr.write(st + "\n");
							continue ROW;
						}
					}
				}
				st = createString(values);
				wr.write(st + "\n");
			}
			wr.close();
			QueryLogs queryLogs = new QueryLogs();
			queryLogs.createQueryLog(UserService.getUserName(), "Update", "Success", buffer.get("database"),
					buffer.get("table"), buffer.get("set_column_name"),
					buffer.get("set_column_name") + "=" + buffer.get("after_value"),
					"where " + buffer.get("condition_column_name") + "=" + buffer.get(("condition_column_value")));
			generateEventLogs(buffer, "update", rowsEffected.toString());
		} catch (Exception e) {
			QueryLogs queryLogs = new QueryLogs();
			queryLogs.createQueryLog(UserService.getUserName(), "Update", "Failure", buffer.get("database"),
					buffer.get("table"), buffer.get("set_column_name"),
					buffer.get("set_column_name") + "=" + buffer.get("after_value"),
					"where " + buffer.get("condition_column_name") + "=" + buffer.get(("condition_column_value")));
			throw e;
		}
	}

	private static void rollback(Map<String, Map<String, String>> tableData) throws IOException {
		for (String database : tableData.keySet()) {
			Map<String, String> table = tableData.get(database);
			for (String s : table.keySet()) {
				String tableName = "resources\\Databases\\" + database + "\\" + s + ".txt";
				File f = new File(tableName);
				BufferedWriter writer = new BufferedWriter(new FileWriter(f));
				writer.write(table.get(s));
				writer.close();
			}
		}
	}

	private static String createString(String[] values) {
		String st = "";
		for (String s : values) {
			st += s.replaceAll(" \"\" ", "") + "|";
		}
		return st.substring(0, st.length() - 1);
	}

	private static void generateEventLogs(Map<String, String> buffer, String operation, String rowsEffected)
			throws IOException {
		LogBuilder logBuilder = new LogBuilder();
		logBuilder.tableName(buffer.get("table"));
		logBuilder.databaseName(buffer.get("database"));
		logBuilder.operationName(operation);
		logBuilder.rowsAffected(rowsEffected.toString());
		LogDetails logDetails = logBuilder.build();
		EventLogs.createEventLog(logDetails);
	}
}
