package pl.swmind.robust.webapp.user.role.form;

import com.vaadin.data.Item;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;

/**
 * Factory for form fields. <br>
 * <p/>
 * Creation date: 17/10/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 *
 */
public class FieldFactory extends DefaultFieldFactory {
    protected void initBox(ComboBox box) {
        box.setImmediate(true);
        box.setNullSelectionAllowed(false);
    }

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
        return super.createField(item, propertyId, uiContext);
    }
}
