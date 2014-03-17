package modules.listcellview;

import javafx.scene.control.ListCell;
import model.Matterhorn;
import model.User;

public class ListMatterhorn extends ListCell<Matterhorn> {
    @Override
    protected void updateItem(Matterhorn matterhorn, boolean empty) {
        super.updateItem(matterhorn, empty);
        if(!empty){
            this.setText(matterhorn.getMaterial().getName());
        }
    }
}
