package modules.importWizard.controller;

import dao.DataDAO;
import dao.MaterialDAO;
import dao.UserDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import model.Data;
import model.User;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.RegexUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class DataImportController implements Initializable{
    public ProgressBar progressBar;

    Logger log = LoggerFactory.getLogger(DataImportController.class);

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
        }
        progressBar.setVisible(true);
        Task worker = createWorker(this.file);
        progressBar.progressProperty().bind(worker.progressProperty());
        new Thread(worker).start();
    }

    public Task createWorker(final File file) {
        return new Task() {
            @Override
            protected Object call() throws IOException, InvalidFormatException {
                UserDAO userDAO = new UserDAO();
                MaterialDAO materialDao = new MaterialDAO();
                DataDAO dataDao = new DataDAO();
                Workbook wb = WorkbookFactory.create(file);
                Sheet sheet = wb.getSheetAt(0);

                int last = sheet.getLastRowNum();
                output.appendText("Found " + last + " Data in the Log");

                for (int i = 0; i < last; i++) {
                    if (i == 0) continue;
                    Row row = sheet.getRow(i);
                    Date date = row.getCell(0).getDateCellValue();
                    int userId = new Double(row.getCell(1).getNumericCellValue()).intValue();
                    String actionCell = row.getCell(2).getStringCellValue();

                    String materialId = RegexUtils.getIdFromString(actionCell);
                    String action = RegexUtils.getActionFrom(actionCell);

                    Data data = new Data();
                    data.setAction(action);
                    data.setDate(date);
                    data.setMaterial(materialDao.find(materialId));

                    User user = userDAO.find(userId);
                    if (user == null) {
                        user = new User();
                        user.setMoodleId(userId);
                        userDAO.save(user);
                        log.info("Saved new User: " + userId + "\n");
                    }

                    data.setUser(user);
                    dataDao.save(data);
                    updateProgress(i, last);
                }

                Platform.runLater(
                        new Runnable() {
                            public void run() {
                                progressBar.setVisible(false);
                                new WizardController().changeStep(3);
                            }
                        }
                );

                return true;
            }

        };

    }



}
