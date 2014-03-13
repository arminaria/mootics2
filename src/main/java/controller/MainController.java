package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

public class MainController {
    public static StackPane main;

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

    public void changeMain(String mainFxml) {
        change(main, getContent(mainFxml));
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
    }

    public void gotoImportWizard() {
        changeMain("/view/importwizard/importMain.fxml");
    }

    public void gotoCollector() {
        changeMain("/view/collector.fxml");
    }

    public void gotoBackUp() {
        changeMain("/view/backup/backupMain.fxml");
    }

    public void gotoUserProfile() {
        changeMain("/view/userprofile/main.fxml");
    }

    public void gotoStatistics() {
        changeMain("/view/statistik/main.fxml");
    }

    public void gotoWeeklyUsage() {
        changeMain("/view/weeklyusage/main.fxml");
    }

    public void gotoMonthlyUsage() {
        changeMain("/view/monthlyusage/main.fxml");
    }

    public void gotoCorrelation() {
        changeMain("/view/correlation/main.fxml");
    }
}
