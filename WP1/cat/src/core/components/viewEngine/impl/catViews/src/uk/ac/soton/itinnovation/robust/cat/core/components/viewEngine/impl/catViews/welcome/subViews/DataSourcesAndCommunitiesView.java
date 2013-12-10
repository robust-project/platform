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
//      Created Date :          19 Jun 2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.subViews;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.Paintable;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.LabelledComponent;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.WindowView;

import com.vaadin.ui.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaadin.dialogs.ConfirmDialog;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.ViewResources;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UUIDItem;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.WelcomeController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.WelcomeViewListener;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.subViewEvents.SelectedCommunitiesCD;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.DataSource;

public class DataSourcesAndCommunitiesView extends WindowView {
    
    private WelcomeController welcomeController;

    private ListSelect dataSourcesList;
    private ListSelect availableCommunitiesList;
    private ListSelect selectedCommunitiesList;

    private UUIDItem currSelectedDataSource;
    private UUIDItem currSelectedAvailableCommunity;
    private UUIDItem currSelectedCurrentCommunity;

    private Label dataSourceDetails;
    private Label availableCommunityDetails;
    private Label selectedCommunityDetails;
    
    private Button removeButton;
    private Button addCommunityButton;
    private Button removeCommunityButton;
    private Button refreshAvailableCommunitiesButton;
    
    private String panelWidth = "200px";
    private String listSelectHeight = "100px";
    private String detailsHeight = "100px";
    
    private HashSet<String> availableCommunitiesSet;
    private HashSet<String> selectedCommunitiesSet;
    private SelectedCommunitiesCD selectedCommunitiesCD;
    
    public DataSourcesAndCommunitiesView(Component parent, WelcomeController welcomeController) {
        //super(parent, "Data Sources and Communities");
        super(parent, "");
        
        this.welcomeController = welcomeController;
        
        // Size & position window
        window.setWidth("715px");
        window.setHeight("400px");
        //centreWindow();
        window.setPositionX(100);
        window.setPositionY(100);

        createComponents();
        
        setDataSources();
    }

    private void setDataSources() {
        dataSourcesList.removeAllItems();
        Set<DataSource> dataSources = welcomeController.getCurrentDataSources();
        if (dataSources == null)
            return;

        for (DataSource dataSource : dataSources) {
            UUIDItem item = new UUIDItem(dataSource.getName(), dataSource.getUuid(), dataSource);
            dataSourcesList.addItem(item);
        }
    }
    
    private void addDataSource(DataSource dataSource) {
        UUIDItem item = new UUIDItem(dataSource.getName(), dataSource.getUuid(), dataSource);
        dataSourcesList.addItem(item);
        updateView();
    }
    
    private void removeDataSource(UUID dataSourceID) {
        for (Object itemId : dataSourcesList.getItemIds()) {
            UUIDItem dsItem = (UUIDItem) itemId;
            if (dsItem.getID().equals(dataSourceID)) {
                dataSourcesList.removeItem(dsItem);
                updateView();
                break;
            }
        }
    }

    public void onDataSourceAdded(DataSource dataSource) {
        addDataSource(dataSource);
    }

    public void onDataSourceDeleted(UUID dataSourceID) {
        removeDataSource(dataSourceID);
    }

    public synchronized void setCommunitiesForCurrentDataSource(Set<Community> dataSourceCommunities, Set<Community> currentCommunities) {
        availableCommunitiesList.removeAllItems();
        selectedCommunitiesList.removeAllItems();
        
        availableCommunitiesSet = new HashSet<String>();
        selectedCommunitiesSet = new HashSet<String>();
        
        selectedCommunitiesCD = new SelectedCommunitiesCD();
        selectedCommunitiesCD.reset();

        if (currentCommunities != null) {
            for (Community comm : currentCommunities) {
                UUIDItem item = new UUIDItem(comm.getName(), comm.getUuid(), comm);
                selectedCommunitiesList.addItem(item);
                String commKey = comm.getName() + comm.getCommunityID();
                selectedCommunitiesSet.add(commKey);
            }
        }
        
        if (dataSourceCommunities != null) {
            for (Community comm : dataSourceCommunities) {
                UUIDItem item = new UUIDItem(comm.getName(), comm.getUuid(), comm);
                String commKey = comm.getName() + comm.getCommunityID();
                if (! selectedCommunitiesSet.contains(commKey)) {
                    availableCommunitiesSet.add(commKey);
                    availableCommunitiesList.addItem(item);
                }
            }
        }
        
    }
    
    // Private methods -----------------------------------------------------------
    private void createComponents() {
        VerticalLayout vl = (VerticalLayout) window.getContent();

        // Headline
        vl.addComponent(createHeadline("Data Sources and Communities"));

        // Data source and community panels
        HorizontalLayout hl = new HorizontalLayout();
        vl.addComponent(hl);
        //hl.addComponent(UILayoutUtil.createSpace("12px", null, true));

        // Data sources panel
        hl.addComponent(createDataSourcesPanel());

        // Space
        hl.addComponent( UILayoutUtil.createSpace("12px", null, true) );
        
        // Vertical line
        Component spacer = UILayoutUtil.createSpace("11px", "catVertRule", true);
        spacer.setHeight("100%");
        hl.addComponent(spacer);

        // Communities panel
        hl.addComponent(createCommunitiesPanel());

        // Space and horizontal line
        vl.addComponent(UILayoutUtil.createSpace("8px", null));
        Component spacer2 = UILayoutUtil.createSpace("8px", "catHorizRule", true);
        spacer2.setWidth("100%");
        vl.addComponent(spacer2);
        vl.addComponent(UILayoutUtil.createSpace("8px", null));
       
        // Buttons
        HorizontalLayout bHL = new HorizontalLayout();
        vl.addComponent(bHL);
        vl.setComponentAlignment(bHL, Alignment.BOTTOM_RIGHT);

        Button button = new Button("Cancel");
        button.addListener(new DiscardChangesListener());
        bHL.addComponent(button);

        bHL.addComponent(UILayoutUtil.createSpace("4px", null, true));

        button = new Button("Save");
        button.addListener(new SaveChangesListener());
        bHL.addComponent(button);

        // Space
        bHL.addComponent(UILayoutUtil.createSpace("24px", null, true));
    }
    
    private Component createDataSourcesPanel() {
        VerticalLayout vl = new VerticalLayout();

        Label label = new Label("Data Sources");
        label.addStyleName("catSectionFont");
        label.addStyleName("catBlue");
        vl.addComponent(label);

        // Space
        vl.addComponent(UILayoutUtil.createSpace("4px", null));

        // Community list
        dataSourcesList = new ListSelect();
        dataSourcesList.setWidth(panelWidth);
        dataSourcesList.setHeight(listSelectHeight);
        dataSourcesList.setImmediate(true);
        dataSourcesList.addListener(new DataSourceSelectListener());
        vl.addComponent(dataSourcesList);
        vl.setComponentAlignment(dataSourcesList, Alignment.MIDDLE_LEFT);

        // Space
        vl.addComponent(UILayoutUtil.createSpace("4px", null));

        // Label
        Label label2 = new Label("Details");
        label2.addStyleName("catSectionFont");
        label2.addStyleName("catBlue");
        vl.addComponent(label2);

        // Space
        vl.addComponent(UILayoutUtil.createSpace("4px", null));

        // Description
        dataSourceDetails = new Label();
        dataSourceDetails.setContentMode(Label.CONTENT_RAW);
        dataSourceDetails.addStyleName("catBorder");
        dataSourceDetails.addStyleName("small");
        dataSourceDetails.setWidth(panelWidth);
        dataSourceDetails.setHeight(detailsHeight);
        vl.addComponent(dataSourceDetails);
        vl.setComponentAlignment(dataSourceDetails, Alignment.MIDDLE_LEFT);
    
        vl.addComponent(UILayoutUtil.createSpace("12px", null));

        // Add/remove controls
        HorizontalLayout hl = new HorizontalLayout();
        vl.addComponent(hl);
        vl.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);

        Button button = new Button("Add");
        button.setDescription("Add data source for communities");
        button.addListener(new AddDataSourceButtonListener());
        //button.setEnabled(!isPublicDemo());
        hl.addComponent(button);
        hl.setExpandRatio(button, 0.5f);

        // Space
        hl.addComponent(UILayoutUtil.createSpace("4", null, true));

        removeButton = new Button("Remove");
        removeButton.setDescription("Remove data source");
        removeButton.addListener(new RemoveDataSourceButtonListener());
        removeButton.setEnabled(false);
        hl.addComponent(removeButton);
        hl.setExpandRatio(removeButton, 0.5f);

        // Space
        hl.addComponent(UILayoutUtil.createSpace("4", null, true));

        return vl;
    }
    
    private Component createCommunitiesPanel() {
        HorizontalLayout hl = new HorizontalLayout();        
        // Available communities
        hl.addComponent(createAvailableCommunitiesPanel());
        
        // Space
        hl.addComponent(UILayoutUtil.createSpace("12px", null, true));

        // Arrow controls
        hl.addComponent(createArrowControls());

        // Space
        hl.addComponent(UILayoutUtil.createSpace("12px", null, true));

        // Current communities
        hl.addComponent(createSelectedCommunitiesPanel());

        return hl;
    }

    private Component createAvailableCommunitiesPanel() {
        VerticalLayout vl = new VerticalLayout();

        Label label = new Label("Available Communities");
        label.addStyleName("catSectionFont");
        label.addStyleName("catBlue");
        vl.addComponent(label);

        // Space
        vl.addComponent(UILayoutUtil.createSpace("4px", null));

        // Available communities list
        availableCommunitiesList = new ListSelect();
        availableCommunitiesList.setWidth(panelWidth);
        availableCommunitiesList.setHeight(listSelectHeight);
        availableCommunitiesList.setImmediate(true);
        availableCommunitiesList.addListener(new AvailableCommuntiesSelectListener());
        vl.addComponent(availableCommunitiesList);
        vl.setComponentAlignment(availableCommunitiesList, Alignment.MIDDLE_LEFT);
        
        // Space
        vl.addComponent(UILayoutUtil.createSpace("4px", null));

        // Label
        Label label2 = new Label("Details");
        label2.addStyleName("catSectionFont");
        label2.addStyleName("catBlue");
        vl.addComponent(label2);

        // Space
        vl.addComponent(UILayoutUtil.createSpace("4px", null));

        // Description
        availableCommunityDetails = new Label();
        availableCommunityDetails.setContentMode(Label.CONTENT_RAW);
        availableCommunityDetails.addStyleName("catBorder");
        availableCommunityDetails.addStyleName("small");
        availableCommunityDetails.setWidth(panelWidth);
        availableCommunityDetails.setHeight(detailsHeight);
        vl.addComponent(availableCommunityDetails);
        vl.setComponentAlignment(availableCommunityDetails, Alignment.MIDDLE_LEFT);
    
        // Space
        vl.addComponent(UILayoutUtil.createSpace("12px", null));

        refreshAvailableCommunitiesButton = new Button("Refresh List");
        refreshAvailableCommunitiesButton.setDescription("Get updated communities list from data source");
        refreshAvailableCommunitiesButton.addListener(new RefreshAvailableCommunitiesButtonListener());
        refreshAvailableCommunitiesButton.setEnabled(false);
        vl.addComponent(refreshAvailableCommunitiesButton);
        vl.setExpandRatio(refreshAvailableCommunitiesButton, 0.5f);

        return vl;
    }
    
    private Component createSelectedCommunitiesPanel() {        
        VerticalLayout vl = new VerticalLayout();
        
        Label label = new Label("Selected Communities");
        label.addStyleName("catSectionFont");
        label.addStyleName("catBlue");
        vl.addComponent(label);

        // Space
        vl.addComponent(UILayoutUtil.createSpace("4px", null));

        // Selected communities list
        selectedCommunitiesList = new ListSelect();
        selectedCommunitiesList.setWidth(panelWidth);
        selectedCommunitiesList.setHeight(listSelectHeight);
        selectedCommunitiesList.setImmediate(true);
        selectedCommunitiesList.addListener(new CurrentCommuntiesSelectListener());
        vl.addComponent(selectedCommunitiesList);
        vl.setComponentAlignment(selectedCommunitiesList, Alignment.MIDDLE_LEFT);

        // Space
        vl.addComponent(UILayoutUtil.createSpace("4px", null));

        // Label
        Label label2 = new Label("Details");
        label2.addStyleName("catSectionFont");
        label2.addStyleName("catBlue");
        vl.addComponent(label2);

        // Space
        vl.addComponent(UILayoutUtil.createSpace("4px", null));

        // Description
        selectedCommunityDetails = new Label();
        selectedCommunityDetails.setContentMode(Label.CONTENT_RAW);
        selectedCommunityDetails.addStyleName("catBorder");
        selectedCommunityDetails.addStyleName("small");
        selectedCommunityDetails.setWidth(panelWidth);
        selectedCommunityDetails.setHeight(detailsHeight);
        vl.addComponent(selectedCommunityDetails);
        vl.setComponentAlignment(selectedCommunityDetails, Alignment.MIDDLE_LEFT);
    
        vl.addComponent(UILayoutUtil.createSpace("4", null));

        return vl;
    }
    
    private Component createArrowControls() {
        VerticalLayout vl = new VerticalLayout();

        // Space
        vl.addComponent(UILayoutUtil.createSpace("30px", null));

        addCommunityButton = new Button();
        addCommunityButton.addStyleName("small");
        addCommunityButton.addStyleName("icon-on-top");
        addCommunityButton.setIcon(ViewResources.CATAPPResInstance.getResource("16x16RightArrow"));
        addCommunityButton.setEnabled(false);
        addCommunityButton.setImmediate(true);
        addCommunityButton.addListener(new RightClickListener());
        vl.addComponent(addCommunityButton);

        // Space
        vl.addComponent(UILayoutUtil.createSpace("30px", null));

        removeCommunityButton = new Button();
        removeCommunityButton.addStyleName("small");
        removeCommunityButton.addStyleName("icon-on-top");
        removeCommunityButton.setIcon(ViewResources.CATAPPResInstance.getResource("16x16LeftArrow"));
        removeCommunityButton.setEnabled(false);
        removeCommunityButton.setImmediate(true);
        removeCommunityButton.addListener(new LeftClickListener());
        vl.addComponent(removeCommunityButton);

        return vl;
    }

    // Private internal event handlers -------------------------------------------
    private void onDiscardChangesClicked() {
        setVisible(false);
    }

    private void onSaveChangesClicked() {

        if ((selectedCommunitiesCD == null) || (! selectedCommunitiesCD.isDataChanged()) ) {
            System.out.println("onSaveChangesClicked: no changes to save");
        }
        else if (selectedCommunitiesCD.getRemovedCommunities().size() > 0) {
            // if we are removing any communities, confirm with user
            createRemoveCommunitiesConfirmDialog();
        }
        else {
            // otherwise save changes anyway
            saveChanges();
        }
        
        setVisible(false);
    }
    
    private void createRemoveCommunitiesConfirmDialog() {
        ConfirmDialog.show(compParent.getWindow(), "Please Confirm:", "Deleting one or more communities with any defined risks, etc. OK?",
                "Yes", "No", new ConfirmDialog.Listener() {
            @Override
            public void onClose(ConfirmDialog dialog) {
                if (dialog.isConfirmed()) {
                    // Confirmed to continue
                    saveChanges();
                } else {
                    // User did not confirm
                }
            }
        });
    }

    private void saveChanges() {
        System.out.println("Saving data sources and communities");
        
        Collection<WelcomeViewListener> listeners = getListenersByType();
        for (WelcomeViewListener listener : listeners) {
            Set<Community> addedCommunities = selectedCommunitiesCD.getAddedCommunities();
            Set<Community> removedCommunities = selectedCommunitiesCD.getRemovedCommunities();
            if (addedCommunities.size() > 0) {
                listener.onAddCommunities(addedCommunities);
            }
            if (removedCommunities.size() > 0) {
                listener.onRemoveCommunities(removedCommunities);
            }
        }
        
        setVisible(false);
    }
  
    // Internal event listeners --------------------------------------------------
    private void onDataSourceSelectChange(UUIDItem item) {
        if (item != null) {
            currSelectedDataSource = item;
            DataSource ds = (DataSource)currSelectedDataSource.getData();
            dataSourceDetails.setValue(setDataSourceDetails(ds));
            removeButton.setEnabled(true);
            refreshAvailableCommunitiesButton.setEnabled(true);
            
            Collection<WelcomeViewListener> listeners = getListenersByType();

            for (WelcomeViewListener listener : listeners) {
                listener.onDataSourceSelected(currSelectedDataSource.getID());
            }

        } else {
            currSelectedDataSource = null;
            dataSourceDetails.setValue("");
            removeButton.setEnabled(false);
            refreshAvailableCommunitiesButton.setEnabled(false);
            availableCommunitiesList.removeAllItems();
            selectedCommunitiesList.removeAllItems();
        }
    }

    private void onAvailableCommuntiesSelectChange(UUIDItem item) {
        if (item != null) {
            currSelectedAvailableCommunity = item;
            Community comm = (Community)currSelectedAvailableCommunity.getData();
            availableCommunityDetails.setValue(setCommunityDetails(comm));
            addCommunityButton.setEnabled(true);
        } else {
            currSelectedAvailableCommunity = null;
            availableCommunityDetails.setValue("");
            addCommunityButton.setEnabled(false);
        }
    }

    private void onCurrentCommuntiesSelectChange(UUIDItem item) {
        if (item != null) {
            currSelectedCurrentCommunity = item;
            Community comm = (Community)currSelectedCurrentCommunity.getData();
            selectedCommunityDetails.setValue(setCommunityDetails(comm));
            removeCommunityButton.setEnabled(true);
        } else {
            currSelectedCurrentCommunity = null;
            selectedCommunityDetails.setValue("");
            removeCommunityButton.setEnabled(false);
        }
    }

    private String setDataSourceDetails(DataSource ds) {
        String details;
        details = "Endpoint: " + ds.getEndpoint() + "</br>";
        details += "Description: N/A";
        return details;
    }
    
    private String setCommunityDetails(Community comm) {
        String details;
        details = "ID: " + comm.getCommunityID() + "</br>";
        details += "Description: N/A";
        return details;
    }
    
    private void onAddDataSourceClick() {
        DataSourceCreateView dataSourceCreateView = new DataSourceCreateView(window.getContent());
        dataSourceCreateView.addListener(welcomeController);
    }

    private void onRemoveDataSourceClick() {
        if (currSelectedDataSource != null
                && !currSelectedDataSource.getLabel().equals("")) {

            //TODO - pop up confirmation dialog

            Collection<WelcomeViewListener> listeners = getListenersByType();

            for (WelcomeViewListener listener : listeners) {
                listener.onRemoveDataSource(currSelectedDataSource.getID());
            }
        }
    }
    
    private void onRefreshAvailableCommunitiesClick() {
        try {
            onDataSourceSelectChange(currSelectedDataSource);
        } catch (Exception e) {
            e.printStackTrace();
            displayWarning("Data source error:", "Could not get communities. Please click refresh to try again.");
        }
    }
        
    private synchronized void onAddCommunityClick() {
        if (currSelectedAvailableCommunity != null) {
            Community comm = (Community)currSelectedAvailableCommunity.getData();
            if (comm != null) {
                selectedCommunitiesList.addItem(currSelectedAvailableCommunity);
                availableCommunitiesList.removeItem(currSelectedAvailableCommunity);
                selectedCommunitiesCD.addCommunity(comm);
            }
        }
    }

    private synchronized void onRemoveCommunityClick() {
        if (currSelectedCurrentCommunity != null) {
            Community comm = (Community)currSelectedCurrentCommunity.getData();
            if (comm != null) {
                availableCommunitiesList.addItem(currSelectedCurrentCommunity);
                selectedCommunitiesList.removeItem(currSelectedCurrentCommunity);
                selectedCommunitiesCD.removeCommunity(comm);
            }
        }
    }

    private class DataSourceSelectListener implements Property.ValueChangeListener {
        @Override
        public void valueChange(Property.ValueChangeEvent vce) {
            try {
                onDataSourceSelectChange((UUIDItem) vce.getProperty().getValue());
            }
            catch (Exception e) {
                e.printStackTrace();
                displayWarning("Data source error:", "Could not get communities. Please click refresh to try again.");
            }
        }
    }
    
    private class AvailableCommuntiesSelectListener implements Property.ValueChangeListener {
        @Override
        public void valueChange(Property.ValueChangeEvent vce) {
            onAvailableCommuntiesSelectChange((UUIDItem) vce.getProperty().getValue());
        }
    }

    private class CurrentCommuntiesSelectListener implements Property.ValueChangeListener {
        @Override
        public void valueChange(Property.ValueChangeEvent vce) {
            onCurrentCommuntiesSelectChange((UUIDItem) vce.getProperty().getValue());
        }
    }

    private class AddDataSourceButtonListener implements Button.ClickListener {
        @Override
        public void buttonClick(Button.ClickEvent ce) {
            onAddDataSourceClick();
        }
    }

    private class RemoveDataSourceButtonListener implements Button.ClickListener {
        @Override
        public void buttonClick(Button.ClickEvent ce) {
            onRemoveDataSourceClick();
        }
    }

    private class RefreshAvailableCommunitiesButtonListener implements Button.ClickListener {
        @Override
        public void buttonClick(Button.ClickEvent ce) {
            onRefreshAvailableCommunitiesClick();
        }
    }
            
    private class RightClickListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onAddCommunityClick();
        }
    }
    
    private class LeftClickListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onRemoveCommunityClick();
        }
    }
    
    private class DiscardChangesListener implements Button.ClickListener {
        @Override
        public void buttonClick(Button.ClickEvent event) {
            onDiscardChangesClicked();
        }
    }

    private class SaveChangesListener implements Button.ClickListener {
        @Override
        public void buttonClick(Button.ClickEvent event) {
            onSaveChangesClicked();
        }
    }
    
}