package dao;

import controller.DBController;
import model.Data;
import model.Material;
import model.User;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DAO{

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
        if(find(user.getMoodleId()) == null){
            super.save(user);
        }
    }

    public List<User> getAllUser() {
        if(users.isEmpty()){
            TypedQuery<User> q = db.em().createQuery("SELECT u FROM User u", User.class);
            users = q.getResultList();
        }

        return users;
    }

    public void insert(User user) {
            db.em().persist(user);
    }


}
