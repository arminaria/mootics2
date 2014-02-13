package dao;

import controller.DBController;
import model.Material;

import javax.persistence.TypedQuery;
import java.util.List;

public class MaterialDAO {


    DBController db = DBController.getInstance();

    public Material find(String materialId) {
        TypedQuery<Material> q = db.em().createQuery("SELECT m FROM Material m where materialId=:materialId", Material.class);
        q.setParameter("materialId", materialId);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return q.setMaxResults(1).getResultList().get(0);
    }

    public List<String> getCategories() {
        TypedQuery<String> q = db.em().createQuery("select distinct m.category from Material m", String.class);
        return q.getResultList();
    }
}
