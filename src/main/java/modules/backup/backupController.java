package modules.backup;

import com.google.common.io.Files;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class backupController implements Initializable {


    public static final String PREFIX = ".db.backup_";
    public static final String DBNAME = ".db.h2.db";
    public ListView list;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File root = new File(".");
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.getName().contains(PREFIX)) {
                String time = file.getName().split(PREFIX)[1];
                addToList(time);
            }
        }

        ObservableList items = list.getItems();
        for (Object item : items) {
            System.out.println(item);
        }
    }

    private void addToList(String time){
        long l = Long.parseLong(time);
        String format = sdf.format(l);
        list.getItems().add(format);
    }

    private long getTimeFrom(String format){
        try {
           return sdf.parse(format).getTime();
        } catch (ParseException ignore) {
            throw new RuntimeException(ignore);
        }
    }

    public void createBackup() throws IOException {
        File dbFile = new File(DBNAME);
        long l = System.currentTimeMillis();
        File toDb = new File(PREFIX + l);
        if(dbFile.exists()){
            Files.copy(dbFile, toDb);
            addToList(String.valueOf(l));
        }
    }

    public void restore() throws IOException {
        String selectedItem = (String) list.getSelectionModel().getSelectedItem();
        if(!selectedItem.isEmpty()){
            list.getItems().remove(selectedItem);
            String name = String.valueOf(getTimeFrom(selectedItem));
            File toRestore = new File(PREFIX + name);
            File toDelete = new File(DBNAME);
            toDelete.delete();
            Files.copy(toRestore, toDelete);
            toRestore.delete();
        }
    }
}
