package com.csci5408.centdb.persistence;

import com.csci5408.centdb.model.User;

import java.io.IOException;
import java.util.List;

public interface UserDao {
    public List<User> getUsers() throws IOException;
    public User getUserDetails(String userId) throws IOException;
    public void addUser(User user) throws IOException;
    public String userValidation(User user) throws IOException;
}
