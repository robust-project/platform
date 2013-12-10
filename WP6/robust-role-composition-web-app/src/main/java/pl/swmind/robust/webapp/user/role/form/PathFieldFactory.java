package pl.swmind.robust.webapp.user.role.form;

import com.vaadin.data.Item;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

import java.util.List;

import static pl.swmind.robust.webapp.user.role.form.FormFields.USER;

/**
 * Factory for form fields in role path scenario. <br>
 * <p/>
 * Creation date: 21/11/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public class PathFieldFactory extends FieldFactory {
    private final ComboBox usersBox = new ComboBox("User");

    public PathFieldFactory(List<String> userList) {
        for(String user: userList){
            usersBox.addItem(user);
        }
        initBox(usersBox);
    }

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
        if (USER.getFieldName().equals(propertyId)) {
            return usersBox;
        } else {
            return super.createField(item,propertyId,uiContext);
        }
    }
}

