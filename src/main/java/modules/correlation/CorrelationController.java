package modules.correlation;

import dao.GradeDAO;
import dao.MaterialDAO;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import model.GradeName;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by J(ab)DK on 10.03.14.
 */
public class CorrelationController implements Initializable{

    public ListView categoryList;
    public ListView gradesList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // fill out lists
        MaterialDAO materialDAO = new MaterialDAO();
        GradeDAO gradeDAO = new GradeDAO();
        List<String> categories = materialDAO.getCategories();
        categoryList.getItems().addAll(categories);
        List<GradeName> gradeNames = gradeDAO.getAllAvailableGradeNames();
        gradesList.getItems().addAll(gradeNames);

        gradesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        categoryList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


    }
}
