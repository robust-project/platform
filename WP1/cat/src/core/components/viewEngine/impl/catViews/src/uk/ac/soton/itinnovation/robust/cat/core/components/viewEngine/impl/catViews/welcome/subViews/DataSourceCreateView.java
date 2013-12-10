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
//      Created Date :          26 Jun 2013
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

public class DataSourceCreateView extends DataSourceDefinitionView {

    public DataSourceCreateView(Component parent) {
        super(parent, "Create new data source");
        
        // Size & position window
        window.setWidth("310px");
        window.setHeight("450px");
        //centreWindow();
        window.setPositionX(120);
        window.setPositionY(120);

        createComponents();
    }

    private void createComponents() {
        VerticalLayout vl = (VerticalLayout) window.getContent();

        // Headline
        vl.addComponent(createHeadline("Please enter data source details"));

        // Space
        vl.addComponent(UILayoutUtil.createSpace("12px", null));

        // DataSource components
        HorizontalLayout hl = new HorizontalLayout();
        vl.addComponent(hl);
        //hl.addComponent(UILayoutUtil.createSpace("12px", null, true));
        
        VerticalLayout compVL = new VerticalLayout();
        createDataSourceComponents(compVL);
        
        hl.addComponent(compVL);
        //hl.addComponent(UILayoutUtil.createSpace("12px", null, true));

        // Space
        vl.addComponent(UILayoutUtil.createSpace("40px", null));

        // Buttons
        HorizontalLayout bHL = new HorizontalLayout();
        vl.addComponent(bHL);
        vl.setComponentAlignment(bHL, Alignment.BOTTOM_RIGHT);

        Button button = new Button("Cancel");
        button.addListener(new DiscardDataSourceListener());
        bHL.addComponent(button);

        bHL.addComponent(UILayoutUtil.createSpace("4px", null, true));

        button = new Button("Save Data Source");
        button.addListener(new SaveDataSourceListener());
        bHL.addComponent(button);

        // Space
        bHL.addComponent(UILayoutUtil.createSpace("24px", null, true));
    }

    // Private internal event handlers -------------------------------------------
    private void onDiscardDataSourceClicked() {
        setVisible(false);
    }

    private void onSaveDataSourceClicked() {
        String dsName = (String)dataSourceNameField.getValue();
        //String dsHostname = (String)dataSourceHostnameField.getValue();
        //String dsService = (String)dataSourceServiceField.getValue();
        String dsEndpoint = (String)dataSourceEndpointField.getValue();
        
        if ( (dsName == null) || (dsName.trim().equals(""))) {
            displayWarning("Error:", "Please enter a name");
            return;
        }
        
        if ( (dsEndpoint == null) || (dsEndpoint.trim().equals(""))) {
            displayWarning("Error:", "Please enter an endpoint");
            return;
        }

        try {
            DataSource dataSource = new DataSource(dsName, new URI(dsEndpoint));

            Collection<WelcomeViewListener> listeners = getListenersByType();
            for (WelcomeViewListener listener : listeners) {
                listener.onAddDataSource(dataSource);
            }

        } catch (URISyntaxException ex) {
            Logger.getLogger(DataSourceCreateView.class.getName()).log(Level.SEVERE, null, ex);
        }

        setVisible(false);
        
    }
    
    private class DiscardDataSourceListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onDiscardDataSourceClicked();
        }
    }

    private class SaveDataSourceListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            try {
                onSaveDataSourceClicked();
            }
            catch (Exception e) {
                e.printStackTrace();
                displayWarning("Data source error:", e.getMessage());
            }
        }
    }
}