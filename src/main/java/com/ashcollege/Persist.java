package com.ashcollege;


import com.ashcollege.entities.Client;
import com.ashcollege.entities.Match;
import com.ashcollege.entities.Team;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.github.javafaker.Faker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.ashcollege.utils.Errors.*;


@Transactional
@Component
@SuppressWarnings("unchecked")
public class Persist {

    private static final Logger LOGGER = LoggerFactory.getLogger(Persist.class);

    private final SessionFactory sessionFactory;

    private List<Team> teamList;

    @Autowired
    public Persist(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public Session getQuerySession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Object object) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(object);
    }

    public <T> T loadObject(Class<T> clazz, int oid) {
        return this.getQuerySession().get(clazz, oid);
    }

    public <T> List<T> loadUserList(Class<T> clazz) {
        return this.sessionFactory.getCurrentSession().createQuery("FROM User").list();
    }

    public <T> List<T> loadTeamList(Class<T> clazz) {
        return this.sessionFactory.getCurrentSession().createQuery("FROM Team").list();
    }

    public <T> List<T> loadMatchList(Class<T> clazz) {
        return this.sessionFactory.getCurrentSession().createQuery("FROM Match").list();
    }

    public Client getClientByFirstName(String firstName) {
        return (Client) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM Client WHERE firstName = :firstName")
                .setParameter("firstName", firstName)
                .setMaxResults(1)
                .uniqueResult();
    }

    public User login(String email, String password) {
        if (email != null && password != null) {
            return (User) this.sessionFactory.getCurrentSession().createQuery(
                            "FROM User WHERE email = :email AND password = :password")
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .setMaxResults(1)
                    .uniqueResult();
        }
        // check if not null
        return null;
    }


    public boolean isUsernameExist (String username) {
        User userList = (User) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM User WHERE username = :username")
                .setParameter("username", username)
                .uniqueResult();

        return (userList != null);
    }
    public BasicResponse signUp(String username,String email, String password) {
        BasicResponse basicResponse;
        if (!isUsernameExist(username) && isEmailCorrect(email)) {
            Faker faker = new Faker();
            User user = new User(username,email, password, faker.random().hex());
            save(user);
            basicResponse = new LoginResponse(true, null, user);
        } else {
            basicResponse = new BasicResponse(false, ERROR_SIGN_UP_USERNAME_TAKEN);
        }
        return basicResponse;
    }

    public User getUserBySecret(String secret) {
        return (User) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM User WHERE secret = :secret")
                .setParameter("secret", secret)
                .setMaxResults(1)
                .uniqueResult();
    }

    public void createTeams() {
        Faker faker = new Faker();
        teamList = new ArrayList<>();
        List<Team> teams = (List<Team>) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM Team")
                .list();
        if (teams.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                Team team = new Team(faker.country().capital());
//                team.setSkillLevel(faker.random().nextInt(0,100));
                save(team);
                teamList.add(team);
            }
        } else {
            teamList = teams;
        }

        for (Team team : teamList) {
//            team.setSkillLevel(faker.random().nextInt(0, 100));
        }
        System.out.println(teamList.toString());
    }

    public boolean isEmailCorrect (String email) {
        return email.contains("@") && email.contains(".") && (email.indexOf(".") - email.indexOf("@") > 1) && (email.indexOf("@") != 0);
    }

    public boolean isPasswordCorrect (String password) {
        return password.length() >= 8;
    }

    public void changeProfile (String category, String toChange, String secret) {
        switch (category){
            case "username":
                if (!isUsernameExist(toChange)) {
                    User user = getUserBySecret(secret);
                    user.setUsername(toChange);
                    save(user);
                }
                break;
            case "email":
                if (isEmailCorrect(toChange)) {
                    User user = getUserBySecret(secret);
                    user.setEmail(toChange);
                    save(user);
                }
                break;
            case "password":
                if (isPasswordCorrect(toChange)) {
                    User user = getUserBySecret(secret);
                    user.setPassword(toChange);
                    save(user);
                }
                break;
        }
    }
}