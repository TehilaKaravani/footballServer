package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.*;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.UserResponse;
import com.ashcollege.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.ashcollege.utils.Constants.*;
import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {

    @Autowired
    private Persist persist;

    private final List<SseEmitter> clients = new ArrayList<>();


    @PostConstruct
    public void init() {
//        persist.delete("Gamble");
//        persist.delete("Match");
//        persist.delete("Team");

        persist.createTeams();
        final ArrayList<ArrayList<Match>> league = persist.getLeagueGames();

        for (int i = 0; i < league.size(); i++) {
            for (int j = 0; j < league.get(i).size(); j++) {
                persist.save(league.get(i).get(j));
            }
        }

        new Thread(() -> {
            for (int i = 0; i < league.size() + 1; i++) {
                System.out.println("-------------------switch----------------");
                List<Match> liveMatches = persist.loadLiveMatchList();

                for (int j = 0; j < liveMatches.size(); j++) {

                    persist.checkGambling(liveMatches.get(j));
                    persist.setSkills(liveMatches.get(j));

                    liveMatches.get(j).setIsLive(false);
                    persist.save(liveMatches.get(j));
                }
                if (i < league.size()) {
                    for (int j = 0; j < league.get(i).size(); j++) {
                        league.get(i).get(j).setIsLive(true);
                        persist.save(league.get(i).get(j));
                    }
                }
                try {
                    Thread.sleep(CYCLE_TIME * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    persist.addGoals();
                    for (SseEmitter emitter : clients) {
                        emitter.send(persist.loadMatchList());
                    }
                } catch (Exception e) {
                }

            }
        }).start();
    }


    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public Object hello() {
        return "Hello From Server";
    }

    @RequestMapping(value = "/sign-up", method = {RequestMethod.POST})
    public BasicResponse signUp(String username, String email, String password, String password2) {
        int errorCode;
        if (password.equals(password2)) {
            return persist.signUp(username, email, password);
        } else {
            errorCode = ERROR_SIGN_UP_PASSWORDS_DONT_MATCH;
        }
        return new BasicResponse(false, errorCode);
    }


    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    public BasicResponse login(String email, String password) {
        return persist.login(email, password);
    }

    @RequestMapping(value = "get-user-by-secret", method = {RequestMethod.POST})
    public BasicResponse getUserBySecret(String secret) {
        BasicResponse basicResponse;
        boolean success = false;
        User user;
        user = persist.getUserBySecret(secret);
        if (user != null) {
            basicResponse = new UserResponse(true, null, user);
        } else {
            basicResponse = new BasicResponse(success, ERROR_LOGIN_WRONG_CREDS);
        }
        return basicResponse;
    }

    @RequestMapping(value = "start-streaming", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter CreateStreamingSession() {
        try {
            SseEmitter sseEmitter = new SseEmitter((long) (CONNECTION_ֹTIMEOUT));
            clients.add(sseEmitter);
            return sseEmitter;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "change-username-or-email", method = {RequestMethod.POST})
    public UserResponse changeUsernameOrEmail(String category, String toChange, String secret) {
        return persist.changeUsernameOrEmail(category, toChange, secret);
    }

    @RequestMapping(value = "change-password", method = {RequestMethod.POST})
    public UserResponse changePassword(String toChange, String currentPassword, String secret) {
        return persist.changePassword(toChange, currentPassword, secret);
    }

    @RequestMapping(value = "add-gamble", method = {RequestMethod.POST})
    public BasicResponse addGamble(String secret, int matchId, int teamNum, int sum, double ratio) {
        return persist.addGamble(secret, matchId, teamNum, sum, ratio);
    }

    @RequestMapping(value = "get-user-gambling", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Gamble> getUserGambling(String secret) {
        return persist.getUserGambling(secret);
    }

}
