package com.ashcollege.entities;

import com.github.javafaker.Faker;
import static com.ashcollege.utils.Constants.*;

public class Team {
    private int id;
    private String name;
    private int skillLevel;

    public Team() {
        calSkillLevel();
    }

    public Team(String name) {
        this.name = name;
        calSkillLevel();
    }

    public Team(int id) {
        this.id = id;
        calSkillLevel();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void calSkillLevel (){
        Faker faker = new Faker();
        int protection = faker.random().nextInt(MIN_LEVEL, MAX_LEVEL);
        int attack = faker.random().nextInt(MIN_LEVEL, MAX_LEVEL);
        this.skillLevel = (int) ((protection * PROTECTION_RATIO) + (attack * ATTACK_RATIO));
    }

    public void reduceSkillLevel (int value) {
        this.skillLevel -= value;
        if (this.skillLevel < MIN_LEVEL) {
            this.skillLevel = MIN_LEVEL;
        }
    }

    public void increaseSkillLevel (int value) {
        this.skillLevel += value;
        if (this.skillLevel > MAX_LEVEL) {
            this.skillLevel = MAX_LEVEL;
        }
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(Integer skillLevel) {
        this.skillLevel = skillLevel;
    }
}
