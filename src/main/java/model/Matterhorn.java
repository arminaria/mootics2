package model;

import javax.persistence.*;

@Entity
public class Matterhorn {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Material material;
    @Column private Long views;
    @Column private Long time;
    @Column private Long averageWatch;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getAverageWatch() {
        return averageWatch;
    }

    public void setAverageWatch(Long averageWatch) {
        this.averageWatch = averageWatch;
    }

    @Override
    public String toString() {
        return "Matterhorn{" +
                "id=" + id +
                ", material=" + material.getName() +
                ", views=" + views +
                ", time=" + time +
                ", averageWatch=" + averageWatch +
                '}';
    }
}
