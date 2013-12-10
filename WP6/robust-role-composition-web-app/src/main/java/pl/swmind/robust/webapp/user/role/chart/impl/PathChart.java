package pl.swmind.robust.webapp.user.role.chart.impl;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.swmind.robust.webapp.user.role.chart.LineChart;
import pl.swmind.robust.webapp.user.role.dto.CoverageEntry;
import pl.swmind.robust.webapp.user.role.form.PathFieldFactory;
import pl.swmind.robust.ws.behaviouranalysis.RobustBehaviourServiceException_Exception;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static pl.swmind.robust.webapp.user.role.form.FormFields.*;

/**
  * Role path timeline chart within requested time window. <br>
  * <p/>
  * Creation date: 11/09/12<br>
  *
  * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
  */
@Component
@Scope("prototype")
public class PathChart extends LineChart {
    private static final Logger logger = Logger.getLogger(PathChart.class);
    private static final String caption = "Choose user for his/her role path";
    private static final String description = "Role path timeline";

    public void initChart(){
        init();
        String[] roles = new String[]{"Unmatched","Inactive","Lurker","Contributor","Super User","Follower","BroadCaster",
            "Daily User","Leader","Celebrity"};
        List<String> userList = getUsers();

        setStyleName(styleNameLocal);
        setDescription(description);
        initFieldFactory(userList);
        form.setFormFieldFactory(fieldFactory);

        form.setVisibleItemProperties(
            Arrays.asList(new String[]{ USER.getFieldName(), START_DATE.getFieldName(), END_DATE.getFieldName()})
        );

        formLayout.removeComponent(form);

        HorizontalLayout formWithLegend = new HorizontalLayout();
        Table table = new Table("Roles legend.");
        table.setStyleName(styleNameLocal);
        table.setHeight("223px");
        table.setSelectable(true);
        table.setImmediate(true);
        table.addContainerProperty("ID", Integer.class, null);
        table.addContainerProperty("Name", String.class, null);

        for(int i = 0; i < roles.length; i++){
            table.addItem(new Object[] { i, roles[i]}, i);
        }

        formWithLegend.addComponent(form);
        Label spacer = new Label("");
        spacer.setWidth("1em");
        formWithLegend.addComponent(spacer);
        formWithLegend.addComponent(table);
        formWithLegend.setStyleName(styleNameLocal);
        formLayout.addComponent(formWithLegend);
    }

    private void initFieldFactory(List<String> userList) {
        try {
            this.fieldFactory = new PathFieldFactory(userList);
        } catch (Exception e) {
            getWindow().showNotification("[Error] Problem with Form creation");
            logger.error(e.getMessage());
        }
    }

    private List<String> getUsers() {
        List<String> userList = new LinkedList<String>();
        try {
            userList = behaviourAnalysisClient.getMostValuableUsers(platformId, communityId);
        } catch (RobustBehaviourServiceException_Exception e) {
            getWindow().showNotification("[Error] Users could not be got from Web Service.");
            logger.error(e.getMessage());
        }
        return userList;
    }

    @Override
    protected void handleRequest() {
        logger.info("Filling chart for " + formRequest.getUserName() + " in:" + formRequest.getStartDate() + ", " + formRequest.getEndDate());

        CoverageEntry coverageEntry = new CoverageEntry();
        try {
            coverageEntry = behaviourAnalysisClient.getRolePath(platformId, communityId,formRequest.getUserName(),formRequest.getStartDate(), formRequest.getEndDate());
        } catch (RobustBehaviourServiceException_Exception e) {
            getWindow().showNotification("[Error] User role path could not be got from Web Service.");
            logger.error(e.getMessage());
        }

        logger.info("Got : " + coverageEntry.getTimeline().size() + " data");

        handle(coverageEntry, formRequest.getUserName());

        logger.info("Chart filled.");
    }
}