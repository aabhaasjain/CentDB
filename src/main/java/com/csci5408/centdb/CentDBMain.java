package com.csci5408.centdb;

import com.csci5408.centdb.services.UserService;

public class CentDBMain {

	public static void main(String[] args) throws Exception {

		UserService userInterface = new UserService();
		userInterface.userAccessControl();
	}
}
