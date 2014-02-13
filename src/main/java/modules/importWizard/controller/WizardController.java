package modules.importWizard.controller;

import controller.MainController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class WizardController extends MainController implements Initializable{


    public SplitPane splitPane;
    @FXML
    public static Button backBtn;
    @FXML
    public static Button forwardBtn;
    @FXML
    public static StackPane step;

    private static int currentPage = 0;
    public Button menu_step_1;
    public Button menu_step_2;
    public Button menu_step_3;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(currentPage==0){
            changeStep(1);
        }
    }

    public void changeStep(int page){
        // change the content
        change(step, getContent("/view/importwizard/step" + page + ".fxml"));
        currentPage = page;

        // Set new Actions to the Buttons
        backBtn.setOnAction(getEventHandler());
        forwardBtn.setOnAction(getEventHandler());

        // set the new states of the buttons
        switch (page){
            case 1:
                backBtn.setDisable(true);
                forwardBtn.setDisable(false);
                break;
            case 3:
                backBtn.setDisable(false);
                forwardBtn.setDisable(true);
                break;
            default:
                backBtn.setDisable(false);
                forwardBtn.setDisable(false);
                break;
        }
    }

    private EventHandler<ActionEvent> getEventHandler(){
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button btn = (Button) actionEvent.getSource();
                if(btn == backBtn && currentPage!=1){
                    changeStep(currentPage-1);
                }
                if(btn == forwardBtn && currentPage!=3){
                    changeStep(currentPage+1);
                }
            }
        };
    }


    public void gotoStep1() {
        changeStep(1);
    }
    public void gotoStep2() {
        changeStep(2);
    }
    public void gotoStep3() {
        changeStep(3);
    }
}
