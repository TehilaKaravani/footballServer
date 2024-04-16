package com.ashcollege.entities;

import com.ashcollege.Persist;

import java.util.ArrayList;
import java.util.List;

public class Cycle {
    private List<Match> games;


    public Cycle() {
//        Persist persist;
//        persist.loadTeamList(Team.class);
        games = new ArrayList<>();
        Match match1 = new Match(1,new Team(1),new Team(2),0,0);
        Match match2 = new Match(2,new Team(3),new Team(4),0,0);
        Match match3 = new Match(3,new Team(5),new Team(6),0,0);
        Match match4 = new Match(4,new Team(7),new Team(8),0,0);
        games.add(match1);
        games.add(match2);
        games.add(match3);
        games.add(match4);
    }

    public List<Match> getGames() {
        return games;
    }

    public void setGames(List<Match> games) {
        this.games = games;
    }
}
