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
//      Created Date :          28 Mar 2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.subViews;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.LabelledComponent;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.WindowView;

import com.vaadin.ui.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.WelcomeViewListener;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;

public class CommunityObjectiveCreateView extends WindowView {

    private Community community;
    
    private TextField objectiveNameField;
    private TextArea objectiveSummaryTextArea;

    public CommunityObjectiveCreateView(Component parent, Community comm) {
        super(parent, "Create new objective");
        
        community = comm;

        // Size & position window
        window.setWidth("310px");
        window.setHeight("350px");
        centreWindow();

        createComponents();
    }

    // Private methods -----------------------------------------------------------
    private void createComponents() {
        VerticalLayout vl = (VerticalLayout) window.getContent();

        // Headline
        vl.addComponent(createHeadline("Please enter objective details"));

        // Space
        vl.addComponent(UILayoutUtil.createSpace("12px", null));

        // Community components
        HorizontalLayout hl = new HorizontalLayout();
        vl.addComponent(hl);
        //hl.addComponent(UILayoutUtil.createSpace("12px", null, true));
        VerticalLayout compVL = new VerticalLayout();
        hl.addComponent(compVL);
        //hl.addComponent(UILayoutUtil.createSpace("12px", null, true));

        // Name
        objectiveNameField = new TextField();
        objectiveNameField.setWidth("180px");
        LabelledComponent lc = new LabelledComponent("Name", "80px", objectiveNameField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));

        // Summary
        objectiveSummaryTextArea = new TextArea();
        objectiveSummaryTextArea.setWidth("180px");
        lc = new LabelledComponent("Summary", "80px", objectiveSummaryTextArea);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));

        // Space
        vl.addComponent(UILayoutUtil.createSpace("40px", null));

        // Buttons
        HorizontalLayout bHL = new HorizontalLayout();
        vl.addComponent(bHL);
        vl.setComponentAlignment(bHL, Alignment.BOTTOM_RIGHT);

        Button button = new Button("Cancel");
        button.addListener(new DiscardObjectiveListener());
        bHL.addComponent(button);

        bHL.addComponent(UILayoutUtil.createSpace("4px", null, true));

        button = new Button("Save objective");
        button.addListener(new SaveObjectiveListener());
        bHL.addComponent(button);

        // Space
        bHL.addComponent(UILayoutUtil.createSpace("24px", null, true));
    }

    // Private internal event handlers -------------------------------------------
    private void onDiscardObjectiveClicked() {
        setVisible(false);
    }

    private void onSaveObjectiveClicked() {
        String objectiveName = (String)objectiveNameField.getValue();
        String objectiveSummary = (String)objectiveSummaryTextArea.getValue();

        if (objectiveName != null) {
            /*
            CreateViewCD cd = new CreateViewCD(tf, (boolean) (tf.equals("Risk")),
                    ow, gp);

            Collection<RiskEditCommitListener> listeners = getListenersByType();
            for (RiskEditCommitListener listener : listeners) {
                listener.onCommitCreateViewChanges(cd);
            }
                    * */
            
            //CommunityCreateViewCD cd = new CommunityCreateViewCD(commName, commId, commUri, isStream, commStreamName);
            
            try {
                Objective objective = new Objective(objectiveName, objectiveSummary);

                Collection<WelcomeViewListener> listeners = getListenersByType();
                for (WelcomeViewListener listener : listeners) {
                    listener.onAddCommunityObjective(community.getUuid(), objective);
                }

            } catch (Exception ex) {
                Logger.getLogger(CommunityObjectiveCreateView.class.getName()).log(Level.SEVERE, null, ex);
            }

            setVisible(false);
        }
        
    }

    private class DiscardObjectiveListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onDiscardObjectiveClicked();
        }
    }

    private class SaveObjectiveListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onSaveObjectiveClicked();
        }
    }
}