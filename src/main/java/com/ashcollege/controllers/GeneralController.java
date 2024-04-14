package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.Score;
import com.ashcollege.entities.*;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {


    @Autowired
    private Persist persist;


//    private List<EventClients> clients;
//
//    @PostConstruct
//    public void init() {
//        new Thread(() -> {
//            try {
//                Thread.sleep(1000);
//                for (EventClients eventClients : clients) {
//                    eventClients.getEmitter().send(new Date());
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }


    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public Object hello() {
        return "Hello From Server";
    }

    @RequestMapping(value = "/sign-up", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse signUp(String username, String password, String password2) {
        BasicResponse basicResponse = null;
        Integer errorCode = null;
        boolean success = false;
        if (username != null && username.length() > 0) {
            if (password != null && password.length() > 0) {
                if (password.equals(password2)) {
                    return persist.signUp(username, password);
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
    public BasicResponse login(String username, String password) {
        BasicResponse basicResponse = null;
        boolean success = false;
        Integer errorCode = null;
        User user = null;
        if (username != null && username.length() > 0) {
            if (password != null && password.length() > 0) {
                user = persist.login(username, password);
                if (user != null) {
                    basicResponse = new LoginResponse(true, errorCode, user.getId(), user.getSecret());
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
        return persist.loadUserList(User.class);
    }

    @RequestMapping(value = "get-teams")
    public List<Team> getTeams() {
        return persist.loadTeamList(Team.class);
    }

    @RequestMapping(value = "get-matches")
    public List<Match> getMatches() {
        List<Match> matchList = persist.loadMatchList(Match.class);
        return matchList;
    }

    @RequestMapping(value = "get-score")
    public List<Score> getScore() {
        List<Match> matchList = persist.loadMatchList(Match.class);
        List<Team> teamList = persist.loadTeamList(Team.class);
        List<Score> scoreList = new ArrayList<>();

        for (Team team : teamList) {
           scoreList.add(team.getId() - 1,new Score(team));
        }

        for (Match match : matchList) {
            if (match.winner() == null) {
                int indexT1 = match.getTeam1().getId() - 1;
                int indexT2 = match.getTeam2().getId() - 1;
                scoreList.get(indexT1).addPoints(1);
                scoreList.get(indexT2).addPoints(1);

            }else {
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
            basicResponse = new LoginResponse(true, errorCode, user.getId(), user.getSecret());
        } else {
            errorCode = ERROR_LOGIN_WRONG_CREDS;
            basicResponse = new BasicResponse(success, errorCode);
        }
        return basicResponse;
    }

//    @RequestMapping(value = "start-streaming", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter CreateStreamingSession(String secret) {
//        SseEmitter sseEmitter = new SseEmitter((long) (10 * 60 * 1000));
//        try {
//            clients.add(new EventClients(secret, sseEmitter));
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//        return sseEmitter;
//    }

    @RequestMapping(value = "create-teams")
    public String createTeams() {
        persist.createTeams();
        return "ok";
    }
}
