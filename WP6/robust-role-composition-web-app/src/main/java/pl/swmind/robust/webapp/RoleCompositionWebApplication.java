package pl.swmind.robust.webapp;

import com.vaadin.Application;
import com.vaadin.ui.Window;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.Serializable;
import java.util.Properties;


/**
 * Application for local deployment and testing. <br>
 * <p/>
 * Creation date: 21/11/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public class RoleCompositionWebApplication extends Application implements Serializable {

    @Override
    public void init() {
        Window window = new Window();
        setMainWindow(window);

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/role-composition-context.xml");
        TabLayout tabLayout = context.getBean(TabLayout.class);

        Properties props = (Properties) context.getBean("localProps");
        String platformId = props.getProperty("platformId");
        String communityId = props.getProperty("communityId");
        String styleName = props.getProperty("styleName");

        tabLayout.init(platformId, communityId,styleName);

        String theme = props.getProperty("theme");
        setTheme(theme);

        window.setContent(tabLayout);
    }
}



































