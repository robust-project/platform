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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.LabelledComponent;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.WindowView;

import com.vaadin.ui.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UUIDItem;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.WelcomeController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.WelcomeViewListener;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.DBType;
import uk.ac.soton.itinnovation.robust.riskmodel.DataLocation;
import uk.ac.soton.itinnovation.robust.riskmodel.DataSource;
import uk.ac.soton.itinnovation.robust.riskmodel.DatabaseLocation;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;
import uk.ac.soton.itinnovation.robust.riskmodel.StreamLocation;

public class CommunityCreateView extends DataSourceDefinitionView {

    private WelcomeController welcomeController;

    private TextField communityNameField;
    private TextField communityIdField;
    private TextField platformField;
    
    private TextField databaseHostnameField;
    private TextField databasePortField;
    private TextField databaseSchemaNameField;

    private TextField streamNameField;
    private TextField streamUriField;

    private OptionGroup dataLocationOptions;
    
    private Select dataSourcesList;
    private UUIDItem currSelectedDataSource;
   
    private VerticalLayout dataSourceVL;
    private VerticalLayout databaseVL;
    private VerticalLayout streamVL;
    
    private Select databaseType;
    
    private static String MYSQL = "MySQL";
    private static String POSTGRES = "PostgreSQL";
    
    private final String[] databaseTypes = new String[] {MYSQL, POSTGRES};
    private final int[] databasePorts = new int[] {3306, 5432};
    private final HashMap<String, Integer> defaultDatabasePorts;
    
    private static String SIOC_DATASOURCE_OPTION = "SIOC data source";
    private static String DATABASE_OPTION = "Database";
    private static String STREAM_OPTION = "Stream data";
    
    private String panelWidth = "200px";
    private String listSelectHeight = "100px";

    public CommunityCreateView(Component parent, WelcomeController welcomeController) {
        super(parent, "Create new community");
        
        this.welcomeController = welcomeController;
        
        // Setup default database ports map
        defaultDatabasePorts = new HashMap<String, Integer>();
        for (int i=0; i<databaseTypes.length; i++) {
            defaultDatabasePorts.put(databaseTypes[i], databasePorts[i]);
        }
        
        // Size & position window
        window.setWidth("310px");
        window.setHeight("520px");
        //centreWindow();
        window.setPositionX(120);
        window.setPositionY(120);

        createComponents();
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

    private void createComponents() {
        VerticalLayout vl = (VerticalLayout) window.getContent();

        // Headline
        vl.addComponent(createHeadline("Please enter community details"));

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
        communityNameField = new TextField();
        communityNameField.setWidth("180px");
        LabelledComponent lc = new LabelledComponent("Name", "80px", communityNameField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));

        // ID
        communityIdField = new TextField();
        communityIdField.setWidth("180px");
        lc = new LabelledComponent("ID", "80px", communityIdField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));
        
        // Platform
        platformField = new TextField();
        platformField.setWidth("180px");
        lc = new LabelledComponent("Platform", "80px", platformField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));
        
        // Options (radio buttons)
        dataLocationOptions = new OptionGroup();
        dataLocationOptions.addItem(SIOC_DATASOURCE_OPTION);
        dataLocationOptions.addItem(DATABASE_OPTION);
        dataLocationOptions.addItem(STREAM_OPTION);
        dataLocationOptions.addListener(new DataLocationSelectListener());
        lc = new LabelledComponent("Data location", "80px", dataLocationOptions);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));

        dataSourceVL = new VerticalLayout();
        compVL.addComponent(dataSourceVL);
        createDataSourceComponents(dataSourceVL);
        
        databaseVL = new VerticalLayout();
        compVL.addComponent(databaseVL);
        createDatabaseComponents(databaseVL);
        
        streamVL = new VerticalLayout();
        compVL.addComponent(streamVL);
        createStreamComponents(streamVL);
        
        // select SIOC data source initially
        dataLocationOptions.select(SIOC_DATASOURCE_OPTION);
        
        // Space
        vl.addComponent(UILayoutUtil.createSpace("40px", null));

        // Buttons
        HorizontalLayout bHL = new HorizontalLayout();
        vl.addComponent(bHL);
        vl.setComponentAlignment(bHL, Alignment.BOTTOM_RIGHT);

        Button button = new Button("Cancel");
        button.addListener(new DiscardCommunityListener());
        bHL.addComponent(button);

        bHL.addComponent(UILayoutUtil.createSpace("4px", null, true));

        button = new Button("Save Community");
        button.addListener(new SaveCommunityListener());
        bHL.addComponent(button);

        // Space
        bHL.addComponent(UILayoutUtil.createSpace("24px", null, true));
    }

    // Private internal event handlers -------------------------------------------
    private void onDiscardCommunityClicked() {
        setVisible(false);
    }

    private void onSaveCommunityClicked() {
        String commName = (String)communityNameField.getValue();
        String commId = (String)communityIdField.getValue();
        String platform = (String)platformField.getValue();
        
        if ( (commName == null) || (commName.trim().equals(""))) {
            displayWarning("Error:", "Please enter a community name");
            return;
        }
        
        if ( (commId == null) || (commId.trim().equals(""))) {
            displayWarning("Error:", "Please enter community id");
            return;
        }
        
        if ( (platform == null) || (platform.trim().equals(""))) {
            displayWarning("Error:", "Please enter platform");
            return;
        }
        
        DataLocation dataLocation = getDataLocation();
        
        if (dataLocation == null) {
            System.out.println("WARNING: no data location created for new community");
            return;
        }

        //if (commName != null) {
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
                Community comm = new Community(commName, commId, dataLocation, platform);

                Collection<WelcomeViewListener> listeners = getListenersByType();
                for (WelcomeViewListener listener : listeners) {
                    listener.onAddCommunity(comm);
                }

            } catch (Exception ex) {
                Logger.getLogger(CommunityCreateView.class.getName()).log(Level.SEVERE, null, ex);
            }

            setVisible(false);
        //}
        
    }

    @Override
    public void createDataSourceComponents(VerticalLayout vl) {
        HorizontalLayout hl = new HorizontalLayout();
        // data sources list
        dataSourcesList = new Select();
        dataSourcesList.setWidth("100px");
        dataSourcesList.setImmediate(true);
        dataSourcesList.setNullSelectionAllowed(false);
        dataSourcesList.addListener(new DataSourceSelectListener());

        LabelledComponent lc = new LabelledComponent("Data source", "80px", dataSourcesList);
        hl.addComponent((Component) lc.getImplContainer());
        
        Button editDataSourcesButton = new Button("Edit");
        editDataSourcesButton.setDescription("Edit data sources");
        editDataSourcesButton.addListener(new EditDataSourcesListener());
        hl.addComponent(editDataSourcesButton);
        
        vl.addComponent(hl);
        vl.addComponent(UILayoutUtil.createSpace("10px", null));
        
        // Space
        vl.addComponent(UILayoutUtil.createSpace("4px", null));
        
        displaySourceNameField = false;
        setDefaults = false; // don't set default values
        
        // add data source details
        super.createDataSourceComponents(vl);
        
        //dataSourceHostnameField.setReadOnly(true);
        //dataSourceServiceField.setReadOnly(true);
        //dataSourceEndpointField.setReadOnly(true);
        
        dataSourceHostnameField.setEnabled(false);
        dataSourceServiceField.setEnabled(false);
        dataSourceEndpointField.setEnabled(false);
    }

    private void createDatabaseComponents(VerticalLayout compVL) {
        // Database type
        databaseType = new Select();
        databaseType.setWidth("100px");
        databaseType.setNullSelectionAllowed(false);
        
        for (String dbType : databaseTypes) {
            databaseType.addItem(dbType);
        }
        
        databaseType.addListener(new DatabaseTypeChangeListener());

        LabelledComponent lc = new LabelledComponent("Type", "80px", databaseType);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));
        
        // Database hostname
        databaseHostnameField = new TextField();
        databaseHostnameField.setWidth("180px");
        lc = new LabelledComponent("Hostname", "80px", databaseHostnameField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));
        
        // Database port
        databasePortField = new TextField();
        databasePortField.setWidth("180px");
        lc = new LabelledComponent("Port", "80px", databasePortField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));
        
        // Database schema name
        databaseSchemaNameField = new TextField();
        databaseSchemaNameField.setWidth("180px");
        lc = new LabelledComponent("DB Name", "80px", databaseSchemaNameField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));
        
        databaseType.select(databaseTypes[0]); // select first in list by default
    }

    private void createStreamComponents(VerticalLayout compVL) {
        // Stream name
        streamNameField = new TextField();
        streamNameField.setWidth("180px");
        LabelledComponent lc = new LabelledComponent("Stream Name", "80px", streamNameField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));
        
        // URI
        streamUriField = new TextField();
        streamUriField.setWidth("180px");
        lc = new LabelledComponent("URI", "80px", streamUriField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));
    }
    
    private void onDataLocationValueChanged() {
        String dataLocation = (String) dataLocationOptions.getValue();
        if (dataLocation == null) {
            // do nothing
        }
        else if (dataLocation.equals(SIOC_DATASOURCE_OPTION)) {
            setDataSources();
            dataSourceVL.setVisible(true);
            databaseVL.setVisible(false);
            streamVL.setVisible(false);
        }
        else if (dataLocation.equals(DATABASE_OPTION)) {
            dataSourceVL.setVisible(false);
            databaseVL.setVisible(true);
            streamVL.setVisible(false);
        }
        else if (dataLocation.equals(STREAM_OPTION)) {            
            dataSourceVL.setVisible(false);
            databaseVL.setVisible(false);
            streamVL.setVisible(true);
        }
    }
    
    private DataLocation getDataLocation() {
        DataLocation dataLocation = null;
        String dataLocationOption = (String) dataLocationOptions.getValue();
        System.out.println("Creating data location of type: " + dataLocationOption);
        if (dataLocationOption == null) {
            displayWarning("Error:", "Please select a data location");
        }
        else if (dataLocationOption.equals(SIOC_DATASOURCE_OPTION)) {
            UUIDItem dataSourceItem = (UUIDItem)dataSourcesList.getValue();
            if (dataSourceItem != null) {
                String dataSourceName = dataSourceItem.toString();
                String dataSourceEndpoint = ((String) dataSourceEndpointField.getValue()).trim();
                
                System.out.println("dataSourceName: " + dataSourceName);
                System.out.println("dataSourceEndpoint: " + dataSourceEndpoint);

                dataLocation = (DataLocation)dataSourceItem.getData();
            }
            else {
                displayWarning("Error:", "Please select a data source");
            }
        }
        else if (dataLocationOption.equals(DATABASE_OPTION)) {
            String databaseTypeItem = (String)databaseType.getValue();
            if (databaseTypeItem != null) {
                String dbTypeStr = databaseTypeItem.toString();
                String dbHostname = ((String)databaseHostnameField.getValue()).trim();
                String dbPort = ((String)databasePortField.getValue()).trim();
                String dbName = ((String)databaseSchemaNameField.getValue()).trim();
                
                DBType dbType;
                
                System.out.println("dbTypeStr: " + dbTypeStr);
                System.out.println("dbHostname: " + dbHostname);
                System.out.println("dbPort: " + dbPort);
                System.out.println("dbName: " + dbName);

                if (dbHostname.equals("")) {
                    displayWarning("Error:", "Please enter a database hostname");
                    return null;
                }

                if (dbPort.equals("")) {
                    displayWarning("Error:", "Please enter a database port");
                    return null;
                }
                
                if (dbName.equals("")) {
                    displayWarning("Error:", "Please enter a database name");
                    return null;
                }

                if (dbTypeStr.equals(MYSQL)) {
                    dbType = DBType.SQL;
                }
                else if (dbTypeStr.equals(POSTGRES)) {
                    dbType = DBType.POSTGRES;
                }
                else {
                    displayWarning("Error:", "Unknown database type: " + "dbTypeStr");
                    return null;
                }
                
                dataLocation = new DatabaseLocation(dbPort, dbHostname, dbType, dbName);
            }
            else {
                displayWarning("Error:", "Please select a database type");
            }
        }
        else if (dataLocationOption.equals(STREAM_OPTION)) {
            String streamName = ((String)streamNameField.getValue()).trim();
            String streamUri = ((String)streamUriField.getValue()).trim();
            System.out.println("streamName: " + streamName);
            System.out.println("streamUri: " + streamUri);
            
            if (streamName.equals("")) {
                displayWarning("Error:", "Please enter a stream name");
                return null;
            }
            
            if (streamUri.equals("")) {
                displayWarning("Error:", "Please enter a stream URI");
                return null;
            }
            
            try {
                dataLocation = new StreamLocation(streamUri, streamName);
            }
            catch (Exception e) {
                displayWarning("Error:", e.getMessage());
                return null;
            }
        }
        
        return dataLocation;
    }

    // Internal event listeners --------------------------------------------------
    private void onDataSourceSelectChange(UUIDItem item) {
        if (item != null) {
            currSelectedDataSource = item;
            DataSource ds = (DataSource)currSelectedDataSource.getData();
            //TODO: update details
            //dataSourceNameField.setValue(ds.getName());
            //dataSourceEndpointField.setReadOnly(false);
            URI endpoint = ds.getEndpoint();
            dataSourceEndpointField.setValue( (endpoint == null) ? "" : endpoint.toString());           
            //dataSourceEndpointField.setReadOnly(true);
        } else {
            currSelectedDataSource = null;
            dataSourceEndpointField.setValue("");
        }
    }
    
    private void onEditDataSourcesClicked() {
        welcomeController.createDataSourcesAndCommunitiesView();
    }
   
    private void onDatabaseTypeValueChanged() {
        String dbType = (String)databaseType.getValue();
        if (dbType != null) {
            Integer defaultPort = defaultDatabasePorts.get(dbType);
            if (defaultPort != null) {
                String port = defaultPort.toString();
                databasePortField.setValue(port);
            }
        }
    }

    public void onDataSourceAdded(DataSource dataSource) {
        addDataSource(dataSource);
    }

    public void onDataSourceDeleted(UUID dataSourceID) {
        removeDataSource(dataSourceID);
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

    private class DataLocationSelectListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent event) {
            onDataLocationValueChanged();
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
                displayWarning("Data source error:", e.getMessage());
            }
        }
    }
    
    private class DatabaseTypeChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent event) {
            onDatabaseTypeValueChanged();
        }
    }
    
    private class DiscardCommunityListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onDiscardCommunityClicked();
        }
    }

    private class SaveCommunityListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onSaveCommunityClicked();
        }
    }
    
    private class EditDataSourcesListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onEditDataSourcesClicked();
        }
    }
}