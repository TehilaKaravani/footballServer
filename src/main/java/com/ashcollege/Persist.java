package com.ashcollege;


import com.ashcollege.entities.Gamble;
import com.ashcollege.entities.Match;
import com.ashcollege.entities.Team;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.UserResponse;
import com.github.javafaker.Faker;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import static com.ashcollege.utils.Constants.*;
import static com.ashcollege.utils.Errors.*;


@Transactional
@Component
@SuppressWarnings("unchecked")
public class Persist {
    private final SessionFactory sessionFactory;

    @Autowired
    public Persist(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public void save(Object object) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(object);
    }

    public void delete(String tableName) {
        if (tableName != null && !tableName.isEmpty()) {
            sessionFactory.getCurrentSession().createQuery("DELETE FROM " + tableName).executeUpdate();
        }
    }
    private <T> List<T> loadTeamList() {
        return this.sessionFactory.getCurrentSession().createQuery("FROM Team").list();
    }

    public <T> List<T> loadMatchList() {
        return this.sessionFactory.getCurrentSession().createQuery("FROM Match").list();
    }

    public List<Match> loadLiveMatchList() {
        List<Match> matches = this.sessionFactory.getCurrentSession().createQuery("FROM Match").list();
        return matches.stream().filter((match) -> ((match.getIsLive() != null) && (match.getIsLive()))).toList();
    }

    public ArrayList<ArrayList<Match>> getLeagueGames() {
        List<Team> teams = loadTeamList();
        ArrayList<ArrayList<Match>> league = new ArrayList<>();
        int rounds = teams.size() - 1;
        int matchesPerRound = teams.size() / 2;
        for (int i = 0; i < rounds; i++) {
            ArrayList<Match> round = new ArrayList<>();
            for (int j = 0; j < matchesPerRound; j++) {
                Match match = new Match(teams.get(j), teams.get(teams.size() - 1 - j));
                round.add(match);
            }
            league.add(round);
            Collections.rotate(teams.subList(1, teams.size()), 1);
        }
        return league;
    }

    public BasicResponse login(String email, String password) {
        BasicResponse basicResponse;
        Integer errorCode = null;
        User user = null;

        if (email != null && !email.isEmpty()) {
            if (password != null && !password.isEmpty()) {
                user = (User) this.sessionFactory.getCurrentSession().createQuery(
                                "FROM User WHERE email = :email AND password = :password")
                        .setParameter("email", email)
                        .setParameter("password", password)
                        .setMaxResults(1)
                        .uniqueResult();

            } else {
                errorCode = ERROR_SIGN_UP_NO_PASSWORD;
            }
        } else {
            errorCode = ERROR_SIGN_UP_NO_EMAIL;
        }

        if (user == null) {
            if (errorCode == null) {
                errorCode = ERROR_LOGIN_WRONG_CREDS;
            }
            basicResponse = new BasicResponse(false, errorCode);
        } else {
            basicResponse = new UserResponse(true, null, user);
        }
        return basicResponse;
    }


    private boolean isUsernameAvailable(String username) {
        User user = null;
        if (username != null && !username.isEmpty()) {
            user = (User) this.sessionFactory.getCurrentSession().createQuery(
                            "FROM User WHERE username = :username")
                    .setParameter("username", username)
                    .uniqueResult();

        }
        return (user == null);
    }

    public BasicResponse signUp(String username, String email, String password) {
        Integer errorCode = null;
        if (username != null && !username.isEmpty()) {
            if (password != null && !password.isEmpty()) {
                if (email != null && !email.isEmpty()) {
                    if (isEmailCorrect(email)) {
                        if (isUsernameAvailable(username)) {
                            if (isPasswordStrong(password)) {
                                User user = new User(username, email, password);
                                save(user);
                                return new UserResponse(true, null, user);
                            }
                        } else {
                            errorCode = ERROR_SIGN_UP_USERNAME_TAKEN;
                        }
                    } else {
                        errorCode = ERROR_EMAIL_FORMAT;
                    }
                } else {
                    errorCode = ERROR_SIGN_UP_NO_EMAIL;
                }
            } else {
                errorCode = ERROR_SIGN_UP_NO_PASSWORD;
            }
        } else {
            errorCode = ERROR_SIGN_UP_NO_USERNAME;
        }
        return new BasicResponse(false, errorCode);
    }

    public User getUserBySecret(String secret) {
        User user = null;
        if (secret != null && !secret.isEmpty()) {
            user = (User) this.sessionFactory.getCurrentSession().createQuery(
                            "FROM User WHERE secret = :secret")
                    .setParameter("secret", secret)
                    .setMaxResults(1)
                    .uniqueResult();
        }
        return user;
    }

    private Match getMatchById(int id) {
        return (Match) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM Match WHERE id = :id")
                .setParameter("id", id)
                .setMaxResults(1)
                .uniqueResult();
    }

    public void createTeams() {
        Faker faker = new Faker();
        List<Team> teams = (List<Team>) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM Team")
                .list();
        if (teams.isEmpty()) {
            for (int i = 0; i < TEAM_NUM; i++) {
                Team team = new Team(faker.country().capital());
                save(team);
            }
        }
    }

    private boolean isEmailCorrect(String email) {
        return email.contains("@") && email.contains(".") && (email.lastIndexOf(".") - email.indexOf("@") > 1) && (email.indexOf("@") != 0);
    }

    private boolean isPasswordStrong(String password) {
        return password != null && password.length() >= 8;
    }

    public UserResponse changeUsernameOrEmail(String category, String toChange, String secret) {
        Integer errorCode = null;
        UserResponse userResponse;
        User user = null;
        if ((category.equals("username") || category.equals("email")) && toChange != null && !toChange.isEmpty() && secret != null && !secret.isEmpty()) {
            user = getUserBySecret(secret);
            if (user != null) {
                switch (category) {
                    case "username":
                        if (isUsernameAvailable(toChange)) {
                            user.setUsername(toChange);
                            save(user);
                        } else {
                            errorCode = ERROR_SIGN_UP_USERNAME_TAKEN;
                        }
                        break;
                    case "email":
                        if (isEmailCorrect(toChange)) {
                            user.setEmail(toChange);
                            save(user);
                        } else {
                            errorCode = ERROR_EMAIL_FORMAT;
                        }
                        break;
                }
            }else {
                errorCode = ERROR_NO_SUCH_USER;
            }
        }else {
            errorCode = ERROR_LOGIN_WRONG_CREDS;
        }

        if (errorCode == null) {
            userResponse = new UserResponse(true, null , user);
        } else {
            userResponse = new UserResponse(false, errorCode, user);
        }
        return userResponse;
    }

    public UserResponse changePassword(String toChange, String currentPassword, String secret) {
        Integer errorCode = null;
        UserResponse userResponse;
        User user = null;
        if (toChange != null && !toChange.isEmpty() && secret != null && !secret.isEmpty()) {
            user = getUserBySecret(secret);
            if (user.getPassword().equals(currentPassword)) {
                if (isPasswordStrong(toChange)) {
                    user.setPassword(toChange);
                    save(user);
                } else {
                    errorCode = ERROR_WEAK_PASSWORD;
                }
            } else {
                errorCode = ERROR_SIGN_UP_PASSWORDS_DONT_MATCH;
            }
        }else {
            errorCode = ERROR_LOGIN_WRONG_CREDS;
        }

        if (errorCode == null) {
            userResponse = new UserResponse(true, null, user);
        } else {
            userResponse = new UserResponse(false, errorCode, user);
        }
        return userResponse;
    }

    public void addGoals() {
        List<Match> liveMatches = loadLiveMatchList();
        for (Match game : liveMatches) {
            Faker faker = new Faker();
            int goalRandom = faker.random().nextInt(0, 100);
            if (goalRandom <= GOAL_CHANCE) {
                int weatherEffectRandom = faker.random().nextInt(0, 100);
                if ((game.getTeam1().getSkillLevel() > game.getTeam2().getSkillLevel()) && (weatherEffectRandom > WEATHER_EFFECT_CHANCE)) {
                    game.addGoal_T1();
                } else if (game.getTeam1().getSkillLevel() < game.getTeam2().getSkillLevel() && (weatherEffectRandom > WEATHER_EFFECT_CHANCE)) {
                    game.addGoal_T2();
                } else {
                    if (faker.random().nextInt(0, 1) == 0) {
                        game.addGoal_T1();
                    } else {
                        game.addGoal_T2();
                    }
                }
            }
            save(game);
        }
    }


    public BasicResponse addGamble(String secret, int matchId, int teamNum, int gambleSum ,double ratio) {
        User user;
        user = getUserBySecret(secret);
        int errorCode;
        if (user != null) {
            Gamble gamble;
            Match match = getMatchById(matchId);
            if (user.getBalance() >= gambleSum) {
                if (match.getIsLive() == null) {
                    user.reduceBalance(gambleSum);
                    gamble = new Gamble(user, match, teamNum, gambleSum,ratio);
                    save(gamble);
                    save(user);
                    return new BasicResponse(true, null);
                } else {
                    errorCode = ERROR_GAME_ALREADY_STARTED;
                }
            } else {
                errorCode = ERROR_NOT_ENOUGH_MONEY;
            }
        } else {
            errorCode = ERROR_NO_SUCH_USER;
        }
        return new BasicResponse(false, errorCode);
    }

    public List<Gamble> getGamblingByMatch(int matchId) {
        return (List<Gamble>) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM Gamble WHERE match_id = :match_id")
                .setParameter("match_id", matchId)
                .list();
    }

    public void checkGambling(Match match) {
        boolean isCorrect = false;
        List<Gamble> gambleList = getGamblingByMatch(match.getId());
        for (Gamble gamble : gambleList) {
            if (match.winner() == match.getTeam1() && (gamble.getTeam() == 1)) {
                isCorrect = true;
            } else if (match.winner() == match.getTeam2() && (gamble.getTeam() == 2)) {
                isCorrect = true;
            } else if (match.winner() == null && (gamble.getTeam() == 0)) {
                isCorrect = true;
            }
            gamble.setIsCorrect(isCorrect);
            if (isCorrect) {
                gamble.getUser().addToBalance(gamble.getRatio() * gamble.getSum());
                save(gamble.getUser());
            }
            save(gamble);
        }

    }

    public List<Gamble> getUserGambling(String secret) {
        User user = getUserBySecret(secret);
        return (List<Gamble>) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM Gamble WHERE user_id = :user_id")
                .setParameter("user_id", user.getId())
                .list();
    }

    public void setSkills (Match match) {
        Team winner = match.winner();
        Team loser;
        if (winner != null) {
            double teamsRatio;
            if (winner.getId() == match.getTeam1().getId()) {
                loser = match.getTeam2();
                teamsRatio = (double) match.getTeam2().getSkillLevel() / (double) match.getTeam1().getSkillLevel();
            } else {
                loser = match.getTeam1();
                teamsRatio = (double) match.getTeam1().getSkillLevel() / (double) match.getTeam2().getSkillLevel();
            }
            Faker faker = new Faker();
            winner.increaseSkillLevel((int) (faker.random().nextInt(1, 10) * teamsRatio));
            loser.reduceSkillLevel((int) (faker.random().nextInt(1, 10) * teamsRatio));
            save(winner);
            save(loser);
        }
    }

}