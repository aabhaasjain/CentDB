package com.csci5408.centdb.services;

import static com.csci5408.centdb.model.util.Constants.SECURITY_QUESTIONS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import com.csci5408.centdb.logging.GeneralLogs;
import com.csci5408.centdb.model.User;
import com.csci5408.centdb.persistence.impl.FileSystemUserDao;
import com.csci5408.centdb.services.analytics.Analytics;
import com.csci5408.centdb.services.exportstructurevalue.ExportService;
import com.csci5408.centdb.services.reverseengineering.ReverseEngineering;

public class UserService {
	FileSystemUserDao userDao;
	GeneralLogs generalLogs = new GeneralLogs();
	public static Scanner sc = new Scanner(System.in);
	private static String name;

	public static String getUserName() {
		return name;
	}

	public void setUserName(String userName) {
		name = userName;
	}

	public UserService() throws IOException {
		userDao = new FileSystemUserDao();
	}

	public void userAccessControl() throws Exception {
		List<String> securityAnswers = new ArrayList<>();
		ACCESS: do {
			System.out.println("Please enter an option to continue\n1. Register\n2. Login\n3. Exit");
			int choice = sc.nextInt();
			sc.nextLine();

			switch (choice) {

			case 1:
				try {
					User user = new User();
					System.out.println("Enter a userId");
					final String userId = sc.nextLine();
					User userDetails = userDao.getUserDetails(userId);
					if (!Objects.isNull(userDetails)) {
						System.out.println("User Id already exists. Please enter another User ID or Login.");
						generalLogs.createGeneralLogs(userId,"Failed","Registration attempted, User Id already exists");
						continue ACCESS;
					}
					user.setUserId(userId);
					System.out.println("Enter password (min length : 8 characters)");
					String password = sc.nextLine();
					while (!isPasswordValid(password)) {
						System.out.println("Please enter a password with minimum 8 characters");
						password = sc.nextLine();
					}
					user.setPassword(password);

					System.out.println("Please answer the following security questions:");

					for (String securityQuestion : SECURITY_QUESTIONS) {
						System.out.println(securityQuestion);
						String securityAnswer = sc.nextLine();
						securityAnswers.add(securityAnswer);
					}
					user.setSecurityAnswers(securityAnswers);
					userDao.addUser(user);
					System.out.println("User registered successfully!");
					generalLogs.createGeneralLogs(userId,"Success","User registered successfully");
				} finally {
				}
				break;

			case 2:
				User registerUser = new User();
				securityAnswers = new ArrayList<>();
				System.out.println("Enter User Id: ");
				String userInput = sc.nextLine();
				registerUser.setUserId(userInput);
				System.out.println("Enter your password: ");
				userInput = sc.nextLine();
				registerUser.setPassword(userInput);
				System.out.println("");
				for (String securityQuestion : SECURITY_QUESTIONS) {
					System.out.println(securityQuestion);
					userInput = sc.nextLine();
					securityAnswers.add(userInput);
				}
				registerUser.setSecurityAnswers(securityAnswers);
				String errorMessage = userDao.userValidation(registerUser);
				if (!errorMessage.equals("")) {
					System.out.println(errorMessage);
					generalLogs.createGeneralLogs(UserService.getUserName(),"Failed",errorMessage);
					continue ACCESS;
				}
				setUserName(registerUser.getUserId());
				System.out.println("Successfully logged in!");
				generalLogs.createGeneralLogs(UserService.getUserName(),"Success","Successfully logged in!");
				userOperations();
				break;

			case 3:
				System.out.println("Thank you!");
				generalLogs.createGeneralLogs(UserService.getUserName(),"Success","User logged out!");
				System.exit(0);
			}
		} while (true);
	}

	private static boolean isPasswordValid(String password) {
		return password.length() > 8;
	}

	private static void userOperations() throws Exception {
		GeneralLogs generalLogs = new GeneralLogs();
		CheckTypeOfQuery checkTypeOfQuery = new CheckTypeOfQuery();
		boolean validateQuery = false;
		do {
			System.out.println(
					"\nPlease select an operation to perform\n1. Write Queries\r\n2. Export\r\n3. Data Model\r\n4. Analytics\r\n5. Logout");
			int ch = sc.nextInt();
			switch (ch) {
			case 1:
				Scanner scanner = new Scanner(System.in);
				System.out.println("inside case1");
				String query;
				System.out.println("Enter valid query");
				query = scanner.nextLine();
				QueryValidator queryValidator = new QueryValidator();
				validateQuery = queryValidator.validateQuery(query);
				System.out.println("Query Validation " + validateQuery);
				if (validateQuery) {
					checkTypeOfQuery.checkTypeOfQuery(query);
					generalLogs.createGeneralLogs(UserService.getUserName(),"Success","User entered a valid query :"+query);
				}
				else{
					generalLogs.createGeneralLogs(UserService.getUserName(),"Failed","User entered an invalid query :"+query);
				}
				break;
			case 2:
				ExportService exportService = new ExportService();
				exportService.createExport();
				generalLogs.createGeneralLogs(UserService.getUserName(),"Success","Export Service completed successfully!");
				break;
			case 3:
				System.out.println("Here are the databases... ");
				BufferedReader br = new BufferedReader(new FileReader("resources\\databases.txt"));
				String line = null;
				int count = 0;
				Map<Integer, String> databaseMap = new HashMap<>();
				while ((line = br.readLine()) != null) {
						count += 1;
						databaseMap.put(count, line);
				}
				for (Integer i : databaseMap.keySet()) {
					System.out.println(i + "." + databaseMap.get(i));
				}
				System.out.println("Please choose the database to get the data model(reverse engineer)");
				int choice = sc.nextInt();
				generalLogs.createGeneralLogs(UserService.getUserName(),"Success","User selected "+choice+" database for Data Model");
				ReverseEngineering.reverseEngineer(databaseMap.get(choice));
				break;
			case 4:
				Analytics analytics = new Analytics();
				analytics.countQueries("resources\\Databases");
				generalLogs.createGeneralLogs(UserService.getUserName(),"Success","Analytics completed successfully!");
				break;
			case 5:
				System.out.println("Logging out. Thank you!");
				System.exit(0);
				break;
			}
		} while (true);
	}
}
