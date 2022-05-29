package com.csci5408.centdb.persistence.impl;

import com.csci5408.centdb.encryption.Aes;
import com.csci5408.centdb.model.User;
import com.csci5408.centdb.persistence.UserDao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.csci5408.centdb.model.util.Constants.DELIMITER;
import static com.csci5408.centdb.model.util.Constants.USER_PATH;

public class FileSystemUserDao implements UserDao {
	private FileWriter fileWriter;
	private FileReader fileReader;

	public FileSystemUserDao() throws IOException {
		fileReader = new FileReader("resources/User_Profile.txt");
		fileWriter = new FileWriter("resources/User_Profile.txt", true);
	}

	public List<User> getUsers() throws IOException {
		List<User> userList = new ArrayList<>();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(USER_PATH));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] str = line.split(Pattern.quote(DELIMITER));
			if (!Objects.isNull(str) && str.length > 1) {
				userList.add(new User(str[0], str[1], Arrays.asList(str[2], str[3])));
			}
		}
		return userList;
	}

	public User getUserDetails(String userId) throws IOException {
		for (User user : getUsers()) {
			if (userId.equals(user.getUserId())) {
				return user;
			}
		}
		return null;
	}

	public void addUser(User user) throws IOException {
		fileWriter = new FileWriter("resources/User_Profile.txt", true);
		fileWriter.write("\n");
		fileWriter.write(user.getUserId());
		fileWriter.write(DELIMITER + Aes.encrypt(user.getPassword(), "Test"));
		for (String securityAnswer : user.getSecurityAnswers()) {
			fileWriter.write(DELIMITER + securityAnswer);
		}
		fileWriter.close();
	}

	public String userValidation(User user) throws IOException {
		User userDetails = getUserDetails(user.getUserId());
		String Message = "";
		if (!Objects.isNull(userDetails)) {
			String decryptedPassword = Aes.decrypt(userDetails.getPassword(), "Test");
			if (!decryptedPassword.equals(user.getPassword())) {
				Message = String.format("\nUser Id and Password does not match. Please try again!");
			} else {

				if (!userDetails.getSecurityAnswers().get(0).equals(user.getSecurityAnswers().get(0))
						|| !userDetails.getSecurityAnswers().get(1).equals(user.getSecurityAnswers().get(1))) {
					Message = String.format("\nSecurity answers entered are incorrect. Please try again!");
				}
				System.out.println(user.getSecurityAnswers().get(0) + " first answer");
				System.out.println(user.getSecurityAnswers().get(1) + " second answer");
			}
		}
		else{
			Message = "User Id not found! Please try again.";
		}
		return Message;
	}

}
