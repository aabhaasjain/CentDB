package com.csci5408.centdb.model;

import java.util.List;

public class User {
    private String userId;
    private String password;
    private List<String> securityAnswers;

    public User() {
    }

    public User(String userId, String password, List<String> securityAnswers) {
        this.userId = userId;
        this.password = password;
        this.securityAnswers = securityAnswers;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getSecurityAnswers() {
        return securityAnswers;
    }

    public void setSecurityAnswers(List<String> securityAnswers) {
        this.securityAnswers = securityAnswers;
    }
}
