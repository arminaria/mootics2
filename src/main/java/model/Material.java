package model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    @Column
    private String materialId;
    @Column
    private String name;
    @Column
    private String url;
    @Column
    private String type;
    @Column
    private String section;
    @Column
    private String category;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Data> data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", materialId='" + materialId + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", section='" + section + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
