package com.ashcollege.entities;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String secret;
    private int balance;

    public User(String username,String email, String password, String secret) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.secret = secret;
        this.balance = 1000;
    }

    public User(int id, String username, String password) {
        this(username, password);
        this.id = id;
        this.balance = 1000;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = 1000;
    }

    public User() {
        this.balance = 1000;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSameUsername (String username) {
        return this.username.equals(username);
    }

    public boolean isSameCreds (String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void addToBalance (int moneyToAdd) {
        this.balance = this.balance + moneyToAdd;
    }
    public void reduceBalance (int moneyToReduce) {
        if (moneyToReduce <= this.balance) {
            this.balance = this.balance - moneyToReduce;
        }
    }
}
