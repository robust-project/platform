package pl.swmind.robust.webapp.user.role.path;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import pl.swmind.robust.webapp.SpringEnabled;
import pl.swmind.robust.webapp.user.role.chart.impl.PathChart;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 11/04/13<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public class PathChartTest extends SpringEnabled {
    private @Autowired PathChart pathChart;
    private @Value("${platformId}") String platformId;
    private @Value("${communityId}") String communityId;
    private @Value("${styleName}") String styleName;

    @Test
    public void shouldChartInitPass(){
        pathChart.setPlatformId(platformId);
        pathChart.setCommunityId(communityId);
        pathChart.setStyleNameLocal(styleName);
        pathChart.initChart();
    }
}
