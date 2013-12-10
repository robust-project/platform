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

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.ViewResources;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import org.vaadin.vaadinvisualizations.PieChartImage;

import org.apache.log4j.Logger;
import org.vaadin.vaadinvisualizations.LineChart;

public class ROEvalHistoryView extends AbstractDashIndicatorView {

    static transient Logger log = Logger.getLogger(ROEvalHistoryView.class);
    // Vaadin visualisation plug-in class (just for example)
    private LineChart lineChart;
    private ComboBox riskBox;
    private ComboBox userNamesBox;
    private transient boolean isInitialized = false;
    InlineDateField startTime = null;
    InlineDateField endTime = null;
   Label title;
   Button refreshbt;
    private boolean refreshView = true; // allow view to be refreshed after updating parameters
    private final ROEvalHistoryController controller;
    private Thread viewUpdaterThread;

    public ROEvalHistoryView(ROEvalHistoryController controller) {
        super();
        
        this.controller = controller;

        createComponents();
    }

    @Override
    public void refreshView() {
        if (viewUpdaterThread != null) {
            // Cancel any previous running thread
            viewUpdaterThread.interrupt();
        }
        viewUpdaterThread = new Thread(new ViewUpdaterThread());
        viewUpdaterThread.start();
    }

    //chooses the provided risk title and displays the risk evaluation results
    public void updateRisks(UUID riskUuid, Map<UUID, String> allRisks, boolean refreshView)//risk uuid, Map<date, double>
    {
        if (allRisks == null || allRisks.isEmpty()) {           
            resetGraph();
            riskBox.removeAllItems();
        } else {
//      isInitialized=false;
            RiskItem selectedRskItem = null;
            //update the risk list
            log.info("adding risk titles to riskbox");
            riskBox.removeAllItems();
            for (Entry entry : allRisks.entrySet()) {
                RiskItem rskItem = new RiskItem((UUID) entry.getKey(), (String) entry.getValue());
                riskBox.addItem(rskItem);
                if (riskUuid!=null && riskUuid.equals(entry.getKey())) {
                    log.info(" selected risk item " + entry.getValue());
                    selectedRskItem = rskItem;
                    riskBox.setNullSelectionAllowed(false);
                }
            }


            
            riskBox.setTextInputAllowed(false);
            riskBox.setNullSelectionAllowed(false);
            //riskBox.addListener(new ComboxListener()); //KEM only add listener at creation time, otherwise we get multiple listeners which each perform a time-consuming operation
            //select the provided risk in the box 
            log.info("setting selected risk item " + allRisks.get(riskUuid));
            // RiskItem rskItem = new RiskItem((UUID) riskUuid, (String) allRisks.get(riskUuid));
            this.refreshView = refreshView;
            riskBox.setValue(selectedRskItem);
        }

    }
    
    private void resetGraph(){
        try{
        viewContents.removeComponent(lineChart);
        }catch(Exception ex){
            //nothing to do 
        }
        lineChart = new LineChart();
        lineChart.addXAxisLabel("Date");
        lineChart.addLine("Probability");

        lineChart.setSizeFull();
         lineChart.setOption("width", 600);  // Chart size
        lineChart.setOption("height", 400);
        lineChart.setWidth("600px");        // View size
        lineChart.setHeight("420px");
        viewContents.addComponent(lineChart);
            
    }

    public void updateGraph(String title, Map<Date, Double> evalResults)//risk uuid, risk title, Map<date, double>
    {
        if (title == null) {
            resetGraph();
        }

        log.info("graph is being updated with the provided info" + evalResults);

        if (evalResults != null) {
            for (Date date : evalResults.keySet()) {
                log.info(date+" : "+ evalResults.get(date));
            }
        }
        
        viewContents.removeComponent(lineChart);
        lineChart = new LineChart();
        viewContents.addComponent(lineChart);
        lineChart.setOption("width", 600);  // Chart size
        lineChart.setOption("height", 400);
        lineChart.setWidth("600px");        // View size
        lineChart.setHeight("420px");
        // Update line chart with in-coming data
        lineChart.setOption("legend", "bottom");
        lineChart.setOption("title", title);

        lineChart.addXAxisLabel("Date");
        lineChart.addLine("Probability");
        
        if (evalResults != null) {
            TreeMap<Date, Double> tmtDate = new TreeMap<Date, Double>(new DateComparator());
            tmtDate.putAll(evalResults);
            log.info("evaluation result is not null, going through them");
            for (Date date : tmtDate.keySet()) {
                //log.info("adding to the chart " + tmtDate.get(date));
                lineChart.add(date.toString(), new double[]{tmtDate.get(date)});
            }
        }
        viewContents.childRequestedRepaint(null);

    }



    void updateNames(Set<String> currentNames, String selectedName) {
        if (currentNames != null && !currentNames.isEmpty()) {
            log.info("updating names with " + currentNames);
            for (String name : currentNames) {
                userNamesBox.addItem(name);
            }
            
            if (selectedName != null) {
                userNamesBox.setValue(selectedName);
            }
            
            log.info("adding the names box to view");
            viewContents.addComponent(userNamesBox);
        }else{
            viewContents.removeComponent(userNamesBox);
        }
    }

    private class DateComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            if ((o1 instanceof Date) && (o2 instanceof Date)) {
                if (((Date) o1).before((Date) o2)) {
                    return -1;
                }

                if (((Date) o1).after((Date) o2)) {
                    return +1;
                }

                if (((Date) o1).equals((Date) o2)) {
                    return 0;
                }
            }

            throw new RuntimeException("cannot compare types other than Date " + o1.getClass());
        }
    }

    // IDashIndicatorView --------------------------------------------------------
    @Override
    public String getIndicatorName() {
        return "Risk/Opportunity Evaluation History";
    }

    @Override
    public ThemeResource getIndicatorIcon() {
        // TODO: Create new icon for this visualisation view
        return ViewResources.CATAPPResInstance.getResource("ROEvalHistoryIcon");
    }

    @Override
    public String getDescription() {
        return "Risk/Opportunity historical view. Shows a summary of the assessment results computed between two dates.";
    }

    // Private methods -----------------------------------------------------------
    private void createComponents() {
        try {
            viewContents.setWidth("800");
            viewContents.setHeight("800px");

            // A title
            title = new Label("Risk/Opportunity historical view");
            title.addStyleName("catHeadlineFont");
            title.addStyleName("catBlue");
            viewContents.addComponent(title);

            // Some vertical space
            viewContents.addComponent(UILayoutUtil.createSpace("4px", null));

            Set<String> riskTitles = new HashSet<String>();
            riskBox = new ComboBox("Risk Title", riskTitles);
            riskBox.setTextInputAllowed(false);
            riskBox.addListener(new ComboBox.ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    onRiskChoice(event);
                }
            }); // KEM moved from updateRisks method
            viewContents.addComponent(riskBox);
            refreshbt = new Button("Refresh");
            refreshbt.addListener(new BtnListener());
            viewContents.addComponent(refreshbt);
            HorizontalLayout hl = new HorizontalLayout();
            viewContents.addComponent(hl);
            startTime = new InlineDateField("From Date: ");
            startTime.setResolution(InlineDateField.RESOLUTION_DAY);
            startTime.setImmediate(true);
            DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
            startTime.setValue(formatter.parse("2012.07.08"));
            startTime.addListener(new DateListener());
            hl.addComponent(startTime);
            hl.addComponent(UILayoutUtil.createSpace("20px", null, true));

            endTime = new InlineDateField("To Date: ");
            endTime.setResolution(InlineDateField.RESOLUTION_DAY);
            endTime.setImmediate(true);
            endTime.setValue(new Date());
            endTime.addListener(new DateListener());
            hl.addComponent(endTime);

       
            
            Set<String> names = new HashSet<String>();
            userNamesBox = new ComboBox("Results ", names);
            userNamesBox.setTextInputAllowed(false);
            userNamesBox.addListener(new ComboBox.ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    onNameChange(event);
                }
            });
            //dont add until we have names
            viewContents.addComponent(userNamesBox);
     
            //LineChart
            log.info("creating the linechart object");
            resetGraph();
        } catch (Exception ex) {
            throw new RuntimeException("This view needs refreshing. " + ex.getMessage(), ex);
        }
    }

    private void onRiskChoice(ValueChangeEvent event) {
        log.info("on Risk choice called");
        log.info(event.getProperty());
        log.info(event.getProperty().getValue());

        if (! refreshView)
            return;
        
        // Find any listeners and notify them of our UI event
        List<ROEvalHistoryViewListener> listeners = getListenersByType();
        for (ROEvalHistoryViewListener listener : listeners) {
            if (event.getProperty() != null && event.getProperty().getValue() != null) {
                log.info("notifying listeners about " + ((RiskItem) event.getProperty().getValue()).uuid);
                listener.onRiskChange(((RiskItem) event.getProperty().getValue()).uuid, (Date)startTime.getValue(), (Date)endTime.getValue());
            }

        }
    }
    
    private void onNameChange(ValueChangeEvent event){
         log.info("on Name change called");
        log.info(event.getProperty());
        log.info(event.getProperty().getValue());
        
        // Find any listeners and notify them of our UI event
        List<ROEvalHistoryViewListener> listeners = getListenersByType();
        for (ROEvalHistoryViewListener listener : listeners) {
            if (event.getProperty() != null && event.getProperty().getValue() != null) {
                log.info("notifying listeners about " + (event.getProperty().getValue()));
                listener.onNameChange((String) event.getProperty().getValue(),(Date)startTime.getValue(), (Date)endTime.getValue());
            }

        }
    }

    // Private event listeners ---------------------------------------------------


    private class RiskItem implements Serializable {

        String title;
        UUID uuid;

        public RiskItem(UUID uuid, String title) {
            this.uuid = uuid;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private class DateListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent event) {
            onDateChange(event);
        }
    }
    
    private class BtnListener implements ClickListener{

                @Override
                public void buttonClick(ClickEvent event) {
                    obBtnClick(event);
                }
   }
    
   private void obBtnClick(ClickEvent event) {
        List<ROEvalHistoryViewListener> listeners = getListenersByType();
        for (ROEvalHistoryViewListener listener : listeners) {       
                log.info("notifying listeners about refresh request ");
                listener.onRefresh();
        }
   }

    private void onDateChange(ValueChangeEvent event) {
        // Find any listeners and notify them of our UI event
       
        List<ROEvalHistoryViewListener> listeners = getListenersByType();
        for (ROEvalHistoryViewListener listener : listeners) {
            if (event.getProperty() != null && event.getProperty().getValue() != null) {
                log.info("notifying listeners about date change " + ((Date) event.getProperty().getValue()).toString());
                Date startDate=(Date)startTime.getValue();
                Date endDate=(Date)endTime.getValue();
                listener.onDateChange(startDate,endDate);
            }

        }

    }
    
    private class ViewUpdaterThread implements Runnable {
        @Override
        public void run() {
            controller.initializeView(true);
        }
    }
}
