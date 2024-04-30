package com.ashcollege.entities;

import com.github.javafaker.Faker;

public class SkillLevel {
    private Integer protection;

    private Integer attack;

    private Integer morale;

    private Integer luck;

    Faker faker = new Faker();

    public SkillLevel(){
        this.protection = faker.random().nextInt(0, 100);
        this.attack = faker.random().nextInt(0, 100);
//        updateMoraleAndLuck();
    }

    public Integer getSkillLevel () {
        Integer skillLevel;
        skillLevel = (int) ((this.protection * 0.5) + (this.attack * 0.5));
//        skillLevel = (int) (skillLevel * 0.9 + this.morale *0.05 + this.luck * 0.05);
        return skillLevel;
    }

    public void updateMoraleAndLuck () {
        this.morale = faker.random().nextInt(0, 100);
        this.luck = faker.random().nextInt(-50, 50);
    }

    public Integer getProtection() {
        return protection;
    }

    public void setProtection(Integer protection) {
        this.protection = protection;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public Integer getMorale() {
        return morale;
    }

    public void setMorale(Integer morale) {
        this.morale = morale;
    }

    public Integer getLuck() {
        return luck;
    }

    public void setLuck(Integer luck) {
        this.luck = luck;
    }
}
