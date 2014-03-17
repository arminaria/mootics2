package dao;

import controller.DBController;
import model.*;

import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class DataDAO extends DAO{
    DBController db = DBController.getInstance();

    public void insert(Data o) {
        db.em().persist(o);
    }

    public void save(Data d) {
        if(!dataInDb(d)){
            super.save(d);
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

    public Long getClickCount(User user, GradeName gradeName) {
        if(gradeName.getName().contains("Course total")){
            return new Long(user.getData().size());
        }
        // find the material for the grade
        TypedQuery<Material> query = db.em().createQuery("SELECT m From Material m where m.name=:name", Material.class);
        query.setParameter("name", getMaterialNameFrom(gradeName));
        query.setMaxResults(1);
        List<Material> resultList = query.getResultList();
        if(resultList.isEmpty()){
            return new Long(0);
        }
        Material material = resultList.get(0);

        // get the section for the grade
        String section = material.getSection();

        // get date of the last attempt
        TypedQuery<Date> queryDate = db.em().createQuery("SELECT max (d.date) from Data d where d.user =:user " +
                "and (material.name=:materialName)", Date.class);
        queryDate.setParameter("user", user);
        queryDate.setParameter("materialName", getMaterialNameFrom(gradeName));
        queryDate.setMaxResults(1);
        Date date = queryDate.getResultList().get(0);


        // find the clicks before last attempt in the section
        TypedQuery<Long> q = db.em().createQuery("SELECT count(d) from Data d where d.user = :user " +
                "and (d.material.section=:section or d.material.section is null or d.material.section='section-0') and d.date <= :date", Long.class);
        q.setParameter("user", user);
        q.setParameter("section", section);
        q.setParameter("date",date);

        return q.getResultList().get(0);
    }

    private String getMaterialNameFrom(GradeName gradeName){
        return gradeName.getName().split(":")[1].trim();
    }

    public List<Data> getVideoData(User user, Matterhorn matterhorn) {
        TypedQuery<Data> q = db.em().
                createQuery("SELECT d FROM Data d WHERE d.user=:user and d.material.category='Video' and d.material.id =:materialId", Data.class);
        q.setParameter("user", user);
        q.setParameter("materialId", matterhorn.getMaterial().getId());
        return q.getResultList();
    }
}
