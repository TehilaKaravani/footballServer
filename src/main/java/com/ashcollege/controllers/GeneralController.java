package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.*;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.UserResponse;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {


    @Autowired
    private Persist persist;


    private final List<SseEmitter> clients = new ArrayList<>();

    @PostConstruct
    public void init() {
        persist.delete("Gamble");
        persist.delete("Match");
        persist.delete("Team");

        persist.createTeams();
        List<Team> teams = persist.loadTeamList();
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
                    Thread.sleep(30000);
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
                for (SseEmitter emitter : clients) {
                    try {
                        emitter.send(persist.loadMatchList());
                        persist.addGoals();
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
        Integer errorCode = null;
        if (password.equals(password2)) {
            //check strong password--------------------------------
            return persist.signUp(username, email, password);

        } else {
            errorCode = ERROR_SIGN_UP_PASSWORDS_DONT_MATCH;
        }
        return new BasicResponse(false, errorCode);
    }


    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse login(String email, String password) {
        return persist.login(email, password);
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

    @RequestMapping(value = "get-user-by-secret")
    public BasicResponse getUserBySecret(String secret) {
        BasicResponse basicResponse = null;
        boolean success = false;
        Integer errorCode = null;
        User user = null;
        user = persist.getUserBySecret(secret);
        if (user != null) {
            basicResponse = new UserResponse(true, errorCode, user);
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

    @RequestMapping(value = "change-username-or-email")
    public UserResponse changeUsernameOrEmail(String category, String toChange, String secret) {
        return persist.changeUsernameOrEmail(category, toChange, secret);
    }

    @RequestMapping(value = "change-password")
    public UserResponse changePassword(String toChange, String currentPassword, String secret) {
        return persist.changePassword(toChange, currentPassword, secret);
    }

    @RequestMapping(value = "add-gamble")
    public BasicResponse addGamble(String secret, int matchId, int teamNum, int sum) {
        return persist.addGamble(secret, matchId, teamNum, sum);
    }

    @RequestMapping(value = "get-gamble")
    public List<Gamble> getGamble() {
        return persist.loadGambleList();
    }

    @RequestMapping(value = "get-user-gambling")
    public List<Gamble> getUserGambling(String secret) {
        return persist.getUserGambling(secret);
    }

}
