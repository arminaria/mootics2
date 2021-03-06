import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mootics extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    Logger log = LoggerFactory.getLogger(Mootics.class);

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(this.getClass().getResource("/view/"));
        BorderPane root =  (BorderPane) loader.load(this.getClass().getResourceAsStream("/view/main.fxml"));
        log.info("Starting ");
        Scene scene = new Scene(root, 800, 600);

        MainController.main = new StackPane();
        root.setCenter(MainController.main);
        scene.getStylesheets().add("/style/style.css");
        stage.setScene(scene);
        stage.setTitle("Mootics");
        stage.show();
        new MainController().gotoEndSurvey();
    }
}
