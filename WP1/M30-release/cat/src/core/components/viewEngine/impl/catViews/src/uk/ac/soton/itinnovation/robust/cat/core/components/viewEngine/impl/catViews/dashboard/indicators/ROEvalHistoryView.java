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
    private ComboBox userNames;
    private transient boolean isInitialized = false;
    InlineDateField startTime = null;
    InlineDateField endTime = null;

    public ROEvalHistoryView() {
        super();

        createComponents();
    }

    //chooses the provided risk title and displays the risk evaluation results
    public void updateRisks(UUID riskUuid, Map<UUID, String> allRisks)//risk uuid, Map<date, double>
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
        lineChart.addXAxisLabel("Time");
        lineChart.setSizeFull();
        viewContents.addComponent(lineChart);
            
    }

    public void updateGraph(String title, Map<Date, Double> evalResults)//risk uuid, risk title, Map<date, double>
    {
        if(title==null){
            resetGraph();
        }
        
        log.info("graph is being updated with the provided info");
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

        TreeMap<Date, Double> tmtDate = new TreeMap<Date, Double>(new DateComparator());
        tmtDate.putAll(evalResults);

        if (evalResults != null) {
            log.info("evaluation result is not null, going through them");
            for (Date date : tmtDate.keySet()) {
                //log.info("adding to the chart " + tmtDate.get(date));
                lineChart.add(date.toString(), new double[]{tmtDate.get(date)});
            }
        }
        viewContents.childRequestedRepaint(null);

    }

    public void showNamesBox(String[] names) {
        userNames = new ComboBox("User names");
        viewContents.addComponent(userNames);

    }

    public void hideNamesBox() {
        viewContents.removeComponent(userNames);
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

    // Private methods -----------------------------------------------------------
    private void createComponents() {
        try {
            viewContents.setWidth("800");
            viewContents.setHeight("800px");

            // A title
            Label title = new Label("Risk/Opportunity historical view");
            title.addStyleName("catHeadlineFont");
            title.addStyleName("catBlue");
            viewContents.addComponent(title);

            // Some vertical space
            viewContents.addComponent(UILayoutUtil.createSpace("4px", null));

            //LineChart
            log.info("creating the linechart object");
//        lineChart = new LineChart();
//        viewContents.addComponent(lineChart);
            resetGraph();



            Set<String> riskTitles = new HashSet<String>();
            riskBox = new ComboBox("Risk Title", riskTitles);
            riskBox.setTextInputAllowed(false);
            riskBox.addListener(new ComboxListener()); // KEM moved from updateRisks method
            viewContents.addComponent(riskBox);

            Button bt = new Button("Refresh");
            bt.addListener(new BtnListener());
            viewContents.addComponent(bt);

            HorizontalLayout hl = new HorizontalLayout();
            viewContents.addComponent(hl);


            startTime = new InlineDateField("From Date: ");
            startTime.setResolution(InlineDateField.RESOLUTION_DAY);
            startTime.setImmediate(true);
        startTime.setValue(new Date());
        startTime.addListener(new DateListener());
        hl.addComponent(startTime);
        hl.addComponent(UILayoutUtil.createSpace("20px", null, true));

        endTime = new InlineDateField("To Date: ");
        endTime.setResolution(InlineDateField.RESOLUTION_DAY);
        endTime.setImmediate(true);
        endTime.setValue(new Date());
        endTime.addListener(new DateListener());
        hl.addComponent(endTime);
//    viewContents.addComponent( startTime );
//    viewContents.addComponent( endTime );
        }catch(Exception ex){
            throw new RuntimeException("This view needs refreshing. "+ex.getMessage(), ex);
        }
    }

    private void onRiskChoice(ValueChangeEvent event) {
        log.info("on Risk choice called");
        log.info(event.getProperty());
        log.info(event.getProperty().getValue());

        // Find any listeners and notify them of our UI event
        List<ROEvalHistoryViewListener> listeners = getListenersByType();
        for (ROEvalHistoryViewListener listener : listeners) {
            if (event.getProperty() != null && event.getProperty().getValue() != null) {
                log.info("notifying listeners about " + ((RiskItem) event.getProperty().getValue()).uuid);
                listener.onRiskChange(((RiskItem) event.getProperty().getValue()).uuid, null, null);
            }

        }
    }

    // Private event listeners ---------------------------------------------------
//  private class ButtonListener implements Button.ClickListener
//  {
//    @Override
//    public void buttonClick(Button.ClickEvent event)
//    { onButtonClicked(); }
//  }
    private class ComboxListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent event) {
            onRiskChoice(event);
        }
    }

    private class RiskItem {

        String title;
        UUID uuid;

        public RiskItem(UUID uuid, String title) {
            this.uuid = uuid;
            this.title = title;
        }

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
}
