package com.csci5408.centdb.logging;

import com.csci5408.centdb.model.LogDetails;
import com.csci5408.centdb.model.Transaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

import static com.csci5408.centdb.model.util.Constants.*;

public class EventLogs {

	public static void createTransactionLog(Transaction transaction, String databaseName) throws IOException {
		try {
			FileWriter fileWriter;
			File file = new File(String.format(TRANSACTION_LOG_PATH, databaseName));
			System.out.println(file);
			fileWriter = new FileWriter(String.format(TRANSACTION_LOG_PATH, databaseName), true);

			if (!file.exists()) {
				file.createNewFile();
			}
			if (file.length() == 0) {
				fileWriter.write(String.format(TRANSACTION_LOG_HEADER));
			}
			fileWriter.write(generateTransactionLogRow(transaction));
			fileWriter.close();
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static void createCrashReport(LogDetails logDetails) throws IOException {
		FileWriter fileWriter;
		File file = new File(String.format(CRASH_REPORT_PATH));
		if (!file.exists()) {
			file.createNewFile();
		}
		fileWriter = new FileWriter(String.format(CRASH_REPORT_PATH), true);
		fileWriter.write(generateCrashReport(logDetails));
		fileWriter.close();
	}

	public static void createEventLog(LogDetails logDetails) throws IOException {
		FileWriter fileWriter;
		File file = new File(String.format(EVENT_LOG_PATH));
		if (!file.exists()) {
			file.createNewFile();
		}
		fileWriter = new FileWriter(String.format(EVENT_LOG_PATH), true);
		fileWriter.write(generateEventLog(logDetails));
		fileWriter.close();
	}

	private static String generateEventLog(LogDetails logDetails) {
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		return String.format("%s: %s Database has been changed\n" + "%s operation performed\n" + "%s rows affected\n",
				timeStamp.toString(), logDetails.getDatabaseName(), logDetails.getOperationName(),
				logDetails.getRowsAffected());
	}

	private static String generateCrashReport(LogDetails logDetails) {
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		return String.format(
				"\n---------------------------------------------------------------------------------------------------------------------\n"
						+ "------------------------------------------------------CRASH REPORT---------------------------------------------------\n"
						+ "---------------------------------------------------------------------------------------------------------------------\n"
						+ "TimeStamp: %s\n" + "Error occurred while performing %s in %s.%s\n" + "Error Message: %s",
				timeStamp.toString(), logDetails.getOperationName(), logDetails.getDatabaseName(),
				logDetails.getTableName(), logDetails.getErrorMessage());
	}

	private static String generateTransactionLogRow(Transaction transaction) {
		return String.format("\n" + transaction.getTransactionId() + DELIMITER + transaction.getTransactionNumber()
				+ DELIMITER + transaction.getPreviousPtr() + DELIMITER + transaction.getNextPtr() + DELIMITER
				+ transaction.getOperation() + DELIMITER + transaction.getTableName() + DELIMITER
				+ transaction.getRowId() + DELIMITER + transaction.getColumnName() + DELIMITER
				+ transaction.getBeforeVal() + DELIMITER + transaction.getAfterVal());
	}
}
