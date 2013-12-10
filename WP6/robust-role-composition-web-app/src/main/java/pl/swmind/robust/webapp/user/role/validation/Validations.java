package pl.swmind.robust.webapp.user.role.validation;


import com.vaadin.ui.AbstractOrderedLayout;
import pl.swmind.robust.webapp.user.role.form.FormRequest;

/**
 * Form validations base. <br>
 * <p/>
 * Creation date: 11/09/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
// TODO can be changed to Vaadin validations - check
// TODO check also required fields
public abstract class Validations {
    protected AbstractOrderedLayout mainLayout;
    protected Boolean chartStarted = false;
    protected Boolean validateWithUser = false;

    public Validations(AbstractOrderedLayout mainLayout, Boolean checkIfUserIsNull) {
        this.mainLayout = mainLayout;
        this.validateWithUser = checkIfUserIsNull;
    }

    public boolean isChartLoaded() {
        if(!chartStarted){
            chartStarted = true;
            return false;
        }
        return true;
    }

    abstract public boolean validateRequest(FormRequest formRequest);
}
