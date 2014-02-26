package dao;

import controller.DBController;
import org.apache.poi.ss.formula.functions.T;

import javax.persistence.Entity;

public abstract class DAO {
    protected DBController db = DBController.getInstance();

    public void save(Object ... objects){
        db.start();
        for (Object o : objects) {
            db.insert(o);
        }
        db.commit();
    }

    public void begin(){
        db.em().getTransaction().begin();
    }

    public void commit(){
        db.em().getTransaction().commit();
    }



    public <T> T update(T o){
        T merge = db.em().merge(o);
        return merge;
    }

}
