package dao;

import javafx.collections.ObservableList;
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

    public List<Material> getAllMaterialForCategories(ObservableList<String> categories) {
        StringBuilder query = new StringBuilder("select m from Material m where ");
        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            query.append("m.category='"+category+"' ");
            if(!(i+1==categories.size())){
                query.append("or ");
            }
        }
        TypedQuery<Material> q = db.em().createQuery(query.toString(), Material.class);
        return q.getResultList();
    }
}
