package com.csci5408.centdb.services.transactions;

import static com.csci5408.centdb.model.util.Constants.TRANSACTION_LOG_PATH;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.csci5408.centdb.logging.EventLogs;
import com.csci5408.centdb.model.Transaction;
import com.csci5408.centdb.services.QueryValidator;
import com.csci5408.centdb.services.queryimplementation.DeleteQuery;
import com.csci5408.centdb.services.queryimplementation.InsertQuery;
import com.csci5408.centdb.services.queryimplementation.UpdateQuery;
import com.csci5408.centdb.services.queryimplementation.UseDatabase;

public class Transactions {

	static List<Transaction> transactionsList = new ArrayList<Transaction>();
	static Integer currentPointer = 0;
	static Integer previousPointer = null;
	static Integer nextPointer = null;
	static Integer count = 1;
	static Integer transactionNumber = 101;
	static List<Map<String, String>> bufferPersistence = new ArrayList<>();

	public void processTransaction(String query, String database) throws Exception {
		Map<String, Map<String, String>> tableData = new HashMap<>();
		QueryValidator queryValidator = new QueryValidator();

		String[] commands = query.split(";");
		List<String> commandsList = Arrays.asList(commands);
		COMMAND:
		for (int i = 0; i < commandsList.size(); i++) {
			if (i == 0) {
				currentPointer = null;
				transactionsList.add(createTransactionLog(null, "start"));
				continue;
			}
			if (i == commandsList.size() - 1) {
				nextPointer = null;
			}
			String statement = commandsList.get(i);
			if (statement.trim().startsWith("insert")) {
				if (queryValidator.validateQuery(statement)) {


					Object updatedResult = InsertQuery.insert(statement, database, UseDatabase.getDatabaseName(),
							false);
					if (updatedResult instanceof String) {
						System.out.println("Aborting the transaction");
						break COMMAND;
					}

					addToBuffer(updatedResult, "insert");
				} else {
					throw new Exception("There's an error in the syntax..please check it");
				}
			} else if (statement.trim().startsWith("update"))

			{
				if (queryValidator.validateQuery(statement)) {
					Object updatedResult = UpdateQuery.updateQuery(statement, database, false);

					if (updatedResult instanceof String) {
						System.out.println("Aborting the transaction");
						break COMMAND;
					}

					addToBuffer(updatedResult, "update");
				} else {
					throw new Exception("There's an error in the syntax..please check it");

				}
			} else if (statement.trim().startsWith("delete")) {
				if (queryValidator.validateQuery(statement)) {
					Object rowToBeDeleted = DeleteQuery.deleteQuery(statement, database, false);

					if (rowToBeDeleted instanceof String) {
						System.out.println("Aborting the transaction");
						break COMMAND;
					}

					addToBuffer(rowToBeDeleted, "delete");
				} else {
					throw new Exception("There's an error in the syntax..please check it");

				}
			} else if (statement.trim().startsWith("commit")) {
				if (queryValidator.validateQuery(statement)) {
					CommitToPersistence.commitToPersistenceFile(bufferPersistence, tableData);
					transactionsList.add(createTransactionLog(null, "commit"));
					bufferPersistence.clear();
				} else {
					throw new Exception("There's an error in the syntax..please check it");
				}
			} else if (statement.trim().startsWith("rollback")) {

				if (queryValidator.validateQuery(statement)) {
					transactionsList.add(createTransactionLog(null, "rollback"));
					bufferPersistence.clear();
				} else {
					throw new Exception("There's an error in the syntax..please check it");
				}
			}
		}

		addTransactionLogs();
	}

	private static void addTransactionLogs() throws IOException {
		for (Transaction t : transactionsList) {
			EventLogs.createTransactionLog(t, UseDatabase.getDatabaseName());
		}
	}

	private static Transaction createTransactionLog(Object updatedResult, String operation) {

		List<Map<String, String>> transactionData = (List<Map<String, String>>) updatedResult;
		Transaction transaction = null;
		if (transactionData == null) {
			if (operation.equalsIgnoreCase("start") && currentPointer == null) {
				generateTransactionId();
				currentPointer = count;
				nextPointer = currentPointer + 1;
				transaction = new Transaction(currentPointer.toString(), transactionNumber.toString(), null,
						nextPointer.toString(), operation, "start transaction", null, null, null, null);
			} else if (operation.equalsIgnoreCase("commit") || operation.equalsIgnoreCase("rollback")) {
				transaction = new Transaction(currentPointer.toString(), transactionNumber.toString(),
						previousPointer.toString(), null, operation, "end of transaction", null, null, null, null);
			}
		} else {
			for (Map<String, String> data : transactionData) {
				if (operation.equalsIgnoreCase("insert")) {
					nextPointer = currentPointer + 1;
					transaction = new Transaction(currentPointer.toString(), transactionNumber.toString(),
							previousPointer.toString(), nextPointer.toString(), operation, data.get("table"), null,
							null, null, null);
				} else if (operation.equalsIgnoreCase("update")) {
					nextPointer = currentPointer + 1;
					transaction = new Transaction(currentPointer.toString(), transactionNumber.toString(),
							previousPointer.toString(), nextPointer.toString(), operation, data.get("table"),
							data.get("row_id"), data.get("set_column_name"), data.get("before_value"),
							data.get("after_value"));
				} else if (operation.equalsIgnoreCase("delete")) {
					nextPointer = currentPointer + 1;
					transaction = new Transaction(currentPointer.toString(), transactionNumber.toString(),
							previousPointer.toString(), nextPointer.toString(), operation, data.get("table"),
							data.get("rowId"), null, null, null);
				}
			}
		}
		previousPointer = currentPointer;
		currentPointer = nextPointer;
		return transaction;
	}

	private static void addToBuffer(Object result, String operation) {
		if (result instanceof List) {
			for (Map<String, String> map : (List<Map<String, String>>) result) {
				map.put("queryType", operation);
				bufferPersistence.add(map);
			}
			System.out.println(bufferPersistence);
			transactionsList.add(createTransactionLog(result, operation));
		}
	}

	private static void generateTransactionId() {

		try {
			File file = new File(String.format(TRANSACTION_LOG_PATH, "test"));
			if (file.exists()) {
				Scanner sc = new Scanner(file);
				String s = "";
				while (sc.hasNextLine()) {
					s = sc.nextLine();
					count++;
				}
				sc.close();
				if (count > 0) {
					count -= 2;
					String transactionNo = s.split("\\|")[1].trim();
					transactionNumber = Integer.parseInt(transactionNo) + 1;
				}
			}

		} catch (Exception e) {
			e.getStackTrace();
		}
	}
}
