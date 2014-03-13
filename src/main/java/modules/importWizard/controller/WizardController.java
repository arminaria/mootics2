package modules.importWizard.controller;

import controller.MainController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WizardController extends MainController {


    public SplitPane splitPane;
    @FXML
    public Button backBtn;
    @FXML
    public Button forwardBtn;

    public static StackPane step;

    private static int currentPage = 0;
    public Button menu_step_1;
    public Button menu_step_2;
    public Button menu_step_3;

    Logger log = LoggerFactory.getLogger(MainController.class);

    public void changeStep(int page){

        log.info("changeStep "+step);
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

    public void forward(ActionEvent actionEvent) {
        changeStep(currentPage+1);
    }

    public void back(ActionEvent actionEvent) {
        changeStep(currentPage-1);

    }
}
