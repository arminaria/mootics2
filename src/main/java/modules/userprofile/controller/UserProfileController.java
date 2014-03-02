package modules.userprofile.controller;

import dao.DataDAO;
import dao.MaterialDAO;
import dao.UserDAO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
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

    private static Map<User,List<List<Data>>> userSessionList = new HashMap<User, List<List<Data>>>();
    public ToolBar subButtons;

    Logger log = LoggerFactory.getLogger(UserProfileController.class);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        userList.getSelectionModel().select(0);


    }

    private void update() {
        title.setText(currentUser.getMoodleId().toString());
    }

    public void showUserGraph(ActionEvent actionEvent) {

        List<Data> data = currentUser.getData();
        System.out.println(data.size());
        final List<List<Data>> lists = splitDataBySession(data, new ArrayList<List<Data>>());
        userSessionList.put(currentUser,lists);

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



}
