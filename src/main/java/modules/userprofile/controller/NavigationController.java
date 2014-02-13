package modules.userprofile.controller;

import controller.MainController;
import javafx.event.ActionEvent;

public class NavigationController extends MainController{


    public void gotoGraphs(ActionEvent actionEvent) {
        change(UserProfileController.dataPane,getContent("/view/userprofile/test.fxml"));
    }
}
