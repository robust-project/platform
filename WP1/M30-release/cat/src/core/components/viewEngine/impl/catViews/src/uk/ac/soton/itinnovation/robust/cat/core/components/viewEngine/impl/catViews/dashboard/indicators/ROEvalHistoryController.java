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

// private transient Map<Date, Double> evalResults=null;
    public ROEvalHistoryController() {
        super();

        historyView = new ROEvalHistoryView();
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

        Set<Risk> allRisks = iDataLayer.getRisks(currentComUuid);
        this.iDataLayer = iDataLayer;
        this.currentComUuid = currentComUuid;
        this.currentRisk = currentRisk;


        if ((currentRisk == null) && (allRisks != null) && (!allRisks.isEmpty())) {
            this.currentRisk = allRisks.iterator().next();
            this.currentRiskUuid = this.currentRisk.getId();
            log.info("current risk is " + this.currentRisk.getTitle());
        }

        initializeView();
    }

    // ROEvalHistoryViewListener -------------------------------------------------
    @Override
    public void onRiskChange(UUID riskUuid, Date start, Date end) {//need to add this to the view listener to be notified
        this.currentRiskUuid = riskUuid;
        this.currentRisk = getRiskByUUID(riskUuid);
        log.info("/***Risk changed!!**/");
        //getEvalResults
        log.info("getting evaluation results");
        Map<Date, Double> evalResults = getEvalResults(riskUuid, start, end);//getMockEvalResults(currentRiskUuid, null, null);

        log.info("updating the graph");
        historyView.updateGraph(this.currentRisk == null ? null : this.currentRisk.getTitle(), evalResults);
    }

    private void initializeView() {
//      updateView(UUID riskUuid, String riskTitle, Map<Date, double> evalResults
        log.info("/****initialize view is called in controller*****/");

        log.info("getting list of risks");
        Map<UUID, String> allRisks = getRisksInfo();

        log.info("updating the view");
//      historyView.updateView(this.currentRiskUuid, this.currentRisk.getTitle(), evalResults);//need a step to check if risk has got multiple eval results
        historyView.updateRisks(this.currentRiskUuid, allRisks);
    }

    private Map<Date, Double> getEvalResults(UUID riskUuid, Date start, Date end) {
        Map<Date, Double> evalResults = new HashMap<Date, Double>();
        
        try {
            //call datalayer and get evaluation results
            if (riskUuid == null) {
                throw new RuntimeException("riskUuid provided is null, cannot get the associated risk");
            }
            Set<String> userNames = new HashSet<String>();

            setEvalResults = iDataLayer.getRiskEvalResults(riskUuid, start, end);
            //extract info into map
            if (setEvalResults != null) {
                for (EvaluationResult ev : setEvalResults) {
                    Date date = ev.getForecastDate();

                    List<ResultItem> items = ev.getResultItems();
                    //log.info("found " + items.size() + " result items for risk " + iDataLayer.getRiskByUUID(riskUuid).getTitle());

                    for (ResultItem item : items) { //results may be for multiple users, need a selector in the view to address that
                        //log.info("user name " + item.getName() + ": " + item.getProbability());
                        userNames.add(item.getName());
                        Double probability = item.getProbability();

                        evalResults.put(date, probability);
                    }

                }
            }
            
        } catch (Exception ex) {
            log.error("Network error: couldnt retrieve evaluation results. " + ex.getMessage(), ex);
        }
        
        log.info("finally eval Results to be displayed are " + evalResults.size() + ": " + evalResults.toString());
            return evalResults;

    }

    private Map<Date, Double> getMockEvalResults(UUID riskUuid, Date start, Date end) {
        Map<Date, Double> evalResults = new HashMap<Date, Double>();
        Date date1 = new Date("Sat, 12 Aug 2012 13:30:00 GMT");
        Date date2 = new Date("Sun, 13 Aug 2012 13:30:00 GMT");
        Date date3 = new Date("Mon, 14 Aug 2012 13:30:00 GMT");
        Random generator = new Random();


        evalResults.put(date1, generator.nextDouble());
        evalResults.put(date2, generator.nextDouble());
        evalResults.put(date3, generator.nextDouble());


        return evalResults;
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
            throw new RuntimeException("currentComUuid is null, cannot get the associated risks");
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
        Map<Date, Double> evalResults = getEvalResults(this.currentRiskUuid, startDate, endDate);
        
         log.info("updating the graph");
        historyView.updateGraph(this.currentRisk == null ? null : this.currentRisk.getTitle(), evalResults);
    }

    @Override
    public void onRefresh() {
        initializeView();
    }
}
