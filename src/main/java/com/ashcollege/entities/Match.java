package com.ashcollege.entities;

public class Match {
    private int id;
    private Team team1;
    private Team team2;
    private int goalsT1;
    private int goalsT2;
    private Boolean isLive;

    public Match() {

    }
    public Match(Team team1, Team team2) {
        this.team1 = team1;
        this.team2 = team2;
        this.goalsT1 = 0;
        this.goalsT2 = 0;
        this.isLive = null;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Team getTeam1() {
        return team1;
    }

    public void setTeam1(Team team1) {
        this.team1 = team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public void setTeam2(Team team2) {
        this.team2 = team2;
    }

    public int getGoalsT1() {
        return goalsT1;
    }

    public void setGoalsT1(int goalsT1) {
        this.goalsT1 = goalsT1;
    }

    public int getGoalsT2() {
        return goalsT2;
    }

    public void setGoalsT2(int goalsT2) {
        this.goalsT2 = goalsT2;
    }

    public Boolean getIsLive() {
        return isLive;
    }

    public void setIsLive(Boolean live) {
        isLive = live;
    }

    public Team winner () {
        Team winner = null;
        if (this.goalsT1 > this.goalsT2) {
            winner = this.team1;
        }else if (this.goalsT1 < this.goalsT2){
            winner = this.team2;
        }
        return winner;
    }

    public void addGoalT1() {
        if (this.goalsT1 < 9) {
            this.goalsT1++;
        }
    }
    public void addGoalT2() {
        if (this.goalsT2 < 9) {
            this.goalsT2++;
        }
    }
}
