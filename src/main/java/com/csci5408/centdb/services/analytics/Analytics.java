package com.csci5408.centdb.services.analytics;

import java.io.*;
import java.util.*;

public class Analytics {
    File[] files;
    File databaseFolder;
    String logFileName;
    String databaseName;
    int countQueries = 0;
    String line;
    ArrayList<String> columns = new ArrayList<>();
    ArrayList<String> data = new ArrayList<>();
    String[] columnSplit = null;
    int count = 0;
    HashMap<String, Integer> countQueriesByTable = new HashMap<>();
    HashMap<String, Integer> countQueriesByUser = new HashMap<>();
    String tableName;
    String queryType;
    String queryStatus;
    String userName;
    BufferedReader br;

    public void countQueries(String masterFolder) throws IOException {
        File dir = new File(masterFolder);
        files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                databaseFolder = file;
                databaseName = file.toString().split("\\\\")[file.toString().split("\\\\").length - 1];
                logFileName = databaseName + "_QueryLogs.txt";
                br = new BufferedReader(new FileReader(file + "\\" + logFileName));

                StringTokenizer st1 = new StringTokenizer(br.readLine(), "\t");
                while (st1.hasMoreTokens()) {
                    columns.add(st1.nextToken());
                    count++;
                }
                if (columns.size() > 0) {
                    for (String column : columns) {
                        columnSplit = column.split("\\|");
                    }
                    while ((line = br.readLine()) != null) {
                        StringTokenizer st2 = new StringTokenizer(line, "\t");
                        for (int i = 0; i < count; i++) {
                            if (st2.hasMoreTokens()) {
                                data.add(st2.nextToken());
                            }
                        }
                    }
					for (String datum : data) {
						userName = datum.split("\\|")[0];
						queryType = datum.split("\\|")[2];
						queryStatus = datum.split("\\|")[1];
						tableName = datum.split("\\|")[4];
						if (queryType.equalsIgnoreCase("update") && queryStatus.equalsIgnoreCase("success")) {
							if (countQueriesByTable.containsKey(tableName)) {
								countQueriesByTable.put(tableName, countQueriesByTable.get(tableName) + 1);
							} else {
								countQueriesByTable.put(tableName, 1);
							}
						}

						if (countQueriesByUser.containsKey(userName)) {
							countQueriesByUser.put(userName, countQueriesByUser.get(userName) + 1);
						} else {
							countQueriesByUser.put(userName, 1);
						}

					}
					countQueriesByUser.forEach((userKey, queryCount) -> {
						System.out.println("User "+userKey+" submitted "+ queryCount + " queries on "+databaseName);
					});

                }
                countQueriesByTable.forEach((tableKey, queryCount) -> {
                    System.out.println("Total " + queryCount + " Update operations are performed on " + tableKey);
                });
                br.close();
            }
            data.clear();
            countQueriesByTable.clear();
			countQueriesByUser.clear();
            countQueries = 0;
            System.out.println("\n");
        }
    }
}
