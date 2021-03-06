package modules.statistik;


import dao.DataDAO;
import dao.GradeDAO;
import dao.UserDAO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import model.Grade;
import model.GradeName;
import model.User;
import modules.listcellview.ListGradeName;
import modules.listcellview.ListUser;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by ara on 24.02.14.
 */
public class StatistikController implements Initializable {
    private static Logger log = LoggerFactory.getLogger(StatistikController.class);


    public StackPane chartPane;
    public ListView<GradeName> GradeListView;
    public ListView<User> userListView;
    public ListView FilterListView;
    public Button btnAllUser;
    public Text subtitle;
    public ScatterChart<Number, Number> chart;
    public Text r2;
    public Text regressionEQ;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserDAO userDAO = new UserDAO();
        List<User> allUser = userDAO.getAllUser();
        userListView.getItems().addAll(allUser);
        Callback<ListView<User>, ListCell<User>> cellFactory = new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> listView) {
                return new ListUser();
            }
        };
        userListView.setCellFactory(cellFactory);
        userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // what should happen if a item is selected
        userListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue observableValue, User o, User o2) {
                setSubTitle();
            }
        });

        userListView.getSelectionModel().selectAll();

        GradeDAO gradeDAO = new GradeDAO();
        List<GradeName> allAvailableGradeNames = gradeDAO.getAllAvailableGradeNames();
        GradeListView.getItems().addAll(allAvailableGradeNames);
        Callback<ListView<GradeName>, ListCell<GradeName>> cellFactory2 = new Callback<ListView<GradeName>, ListCell<GradeName>>() {
            @Override
            public ListCell<GradeName> call(ListView<GradeName> listView) {
                return new ListGradeName();
            }
        };
        GradeListView.setCellFactory(cellFactory2);
        GradeListView.getSelectionModel().select(0);

        GradeListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<GradeName>() {
            @Override
            public void changed(ObservableValue<? extends GradeName> observableValue, GradeName gradeName, GradeName gradeName2) {
                update();
            }
        });

        update();

    }

    private void update() {
        setSubTitle();
        updateChart();

    }

    private void setSubTitle() {
        ObservableList<User> users = userListView.getSelectionModel().getSelectedItems();
        ObservableList<GradeName> selectedItems = GradeListView.getSelectionModel().getSelectedItems();
        StringBuilder sb = new StringBuilder("For User: ");
        if(users.size() <= 3) {
            for (User user : users) {
                sb.append(user.getMoodleId() + " ");
            }
        }else{
            sb.append("multiple users");
        }
        sb.append(" / for Grades: ");
        for (GradeName gradeName : selectedItems) {
            sb.append(gradeName.getName() + " ");
        }
        subtitle.setText(sb.toString());
    }

    private void updateChart() {
        ObservableList<User> users = userListView.getSelectionModel().getSelectedItems();
        GradeName gradeName = GradeListView.getSelectionModel().getSelectedItem();


        DataDAO dataDAO = new DataDAO();
        GradeDAO gradeDAO = new GradeDAO();
        SimpleRegression regression = new SimpleRegression();

        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
        series.setName(gradeName.getName());
        for (User user : users) {
            try {
                Long clickCount = dataDAO.getClickCount(user, gradeName);
                Grade grade = gradeDAO.getGrades(user, gradeName);
                double gradeValue = grade.getValue();
                regression.addData(clickCount, gradeValue);
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<Number, Number>(clickCount, gradeValue);
                series.getData().add(dataPoint);
            } catch (Exception e) {
                log.warn("User {} did not participate in test {} ", user.getMoodleId() , gradeName.getName());
            }
        }
        chart.getYAxis().setLabel("Grade in " + gradeName.getName());
        chart.getXAxis().setLabel("Actions in the Section for the user before last attempt");

        regressionEQ.setText(regression.getIntercept() + "+" + regression.getSlope() + "x");
        r2.setText(String.valueOf(regression.getRSquare()));

        chart.getData().clear();
        chart.getData().add(series);
    }

    @FXML
    public void selectAllUser() {
        userListView.getSelectionModel().selectAll();
        update();
    }
}
