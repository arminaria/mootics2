package modules.matterhorn;

import dao.MatterhornDAO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import model.Matterhorn;
import model.User;
import modules.listcellview.ListMatterhorn;
import modules.listcellview.ListUser;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by JabUser on 17.03.14.
 */
public class MatterhornVerifyController implements Initializable {

    @FXML
    public StackPane pane;
    @FXML
    public ListView<Matterhorn> videoList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MatterhornDAO matterhornDAO = new MatterhornDAO();
        List<Matterhorn> allMatterhorn = matterhornDAO.getAllMatterhorn();

        videoList.setCellFactory(new Callback<ListView<Matterhorn>, ListCell<Matterhorn>>() {
            @Override
            public ListCell<Matterhorn> call(ListView<Matterhorn> userListView) {
                return new ListMatterhorn();
            }
        });
        videoList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Matterhorn>() {
            @Override
            public void changed(ObservableValue observableValue, Matterhorn o, Matterhorn o2) {
                update(o2);
            }
        });
        videoList.getItems().setAll(allMatterhorn);
    }

    private void update(Matterhorn m) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        final BarChart<String, Number> bc =
                new BarChart<String, Number>(xAxis, yAxis);

        XYChart.Series s = new XYChart.Series();
        int size = 0;
        try{
             size = m.getMaterial().getData().size();
        }catch (NullPointerException ignore){}

        s.getData().add(new XYChart.Data("Isis", size));
        s.getData().add(new XYChart.Data("Matterhorn", m.getViews()));

        bc.getData().add(s);

        pane.getChildren().clear();
        pane.getChildren().add(bc);

    }
}
