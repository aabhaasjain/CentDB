package com.csci5408.centdb.services;

import com.csci5408.centdb.logging.GeneralLogs;
import com.csci5408.centdb.services.queryimplementation.*;

import com.csci5408.centdb.services.queryimplementation.CreateDatabase;
import com.csci5408.centdb.services.queryimplementation.CreateTable;
import com.csci5408.centdb.services.queryimplementation.DeleteQuery;
import com.csci5408.centdb.services.queryimplementation.InsertQuery;
import com.csci5408.centdb.services.queryimplementation.SelectQuery;
import com.csci5408.centdb.services.queryimplementation.UpdateQuery;
import com.csci5408.centdb.services.queryimplementation.UseDatabase;
import com.csci5408.centdb.services.transactions.Transactions;

public class CheckTypeOfQuery {
	String databaseName;

	public void checkTypeOfQuery(String query) throws Exception {
		GeneralLogs generalLogs = new GeneralLogs();
		String querySplitArray[] = query.split(" ");
		if (querySplitArray[0].equalsIgnoreCase("create") && querySplitArray[1].equalsIgnoreCase("database")) {
			generalLogs.createGeneralLogs(UserService.getUserName(), "Success",
					"User entered create database query :" + query);
			CreateDatabase createDatabase = new CreateDatabase();
			createDatabase.createDb(query);
		} else if (UseDatabase.isDatabaseSet() || querySplitArray[0].equalsIgnoreCase("use")) {
			if (querySplitArray[0].equalsIgnoreCase("update")) {
				generalLogs.createGeneralLogs(UserService.getUserName(), "Success",
						"User entered an update query :" + query);
				UpdateQuery updateQuery = new UpdateQuery();
				updateQuery.updateQuery(query, "resources\\Databases\\" + UseDatabase.getDatabaseName(), true);
			} else if (querySplitArray[0].equalsIgnoreCase("delete") && querySplitArray[1].equalsIgnoreCase("from")) {
				DeleteQuery deleteQuery = new DeleteQuery();
				generalLogs.createGeneralLogs(UserService.getUserName(), "Success",
						"User entered a delete query :" + query);
				deleteQuery.deleteQuery(query, "resources\\Databases\\" + UseDatabase.getDatabaseName(), true);
			} else if (querySplitArray[0].equalsIgnoreCase("use")) {
				generalLogs.createGeneralLogs(UserService.getUserName(), "Success",
						"User entered use database query :" + query);
				UseDatabase useDatabase = new UseDatabase();
				useDatabase.use(query);
			} else if (querySplitArray[0].equalsIgnoreCase("create") && querySplitArray[1].equalsIgnoreCase("table")) {
				generalLogs.createGeneralLogs(UserService.getUserName(), "Success",
						"User entered create table query :" + query);
				CreateTable createTable = new CreateTable();
				createTable.createTable(query);
			} else if (querySplitArray[0].equalsIgnoreCase("insert") && querySplitArray[1].equalsIgnoreCase("into")) {
				generalLogs.createGeneralLogs(UserService.getUserName(), "Success",
						"User entered an insert table query :" + query);
				InsertQuery.insert(query, "resources\\Databases\\" + UseDatabase.getDatabaseName(),
						UseDatabase.getDatabaseName(), true);
			} else if (querySplitArray[0].equalsIgnoreCase("drop")) {
				generalLogs.createGeneralLogs(UserService.getUserName(), "Success",
						"User entered a drop query :" + query);
				DropTable.dropTable(query);
			} else if (querySplitArray[0].equalsIgnoreCase("select")) {
				generalLogs.createGeneralLogs(UserService.getUserName(), "Success",
						"User entered select table query :" + query);
				SelectQuery.select(query, "resources\\Databases\\" + UseDatabase.getDatabaseName());
			} else if (querySplitArray[0].equalsIgnoreCase("begin") || (querySplitArray[0].equalsIgnoreCase("start"))) {
				generalLogs.createGeneralLogs(UserService.getUserName(), "Success",
						"User started the transaction :" + query);
				Transactions transaction = new Transactions();
				transaction.processTransaction(query, "resources\\Databases\\" + UseDatabase.getDatabaseName());
			}
		} else {
			System.out.println("Please perform use database query first");
			generalLogs.createGeneralLogs(UserService.getUserName(), "Failed", "User didn't select use database first");
			return;
		}
	}
}
