package modules.listcellview;

import javafx.scene.control.ListCell;
import model.GradeName;
import model.User;

/**
 * Created by ara on 24.02.14.
 */
public class ListGradeName extends ListCell<GradeName> {
    @Override
    protected void updateItem(GradeName gradeName, boolean empty) {
        super.updateItem(gradeName, empty);
        if(!empty){
            String name = gradeName.getName();
            String[] split = name.split(" ");
            if (name.contains("One Try Only!")) {
                this.setText(name.replace("(One Try Only!)", ""));
            } else if (split.length >= 3) {
                String s = split[split.length - 1];
                String s1 = split[split.length - 2];
                String s2 = split[split.length - 3];
                this.setText(s2 + " " + s1 + " " + s);
            } else {
                this.setText(name);
            }
        }
    }
}
