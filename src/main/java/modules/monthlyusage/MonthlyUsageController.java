package modules.monthlyusage;

import dao.UserDAO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import model.Data;
import model.User;
import modules.listcellview.ListUser;
import org.springframework.data.mapping.model.SpELContext;

import java.net.URL;
import java.util.*;

public class MonthlyUsageController implements Initializable {

    public ListView<User> userList;
    public StackPane main;

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
    }

    public void selectAll(ActionEvent actionEvent) {
        userList.getSelectionModel().selectAll();
    }

    private void update() {
        main.getChildren().clear();

        LineChart<Number, Number> chart = new LineChart<Number, Number>(
                new NumberAxis(1, 31, 1), new NumberAxis()
        );

        ObservableList<User> selectedItems = userList.getSelectionModel().getSelectedItems();
        List<Data> data = new ArrayList<Data>();

        for (User user : selectedItems) {
            data.addAll(user.getData());
        }

        Map<Integer, List<Data>> monthlyData = getMonthlyData(data, new ArrayList<List<Data>>());
        for (Integer month : monthlyData.keySet()) {
            List<Data> monthly = monthlyData.get(month);
            int[] dailyData = getDailyData(monthly);
            XYChart.Series<Number, Number> s = new XYChart.Series<Number, Number>();
            s.setName(String.valueOf(month+1));
            for (int i = 0; i < dailyData.length; i++) {
                Integer daily = dailyData[i];
                s.getData().add(new XYChart.Data<Number, Number>(i + 1, daily));
            }
            chart.getData().add(s);
        }
        main.getChildren().add(chart);
    }

    private Map<Integer, List<Data>> getMonthlyData(List<Data> data, ArrayList<List<Data>> lists) {
        HashMap<Integer, List<Data>> map = new HashMap<Integer, List<Data>>();
        for (Data d : data) {
            Date date = d.getDate();
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            int i = c.get(Calendar.MONTH);

            List<Data> dataI = map.get(i);
            if (dataI == null) {
                dataI = new ArrayList<Data>();
                map.put(i, dataI);
            } else {
                map.get(i).add(d);
            }
        }

        return map;
    }

    private int[] getDailyData(List<Data> data) {
        int[] daily = new int[31];

        for (Data d : data) {
            Date date = d.getDate();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int i = c.get(Calendar.DAY_OF_MONTH);
            daily[i - 1]++;
        }

        return daily;
    }

    public static void main(String[] args) {
        int[] daily = new int[31];
        for (int i = 0; i < daily.length; i++) {
            Integer integer = daily[i];
            System.out.println(integer);
        }
    }
}
