package pl.swmind.robust.webapp.user.role.validation.impl;

import com.vaadin.ui.AbstractOrderedLayout;
import pl.swmind.robust.webapp.user.role.form.FormRequest;
import pl.swmind.robust.webapp.user.role.validation.Validations;

import java.util.Date;

/**
 * Validations for form fields in role path scenario. <br>
 * <p/>
 * Creation date: 21/11/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public class PathValidations extends Validations {
    public PathValidations(AbstractOrderedLayout mainLayout, Boolean checkIfUserIsNull) {
        super(mainLayout, checkIfUserIsNull);
    }

    public boolean validateRequest(FormRequest formRequest){
        return requestIsWithoutNull(formRequest) && !validateTimeWindow(formRequest);
    }

    private boolean validateTimeWindow(FormRequest formRequest) {
        if(formRequest.getStartDate().after(formRequest.getEndDate())){
            mainLayout.getWindow().showNotification("[Error] start date must be before end date");
            return true;
        }
        if(formRequest.getStartDate().after(new Date()) || formRequest.getEndDate().after(new Date()) ) {
            mainLayout.getWindow().showNotification("[Error] date can't be in future");
            return true;
        }
        return false;
    }

    private boolean requestIsWithoutNull(FormRequest formRequest) {
        if(formRequest == null || formRequest.getStartDate() == null || formRequest.getEndDate() == null
            || formRequest.getUserName() == null)
        {
            mainLayout.getWindow().showNotification("[Error] none of form's elements can be null");
            return false;
        }
        return true;
    }
}
