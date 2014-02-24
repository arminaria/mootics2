package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

public class MainController {
    @FXML
    public static Pane left;
    @FXML
    public static Pane main;

    private static Logger log = LoggerFactory.getLogger(MainController.class);

    protected Parent getContent(String mainFxml) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL resource = this.getClass().getResource(mainFxml);
            if(resource.getProtocol().equals("file")){
                URL url = new File(resource.getFile()).getParentFile().toURI().toURL();
                loader.setLocation(url);
            }else{
                loader.setLocation(this.getClass().getResource("/view/"));
            }
            return (Parent) loader.load(this.getClass().getResourceAsStream(mainFxml));
        } catch (Exception e) {
            log.error("could not load:{}", mainFxml);
            throw new RuntimeException(e);
        }
    }

    private void hideLeft(){
        left.setPrefSize(0,0);
    }

    private void showLeft(){
        left.setPrefSize(100,100);
    }

    public void changeMain(String mainFxml) {
        change(main, getContent(mainFxml));
    }

    public void changeLeft(String fxml) {
        change(left, getContent(fxml));
    }

    protected void change(Pane pane, Parent content) {
        pane.getChildren().clear();
        pane.getChildren().add(content);
    }


    public void gotoTest() {
        changeMain("/view/test.fxml");
    }

    public void gotoTest2() {
        changeMain("/view/test2.fxml");
        showLeft();
    }

    public void gotoImportWizard() {
        changeMain("/view/importwizard/importMain.fxml");
        hideLeft();
    }

    public void gotoCollector() {
        changeMain("/view/collector.fxml");
        hideLeft();
    }

    public void gotoBackUp() {
        changeMain("/view/backup/backupMain.fxml");
        hideLeft();
    }

    public void gotoUserProfile() {
        changeMain("/view/userprofile/main.fxml");
        changeLeft("/view/userprofile/left.fxml");
    }

    public void gotoStatistics() {
        changeMain("/view/statistik/main.fxml");
        hideLeft();
    }
}
