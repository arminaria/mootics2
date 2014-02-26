package modules.importWizard.controller;

import controller.DBController;
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
import model.Material;
import model.User;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CategoryUtil;
import utils.RegexUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
            progressBar.setVisible(true);
            Task worker = createWorker(this.file);
            progressBar.progressProperty().bind(worker.progressProperty());
            new Thread(worker).start();
        }
    }

    public Task createWorker(final File file) {
        return new Task() {
            @Override
            protected Object call() {
                try {
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
                        String infoCell = "";
                        if(row.getCell(3).getCellType() == Cell.CELL_TYPE_STRING){
                            row.getCell(3).getStringCellValue();
                        }
                        String materialId = RegexUtils.getIdFromString(actionCell);
                        String action = RegexUtils.getActionFrom(actionCell);
                        String url = RegexUtils.getUrlFrom(actionCell);

                        Data data = new Data();
                        data.setAction(action);
                        data.setDate(date);

                        User user = userDAO.find(userId);
                        if (user == null) {
                            user = new User();
                            user.setMoodleId(userId);
                            userDAO.save(user);
                            log.info("Saved new User: " + userId + "\n");
                        }

                        if(dataDao.dataInDb(data)) {
                            continue;
                        }

                        try {
                            Material material = materialDao.find(materialId);
                            if(material == null && materialId != null){
                                material = new Material();
                                material.setCategory(CategoryUtil.getCategory(action));
                                material.setMaterialId(materialId);
                                material.setName(infoCell);
                                material.setUrl(url);
                                materialDao.save(material);
                            }

                            data.setMaterial(material);
                        } catch (Exception e) {
                            log.warn("Could not retrieve Material from the database: " + materialId +"\n" +
                                    "actionCell: " + actionCell);
                            continue;
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


                }catch (Exception e) {
                    log.error("failed to import data " , e);
                    System.exit(1);
                }

                return true;
            }

        };

    }



}
