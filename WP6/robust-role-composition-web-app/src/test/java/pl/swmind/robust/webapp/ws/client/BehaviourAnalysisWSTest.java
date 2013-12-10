package pl.swmind.robust.webapp.ws.client;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.swmind.robust.webapp.SpringEnabled;
import pl.swmind.robust.ws.behaviouranalysis.BehaviourAnalysisService;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 11/04/13<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */

// TODO build shouldn't be dependent on WS state
@Ignore
public class BehaviourAnalysisWSTest extends SpringEnabled{
    private static final String BOARDS_PLATFORM_ID = "BOARDSIE";

    @Autowired
    private BehaviourAnalysisService service;

    @Test
    public void shouldWebServiceBeUp(){
        List<String> platformIds = service.getPlatformIDs();

        assertTrue(platformIds.size() > 0);
        assertTrue(platformIds.contains(BOARDS_PLATFORM_ID));
    }
}
