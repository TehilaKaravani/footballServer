package com.ashcollege;

import com.ashcollege.entities.Team;

public class Score {
    private Team team;
    private int score;

    public Score(Team team) {
        this.team = team;
        this.score = 0;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addPoints (int points) {
        this.score +=points;
    }
}
