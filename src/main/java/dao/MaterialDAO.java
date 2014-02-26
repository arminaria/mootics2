package dao;

import controller.DBController;
import model.Data;
import model.Material;

import javax.persistence.TypedQuery;
import java.util.List;

public class MaterialDAO extends DAO{

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

    public void insert(Material material) {
            db.em().persist(material);
    }
}
