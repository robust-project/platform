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
//      Created By :            Simon Crowle
//      Created Date :          21-Mar-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators;

import com.vaadin.data.Property;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.ViewResources;

import com.vaadin.ui.*;
import com.vaadin.terminal.*;

import java.net.*;
import java.util.Properties;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.LabelledComponent;

public class GfxLizerView extends AbstractDashIndicatorView
{
    private Embedded embeddedView;
  
    private boolean useDefaultSettings;
    private boolean devMode; //flag to indicate development/debug mode
    private boolean componentsCreated = false;
    
    private String polecatEqualizerURL;
    private String polecatDataSourceID;
    private String polecatCommunityID;
    private String polecatIndicators;
    private String polecatStartDateStr;
    private String polecatEndDateStr;
    private String polecatTimeInterval;

    /* Some apparently working settings. We get actual defaults from cat.properties
     equalizerStartDate = 01-01-2007
     equalizerEndDate = 01-01-2008
     equalizerTimeInterval = weekly
     equalizerDataSourceID = 3
     equalizerCommunityID = 264
     equalizerIndicators = In-Degree,Out-Degree,Popularity,Reciprocity,Activity
     */
    
    static Logger log = Logger.getLogger(GfxLizerView.class);
    
    private TextField dataSourceIdField;
    private TextField communityIdField;
    private boolean updatingDevComponents = false;
    private TextField urlField;

    public GfxLizerView() {
        super();

        //createComponents(); // Do this after properties have been set
    }

    @Override
    public void setProperties(Properties props) {
        super.setProperties(props); // get generic properties
        this.getPolecatConfigs(); // set specific properties for GE
        createComponents();
    }
    
    public void updateParameters(String platform, String communityID) {
        if (! useDefaultSettings) {
            this.polecatDataSourceID = platform;
            this.polecatCommunityID = communityID;
        }
        else {
            log.warn("Graphic Equalizer using default settings");
        }
        if (devMode) updateDevComponents();
        updateGraphicEqualizerView();
    }
    
    /**
     * Gets Polecat Graphic Equalizer properties from 'cat.properties' on the
     * class path.
     */
    private void getPolecatConfigs() {
        try {
            String devModeStr = props.getProperty("devMode", "false");
            devMode = Boolean.valueOf(devModeStr);
        } catch (Exception ex) {
            //Optional parameter
        }
        log.info("devMode:  " + devMode);

        try {
            String useDefaultSettingsStr = props.getProperty("useDefaultSettings", "false");
            useDefaultSettings = Boolean.valueOf(useDefaultSettingsStr);
        } catch (Exception ex) {
            //Optional parameter
        }
        log.info("useDefaultSettings:  " + useDefaultSettings);

        try {
            polecatEqualizerURL = props.getProperty("equalizerURL");
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'equalizerURL' parameter from cat.properties. " + ex.getMessage(), ex);
        }
        log.info("polecatEqualizerURL:  " + polecatEqualizerURL);

        try {
            polecatDataSourceID = props.getProperty("equalizerDataSourceID");
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'equalizerDataSourceID' parameter from cat.properties. " + ex.getMessage(), ex);
        }
        log.info("equalizerDataSourceID:  " + polecatDataSourceID);

        try {
            polecatCommunityID = props.getProperty("equalizerCommunityID");
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'equalizerCommunityID' parameter from cat.properties. " + ex.getMessage(), ex);
        }
        log.info("equalizerCommunityID:   " + polecatCommunityID);

        try {
            polecatIndicators = props.getProperty("equalizerIndicators");
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'equalizerIndicators' parameter from cat.properties. " + ex.getMessage(), ex);
        }
        log.info("equalizerIndicators:   " + polecatIndicators);

        try {
            polecatStartDateStr = props.getProperty("equalizerStartDate");
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'equalizerStartDate' parameter from cat.properties. " + ex.getMessage(), ex);
        }
        log.info("equalizerStartDate:  " + polecatStartDateStr);

        try {
            polecatEndDateStr = props.getProperty("equalizerEndDate");
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'equalizerEndDate' parameter from cat.properties. " + ex.getMessage(), ex);
        }
        log.info("equalizerEndDate:  " + polecatEndDateStr);

        try {
            polecatTimeInterval = props.getProperty("equalizerTimeInterval");
        } catch (Exception ex) {
            log.error("Error with loading getting and converting 'equalizerTimeInterval' parameter from cat.properties. " + ex.getMessage(), ex);
        }
        log.info("timeInterval:  " + polecatTimeInterval);
    }

    private void updateGraphicEqualizerView() {
        String URL = getPolecatEqualizerURL();
        updateURL(URL);
    }

    private void updateURL(String url) {
        try {
            URL polecatURL = new URL(url);
            if (devMode && (urlField != null)) this.urlField.setValue(polecatURL);
            embeddedView.setSource(new ExternalResource(polecatURL));
        } catch (MalformedURLException mue) {
            System.out.println("Could not update POLECAT graphic equalizer - URL is malformed: " + url);
        };
    }

    private String getPolecatEqualizerURL() {
        //String URL = "http://robust.meaningmine.com:8080/equalizer-0.5/index.jsp";
        String URL = polecatEqualizerURL;
        URL += "?ci=" + polecatCommunityID; // OBS: change the question mark to an & if the data source is added first //TODO: add the data source into URL
        URL += "&sd=" + polecatStartDateStr;
        URL += "&ed=" + polecatEndDateStr;
        URL += "&ti=" + polecatTimeInterval;
        URL += "&ins=" + polecatIndicators;

        return URL;
    }

    @Override
    public String getIndicatorName() {
        return "Gfx Equalizer";
    }

    @Override
    public ThemeResource getIndicatorIcon() {
        return ViewResources.CATAPPResInstance.getResource("GfxLizerIcon");
    }

    @Override
    public String getDescription() {
        return "Community graphic equalizer. Shows the community features evolution over time.";
    }

    // Private methods -----------------------------------------------------------
    private void createComponents() {
        // Title
        Label label = new Label("Community Graphic Equalizer");
        label.addStyleName("catSubHeadlineFont");
        viewContents.addComponent(label);
        
        if (devMode) createDevComponents();

        // Space
        viewContents.addComponent(UILayoutUtil.createSpace("4px", null));

        // Embedded IFrame for graphic equalizer
        embeddedView = new Embedded();
        embeddedView.setType(Embedded.TYPE_BROWSER);
        embeddedView.setWidth("800px");
        embeddedView.setHeight("500px");
        viewContents.addComponent(embeddedView);
    }
    
    private void createDevComponents() {
        HorizontalLayout hl = new HorizontalLayout();
        
        // DataSourceID
        dataSourceIdField = new TextField();
        dataSourceIdField.setWidth("180px");
        LabelledComponent lc = new LabelledComponent("Data source (platform)", "80px", dataSourceIdField);
        hl.addComponent((Component) lc.getImplContainer());

        // CommunityID
        communityIdField = new TextField();
        communityIdField.setWidth("180px");
        lc = new LabelledComponent("Community", "80px", communityIdField);
        hl.addComponent((Component) lc.getImplContainer());

        // Set default values
        updateDevComponents();

        // Finally, add change listeners (after initial values set)
        dataSourceIdField.addListener( new DataSourceIdChangeListener() );
        communityIdField.addListener( new CommunityIdChangeListener() );
        
        // Add horizontal layout
        viewContents.addComponent(hl);
        
        // Add space
        viewContents.addComponent(UILayoutUtil.createSpace("4px", null));
        
        // URL
        urlField = new TextField();
        urlField.setWidth("500px");
        lc = new LabelledComponent("URL", "500px", urlField);
        viewContents.addComponent((Component) lc.getImplContainer());
        
    }
    
    private void updateDevComponents() {
        if (updatingDevComponents)
            return;
        
        updatingDevComponents = true;
        
        if (dataSourceIdField != null) dataSourceIdField.setValue(polecatDataSourceID);
        if (communityIdField != null) communityIdField.setValue(polecatCommunityID);

        updatingDevComponents = false;
    }
    
    private void onDataSourceIdChanged() {
        if (!updatingDevComponents) {
            polecatDataSourceID = (String) dataSourceIdField.getValue();
        }
    }

    private void onCommunityIdChanged() {
        if (!updatingDevComponents) {
            polecatCommunityID = (String) communityIdField.getValue();
        }
    }

    private class DataSourceIdChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent vce) {
            onDataSourceIdChanged();
        }
    }

    private class CommunityIdChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent vce) {
            onCommunityIdChanged();
        }
    }

}
