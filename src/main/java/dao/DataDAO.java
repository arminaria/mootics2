package dao;

import controller.DBController;
import model.Data;
import model.User;

import javax.persistence.TypedQuery;
import java.util.List;

public class DataDAO {
    DBController db = DBController.getInstance();

    public void save(Data d) {
        if(!dataInDb(d)){
            db.start();
            db.insert(d);
            db.commit();
        }
    }

    public boolean dataInDb(Data data) {
        TypedQuery<Data> q = db.em().createQuery("SELECT d from Data d where d.date=:date and d.user=:user", Data.class);
        q.setParameter("date", data.getDate());
        q.setParameter("user", data.getUser());
        q.setMaxResults(1);
        if(q.getResultList().isEmpty())return false;
        return true;
    }

    public List<Data> getDataForUser(User user) {
        TypedQuery<Data> q = db.em().createQuery("SELECT d from Data d where d.user=:user", Data.class);
        q.setParameter("user", user);
        return q.getResultList();

    }
}
