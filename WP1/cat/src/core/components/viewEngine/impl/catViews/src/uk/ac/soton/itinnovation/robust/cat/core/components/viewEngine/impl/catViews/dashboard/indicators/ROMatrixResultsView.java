/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2013
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
//      Created By :            Ken Meacham
//      Created Date :          16 Sep 2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import java.text.DecimalFormat;
import java.util.List;
import uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.WindowView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

public class ROMatrixResultsView extends WindowView {
    private VerticalLayout resultsVL;
    
    private static final String NAME = "name";
    private static final String PROBABILITY = "probability";
    
    public ROMatrixResultsView(Component parent, String title, List<RiskEvalResult> riskEvalResults) {
        super(parent, "Results");
        
        // Size & position window
        window.setWidth("210px");
        window.setHeight("270px");
        
        //centreWindow();
        window.setPositionX(400);
        window.setPositionY(300);

        createComponents();
        
        displayResults(riskEvalResults);
    }

    private void createComponents() {
        VerticalLayout vl = (VerticalLayout) window.getContent();

        //// Headline
        //vl.addComponent(createHeadline("Results"));

        //// Space
        //vl.addComponent(UILayoutUtil.createSpace("12px", null));
        
        resultsVL = new VerticalLayout();
        
        vl.addComponent(resultsVL);
        // Space
        vl.addComponent(UILayoutUtil.createSpace("40px", null));

        // Buttons
        HorizontalLayout bHL = new HorizontalLayout();
        vl.addComponent(bHL);
        vl.setComponentAlignment(bHL, Alignment.BOTTOM_RIGHT);

        Button button = new Button("Close");
        button.addListener(new CloseButtonListener());
        bHL.addComponent(button);

    }

    public void displayResults(List<RiskEvalResult> riskEvalResults) {
        resultsVL.removeAllComponents();
        
        for (RiskEvalResult rer : riskEvalResults) {
            System.out.println(rer.getRiskTitle());
            resultsVL.addComponent(createRiskTitle(rer.getRiskTitle()));
            List<ResultItem> results = rer.getResults();
            resultsVL.addComponent(createResultsTable(results));
        }
    }
    
    private Component createRiskTitle(String title) {
        VerticalLayout vl = new VerticalLayout();
        
        vl.addStyleName("catSubHeadlineFont");
        vl.addComponent(UILayoutUtil.createSpace("8px", null));

        Label riskTitleLabel = new Label();
        vl.addComponent(riskTitleLabel);
        riskTitleLabel.setCaption(title);

        // Space & ruler & space
        vl.addComponent(UILayoutUtil.createSpace("2px", null));
        Component spacer = UILayoutUtil.createSpace("4px", "catHorizRule", true);
        spacer.setWidth("100%");
        vl.addComponent(spacer);
        vl.addComponent(UILayoutUtil.createSpace("8px", null));

        return vl;
    }

    private Component createResultsTable(List<ResultItem> results) {
        VerticalLayout vl = new VerticalLayout();
        
        Table resultsTable = new Table();
        resultsTable.addContainerProperty(NAME, String.class, null);
        resultsTable.addContainerProperty(PROBABILITY, String.class, null);
        resultsTable.setWidth("185px");
        resultsTable.setHeight("100px");
        vl.addComponent(resultsTable);
        
        DecimalFormat df = new DecimalFormat("0.000");
        
        for (ResultItem result : results) {
            System.out.println(result.getName() + ": " + result.getProbability());
            resultsTable.addItem(new Object[] { result.getName(),
                                                df.format(result.getProbability())},
                                                null);
        }
        
        return vl;
    }

    private void onCloseButtonClicked() {
        setVisible(false);
    }
  
    private class CloseButtonListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onCloseButtonClicked();
        }
    }

}
