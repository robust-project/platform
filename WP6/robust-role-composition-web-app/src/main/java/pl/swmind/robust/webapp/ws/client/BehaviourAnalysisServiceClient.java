package pl.swmind.robust.webapp.ws.client;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.vaadinvisualizations.AnnotatedTimeLineEntry;
import pl.swmind.robust.webapp.user.role.dto.CoverageEntry;
import pl.swmind.robust.ws.behaviouranalysis.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

import static java.lang.String.format;

/**
 * Client to dependent Web Service. <br>
 * <p/>
 * Creation date: 11/09/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 *
 * TODO make maxUsers as parameter -> Spring
 */
@Component
public class BehaviourAnalysisServiceClient {
    private final Logger logger = Logger.getLogger(BehaviourAnalysisServiceClient.class);
    private @Autowired BehaviourAnalysisService behaviourAnalysisService;
    private DatatypeFactory datatypeFactory;
    private GregorianCalendar gregCalendar = new GregorianCalendar();
    private final int maxUsers = 10;

    public BehaviourAnalysisServiceClient() throws DatatypeConfigurationException {
        datatypeFactory = DatatypeFactory.newInstance();
    }

    public Composition getComposition(String platformId, String communityId, Date date) throws RobustBehaviourServiceException_Exception {
        logger.info(format("Getting macro composition from WS with (%s,%s,%s)",platformId,communityId,date.toString()));

        XMLGregorianCalendar gregDate = getGregCalFrom(date);
        Composition composition = behaviourAnalysisService.deriveMacroComposition(platformId,communityId,gregDate);

        logger.info("Got composition");
        return composition;
    }

    public HealthIndicatorFeatures getHealthIndicators(String platformID, String communityID, Date endDate) throws RobustBehaviourServiceException_Exception {
        logger.info(format("Getting health indicators for %s, %s at %s",platformID,communityID,endDate.toString()));
        return behaviourAnalysisService.getHealthIndicators(platformID,communityID, getGregCalFrom(endDate));
    }

    public List<String> getMostValuableUsers(String platformId, String communityID) throws RobustBehaviourServiceException_Exception {
        logger.info("Getting most valuable users from WS for " + platformId + ", " + communityID);

        List<String> userIds = behaviourAnalysisService.getUserIDs(platformId, communityID, maxUsers);

        logger.info("Got " + userIds.size() + " user ids.");
        return userIds;
    }

    public CoverageEntry getRolePath(String platformId, String communityID, String userId, Date beginDate, Date endDate) throws RobustBehaviourServiceException_Exception {
        logger.info("Getting role path for " + userId);
        List<GetRolePathResultEntry> result = behaviourAnalysisService.getTSRolePath(platformId, communityID, userId, getGregCalFrom(beginDate), getGregCalFrom(endDate));

        logger.info("Got " + result.size() + " results.");
        List<GetRolePathResultEntry> list = sort(result);
        CoverageEntry coverageEntry = new CoverageEntry();
        SortedMap<Calendar, ArrayList<AnnotatedTimeLineEntry>> rolePath = coverageEntry.getTimeline();

        for(GetRolePathResultEntry entry:list){
            ArrayList<AnnotatedTimeLineEntry> timeLineEntries = new ArrayList<AnnotatedTimeLineEntry>();
            timeLineEntries.add(new AnnotatedTimeLineEntry(entry.getRole().doubleValue(),"",""));
            rolePath.put(entry.getDate().toGregorianCalendar(),timeLineEntries);
        }

        logger.info("Role path container populated.");
        return coverageEntry;
    }

    public List<CoverageEntry> getCompositions(String platformId, String communityId, Date beginDate, Date endDate) throws RobustBehaviourServiceException_Exception {
        logger.info(format("Getting macro compositions from WS with (%s,%s,%s,%s)",platformId,communityId,beginDate.toString(),endDate.toString()));

        List<DeriveTSMacroCompositionsResultEntry> macroCompositions = behaviourAnalysisService.deriveTSMacroCompositions(platformId, communityId, getGregCalFrom(beginDate), getGregCalFrom(endDate));

        logger.info("Got " + macroCompositions.size() + " macro compositions.");
        List<CoverageEntry> coverageEntryList = new LinkedList<CoverageEntry>();

        for(DeriveTSMacroCompositionsResultEntry compositionEntry: macroCompositions){
            Composition composition = compositionEntry.getComposition();
            GregorianCalendar cal = composition.getDate().toGregorianCalendar();
            List<Composition.RoleCoverage.Entry> roleCoverage = composition.getRoleCoverage().getEntry();
            List<Composition.IdToRoleMapping.Entry> idToRoleMapping = composition.getIdToRoleMapping().getEntry();

            for(Composition.RoleCoverage.Entry coverageEntry: roleCoverage){
                double value = coverageEntry.getValue();
                String roleLabel = getRoleLabel(idToRoleMapping, coverageEntry.getKey());
                CoverageEntry coverage = getCoverageBy(roleLabel,coverageEntryList);

                SortedMap<Calendar, ArrayList<AnnotatedTimeLineEntry>> timeline = coverage.getTimeline();
                ArrayList<AnnotatedTimeLineEntry> annotatedList = timeline.get(cal);
                if(annotatedList == null){
                    annotatedList = new ArrayList<AnnotatedTimeLineEntry>();
                }
                annotatedList.add(new AnnotatedTimeLineEntry(value,"",""));
                timeline.put(cal, annotatedList);
                coverage.setTimeline(timeline);
            }
        }

        return coverageEntryList;
    }

    private CoverageEntry getCoverageBy(String roleLabel, List<CoverageEntry> coverageEntryList) {
        for(CoverageEntry entry : coverageEntryList){
            if(entry.getRoleLabel().equals(roleLabel)){
                return entry;
            }
        }
        CoverageEntry coverageEntry = new CoverageEntry();
        coverageEntry.setRoleLabel(roleLabel);
        coverageEntryList.add(coverageEntry);

        logger.info("Creating coverage entry for: " + roleLabel);

        return coverageEntry;
    }

    private String getRoleLabel(List<Composition.IdToRoleMapping.Entry> idToRoleMapping, int role) {
        for(Composition.IdToRoleMapping.Entry entry: idToRoleMapping){
            if(entry.getKey() == role){
                return entry.getValue();
            }
        }
        return  "Role " + role;
    }

    private XMLGregorianCalendar getGregCalFrom(Date beginDate) {
        gregCalendar.setTime(beginDate);
        return datatypeFactory.newXMLGregorianCalendar(gregCalendar);
    }

    private List<GetRolePathResultEntry> sort(List<GetRolePathResultEntry> result) {
        Collections.sort(result, new Comparator<GetRolePathResultEntry>() {
            public int compare(GetRolePathResultEntry o1, GetRolePathResultEntry o2) {
                java.util.Date d1 = o1.getDate().toGregorianCalendar().getTime();
                java.util.Date d2 = o2.getDate().toGregorianCalendar().getTime();

                return d1.compareTo(d2);
            }
        });
        return result;
    }
}









































