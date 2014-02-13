package dao;

import controller.DBController;
import model.Grade;

import javax.persistence.TypedQuery;

public class GradeDAO {
    DBController db = DBController.getInstance();

    public void save(Grade g) {
        if(!isGradeInDB(g)){
            db.start();
            db.insert(g);
            db.commit();
        }
    }

    public boolean isGradeInDB(Grade g){
        TypedQuery<Grade> query = db.em().createQuery("SELECT g from Grade g where g.user=:user and g.name=:name", Grade.class);
        query.setMaxResults(1);
        query.setParameter("user", g.getUser());
        query.setParameter("name", g.getName());
        return !query.getResultList().isEmpty();
    }


}
