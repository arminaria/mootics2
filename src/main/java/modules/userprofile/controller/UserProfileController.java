package modules.userprofile.controller;

import dao.DataDAO;
import dao.MaterialDAO;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import model.Data;
import model.Material;
import model.User;
import modules.userprofile.Graph;
import modules.userprofile.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserProfileController {
    public static User currentUser;
    Logger log = LoggerFactory.getLogger(UserProfileController.class);
    public static Text userIdField;
    public Text text = new Text("");
    @FXML
    public static Pane dataPane;


    public void init() {
        DataDAO dataDAO = new DataDAO();
        MaterialDAO materialDAO = new MaterialDAO();

        List<String> categories = materialDAO.getCategories();
        List<Data> dataForUser = dataDAO.getDataForUser(currentUser);
        Calendar c = Calendar.getInstance();
        c.set(2013, Calendar.DECEMBER, 17, 0, 0);
        Date startDate = c.getTime();
        c.setTime(startDate);
        c.add(Calendar.DATE, 7);
        Date endDate = c.getTime();
        dataPane.getChildren().clear();

        dataPane.getChildren().add(text);
        Canvas nodes = createNodes(categories, getFlow(dataForUser, startDate, endDate));
        dataPane.getChildren().add(nodes);

    }

    private List<Material> getFlow(List<Data> data, Date start, Date end) {
        ArrayList<Material> materials = new ArrayList<Material>();
        for (Data d : data) {
            Date date = d.getDate();
            if (date.after(start) && date.before(end)) {
                materials.add(d.getMaterial());
            } else if (date.compareTo(start) == 0 || date.compareTo(end) == 0) {
                materials.add(d.getMaterial());
            }
        }
        return materials;
    }


    private Canvas createNodes(List<String> nodes, List<Material> flow) {

        Graph graph = new Graph();
        for (String node : nodes) {
            graph.addNode(new Vertex(node));
        }

        for (int i = 0; i < flow.size() - 1; i++) {
            try {

                Material m1 = flow.get(i);
                Material m2 = flow.get(i + 1);

                String c1 = m1.getCategory();
                String c2 = m2.getCategory();

                Vertex from = new Vertex(c1);
                Vertex to = new Vertex(c2);

                log.info(from + " -> " + to);
                if (!from.equals(to))
                    graph.addEdge(from, to);
            }catch (NullPointerException ignore){

            }
        }

        return graph.generate();

    }
}
