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
//      Created Date :          07 Oct 2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.subViews;

import com.vaadin.data.Item;
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
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.WelcomeController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.WelcomeViewListener;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;

public class DeleteDatabaseOptionsView extends WindowView {

    private WelcomeController welcomeController;

    private OptionGroup deleteDatabaseOptions;
    
    public static final String EMPTY_DATABASE_OPTION = "Empty database";
    public static final String IBM_DATABASE_OPTION = "IBM data";
    public static final String BOARDSIE_DATABASE_OPTION = "BOARDSIE data";
    public static final String SAP_DATABASE_OPTION = "SAP data";
    public static final String SQL_SCRIPT_DATABASE_OPTION = "Run SQL script";
    private TextField authTokenField;

    public DeleteDatabaseOptionsView(Component parent, WelcomeController welcomeController) {
        super(parent, "Reset database");
        
        this.welcomeController = welcomeController;
        
        // Size & position window
        window.setWidth("310px");
        window.setHeight("320px");
        //centreWindow();
        window.setPositionX(100);
        window.setPositionY(100);

        createComponents();
    }

    // Private methods -----------------------------------------------------------
    private void createComponents() {
        VerticalLayout vl = (VerticalLayout) window.getContent();

        // Headline
        vl.addComponent(createHeadline("Please select an option"));

        // Space
        vl.addComponent(UILayoutUtil.createSpace("12px", null));

        // Community components
        HorizontalLayout hl = new HorizontalLayout();
        vl.addComponent(hl);
        //hl.addComponent(UILayoutUtil.createSpace("12px", null, true));
        VerticalLayout compVL = new VerticalLayout();
        hl.addComponent(compVL);
        //hl.addComponent(UILayoutUtil.createSpace("12px", null, true));

        // Options (radio buttons)
        deleteDatabaseOptions = new OptionGroup();
        deleteDatabaseOptions.addItem(EMPTY_DATABASE_OPTION);
        deleteDatabaseOptions.addItem(IBM_DATABASE_OPTION);
        deleteDatabaseOptions.addItem(BOARDSIE_DATABASE_OPTION);
        deleteDatabaseOptions.addItem(SAP_DATABASE_OPTION);
        deleteDatabaseOptions.addItem(SQL_SCRIPT_DATABASE_OPTION);
        
        deleteDatabaseOptions.select(EMPTY_DATABASE_OPTION);
        compVL.addComponent(deleteDatabaseOptions);

        // Space
        compVL.addComponent(UILayoutUtil.createSpace("20px", null));

        // Auth token
        authTokenField = new TextField();
        authTokenField.setWidth("180px");
        LabelledComponent lc = new LabelledComponent("Auth token", "80px", authTokenField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("20px", null));

        // Buttons
        HorizontalLayout bHL = new HorizontalLayout();
        vl.addComponent(bHL);
        vl.setComponentAlignment(bHL, Alignment.BOTTOM_RIGHT);

        Button button = new Button("Cancel");
        button.addListener(new DiscardCommunityListener());
        bHL.addComponent(button);

        bHL.addComponent(UILayoutUtil.createSpace("4px", null, true));

        button = new Button("OK");
        button.addListener(new OKButtonListener());
        bHL.addComponent(button);

        // Space
        bHL.addComponent(UILayoutUtil.createSpace("24px", null, true));
    }

    // Private internal event handlers -------------------------------------------
    private void onDiscardCommunityClicked() {
        setVisible(false);
    }

    private void onOKButtonClicked() {
        String deleteDatabaseOption = (String)deleteDatabaseOptions.getValue();
        
        if (deleteDatabaseOption == null)
            return;

        System.out.println("Delete database option: " + deleteDatabaseOption);
        
        String authToken = (String)authTokenField.getValue();
        
        if ( (authToken == null) || (authToken.isEmpty()) )
            return;
        
        /*
        if (createOption.equals(IMPORT_OPTION)) {
            welcomeController.createDataSourcesAndCommunitiesView();
        }
        else { // manual option
            welcomeController.createCommunityCreateView();
        }
        */
        
        setVisible(false);
        
        resetDatabase(deleteDatabaseOption, authToken);
    }

    private void resetDatabase(String deleteDatabaseOption, String authToken) {
        Collection<WelcomeViewListener> listeners = getListenersByType();

        for (WelcomeViewListener listener : listeners) {
            listener.onResetDatabaseClicked(deleteDatabaseOption, authToken);
        }
    }

    private class DiscardCommunityListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onDiscardCommunityClicked();
        }
    }

    private class OKButtonListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onOKButtonClicked();
        }
    }
}