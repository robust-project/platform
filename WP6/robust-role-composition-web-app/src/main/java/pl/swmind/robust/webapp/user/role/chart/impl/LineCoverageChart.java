package pl.swmind.robust.webapp.user.role.chart.impl;

import com.vaadin.ui.Alignment;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.vaadin.vaadinvisualizations.AnnotatedTimeLine;
import org.vaadin.vaadinvisualizations.AnnotatedTimeLineEntry;
import pl.swmind.robust.webapp.user.role.chart.LineChart;
import pl.swmind.robust.webapp.user.role.dto.CoverageEntry;
import pl.swmind.robust.webapp.user.role.form.FieldFactory;
import pl.swmind.robust.ws.behaviouranalysis.RobustBehaviourServiceException_Exception;

import java.util.*;

import static java.lang.String.format;
import static pl.swmind.robust.webapp.user.role.form.FormFields.END_DATE;
import static pl.swmind.robust.webapp.user.role.form.FormFields.START_DATE;

/**
 * Role coverage on line chart. Presents all roles with their corresponding coverage percent in time. <br>
 * <p/>
 * Creation date: 21/11/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
@Component
@Scope("prototype")
public class LineCoverageChart extends LineChart {
    private static final Logger logger = Logger.getLogger(LineCoverageChart.class);
    private static final String caption = "Choose time window for role coverage timeline";
    private static final String description = "Role coverage timeline";

    public void initChart(){
        init();
        try {
            this.fieldFactory = new FieldFactory();
        } catch (Exception e) {
            getWindow().showNotification("[Error] Problem with Form creation");
            logger.error(e.getMessage());
        }

        setDescription(description);
        form.setCaption(caption);
        setStyleName(styleNameLocal);
        form.setFormFieldFactory(fieldFactory);

        form.setVisibleItemProperties(
            Arrays.asList(new String[]{START_DATE.getFieldName(), END_DATE.getFieldName()})
        );
    }

    @Override
    protected void handleRequest() {
        logger.info("Filling chart for " + formRequest.getStartDate() + ", " + formRequest.getEndDate());

        List<CoverageEntry> compositions = new LinkedList<CoverageEntry>();
        try {
            compositions = behaviourAnalysisClient.getCompositions(platformId, communityId, formRequest.getStartDate(), formRequest.getEndDate());
        } catch (RobustBehaviourServiceException_Exception e) {
            getWindow().showNotification("[Error] Compositions could not be got from Web Service.");
            logger.error(e.getMessage());
        }

        logger.info("Got : " + compositions.size() + " data");

        if(validations.isChartLoaded()){
            removeComponent(timeline);
        }
        timeline = new AnnotatedTimeLine();
        timeline.setOption("displayAnnotations", true);
        timeline.setOption("wmode", "transparent");
        timeline.setOption("legendPosition", "newRow");

        timeline.setStyleName(styleNameLocal);
        timeline.setHeight("100%");
        timeline.setWidth("100%");

        addComponent(timeline);
        setComponentAlignment(timeline, Alignment.TOP_LEFT);
        setExpandRatio(timeline,0.6f);

        Map<Calendar, ArrayList<AnnotatedTimeLineEntry>> finalTimeline = new HashMap<Calendar, ArrayList<AnnotatedTimeLineEntry>>();

        for(CoverageEntry coverageEntry : compositions){
            logger.info(format("Got %d entries for %s",coverageEntry.getTimeline().size(),coverageEntry.getRoleLabel()));
            timeline.addLineLabel(coverageEntry.getRoleLabel());

            for(Map.Entry<Calendar, ArrayList<AnnotatedTimeLineEntry>> timelineEntry: coverageEntry.getTimeline().entrySet()){
                Calendar timelineCalendar = timelineEntry.getKey();

                if(finalTimeline.containsKey(timelineCalendar)){
                    ArrayList<AnnotatedTimeLineEntry> finalEntryTimeline = finalTimeline.get(timelineCalendar);
                    if(finalEntryTimeline == null){
                        finalEntryTimeline = new ArrayList<AnnotatedTimeLineEntry>();
                        finalEntryTimeline.addAll(timelineEntry.getValue());
                        finalTimeline.put(timelineCalendar,finalEntryTimeline);
                    }else{
                        finalEntryTimeline.addAll(timelineEntry.getValue());
                    }
                }else{
                    ArrayList<AnnotatedTimeLineEntry> finalEntryTimeline = new ArrayList<AnnotatedTimeLineEntry>();
                    finalEntryTimeline.addAll(timelineEntry.getValue());
                    finalTimeline.put(timelineCalendar,finalEntryTimeline);
                }
            }
        }

        for(Map.Entry<Calendar, ArrayList<AnnotatedTimeLineEntry>> finalEntry : finalTimeline.entrySet()){
            timeline.add(finalEntry.getKey(),finalEntry.getValue());
        }

        logger.info("Chart filled.");
    }
}
