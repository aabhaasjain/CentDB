package com.csci5408.centdb.services.reverseengineering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReverseEngineering {

	static final String delimiter = "\n";

	public static void reverseEngineer(String database) throws IOException {

		List<Map<String, String>> foreignKeys = new ArrayList<>();
		String fileName = "resources\\Databases\\" + database + "\\" + database + "-meta.txt";
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			BufferedWriter bw = new BufferedWriter(new FileWriter("resources\\" + database + "-structure.txt"));
			String line;
			int j = 0;
			String table = "";
			String key = "";
			String referencedTable = "";
			String referencedKey = "";
			READFILE: while ((line = br.readLine()) != null) {
				int i = 0;
				String[] s = line.split("\\|");
				if (s[0].equals("Table")) {
					if (j != 0) {
						bw.append("\n");
					}
					table = s[1];
					bw.append("Table Name : " + table + "\n");
					j++;
				} else {
					int length = s.length;
					String column = "";
					while (i < length) {
						if (s[i].equalsIgnoreCase("foreign_key")) {
							key = s[0];
							referencedTable = s[i + 1];
							referencedKey = s[i + 2];
							column += "Foreign_Key" + " ------ " + "Table: " + referencedTable + " column: "
									+ referencedKey;
							bw.append(column + "\n");
							Map<String, String> keyMap = new HashMap<>();
							keyMap.put("Table", table);
							keyMap.put("Key", key);
							keyMap.put("Referenced Table", referencedTable);
							keyMap.put("Referenced Key", referencedKey);
							foreignKeys.add(keyMap);
							continue READFILE;
						} else {
							column += s[i];
						}
						if (i != (length - 1)) {
							column += " | ";
						}
						i++;
					}
					bw.append(column + "\n");
				}
			}
			List<String> cardinality = findCardinality(foreignKeys, database);
			for (int i = 0; i < foreignKeys.size(); i++) {
				String statement = "The cardinality between " + foreignKeys.get(0).get("Referenced Table") + " and "
						+ foreignKeys.get(0).get("Table") + " is " + cardinality.get(i);
				bw.append("\n\n" + statement);
			}
			bw.close();
		}
		printToConsole("resources\\" + database + "-structure.txt");

	}

	private static void printToConsole(String fileName) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		}

	}

	private static List<String> findCardinality(List<Map<String, String>> foreignKeys, String database)
			throws IOException {

		String cardinality = "";
		List<String> cardinalityList = new ArrayList<>();
		for (Map<String, String> data : foreignKeys) {
			String table = "resources\\Databases\\" + database + "\\" + data.get("Table") + ".txt";
			String id = data.get("Key");
			String referencedTable = "resources\\Databases\\" + database + "\\" + data.get("Referenced Table") + ".txt";
			String referencedId = data.get("Referenced Key");
			BufferedReader tableReader = new BufferedReader(new FileReader(table));
			BufferedReader referencedTableReader = new BufferedReader(new FileReader(referencedTable));
			String tableCoulmns = tableReader.readLine();
			String referencedTableColumns = referencedTableReader.readLine();
			int tableIdIndex = getIndex(tableCoulmns, id);
			int referencedTableIdIndex = getIndex(referencedTableColumns, referencedId);
			boolean tableCheck = checkForDuplicateValues(tableReader, tableIdIndex);
			boolean referencedTableCheck = checkForDuplicateValues(referencedTableReader, referencedTableIdIndex);
			if (referencedTableCheck) {
				if (tableCheck) {
					cardinality = "One-To-One";
				} else {
					cardinality = "One-To-Many";
				}
			} else {
				if (tableCheck) {
					cardinality = "Many-To-One";
				} else {
					cardinality = "Many-To-Many";
				}
			}
			cardinalityList.add(cardinality);
		}
		return cardinalityList;
	}

	private static boolean checkForDuplicateValues(BufferedReader reader, int index) throws IOException {

		String line = null;
		boolean result = true;
		Map<String, Integer> countMap = new HashMap<>();
		while ((line = reader.readLine()) != null) {
			String s[] = line.split("\\|");
			for (int i = 0; i < s.length; i++) {
				if (i == index) {
					if (countMap.containsKey(s[i].trim())) {
						countMap.put(s[i].trim(), countMap.get(s[i].trim()) + 1);
					} else {
						countMap.put(s[i].trim(), 1);
					}
				}
			}
		}
		for (String st : countMap.keySet()) {
			if (countMap.get(st) > 1)
				return false;
			else {
				continue;
			}
		}
		return result;
	}

	private static int getIndex(String columnData, String columnName) {
		int index = -1;
		String columns[] = columnData.split("\\|");
		for (int i = 0; i < columns.length; i++) {
			if (columns[i].equals(columnName)) {
				index = i;
			}
		}
		return index;
	}

}
