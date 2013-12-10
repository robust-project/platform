package pl.swmind.robust.webapp.integration;

import com.vaadin.ui.Window;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pl.swmind.robust.webapp.TabLayout;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.SimpleView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFView;

import java.io.Serializable;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 17/10/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public class RoleCompositionView extends SimpleView implements Serializable, IUFView {
    private TabLayout layout;
    private final ClassPathXmlApplicationContext context;

    public RoleCompositionView(){
        context = new ClassPathXmlApplicationContext("role-composition-proxy-context.xml");
    }

    @Override
    public Object getImplContainer() {
        return layout;
    }

    @Override
    public boolean isVisible() {
        return layout.isVisible();
    }

    @Override
    public void setVisible(boolean visible) {
        layout.setVisible(visible);
    }

    @Override
    public void displayMessage(String title, String content){
        layout.getWindow().showNotification(title, content, Window.Notification.TYPE_HUMANIZED_MESSAGE);
    }

    @Override
    public void displayWarning(String title, String content){
        layout.getWindow().showNotification(title, content, Window.Notification.TYPE_WARNING_MESSAGE);
    }

    @Override
    public void updateView() {
    }

    public void updateView(String platformId, String communityId, String styleName) {
        layout = context.getBean(TabLayout.class);
        layout.init(platformId,communityId,styleName);
    }
}
