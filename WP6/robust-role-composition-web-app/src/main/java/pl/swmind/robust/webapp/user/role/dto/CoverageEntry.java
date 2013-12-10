package pl.swmind.robust.webapp.user.role.dto;

import org.vaadin.vaadinvisualizations.AnnotatedTimeLineEntry;

import java.util.*;

/**
 * Percent coverage for one role in time. <br>
 * <p/>
 * Creation date: 20/12/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public class CoverageEntry {
    private String roleLabel;
    private SortedMap<Calendar, ArrayList<AnnotatedTimeLineEntry>> timeline = new TreeMap<Calendar, ArrayList<AnnotatedTimeLineEntry>>();

    public String getRoleLabel() {
        return roleLabel;
    }

    public void setRoleLabel(String roleLabel) {
        this.roleLabel = roleLabel;
    }

    public SortedMap<Calendar, ArrayList<AnnotatedTimeLineEntry>> getTimeline() {
        return timeline;
    }

    public void setTimeline(SortedMap<Calendar, ArrayList<AnnotatedTimeLineEntry>> timeline) {
        this.timeline = timeline;
    }
}
