/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2012
//
// Copyright in this software belongs to University of Southampton
// IT Innovation Centre of Gamma House, Enterprise Road, 
// Chilworth Science Park, Southampton, SO16 7NS, UK.
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
//      Created By :            Simon Crowle/Bassem Nasser
//      Created Date :          26-Oct-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;

import java.io.Serializable;
import java.util.*;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.dashboard.indicators.IROEvalHistoryController;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

public class ROEvalHistoryController extends UFAbstractEventManager
        implements Serializable,
        ROEvalHistoryViewListener, // Listen for view events
        IROEvalHistoryController {

    private ROEvalHistoryView historyView;
    static transient Logger log = Logger.getLogger(ROEvalHistoryController.class);
    private transient UUID currentComUuid;
    private transient Risk currentRisk; //may not need this, just the uuid
    private transient UUID currentRiskUuid;
    private transient IDataLayer iDataLayer;
    private transient Set<EvaluationResult> setEvalResults;
    private transient Map<String,Map<Date,Double>> userResultsItems;
    private transient Set<String> currentNames=new HashSet<String>();
    private final String UNSPECIFIC="unspecific";

// private transient Map<Date, Double> evalResults=null;
    public ROEvalHistoryController() {
        super();

        historyView = new ROEvalHistoryView(this);
        historyView.addListener(this);
    }

    public AbstractDashIndicatorView getIndicatorView() {
        return historyView;
    }

    @Override
    public void setContextInfo(UUID currentComUuid, Risk currentRisk, IDataLayer iDataLayer) {
        log.info("setting the following context information ");
        log.info("Current Community UUID " + currentComUuid);
        if (currentRisk != null) {
            log.info("Current Risk title " + currentRisk.getTitle());
        }

        this.iDataLayer = iDataLayer;
        this.currentComUuid = currentComUuid;
        this.currentRisk = currentRisk;

        Set<Risk> allRisks = iDataLayer.getRisks(currentComUuid);
        if ((currentRisk == null) && (allRisks != null) && (!allRisks.isEmpty())) {
            this.currentRisk = allRisks.iterator().next();
            this.currentRiskUuid = this.currentRisk.getId();
            log.info("current risk is " + this.currentRisk.getTitle());
        }

        initializeView(false);
    }

    // ROEvalHistoryViewListener -------------------------------------------------
    @Override
    public void onRiskChange(UUID riskUuid, Date start, Date end) {//need to add this to the view listener to be notified
        this.currentRiskUuid = riskUuid;
        this.currentRisk = getRiskByUUID(riskUuid);
        log.info("/***Risk changed!!**/");
        //getEvalResults
        log.info("getting evaluation results");
        Map<String, Map<Date, Double>> allUserItems = getEvalResults(riskUuid, null, null); //getMockEvalResults(currentRiskUuid, null, null);

        Boolean users = isUserNames(allUserItems);
        //if no user names
        if (!users) {
            log.info("updating the graph without user names");
            Map<Date, Double> mapItems = getUserResults(allUserItems, UNSPECIFIC);
            historyView.updateGraph(this.currentRisk == null ? null : this.currentRisk.getTitle(), mapItems);
            log.info("names null or empty, going to hide the names box");
            historyView.updateNames(currentNames, null);

        } else {
            //if user names
            log.info("updating the graph with user names");
            //choose the first name to show their probabilities
            String selectedName = currentNames.iterator().next();
            historyView.updateNames(this.currentNames, selectedName);
            Map<Date, Double> usermapitems = getUserResults(allUserItems, selectedName);
            historyView.updateGraph(this.currentRisk == null ? null : this.currentRisk.getTitle(), usermapitems);
        }

    }

    public void initializeView(boolean refreshView) {
//      updateView(UUID riskUuid, String riskTitle, Map<Date, double> evalResults
        log.info("/****initialize view is called in controller*****/");

        log.info("getting list of risks");
        Map<UUID, String> allRisks = getRisksInfo();
        
        if (allRisks == null)
            return;

        log.info("updating the view");
        historyView.updateRisks(this.currentRiskUuid, allRisks, refreshView);//this will trigger updates to names too 
    }

    //these should be per user, each user should have a set of <date,probability>
    //note that its not always that we have users
    //may be filter per user at mysql server?
    private Map<String, Map<Date, Double>> getEvalResults(UUID riskUuid, Date start, Date end) {

        Map<String, Map<Date, Double>> usersResultItems = new HashMap<String, Map<Date, Double>>();
        currentNames = new HashSet<String>();
        setEvalResults = null;

        try {
            //call datalayer and get evaluation results
            if (riskUuid == null) {
                throw new RuntimeException("riskUuid provided is null, cannot get the associated risk");
            }
            //Set<String> userNames = new HashSet<String>();
            log.info("getting evaluation results for risk " + riskUuid + "between " + start + " and " + end);
            setEvalResults = iDataLayer.getRiskEvalResults(riskUuid, null, null);

            //extract info into map
            if (setEvalResults != null && !setEvalResults.isEmpty()) {
                log.info("found" + setEvalResults.size() + " evaluation results");
                for (EvaluationResult ev : setEvalResults) {
                    Date date = ev.getForecastDate();
                    List<ResultItem> items = ev.getResultItems();

                    if (items != null) {
                        log.info("found " + items.size() + " result items for risk ");
//                    Map<Date, Double> mapResultsItems = new HashMap<Date, Double>();

                        for (ResultItem item : items) { //results may be for multiple users, need a selector in the view to address that
                            log.info("user name " + item.getName() + ": " + item.getProbability());
                            Double probability = item.getProbability();

//                        mapResultsItems.put(date, probability);
                            if (item.getName() != null) {
                                log.info("adding user name to list");
                                currentNames.add(item.getName());//this should avoid duplication of names
                                if (usersResultItems.containsKey(item.getName())) {
                                    //add eval results to the existing ones
                                    log.info("found another result item for " + item.getName());
                                    usersResultItems.get(item.getName()).put(date, probability);
                                } else {
                                    log.info("found new result items for " + item.getName());
                                    Map<Date, Double> mapResultItem = new HashMap<Date, Double>();
                                    mapResultItem.put(date, probability);
                                    usersResultItems.put(item.getName(), mapResultItem);
                                }
                            } else {
                                Map<Date, Double> mapResultItem = new HashMap<Date, Double>();
                                mapResultItem.put(date, probability);
                                usersResultItems.put(UNSPECIFIC, mapResultItem);
                            }
                        }
                    }

                }


                log.info("All evaluation results: ");
                for (String name : usersResultItems.keySet()) {
                    log.info(name + " results: ");
                    for (Date date : usersResultItems.get(name).keySet()) {
                        log.info(date + " : " + usersResultItems.get(name).get(date));
                    }
                }

            } else {
                log.info("no evluation results found for risk uuid " + riskUuid);
            }

        } catch (Exception ex) {
            log.error("Network error: couldnt retrieve evaluation results. " + ex.getMessage(), ex);
        }
        
         log.info("finally eval Results items are " + usersResultItems.size() + ": " + usersResultItems.toString());
            return usersResultItems;
    }



    private Risk getRiskByUUID(UUID riskUuid) {
        if (riskUuid == null) {
            throw new RuntimeException("riskUuid provided is null, cannot get the associated risk");
        }
        return iDataLayer.getRiskByUUID(riskUuid);
    }

    private Map<UUID, String> getRisksInfo() {
        Map<UUID, String> allRisks = null;

        if (currentComUuid == null) {
            //throw new RuntimeException("currentComUuid is null, cannot get the associated risks");
            return null;
        }

        log.info("gettting the list of risks for community " + currentComUuid);
        Set<Risk> risks = iDataLayer.getRisks(currentComUuid);
        if (risks != null) {
            allRisks = new HashMap<UUID, String>();
            for (Risk risk : risks) {
                log.info("risk " + risk.getTitle());
                allRisks.put(risk.getId(), risk.getTitle());
            }
        }

        return allRisks;
    }

    @Override
    public void onDateChange(Date startDate, Date endDate) {
        log.info("/***Date changed!!**/");
        Map<String,Map<Date, Double>> allUserItems= getEvalResults(this.currentRiskUuid, startDate, endDate);

         Boolean users = isUserNames(allUserItems);

        //if no user names
        if (!users) {
            log.info("updating the graph without user names");
            Map<Date, Double> mapItems=getUserResults(allUserItems, UNSPECIFIC);
            historyView.updateGraph(this.currentRisk == null ? null : this.currentRisk.getTitle(),mapItems);
        } 
    }

    @Override
    public void onRefresh() {
        initializeView(true);
    }

    @Override
    public void onNameChange(String username, Date start, Date end) {
        log.info("/***Name changed to !!**/" +username);
        userResultsItems=getEvalResults(currentRiskUuid, start, end);
        Map<Date, Double> userResults = getUserResults(userResultsItems, username);
        historyView.updateGraph(this.currentRisk == null ? null : this.currentRisk.getTitle(),userResults);
    }

    private Boolean isUserNames(Map<String, Map<Date,Double>> userItems) {
        if(userItems.isEmpty() || userItems==null)
            return false;
        
        if(userItems!=null && userItems.size()>1) //not just probability ones
            return true;
        
         if(userItems!=null && userItems.size()== 1 && !userItems.containsKey(UNSPECIFIC)) //not just probability ones
            return true;
         
        return false;
    }
    
    private Map<Date, Double> getUserResults(Map<String, Map<Date, Double>> allEvalResults, String username) {
        log.info("/*****Get user results***/");
        Map<Date, Double> userresults = null;
        if (username != null) {
            userresults = allEvalResults.get(username);

            log.info("All evaluation results: ");
            for (String name : allEvalResults.keySet()) {
                log.info(name + " results: ");
                for (Date date : allEvalResults.get(name).keySet()) {
                    log.info(date + " : " + allEvalResults.get(name).get(date));
                }
            }

            log.info(username + " results: ");
            if (userresults != null) {
                for (Date date : userresults.keySet()) {
                    log.info(date + " : " + userresults.get(date));
                }
            }
        }
        return userresults;
    }
}
