package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.Score;
import com.ashcollege.entities.*;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {


    @Autowired
    private Persist persist;


    private final List<SseEmitter> clients = new ArrayList<>();

    @PostConstruct
    public void init() {
        persist.createTeams();
        final ArrayList<ArrayList<Match>> league = persist.getLeague();

//        for (ArrayList<Match> matches : league) {
//            new Thread(() -> {
//                while (true) {
//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    for (SseEmitter emitter : clients) {
//                        try {
//                            emitter.send(matches);
//                        } catch (Exception e) {
////                        System.out.println("Client leave");
////                        clients.remove(eventClients);
//                        }
//                    }
//                }
//            }).start();
//        }

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (SseEmitter emitter : clients) {
                    try {
                        emitter.send(league.get(0));
                    } catch (Exception e) {
//                        System.out.println("Client leave");
//                        clients.remove(eventClients);
                    }
                }
            }
        }).start();
    }

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public Object hello() {
        return "Hello From Server";
    }

    @RequestMapping(value = "/sign-up", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse signUp(String username, String email, String password, String password2) {
        BasicResponse basicResponse = null;
        Integer errorCode = null;
        if (username != null && username.length() > 0) {
            if (password != null && password.length() > 0) {
                if (password.equals(password2)) {
                    return persist.signUp(username, email, password);
                } else {
                    errorCode = ERROR_SIGN_UP_PASSWORDS_DONT_MATCH;
                }
            } else {
                errorCode = ERROR_SIGN_UP_NO_PASSWORD;
            }
        } else {
            errorCode = ERROR_SIGN_UP_NO_USERNAME;
        }
        return new BasicResponse(false, errorCode);
    }


    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse login(String email, String password) {
        BasicResponse basicResponse = null;
        boolean success = false;
        Integer errorCode = null;
        User user = null;

//        check email

        if (email != null && email.length() > 0) {
            if (password != null && password.length() > 0) {
                user = persist.login(email, password);
                if (user != null) {
                    basicResponse = new LoginResponse(true, errorCode, user);
                } else {
                    errorCode = ERROR_LOGIN_WRONG_CREDS;
                }
            } else {
                errorCode = ERROR_SIGN_UP_NO_PASSWORD;
            }
        } else {
            errorCode = ERROR_SIGN_UP_NO_USERNAME;
        }
        if (user == null) {
            basicResponse = new BasicResponse(success, errorCode);
        }
        return basicResponse;
    }


    @RequestMapping(value = "get-users")
    public List<User> getUsers() {
        return persist.loadUserList();
    }

    @RequestMapping(value = "get-teams")
    public List<Team> getTeams() {
        return persist.loadTeamList();
    }

    @RequestMapping(value = "get-matches")
    public List<Match> getMatches() {
        List<Match> matchList = persist.loadMatchList();
        return matchList;
    }

    @RequestMapping(value = "get-score")
    public List<Score> getScore() {
        List<Match> matchList = persist.loadMatchList();
        List<Team> teamList = persist.loadTeamList();
        List<Score> scoreList = new ArrayList<>();

        for (Team team : teamList) {
            scoreList.add(team.getId() - 1, new Score(team));
        }

        for (Match match : matchList) {
            if (match.winner() == null) {
                int indexT1 = match.getTeam1().getId() - 1;
                int indexT2 = match.getTeam2().getId() - 1;
                scoreList.get(indexT1).addPoints(1);
                scoreList.get(indexT2).addPoints(1);

            } else {
                int winnerIndex = match.winner().getId() - 1;
                scoreList.get(winnerIndex).addPoints(3);
            }
        }
        return scoreList;
    }

    @RequestMapping(value = "get-user-by-secret")
    public BasicResponse getUserBySecret(String secret) {
        BasicResponse basicResponse = null;
        boolean success = false;
        Integer errorCode = null;
        User user = null;
        user = persist.getUserBySecret(secret);
        if (user != null) {
            basicResponse = new LoginResponse(true, errorCode, user);
        } else {
            errorCode = ERROR_LOGIN_WRONG_CREDS;
            basicResponse = new BasicResponse(success, errorCode);
        }
        return basicResponse;
    }

    @RequestMapping(value = "start-streaming", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter CreateStreamingSession() {
        try {
            SseEmitter sseEmitter = new SseEmitter((long) (10 * 60 * 1000));
            clients.add(sseEmitter);
            return sseEmitter;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    @RequestMapping(value = "create-teams")
//    public String createTeams() {
//        persist.createTeams();
//        return "ok";
//    }

    @RequestMapping(value = "change-profile")
    public User changeProfile(String category, String toChange, String secret) {
        return persist.changeProfile(category,toChange,secret);
    }

    @RequestMapping(value = "add-match")
    public void addMatch() {
       persist.addMatch(1,4);
    }

    @RequestMapping(value = "get-league")
    public ArrayList<ArrayList<Match>> getLeague() {
        return persist.getLeague();
    }
}
