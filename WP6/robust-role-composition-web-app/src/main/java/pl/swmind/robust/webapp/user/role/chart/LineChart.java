package pl.swmind.robust.webapp.user.role.chart;

import com.vaadin.ui.Alignment;
import org.vaadin.vaadinvisualizations.AnnotatedTimeLine;
import org.vaadin.vaadinvisualizations.AnnotatedTimeLineEntry;
import pl.swmind.robust.webapp.user.role.dto.CoverageEntry;
import pl.swmind.robust.webapp.user.role.validation.impl.PathValidations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * Base for line charts - timeline. <br>
 * <p/>
 * Creation date: 23/11/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public abstract class LineChart extends Chart {
    protected AnnotatedTimeLine timeline;

    public LineChart() {
        validations = new PathValidations(this, true);
    }
    protected void handle(CoverageEntry coverageEntry, String caption) {
        if(validations.isChartLoaded()){
            removeComponent(timeline);
        }
        timeline = new AnnotatedTimeLine();
        timeline.setOption("displayAnnotations", true);
        timeline.setOption("wmode", "transparent");

        timeline.setStyleName(styleNameLocal);
        timeline.setHeight("100%");
        timeline.setWidth("100%");

        setStyleName(styleNameLocal);
        addComponent(timeline);
        setComponentAlignment(timeline, Alignment.TOP_LEFT);
        setExpandRatio(timeline, 0.6f);
        timeline.addLineLabel(caption);

        for(Map.Entry<Calendar, ArrayList<AnnotatedTimeLineEntry>> entry : coverageEntry.getTimeline().entrySet()){
            timeline.add(entry.getKey(),entry.getValue());
        }
    }
}
