package modules.userprofile.controller;

import dao.UserDAO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import model.User;
import modules.listcellview.ListUser;
import org.apache.poi.ss.formula.functions.T;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserListController implements Initializable{
    public ListView<User> userList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // fill the list with users
        List<User> allUser = new UserDAO().getAllUser();
        for (User user : allUser) {
            userList.getItems().add(user);
        }
        userList.getSelectionModel().selectFirst();
        updateUserProfileController(allUser.get(0));

        // Set how to view the users in the list
        Callback<ListView<User>, ListCell<User>> cellFactory = new Callback<ListView<User>, ListCell<User>>() {
            @Override public ListCell<User> call(ListView<User> listView) {
                return new ListUser();
            }
        };
        userList.setCellFactory(cellFactory);

        // what should happen if a item is selected
        userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue observableValue, User o, User o2) {
                updateUserProfileController(o2);
            }
        });

        new UserProfileController().init();
    }

    private void updateUserProfileController(User u){
        UserProfileController.currentUser =u;
        UserProfileController.userIdField.setText(u.getMoodleId().toString());
        new UserProfileController().init();
    }

    public void test(ListView.EditEvent<T> tEditEvent) {
        System.out.println("tests");
    }
}
