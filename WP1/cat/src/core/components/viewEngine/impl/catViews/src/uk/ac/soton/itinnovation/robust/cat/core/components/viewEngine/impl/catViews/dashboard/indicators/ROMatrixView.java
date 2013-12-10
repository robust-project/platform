/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2011
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
//      Created By :            Simon Crowle
//      Created Date :          21 Sep 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators;

//import uk.ac.soton.itinnovation.robust.cat.webapp.ROMatrixResultsView;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.*;

import com.vaadin.ui.*;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import java.util.*;
import uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem;

public class ROMatrixView extends AbstractDashIndicatorView {

    private SVGView roSVGView;
    private Table probTable;
    private Label riskTitle;
    private Label currEvalDate;
    private Label forecastEvalDate;
    private HashSet<String> riskLabels;
    private HashSet<UUID> currSelectedROs;
    
    protected Button viewResultsButton;
    protected Button simulateButton;

    public ROMatrixView() {
        super();

        currSelectedROs = new HashSet<UUID>();
        riskLabels = new HashSet<String>();

        createComponents();
    }

    public void applySVGViz(String svgData) {
        if (roSVGView != null) {
            roSVGView.setContent(svgData);
        }
    }

    public void updateProbList(List<UUIDItem> list) {
        probTable.removeAllItems();
        riskLabels.clear();

        for (UUIDItem item : list) {
            RiskEvalResult rer = (RiskEvalResult) item.getData();
            Object[] cells = {item.getLabel(), rer.getRiskTitle()};
            //Object addedItem = probTable.addItem( cells, item );
            Object addedItem = probTable.addItem(cells, item.getID());
            riskLabels.add(item.getLabel());

            //if (currSelectedROs.contains(item.getID()))
            //{
            //    probTable.select(addedItem);
            //}
        }
    }

    public Integer addOrUpdateProbListItem(UUIDItem item) {
        Integer index;
        //UUIDItem currItem = (UUIDItem)probTable.getItem(item);
        Item currItem = probTable.getItem(item.getID());

        if (currItem == null) {
            String label = item.getLabel();

            if (label == null) {
                label = getNewLabel();
            }

            index = getLabelIndex(label);

            RiskEvalResult rer = (RiskEvalResult) item.getData();
            Object[] cells = {label, rer.getRiskTitle()};
            Object addedItem = probTable.addItem(cells, item.getID());
            riskLabels.add(label);
        } else {
            Property indexProp = currItem.getItemProperty("Index");
            String label = indexProp.toString();
            index = getLabelIndex(label);
        }

        return index;
    }

    private String getNewLabel() {
        String newLabel = null;

        int index = 1;

        while (newLabel == null) {
            String label = "RO" + index;
            if (!riskLabels.contains(label)) {
                newLabel = label;
            }
            index++;
        }

        return newLabel;
    }

    private Integer getLabelIndex(String label) {
        Integer index = null;

        if (label != null) {
            if (label.startsWith("")) {
                String indexStr = label.substring(2);
                try {
                    index = Integer.parseInt(indexStr);
                } catch (NumberFormatException e) {
                    System.err.println("Could not determine index for risk label: " + label);
                }
            }
        }

        return index;
    }

    public void setCurrentEvaluationInfo(String roTitle,
            Date currentDate,
            Date forecastDate) {
        if (currentDate == null || forecastDate == null) {
            riskTitle.setValue(roTitle); // Just display title (or message)
            currEvalDate.setValue("");
            forecastEvalDate.setValue("");
        } else {
            if (roTitle != null) {
                riskTitle.setValue(roTitle); // Just display title (or message)
            }
            if (currentDate != null) {
                currEvalDate.setValue(currentDate.toString());
            }
            if (forecastDate != null) {
                forecastEvalDate.setValue(forecastDate.toString());
            }
        }
    }
    
    public void viewResults(List<RiskEvalResult> riskEvalResults) {
        ROMatrixResultsView resultsView = new ROMatrixResultsView(viewContents, "", riskEvalResults);
    }

    // AbstractDashIndicatorView -------------------------------------------------
    @Override
    public String getIndicatorName() {
        return "R/O Matrix";
    }

    @Override
    public ThemeResource getIndicatorIcon() {
        return ViewResources.CATAPPResInstance.getResource("riskMatrixIcon");
    }
    
    @Override
    public String getDescription() {
        return "Risks and opportunities matrix. This shows the likelihood vs impact matrix of the risks and opportunities defined by you for this community.";
    }

    // Private methods -----------------------------------------------------------
    private void createComponents() {
        // Title
        Label label = new Label("Risk/Opportunity matrix");
        label.addStyleName("catSubHeadlineFont");
        viewContents.addComponent(label);

        // Space
        viewContents.addComponent(UILayoutUtil.createSpace("4px", null));

        HorizontalLayout hl = new HorizontalLayout();
        viewContents.addComponent(hl);

        hl.addComponent(createNavView());

        hl.addComponent(UILayoutUtil.createSpace("20px", null, true));

        hl.addComponent(createROSVGView());
    }

    private Component createROSVGView() {
        roSVGView = new SVGView();
        Component viewComp = (Component) roSVGView.getImplContainer();
        viewComp.setWidth("400px");
        viewComp.setHeight("400px");

        return viewComp;
    }

    private Component createNavView() {
        VerticalLayout vl = new VerticalLayout();

        TabSheet navSheet = new TabSheet();
        navSheet.setWidth("200px");
        navSheet.setHeight("270px");
        vl.addComponent(navSheet);

        TabSheet.Tab tab = navSheet.addTab(createROProbNav(), "Current ROs", null);
        tab.setStyleName("romatrix");

        // Space
        vl.addComponent(UILayoutUtil.createSpace("4px", null));

        riskTitle = new Label("No risk selected");
        riskTitle.setWidth("200px");
        riskTitle.addStyleName("catSubSectionFont");
        vl.addComponent(riskTitle);

        // Space
        Component spacer = UILayoutUtil.createSpace("4px", "catHorizRule");
        spacer.setWidth("200px");
        vl.addComponent(spacer);

        // Evaluation date
        Label label = new Label("Predicted from date:");
        label.addStyleName("catSubSectionFont");
        vl.addComponent(label);

        currEvalDate = new Label("No date yet");
        currEvalDate.setImmediate(true);
        label.addStyleName("catSubSectionFont");
        vl.addComponent(currEvalDate);

        // Space
        vl.addComponent(UILayoutUtil.createSpace("4px", null));

        // Forecast date
        label = new Label("Forecast date:");
        label.addStyleName("catSectionFont");
        vl.addComponent(label);

        forecastEvalDate = new Label("No date yet");
        forecastEvalDate.setImmediate(true);
        label.addStyleName("catSubSectionFont");
        vl.addComponent(forecastEvalDate);

        // Space
        spacer = UILayoutUtil.createSpace("4px", "catHorizRule");
        spacer.setWidth("200px");
        vl.addComponent(spacer);
        vl.addComponent( UILayoutUtil.createSpace("4px", null) );
        
        HorizontalLayout buttonsHL = new HorizontalLayout();
        vl.addComponent(buttonsHL);
        vl.setComponentAlignment(buttonsHL, Alignment.BOTTOM_LEFT);
    
        viewResultsButton = new Button("View Results");
        viewResultsButton.setDescription("View results for selected risk(s)");
        viewResultsButton.addListener(new ViewResultsButtonListener());
        viewResultsButton.setImmediate(true);
        viewResultsButton.setEnabled(false);
        buttonsHL.addComponent(viewResultsButton);
        buttonsHL.setComponentAlignment(viewResultsButton, Alignment.BOTTOM_LEFT);

        simulateButton = new Button("Simulate");
        simulateButton.setDescription("Simulate impact of this event");
        simulateButton.addListener(new SimulateButtonListener());
        simulateButton.setImmediate(true);
        simulateButton.setEnabled(false);
        buttonsHL.addComponent(simulateButton);
        buttonsHL.setComponentAlignment(simulateButton, Alignment.BOTTOM_LEFT);

        return vl;
    }

    private Component createROProbNav() {
        probTable = new Table();
        probTable.setWidth("100%");
        probTable.setHeight("100%");
        probTable.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
        probTable.addStyleName("catDashC2");
        probTable.setImmediate(true);
        probTable.setSelectable(true);
        probTable.setMultiSelect(true);
        probTable.addListener(new ProbTableListener());

        String index = "Index";
        String roLabel = "R/O";
        probTable.addContainerProperty(index, String.class, null);
        probTable.addContainerProperty(roLabel, UUIDItem.class, null);
        probTable.setColumnExpandRatio(index, 1);
        probTable.setColumnExpandRatio(roLabel, 5);

        return probTable;
    }

    // Internal event handlers ---------------------------------------------------
    //private synchronized void onProbTableItemSelected( Set<UUIDItem> items )
    private synchronized void onProbTableItemSelected(Set<UUID> items) {
        if (items != null) {
            currSelectedROs.clear();

            if (items.size() > 0) {
                viewResultsButton.setEnabled(true);
                simulateButton.setEnabled(true);
            } else {
                viewResultsButton.setEnabled(false);
                simulateButton.setEnabled(false);
            }
            
            //for ( UUIDItem item : items )
            //  if ( item != null )
            //    currSelectedROs.add( item.getID() );
            for (UUID item : items) {
                if (item != null) {
                    currSelectedROs.add(item);
                }
            }

            //if (!currSelectedROs.isEmpty()) { //KEM commented out to allow selection to be cleared
                List<ROMatrixViewListener> listeners = getListenersByType();
                for (ROMatrixViewListener listener : listeners) {
                    listener.onROSelected(currSelectedROs);
                }
            //}
        }
        else {
            viewResultsButton.setEnabled(false);
            simulateButton.setEnabled(false);
        }
    }

    private class ProbTableListener implements Table.ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent vce) {
            //onProbTableItemSelected( (Set<UUIDItem>) vce.getProperty().getValue() );
            onProbTableItemSelected((Set<UUID>) vce.getProperty().getValue());
        }
    }
    
    private void onSimulateButtonClicked() {
        List<ROMatrixViewListener> listeners = getListenersByType();
        for (ROMatrixViewListener listener : listeners) {
            listener.onSimulateButtonClicked(currSelectedROs);
        }
    }

    private class SimulateButtonListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onSimulateButtonClicked();
        }
    }
    
    private void onViewResultsButtonClicked() {
        List<ROMatrixViewListener> listeners = getListenersByType();
        for (ROMatrixViewListener listener : listeners) {
            listener.onViewResultsButtonClicked(currSelectedROs);
        }
    }

    private class ViewResultsButtonListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onViewResultsButtonClicked();
        }
    }
    
}