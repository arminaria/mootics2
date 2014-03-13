package modules.correlation;

import dao.GradeDAO;
import dao.MaterialDAO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import model.*;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;

/**
 * Created by J(ab)DK on 10.03.14.
 */
public class CorrelationController implements Initializable{

    public ListView<String> categoryList;
    public ListView<GradeName> gradesList;
    public Text placeHolder;
    public HBox chartBox;
    public GridPane correlationPane;
    private ScatterChart<Number,Number> chart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        gradesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // fill out lists
        MaterialDAO materialDAO = new MaterialDAO();
        GradeDAO gradeDAO = new GradeDAO();
        List<String> categories = materialDAO.getCategories();
        categoryList.getItems().addAll(categories);
        List<GradeName> gradeNames = gradeDAO.getAllAvailableGradeNames();
        gradesList.getItems().addAll(gradeNames);

        gradesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        categoryList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();
        chart = new ScatterChart<Number, Number>(x,y);

    }

    public void showCorrelation(ActionEvent actionEvent) {
        ObservableList categories = categoryList.getSelectionModel().getSelectedItems();
        ObservableList<GradeName> grades = gradesList.getSelectionModel().getSelectedItems();

        final SimpleRegression regression = new SimpleRegression();

        MaterialDAO materialDAO = new MaterialDAO();
        List<Material> materials = materialDAO.getAllMaterialForCategories(categories);
        List<Data> data = new ArrayList<Data>();
        for (Material material : materials) {
            data.addAll(material.getData());
        }

        chart.getXAxis().setLabel("Actions for the selected Categories");
        chart.getYAxis().setLabel("Mean Grade in the selected Grades");

        Map<User,Integer> userData = new HashMap<User, Integer>();
        for (Data d : data) {
            Integer count = userData.get(d.getUser());
            if(count == null){
                userData.put(d.getUser(),1);
            }else{
                count++;
                userData.put(d.getUser(),count);
            }
        }

        XYChart.Series<Number, Number> s = new XYChart.Series<Number, Number>();
        for (User user : userData.keySet()) {
            List<Grade> userGrades = user.getGrades();
            int g = 0;
            int gCount = 0;
            for (Grade userGrade : userGrades) {
                for (GradeName grade : grades) {
                    if(userGrade.getName().equals(grade)){
                        gCount++;
                        g += userGrade.getValue();
                    }

                }
            }
            if(gCount!=0) {
                g /= gCount;
            }
            XYChart.Data<Number, Number> dataPoint = new XYChart.Data<Number, Number>(userData.get(user), g);
            regression.addData(userData.get(user), g);
            s.getData().add(dataPoint);
        }

        chart.getData().clear();
        chart.getData().add(s);

        placeHolder.setVisible(false);

        chartBox.getChildren().clear();
        chartBox.getChildren().add(chart);


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        grid.add(new Text("Correlation Coefficient"),0,0);
        grid.add(new Text(String.valueOf(regression.getR())),1,0);

        grid.add(new Text("Regression Line "),0,1);
        BigDecimal a = new BigDecimal(regression.getIntercept());
        a = a.setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal b = new BigDecimal(regression.getSlope());
        b = b.setScale(2, RoundingMode.HALF_DOWN);
        grid.add(new Text(a.toPlainString() + " + " + b.toPlainString() +" x"),1,1);

        grid.add(new Text("Predict for "),0,2);
        final TextField predictField = new TextField();
        grid.add(predictField,1,2);
        Button predictBtn = new Button("Predict Grade");
        final Text predicted = new Text();
        grid.add(predicted,3,2);
        predictBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String predictedValue = String.valueOf(regression.predict(Integer.valueOf(predictField.getText())));
                predicted.setText(predictedValue);
            }
        });
        grid.add(predictBtn,2,2);

        correlationPane.getChildren().clear();
        correlationPane.getChildren().add(grid);


    }

}
