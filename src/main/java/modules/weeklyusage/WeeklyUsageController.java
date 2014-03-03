package modules.weeklyusage;

import dao.MaterialDAO;
import dao.UserDAO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import model.Data;
import model.Material;
import model.User;
import modules.listcellview.ListUser;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class WeeklyUsageController implements Initializable {
    public ListView<User> userList;
    public StackPane main;
    String[] weekDays = {"So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
                update();
            }
        });
        //userList.getSelectionModel().select(0);
    }

    private void update() {
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
