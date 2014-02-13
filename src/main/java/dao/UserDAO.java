package dao;

import controller.DBController;
import model.User;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    DBController db = DBController.getInstance();
    public static List<User> users = new ArrayList<User>();

    public User find(int userId) {
        TypedQuery<User> q = db.em().createQuery("SELECT m FROM User m where moodleId=:userId", User.class);
        q.setParameter("userId", userId);
        List<User> resultList = q.setMaxResults(1).getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        return resultList.get(0);
    }

    public void save(User user) {
        db.start();
        db.insert(user);
        db.commit();
    }

    public List<User> getAllUser() {
        if(users.isEmpty()){
            TypedQuery<User> q = db.em().createQuery("SELECT u FROM User u", User.class);
            users = q.getResultList();
        }

        return users;
    }
}
