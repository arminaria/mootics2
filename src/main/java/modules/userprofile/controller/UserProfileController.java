package modules.userprofile.controller;

import dao.DataDAO;
import dao.MaterialDAO;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.Data;
import model.Material;
import model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserProfileController {
    public static User currentUser;

    public static Text userIdField;
    public Text text = new Text("");
    @FXML
    public static Pane dataPane;


    public void init() {
        DataDAO dataDAO = new DataDAO();
        MaterialDAO materialDAO = new MaterialDAO();

        List<String> categories = materialDAO.getCategories();
        List<Data> dataForUser = dataDAO.getDataForUser(currentUser);
        Calendar c = Calendar.getInstance();
        c.set(2013,Calendar.DECEMBER,17,0,0);
        Date startDate = c.getTime();
        c.setTime(startDate);
        c.add(Calendar.DATE, 7);
        Date endDate = c.getTime();
        dataPane.getChildren().clear();

        dataPane.getChildren().add(text);
        Canvas nodes = createNodes(categories, getFlow(dataForUser, startDate, endDate));
        dataPane.getChildren().add(nodes);
    }

    private List<Material> getFlow(List<Data> data, Date start, Date end) {
        ArrayList<Material> materials = new ArrayList<Material>();
        for (Data d : data) {
            Date date = d.getDate();
            if (date.after(start) && date.before(end)) {
                materials.add(d.getMaterial());
            } else if(date.compareTo(start)==0 || date.compareTo(end)==0){
                materials.add(d.getMaterial());
            }
        }
        return materials;
    }


    private Canvas createNodes(List<String> nodes, List<Material> flow) {

        Canvas canvas = new Canvas(500, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        int x0 = 10;
        int x = 10;
        int y0 = 10;
        int y = 10;

        int w = 150;
        int h = 80;

        int offset = 20;


        for (String node : nodes) {
            gc.setFill(Color.GREEN);
            gc.fillRoundRect(x, y, w, h, 10, 10);
            gc.setFill(Color.BLACK);
            gc.fillText(node, x + 10, y + 20);
//            gc.fillText("x=" + x + " y=" + y, x + 10, y + 20);
            y = y + h + offset;
        }

        int lineoffset = 10;
        for (int i = 0; i < flow.size() - 1; i++) {
            Material from = flow.get(i);
            Material to = flow.get(i + 1);
            System.out.println(from.getCategory() + " -> " + to.getCategory());
            text.setText(text.getText() + "\n" + from.getCategory() + " -> " + to.getCategory());
            if(from.getCategory().equals(to.getCategory())){
                continue;
            }

            int indexFrom = nodes.indexOf(from.getCategory());
            int indexTo = nodes.indexOf(to.getCategory());

            int startY = ((indexFrom) * (h + offset)) + y0 + lineoffset * i + 5;
            int startX = x0 + w;

            int EndY = ((indexTo) * (h + offset)) + y0 + lineoffset * i + 5;
            int EndX = x0 + w;

            int _x1 = startX + 30 + i * lineoffset;

            // Horizontal
            gc.strokeLine(startX, startY, _x1, startY);
            // Vertical
            gc.strokeLine(_x1, startY, _x1, EndY);
            // Horizontal
            gc.strokeLine(_x1, EndY, EndX, EndY);

            gc.fillOval(EndX - 5 / 2, EndY - 5 / 2, 5, 5);

        }

        return canvas;
    }
}
