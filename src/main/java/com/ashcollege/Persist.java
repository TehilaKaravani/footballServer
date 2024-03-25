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

import java.util.List;

import static com.ashcollege.utils.Errors.*;


@Transactional
@Component
@SuppressWarnings("unchecked")
public class Persist {

    private static final Logger LOGGER = LoggerFactory.getLogger(Persist.class);

    private final SessionFactory sessionFactory;


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

    public User login(String username, String password) {
        // check if not null
        return (User) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM User WHERE username = :username AND password = :password")
                .setParameter("username", username)
                .setParameter("password", password)
                .setMaxResults(1)
                .uniqueResult();
    }

    public BasicResponse signUp(String username, String password) {
        BasicResponse basicResponse;
        Object userList = this.sessionFactory.getCurrentSession().createQuery(
                        "FROM User WHERE username = :username")
                .setParameter("username", username)
                .uniqueResult();

        if (userList == null) {
            Faker faker = new Faker();
            User user = new User(username, password, faker.random().hex());
            save(user);
            basicResponse = new LoginResponse(true, null, user.getId(), user.getSecret());
        } else {
            basicResponse = new BasicResponse(false, ERROR_SIGN_UP_USERNAME_TAKEN);
        }
        return basicResponse;
    }

    public User getUserBySecret (String secret) {
        return (User) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM User WHERE secret = :secret")
                .setParameter("secret", secret)
                .setMaxResults(1)
                .uniqueResult();
    }


}