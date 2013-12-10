package pl.swmind.robust.webapp;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.swmind.robust.webapp.user.role.chart.Chart;
import pl.swmind.robust.webapp.user.role.chart.impl.LineCoverageChart;
import pl.swmind.robust.webapp.user.role.chart.impl.PathChart;
import pl.swmind.robust.webapp.user.role.chart.impl.PieCoverageChart;

/**
 * Main web application layout. Holds tabs for each visualization. <br>
 * <p/>
 * Creation date: 05/11/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
@Component
@Scope("prototype")
public class TabLayout extends VerticalLayout {
    private @Autowired PathChart pathChart;
    private @Autowired PieCoverageChart pieChart;
    private @Autowired LineCoverageChart lineCoverageChart;
    private TabSheet tabSheet;

    public void init(String platformId, String communityId, String styleName){
        tabSheet = new TabSheet();
        tabSheet.setStyleName(styleName);
        tabSheet.setSizeFull();

        setStyleName(styleName);
        setSizeFull();
        addComponent(tabSheet);

        setIds(pathChart,platformId,communityId, styleName);
        setIds(pieChart,platformId,communityId, styleName);
        setIds(lineCoverageChart,platformId,communityId, styleName);

        pathChart.initChart();
        pieChart.initChart();
        lineCoverageChart.initChart();

        addTab(pathChart,pathChart.getDescription());
        addTab(pieChart,pieChart.getDescription());
        addTab(lineCoverageChart,lineCoverageChart.getDescription());
    }

    private void addTab(com.vaadin.ui.Component component, String description){
        tabSheet.addTab(component,description);
    }

    private void setIds(Chart chart, String platformId, String communityId, String styleName){
        chart.setCommunityId(communityId);
        chart.setPlatformId(platformId);
        chart.setStyleNameLocal(styleName);
    }
}