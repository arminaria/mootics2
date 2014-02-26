package modules.listcellview;

import javafx.scene.control.ListCell;
import model.User;

public class ListUser extends ListCell<User> {
    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        if(!empty){
            this.setText(user.getMoodleId().toString());
        }
    }
}
