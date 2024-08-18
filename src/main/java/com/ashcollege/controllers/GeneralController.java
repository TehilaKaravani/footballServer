package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.*;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ashcollege.utils.Constants.*;
import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {

    @Autowired
    private Persist persist;

    private final List<SseEmitter> clients = new ArrayList<>();
    private int remainingTime = CYCLE_TIME;

    @PostConstruct
    public void init() {
        persist.delete("Gamble");
        persist.delete("Match");
        persist.delete("Team");

        persist.createTeams();
        createSeason();

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    remainingTime--;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                persist.addMatchGoals();

                Map<String, Object> matchData = new HashMap<>();
                matchData.put("match", persist.loadMatchList());
                matchData.put("remainingTime", remainingTime);

                for (SseEmitter emitter : clients) {
                    try {
                        emitter.send(matchData);
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }


    private void createSeason() {
        System.out.println("****createSeason****");
        final ArrayList<ArrayList<Match>> leagueMatches = persist.getLeagueGames();

        for (ArrayList<Match> leagueMatch : leagueMatches) {
            for (Match match : leagueMatch) {
                persist.save(match);
            }
        }

        new Thread(() -> {
            for (int i = 0; i < leagueMatches.size() + 1; i++) {
                remainingTime = CYCLE_TIME - 1;
                System.out.println("-------------------switch----------------");
                List<Match> liveMatches = persist.loadLiveMatchList();

                for (Match liveMatch : liveMatches) {
                    persist.checkGambling(liveMatch);
                    persist.setSkills(liveMatch);

                    liveMatch.setIsLive(false);
                    persist.save(liveMatch);
                }
                if (i < leagueMatches.size()) {
                    for (int j = 0; j < leagueMatches.get(i).size(); j++) {
                        leagueMatches.get(i).get(j).setIsLive(true);
                        persist.save(leagueMatches.get(i).get(j));
                    }
                }
                try {
                    Thread.sleep(CYCLE_TIME * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
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
    public SseEmitter createStreamingSession() {
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

    @RequestMapping(value = "start-new-season", method = {RequestMethod.GET, RequestMethod.POST})
    public void startNewSeason() {
        createSeason();
    }

}
