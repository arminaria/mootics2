package dao;

import controller.DBController;
import model.Grade;
import model.GradeName;
import model.User;

import javax.persistence.TypedQuery;
import java.util.List;

public class GradeDAO extends DAO{
    DBController db = DBController.getInstance();

    public void save(Grade g) {
        if(!isGradeInDB(g)){
            super.save(g);
        }
    }

    public boolean isGradeInDB(Grade g){
        TypedQuery<Grade> query = db.em().createQuery("SELECT g from Grade g where g.user=:user and g.name=:name", Grade.class);
        query.setMaxResults(1);
        query.setParameter("user", g.getUser());
        query.setParameter("name", g.getName());
        return !query.getResultList().isEmpty();
    }


    public List<GradeName> getAllAvailableGradeNames() {
        TypedQuery<GradeName> query = db.em().createQuery("SELECT g from GradeName g ", GradeName.class);
        return query.getResultList();
    }

    public void save(GradeName gradeName) {
        if(!isGradeNameInDB(gradeName)) {
            db.start();
            db.em().persist(gradeName);
            db.commit();
        }
    }

    private boolean isGradeNameInDB(GradeName gradeName) {
        return findGradeName(gradeName) != null;
    }

    public GradeName findGradeName(GradeName gradeName) {
        TypedQuery<GradeName> query =
                db.em().createQuery("SELECT g from GradeName g where g.name=:name ", GradeName.class);
        query.setMaxResults(1);
        query.setParameter("name", gradeName.getName());
            List<GradeName> resultList = query.getResultList();
        if(resultList.isEmpty()) {
            return null;
        }else{
            return resultList.get(0);
        }
    }

    public Grade getGrades(User user, GradeName gradeName) throws Exception {
        TypedQuery<Grade> query =
                db.em().createQuery("SELECT g from Grade g where g.user=:user and g.name=:gradeName ", Grade.class);
        query.setParameter("user", user);
        query.setParameter("gradeName", gradeName);
            List<Grade> resultList = query.getResultList();
        if(!resultList.isEmpty()) {
            return resultList.get(0);
        }else{
            throw new Exception("No Grade for this user found");
        }
    }
}
