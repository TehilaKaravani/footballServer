package com.ashcollege.entities;

import com.github.javafaker.Faker;
import static com.ashcollege.utils.Constants.*;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String secret;
    private double balance;

    public User(String username,String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        Faker faker = new Faker();
        this.secret = faker.random().hex();
        this.balance = INITIAL_BALANCE;
    }

    public User() {
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username != null && !username.isEmpty()) {
            this.username = username;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        if (isEmailCorrect(email)) {
            this.email = email;
        }
    }

    private boolean isEmailCorrect(String email) {
        return email.contains("@") && email.contains(".") && (email.lastIndexOf(".") - email.indexOf("@") > 1) && (email.indexOf("@") != 0);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addToBalance (double moneyToAdd) {
        this.balance = this.balance + moneyToAdd;
    }
    public void reduceBalance (double moneyToReduce) {
        if (moneyToReduce <= this.balance) {
            this.balance = this.balance - moneyToReduce;
        }
    }
}
