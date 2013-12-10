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
//      Created Date :          22 Mar 2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.subViews;

import com.vaadin.data.Property;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.WindowView;

import com.vaadin.ui.*;
import java.util.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UUIDItem;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.WelcomeController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.WelcomeViewListener;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;

    // Private internal event handlers -------------------------------------------
public class CommunityObjectivesView extends WindowView {
    
    private WelcomeController welcomeController;
    private CommunityObjectiveCreateView communityCreateView;
    private ListSelect commObjTitleList;
    private Label commObjSummary;
    private Button removeButton;
    
    private Community community;
    private HashMap<UUID, Objective> currObjectives;
    private UUIDItem currSelectedObjective;

    public CommunityObjectivesView(Component parent, WelcomeController ctrl) {
        super(parent, "Community objectives");

        welcomeController = ctrl;
        
        // Size & position window
        window.setWidth("470px");
        window.setHeight("380px");
        centreWindow();
        
        currObjectives = new HashMap<UUID, Objective>();

        createComponents();
    }

    @Override
    public void updateView() {
        community = welcomeController.getCurrentCommunity();
        setHeadline("Objectives: " + welcomeController.getCurrentCommunityName());

        //changeData.reset();
        updateComponents();
    }

    // Private methods -----------------------------------------------------------
    private void createComponents() {
        VerticalLayout vl = (VerticalLayout) window.getContent();
        
        // Headline
        vl.addComponent(createHeadline("Objectives: "));

        // Space
        vl.addComponent(UILayoutUtil.createSpace("12px", null));
        
        // Main controls
        vl.addComponent( createMainControls() );

        // Space
        vl.addComponent(UILayoutUtil.createSpace("12px", null));

        // Add/remove controls
        HorizontalLayout hl = new HorizontalLayout();
        vl.addComponent(hl);
        vl.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
        
        Button button = new Button("Add objective");
        button.addListener(new AddObjectiveButtonListener());
        button.setEnabled(true);
        hl.addComponent(button);
        hl.setExpandRatio(button, 0.5f);

        // Space
        hl.addComponent(UILayoutUtil.createSpace("4", null, true));

        removeButton = new Button("Remove objective");
        removeButton.addListener(new RemoveObjectiveButtonListener());
        removeButton.setEnabled(false);
        hl.addComponent(removeButton);
        hl.setExpandRatio(removeButton, 0.5f);

        // Space
        vl.addComponent(UILayoutUtil.createSpace("32", null));

        // Buttons
        HorizontalLayout bHL = new HorizontalLayout();
        vl.addComponent(bHL);
        vl.setComponentAlignment(bHL, Alignment.BOTTOM_RIGHT);

        //button = new Button("Cancel");
        //button.addListener(new DiscardCommunityObjectivesListener());
        button = new Button("Close");
        button.addListener(new CloseCommunityObjectivesListener());
        bHL.addComponent(button);

        /*
        bHL.addComponent(UILayoutUtil.createSpace("4px", null, true));

        button = new Button("Save objectives");
        button.addListener(new SaveCommunityObjectivesListener());
        bHL.addComponent(button);
        */

        // Space
        bHL.addComponent(UILayoutUtil.createSpace("24px", null, true));
    }
    
    private Component createMainControls() {
        HorizontalLayout hl = new HorizontalLayout();

        // Community objectives list
        hl.addComponent(createObjectivesList());

        // Space
        hl.addComponent(UILayoutUtil.createSpace("20px", null, true));

        // Objective summary
        hl.addComponent(createObjectiveSummary());
        
        return hl;
    }

    private Component createObjectivesList() {
        VerticalLayout vl = new VerticalLayout();
        
        // Label
        Label label = new Label("Objectives");
        label.addStyleName("catSectionFont");
        label.addStyleName("catBlueDark");
        vl.addComponent(label);

        // Objectives list
        commObjTitleList = new ListSelect();
        commObjTitleList.setWidth("200px");
        commObjTitleList.setHeight("150px");
        commObjTitleList.addListener(new ObjectiveSelectListener());
        commObjTitleList.setImmediate(true);
        
        vl.addComponent(commObjTitleList);
        
        return vl;
    }

    private Component createObjectiveSummary() {
        VerticalLayout vl = new VerticalLayout();
        
        // Objectives description
        Label label = new Label("Summary");
        label.addStyleName("catSectionFont");
        label.addStyleName("catBlueDark");
        vl.addComponent(label);

        commObjSummary = new Label();
        commObjSummary.setWidth("200px");
        commObjSummary.setHeight("150px");
        commObjSummary.addStyleName("catBorder");
        commObjSummary.setEnabled(true);
        vl.addComponent(commObjSummary);
        
        return vl;
    }
    
    private void updateComponents() {
        currObjectives.clear();

        commObjTitleList.removeAllItems();
        
        Set<Objective> objectives = welcomeController.getCurrentObjectives();
        
        if (objectives != null) {
            for (Objective objective : objectives) {
                addCurrentObjective(objective);
            }
        }
        
        commObjTitleList.requestRepaint();
    }

    private void addCurrentObjective(Objective obj) {
        UUIDItem item = new UUIDItem(obj.getTitle(), obj.getIdAsUUID());

        currObjectives.put(item.getID(), obj);
        commObjTitleList.addItem(item);
    }


    private void onDiscardCommunityObjectivesClicked() {
        //TODO - reject any changes, if we use this method
        setVisible(false);
    }

    private void onCloseCommunityObjectivesClicked() {
        setVisible(false);
    }

    private void onSaveCommunityObjectivesClicked() {        
        setVisible(false);
    }

    private void onObjSelected(UUIDItem item) {
        if (item != null) {
            currSelectedObjective = item;
            removeButton.setEnabled(true);

            Objective targObj = currObjectives.get(item.getID());

            if (targObj != null) {
                commObjSummary.setValue(targObj.getDescription());
            }
        }
        else {
            currSelectedObjective = null;
            commObjSummary.setValue("");
            removeButton.setEnabled(true);
        }
    }
    
    private void onAddObjectiveClick() {
        //if (communityCreateView == null)
        //{
        communityCreateView = new CommunityObjectiveCreateView(window, community);
        communityCreateView.addListener(welcomeController);
        //}
        //else
        //    communityCreateView.setVisible(true);
    }

    private void onRemoveObjectiveClick() {
        if (currSelectedObjective != null) {

            //TODO - pop up confirmation dialog

            Collection<WelcomeViewListener> listeners = getListenersByType();

            for (WelcomeViewListener listener : listeners) {
                Objective objective = currObjectives.get( currSelectedObjective.getID() );
                listener.onRemoveCommunityObjective(community.getUuid(), objective);
            }
        }
    }
  
    private class DiscardCommunityObjectivesListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onDiscardCommunityObjectivesClicked();
        }
    }

    private class CloseCommunityObjectivesListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onCloseCommunityObjectivesClicked();
        }
    }

    private class SaveCommunityObjectivesListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onSaveCommunityObjectivesClicked();
        }
    }
    
      private class ObjectiveSelectListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent vce) {
            onObjSelected((UUIDItem) vce.getProperty().getValue());
        }
    }

    public class AddObjectiveButtonListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent ce) {
            onAddObjectiveClick();
        }
    }

    public class RemoveObjectiveButtonListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent ce) {
            onRemoveObjectiveClick();
        }
    }
}