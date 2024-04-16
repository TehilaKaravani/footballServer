package com.ashcollege.responses;

import com.ashcollege.entities.User;

public class LoginResponse extends BasicResponse {
    private User user;

    public LoginResponse(boolean success, Integer errorCode, User user) {
        super(success, errorCode);
        this.user = user;
    }

    public LoginResponse(boolean success, Integer errorCode) {
        super(success, errorCode);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
