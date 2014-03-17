package modules.endsurvey;

import dao.MaterialDAO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import model.Matterhorn;
import modules.listcellview.ListMatterhorn;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by JabUser on 17.03.14.
 */
public class EndSurvey implements Initializable{

    public StackPane pane;
    public ListView<String> categoryList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MaterialDAO mDAO = new MaterialDAO();
        List<String> categories = mDAO.getCategories();
        categories.remove("others");
        categoryList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observableValue, String o, String o2) {
                update(o2);
            }
        });

        categoryList.getItems().setAll(categories);
    }

    private void update(String o2) {
        String category = categoryList.getSelectionModel().getSelectedItem();
        String[] antwort = {""};
    }
}
