package dao;

import model.Grade;
import model.Material;
import model.Matterhorn;

import javax.persistence.TypedQuery;
import java.util.List;

public class MatterhornDAO extends DAO {

    public void save(int materialId, int views, int time, int average){
        Matterhorn matterhorn = new Matterhorn();
        Material material = new MaterialDAO().find(String.valueOf(materialId));

        matterhorn.setMaterial(material);
        matterhorn.setViews(Long.valueOf(views));
        matterhorn.setTime(Long.valueOf(time));
        matterhorn.setAverageWatch(Long.valueOf(average));

        super.save(matterhorn);
    }

    public List<Matterhorn> getAllMatterhorn() {
        TypedQuery<Matterhorn> query = db.em().createQuery("SELECT m from Matterhorn m", Matterhorn.class);
        return query.getResultList();
    }
}
