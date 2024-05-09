package com.ashcollege.entities;

public class Gamble {
    private int id;
    private User user;
    private Match match;
    private int team;
    private int sum;
    private Boolean isCorrect;
//    private double ratio;

    public Gamble() {

    }

    public Gamble(User user, Match match, int team, int sum) {
        this.user = user;
        this.match = match;
        this.team = team;
        this.sum = sum;
        this.isCorrect = null;
//        switch (team){
//            case 0:
//                ratio = 100 / (match.getTeam1().getSkillLevel() + match.getTeam2().getSkillLevel());
//                break;
//            case 1:
//                ratio = 100 / match.getTeam1().getSkillLevel();
//                break;
//            case 2:
//                ratio = 100 / match.getTeam2().getSkillLevel();
//                break;
//        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }

    @Override
    public String toString() {
        return "Gamble{" +
                "id=" + id +
                ", user=" + user +
                ", match=" + match +
                ", team=" + team +
                ", sum=" + sum +
                ", isCorrect=" + isCorrect +
                '}';
    }
}
