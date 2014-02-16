package modules.userprofile.controller;

import dao.DataDAO;
import dao.MaterialDAO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
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
    public static Text subtitle;
    Logger log = LoggerFactory.getLogger(UserProfileController.class);
    public static Text userIdField;
    public Text text = new Text("");
    @FXML
    public static Pane dataPane;
    @FXML
    public static HBox bottom;
    private List<List<Data>> weeklyData;
    private Date minDate;
    private Date maxDate;


    public void init() {
        DataDAO dataDAO = new DataDAO();


        maxDate = dataDAO.getMaxDate();
        minDate = dataDAO.getMinDate();

        log.info(maxDate.toString());
        log.info(minDate.toString());

        Calendar start = Calendar.getInstance();
        start.setTime(minDate);

        weeklyData = new ArrayList<List<Data>>();
        while (start.getTime().before(maxDate)) {
            Calendar end = Calendar.getInstance();
            end.setTime(start.getTime());
            end.add(Calendar.DATE, 7);
            List<Data> dataForUser = dataDAO.getDataForUser(currentUser, start.getTime(), end.getTime());
            start.setTime(end.getTime());
            weeklyData.add(dataForUser);
        }

        bottom.getChildren().clear();
        for (int i = 1; i <= weeklyData.size(); i++) {
            Button button = new Button("w " + String.valueOf(i));
            final int finalI = i;
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    showWeek(finalI);
                }
            }
            );
            bottom.getChildren().add(button);
        }

        showWeek(1);

    }

    private void showWeek(int i) {
        MaterialDAO materialDAO = new MaterialDAO();

        List<String> categories = materialDAO.getCategories();

        List<Data> dataForUser = weeklyData.get(i - 1);

        List<Material> flow = new ArrayList<Material>();
        for (Data data : dataForUser) {
            flow.add(data.getMaterial());
        }
        Canvas graph = createNodes(categories, flow);

        dataPane.getChildren().clear();

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        start.setTime(minDate);
        start.add(Calendar.DATE, 7 * i);

        end.setTime(start.getTime());
        start.add(Calendar.DATE, 7);

        subtitle.setText(start.getTime() + " -- " + end.getTime());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(graph);
        dataPane.getChildren().add(scrollPane);


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
                if (!from.equals(to))
                    graph.addEdge(from, to);
            } catch (NullPointerException ignore) {

            }
        }

        return graph.generate();

    }
}
