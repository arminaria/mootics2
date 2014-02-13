package model;


import javax.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Integer id;

    @Column(unique = true)
    private Integer moodleId;
    @Column
    private char gender;
    @Column
    private char expectedDegree;
    @Column
    private int semester;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Data> data;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Grade> grades;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMoodleId() {
        return moodleId;
    }

    public void setMoodleId(Integer moodleId) {
        this.moodleId = moodleId;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public char getExpectedDegree() {
        return expectedDegree;
    }

    public void setExpectedDegree(char expectedDegree) {
        this.expectedDegree = expectedDegree;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", moodleId='" + moodleId + '\'' +
                ", gender=" + gender +
                ", expectedDegree=" + expectedDegree +
                ", semester=" + semester +
                '}';
    }
}
