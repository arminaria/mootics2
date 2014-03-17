package modules.matterhorn;

import dao.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import model.Data;
import model.Grade;
import model.Matterhorn;
import model.User;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MatterhornController implements Initializable {

    @FXML
    public StackPane pane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MatterhornDAO matterhornDAO = new MatterhornDAO();
        /*matterhornDAO.save(3, 169, 31020, 184);
        matterhornDAO.save(4, 102, 10271, 101);
        matterhornDAO.save(22, 84, 42119, 501);
        matterhornDAO.save(34, 115, 45053, 392);
        matterhornDAO.save(35, 80, 35940, 449);
        matterhornDAO.save(43, 91, 103332, 1136);
        matterhornDAO.save(56, 105, 150092, 1429);
        matterhornDAO.save(67, 130, 101696, 782);
        matterhornDAO.save(80, 85, 104872, 1234);
        matterhornDAO.save(92, 39, 79716, 2044);
        matterhornDAO.save(105, 47, 37631, 801);
        matterhornDAO.save(113, 63, 72548, 1152);
        matterhornDAO.save(123, 44, 37482, 852);
        matterhornDAO.save(128, 30, 20673, 689);
        matterhornDAO.save(130, 64, 63615, 994);*/

        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();
        y.setLabel("Grade in Exam");
        x.setLabel("Weighted values of watching videos");
        ScatterChart<Number, Number> chart = new ScatterChart<Number, Number>(x, y);

        XYChart.Series<Number, Number> s = new XYChart.Series<Number, Number>();
        List<Matterhorn> allMatterhorn = matterhornDAO.getAllMatterhorn();
        UserDAO userDAO = new UserDAO();
        DataDAO dataDAO = new DataDAO();
        GradeDAO gradeDAO = new GradeDAO();
        List<User> allUser = userDAO.getAllUser();
        final SimpleRegression regression = new SimpleRegression();

        for (User user : allUser) {
            int weight = 0;
            for (Matterhorn matterhorn : allMatterhorn) {
                List<Data> videoData = dataDAO.getVideoData(user, matterhorn);
                weight += videoData.size() * matterhorn.getAverageWatch();
                try {
                    Grade grade = gradeDAO.getGrades(user, gradeDAO.findGradeName("HotPot: Midterm Exam (One Try Only!)"));
                    XYChart.Data<Number, Number> dataPoint = new XYChart.Data<Number, Number>(weight, grade.getValue());
                    s.getData().add(dataPoint);
                    regression.addData(weight, grade.getValue());
                } catch (Exception e) {
                    continue;
                }
            }
        }

        chart.getData().add(s);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(chart);

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

        borderPane.setBottom(grid);

        pane.getChildren().add(borderPane);
    }


}
