package dao;

import controller.DBController;
import model.Data;
import model.User;

import javax.persistence.TypedQuery;
import java.util.Date;
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

    public Date getMinDate(){
        TypedQuery<Date> q = db.em().createQuery("Select min(d.date) from Data d", Date.class);
        q.setMaxResults(1);
        return q.getResultList().get(0);
    }

    public Date getMaxDate(){
        TypedQuery<Date> q = db.em().createQuery("Select max(d.date) from Data d", Date.class);
        q.setMaxResults(1);
        return q.getResultList().get(0);
    }

    public List<Data> getDataForUser(User user, Date after, Date before){
        TypedQuery<Data> q = db.em().createQuery("SELECT d FROM Data d WHERE d.user=:user and d.date >=:after and d.date <=:before", Data.class);
        q.setParameter("user", user);
        q.setParameter("after", after);
        q.setParameter("before", before);
        return q.getResultList();
    }
}
