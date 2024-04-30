package com.ashcollege.entities;

public class Team {
    private int id;
    private String name;

    private SkillLevel skillLevel;

    public Team() {
    }
    public Team(String name) {
        this.name = name;
    }

//    public Team(String name,SkillLevel skillLevel) {
//        this.name = name;
//        this.skillLevel = skillLevel;
//    }

    public Team(int id) {
        this.id = id;
        this.skillLevel = new SkillLevel();
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

    public SkillLevel getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", skillLevel=" + skillLevel +
                '}';
    }
}
