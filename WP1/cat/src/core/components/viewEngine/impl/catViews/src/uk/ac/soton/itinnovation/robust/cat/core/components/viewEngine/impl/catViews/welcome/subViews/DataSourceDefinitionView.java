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
//      Created Date :          28 Jun 2013
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
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.WelcomeViewListener;
import uk.ac.soton.itinnovation.robust.riskmodel.DataSource;

public abstract class DataSourceDefinitionView extends WindowView {

    protected TextField dataSourceNameField;
    protected TextField dataSourceHostnameField;
    protected TextField dataSourceServiceField;
    protected TextField dataSourceEndpointField;
    
    protected boolean displaySourceNameField = true;
    protected boolean setDefaults = true;
    
    public static String defaultHostname = "robust-demo.softwaremind.pl";
    public static String defaultService = "robust-dataservice-boardsie-ws-1.0-SNAPSHOT/robustDataServiceBoardsIE";
    
    private boolean settingEndpoint;

    public DataSourceDefinitionView(Component parent, String title) {
        super(parent, title);
    }
    
    public void createDataSourceComponents(VerticalLayout compVL) {
        if (displaySourceNameField) {
            // Name
            dataSourceNameField = new TextField();
            dataSourceNameField.setWidth("180px");
            LabelledComponent lc = new LabelledComponent("Name", "80px", dataSourceNameField);
            compVL.addComponent((Component) lc.getImplContainer());
            compVL.addComponent(UILayoutUtil.createSpace("10px", null));
        }

        // Hostname
        dataSourceHostnameField = new TextField();
        dataSourceHostnameField.setWidth("180px");
        dataSourceHostnameField.addListener(new DataSourceHostnameChangedListener());
        LabelledComponent lc = new LabelledComponent("Hostname", "80px", dataSourceHostnameField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));
        
        // Service
        dataSourceServiceField = new TextField();
        dataSourceServiceField.setWidth("180px");
        dataSourceServiceField.addListener(new DataSourceServiceChangedListener());
        lc = new LabelledComponent("Service", "80px", dataSourceServiceField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));
        
        // Endpoint
        dataSourceEndpointField = new TextField();
        dataSourceEndpointField.setWidth("180px");
        dataSourceEndpointField.addListener(new DataSourceEndpointChangedListener());
        lc = new LabelledComponent("Endpoint", "80px", dataSourceEndpointField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));
        
        if (setDefaults) {
            dataSourceHostnameField.setValue(defaultHostname);
            dataSourceServiceField.setValue(defaultService);
        }
    }
    
    protected void dataSourceHostnameChanged() {
        System.out.println("Hostname changed: " + dataSourceHostnameField.getValue());
        sanitiseHostname();
        setEndpoint();
    }
    
    protected void sanitiseHostname() {
        String hostname = ((String)dataSourceHostnameField.getValue()).trim();
        hostname = hostname.replaceFirst("http[s]?://", "");
        hostname = hostname.replaceFirst("[/]+.*", "");
        dataSourceHostnameField.setValue(hostname);
    }
    
    protected void dataSourceServiceChanged() {
        System.out.println("Service changed: " + dataSourceServiceField.getValue());
        sanitiseService();
        setEndpoint();
    }
    
    protected void sanitiseService() {
        String service = ((String)dataSourceServiceField.getValue()).trim();
        service = service.replaceFirst("\\?wsdl", "");
        dataSourceServiceField.setValue(service);
    }
    
    protected void dataSourceEndpointChanged() {
        if (settingEndpoint)
            return;
        
        System.out.println("Endpoint changed: " + dataSourceEndpointField.getValue());
        sanitiseEndpoint();
        String endpoint = (String)dataSourceEndpointField.getValue();
        String hostnameAndService = endpoint.replaceFirst("http[s]?://", "");
        String hostname = "";
        String service = "";
        
        if (hostnameAndService.contains("/")) {
            String[] strings = hostnameAndService.split("/", 2);
            if (strings.length == 2) {
                hostname = strings[0];
                service = strings[1];
            }
        }
        
        dataSourceHostnameField.setValue(hostname);
        dataSourceServiceField.setValue(service);
    }
    
    protected void sanitiseEndpoint() {
        String endpoint = ((String)dataSourceEndpointField.getValue()).trim();
        dataSourceEndpointField.setValue(endpoint);
    }

    protected void setEndpoint() {
        String hostname = (String)dataSourceHostnameField.getValue();
        String service = (String)dataSourceServiceField.getValue();
        String endpoint = "http://" + hostname + "/" + service;
        settingEndpoint = true;
        dataSourceEndpointField.setValue(endpoint);
        settingEndpoint = false;
    }
    
    protected class DataSourceHostnameChangedListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent event) {
            dataSourceHostnameChanged();
        }
    }

    protected class DataSourceServiceChangedListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent event) {
            dataSourceServiceChanged();
        }
    }

    protected class DataSourceEndpointChangedListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent event) {
            dataSourceEndpointChanged();
        }
    }

}