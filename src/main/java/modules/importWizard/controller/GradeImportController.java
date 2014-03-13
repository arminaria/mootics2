package modules.importWizard.controller;

import controller.MainController;
import dao.GradeDAO;
import dao.UserDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import model.Grade;
import model.GradeName;
import model.User;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GradeImportController implements Initializable {
    public ProgressBar progressBar;

    Logger log = LoggerFactory.getLogger(GradeImportController.class);

    public TextArea output;
    private File file;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressBar.setVisible(false);
        progressBar.progressProperty().unbind();
    }

    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ExcelFile", "*.xls", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            this.file = file;
            output.appendText("File choosen: " + this.file.getAbsolutePath() + "\n");
            progressBar.setVisible(true);
            Task worker = createWorker(this.file);
            progressBar.progressProperty().bind(worker.progressProperty());

            new Thread(createWorker(this.file)).start();
        }
    }


    public Task createWorker(final File file) {
        return new Task() {
            @Override
            protected Object call() throws IOException, InvalidFormatException {

                try {
                    UserDAO userDAO = new UserDAO();
                    GradeDAO gradeDAO = new GradeDAO();

                    Workbook wb = null;
                    try {
                        wb = WorkbookFactory.create(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidFormatException e) {
                        e.printStackTrace();
                    }
                    assert wb != null;
                    Sheet sheet = wb.getSheetAt(0);

                    Row firstRow = sheet.getRow(0);
                    int lastRow = sheet.getLastRowNum();
                    int lastCell = new Integer(firstRow.getLastCellNum()) - 1;

                    for (int row = 1; row <= lastRow; row++) {
                        for (int column = 1; column <= lastCell; column++) {
                            Cell currentCell = sheet.getRow(row).getCell(column);
                            if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                Integer userId = new Double(sheet.getRow(row).getCell(0).getNumericCellValue()).intValue();
                                String name = sheet.getRow(0).getCell(column).getStringCellValue();
                                double value = currentCell.getNumericCellValue();
                                log.info("userid: {}, {}:" + value, userId, name);

                                Grade grade = new Grade();

                                GradeName gradeName = new GradeName();
                                gradeName.setName(name);
                                gradeDAO.save(gradeName);

                                grade.setName(gradeDAO.findGradeName(gradeName));

                                grade.setValue(value);
                                User user = userDAO.find(userId);
                                if (user == null) {
                                    user = new User();
                                    user.setMoodleId(userId);
                                    userDAO.save(user);
                                }
                                grade.setUser(user);
                                gradeDAO.save(grade);
                            }
                            updateProgress(row*column, lastRow * lastCell);
                        }
                    }


                    Platform.runLater(
                            new Runnable() {
                                public void run() {
                                    progressBar.setVisible(false);
                                }
                            }
                    );
                } catch (Exception e) {
                    log.error("Could not import Grade", e);
                    System.exit(1);
                }

                return true;
            }

        };

    }
    public void gotoImportMaterials(ActionEvent actionEvent) {
        new MainController().changeMain("/view/importwizard/step1.fxml");
    }

    public void gotoImportLogs(ActionEvent actionEvent) {
        new MainController().changeMain("/view/importwizard/step2.fxml");
    }

}
