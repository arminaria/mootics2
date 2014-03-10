package modules.userprofile.controller;

import dao.DataDAO;
import dao.MaterialDAO;
import dao.UserDAO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import model.Data;
import model.Material;
import model.User;
import modules.listcellview.ListUser;
import modules.userprofile.Graph;
import modules.userprofile.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

public class UserProfileController implements Initializable {
    public static User currentUser;
    public ListView<User> userList;
    public Text title;
    public StackPane main;
    String[] weekDays = {"So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"};
    private static Map<User,List<List<Data>>> userSessionList = new HashMap<User, List<List<Data>>>();
    public ToolBar subButtons;
    public View currentView;
    public enum View {
        USER_GRAPH, WEEKLY_USAGE
    }

    Logger log = LoggerFactory.getLogger(UserProfileController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // fill UserList
        List<User> allUser = new UserDAO().getAllUser();
        userList.getItems().addAll(allUser);
        userList.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> userListView) {
                return new ListUser();
            }
        });
        userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue observableValue, User o, User o2) {
                currentUser = o2;
                update();
            }
        });
        currentView = View.USER_GRAPH;
        userList.getSelectionModel().select(0);
    }

    private void update() {
        title.setText(currentUser.getMoodleId().toString());
        switch (currentView){
            case USER_GRAPH:
                showUserGraph(null);
                break;
            case WEEKLY_USAGE:
                showWeeklyUsage(null);
                break;
            default:showWeeklyUsage(null);
        }

    }

    public void showUserGraph(ActionEvent actionEvent) {
        this.currentView = View.USER_GRAPH;
        List<Data> data = currentUser.getData();
        final List<List<Data>> lists = splitDataBySession(data, new ArrayList<List<Data>>());
        userSessionList.put(currentUser,lists);

        subButtons.getItems().clear();
        for(int i = 0; i<lists.size();i++){
            Button btn = new Button(String.valueOf(i));
            final int finalI = i;
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    updateGraph(lists.get(finalI));
                }
            });
            subButtons.getItems().add(btn);
            updateGraph(lists.get(0));
        }

    }

    private void updateGraph(List<Data> data) {
        MaterialDAO mDAO = new MaterialDAO();
        List<String> categories = mDAO.getCategories();

        Graph graph = new Graph(categories, data);

        ScrollPane scrollPane = new ScrollPane();
        Canvas graphCanvas = graph.generate();
        scrollPane.setContent(graphCanvas);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        main.getChildren().clear();
        main.getChildren().add(scrollPane);
    }

    private List<List<Data>> splitDataBySession(List<Data> data, List<List<Data>> result) {
        if(userSessionList.containsKey(currentUser)) return userSessionList.get(currentUser);
        if (data == null || data.isEmpty()) {
            return result;
        }
        if (data.size() == 1) {
            result.add(data);
            return result;
        }

        List<Data> partialData = new ArrayList<Data>();

        int pointer = -1;

        for (int i = 0; i < data.size() - 1; i++) {
            Data c = data.get(i);
            Data n = data.get(i + 1);
            partialData.add(c);
            if (Math.abs(c.getDate().getTime() - n.getDate().getTime()) > 24 * 60 * 60 * 1000) {
                result.add(partialData);
                pointer = i + 1;
                break;
            }
        }

        if(pointer == -1){
            result.add(data);
            return splitDataBySession(null, result);
        }

        return splitDataBySession(data.subList(pointer, data.size()), result);

    }


    public void showWeeklyUsage(ActionEvent actionEvent) {
        this.currentView = View.WEEKLY_USAGE;

        ObservableList<User> selectedItems = userList.getSelectionModel().getSelectedItems();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>observableArrayList(
                Arrays.asList(weekDays)));

        NumberAxis yAxis = new NumberAxis();

        StackedBarChart<String, Number> barChart = new StackedBarChart<String, Number>(
                xAxis, yAxis
        );

        main.getChildren().clear();

        MaterialDAO mDAO = new MaterialDAO();
        List<String> categories = mDAO.getCategories();
        for (String category : categories) {
            XYChart.Series<String, Number> s = new XYChart.Series<String, Number>();
            s.setName(category);
            for (int i = 1; i <= weekDays.length; i++) {
                String weekDay = weekDays[i - 1];
                int clicks = 0;
                for (User current : selectedItems) {
                    clicks += getClickCountOfDay(i, current.getData(), category);
                }
                XYChart.Data<String, Number> point = new XYChart.Data<String, Number>(weekDay, clicks);
                s.getData().add(point);
            }
            barChart.getData().add(s);
        }
        main.getChildren().add(barChart);
    }

    public int getClickCountOfDay(int weekDay, List<Data> data, String category) {
        int count = 0;
        for (Data d : data) {
            Date date = d.getDate();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int day = c.get(Calendar.DAY_OF_WEEK);
            Material material = d.getMaterial();
            if (material != null) {
                if (day == weekDay && material.getCategory().equals(category)) {
                    count++;
                }
            }
        }
        return count;
    }


    public void selectAll(ActionEvent actionEvent) {
        userList.getSelectionModel().selectAll();
    }
}
