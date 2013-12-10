package pl.swmind.robust.webapp.user.role.validation.impl;

import com.vaadin.ui.AbstractOrderedLayout;
import pl.swmind.robust.webapp.user.role.form.FormRequest;
import pl.swmind.robust.webapp.user.role.validation.Validations;

import java.util.Date;

/**
 * Validations for role coverage forms <br>
 * <p/>
 * Creation date: 21/11/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public class CoverageValidations extends Validations {
    public CoverageValidations(AbstractOrderedLayout mainLayout, Boolean checkIfUserIsNull) {
        super(mainLayout, checkIfUserIsNull);
    }

    public boolean validateRequest(FormRequest formRequest){
        return requestIsWithoutNull(formRequest) && !dateIsInFuture(formRequest);
    }

    private boolean dateIsInFuture(FormRequest formRequest) {
        if(formRequest.getDate().compareTo(new Date()) > 0){
            mainLayout.getWindow().showNotification("[Error] date can not be in the future");
            return true;
        }
        return false;
    }

    private boolean requestIsWithoutNull(FormRequest formRequest) {
        if(formRequest == null || formRequest.getDate() == null){
            mainLayout.getWindow().showNotification("[Error] none of form's elements can be null");
            return false;
        }
        return true;
    }
}
