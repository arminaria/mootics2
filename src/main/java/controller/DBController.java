package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by Armin on 27.11.13.
 */
public class DBController {
    private static final Logger log = LoggerFactory.getLogger(DBController.class);

    private static DBController instance;
    private static EntityManagerFactory emf;
    private static EntityManager em;


    public static DBController getInstance() {
        if (instance == null) {
            instance = new DBController();
        }
        return instance;
    }

    private DBController() {
        emf = Persistence.createEntityManagerFactory("dataPU");
    }

    public EntityManager em() {
        if (em == null) {
            em = emf.createEntityManager();
        }
        return em;
    }

    public void start() {
        em().getTransaction().begin();
    }

    public void commit() {
        em().getTransaction().commit();
    }

    public void insert(Object o){
        em().persist(o);
    }

}
