package com.ashcollege.entities;

import com.github.javafaker.Faker;

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
        int protection = faker.random().nextInt(20, 100);
        int attack = faker.random().nextInt(20, 100);
        this.skillLevel = (int) ((protection * 0.5) + (attack * 0.5));
    }

    public void reduceSkillLevel (int value) {
        this.skillLevel -= value;
        if (this.skillLevel < 20) {
            this.skillLevel = 20;
        }
    }

    public void increaseSkillLevel (int value) {
        this.skillLevel += value;
        if (this.skillLevel > 100) {
            this.skillLevel = 100;
        }
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(Integer skillLevel) {
        this.skillLevel = skillLevel;
    }


    //    public SkillLevel getSkillLevel() {
//        return skillLevel;
//    }

//    public void setSkillLevel(SkillLevel skillLevel) {
//        this.skillLevel = skillLevel;
//    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", skillLevel=" + skillLevel +
                '}';
    }
}
