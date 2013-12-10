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
//      Created Date :          24 Jun 2013
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

public class CommunityCreateOptionsView extends WindowView {

    private WelcomeController welcomeController;

    private OptionGroup createCommunityOptions;
    private static String IMPORT_OPTION = "Import from Data Source";
    private static String MANUAL_OPTION = "Enter manually";

    public CommunityCreateOptionsView(Component parent, WelcomeController welcomeController) {
        super(parent, "Create new community");
        
        this.welcomeController = welcomeController;
        
        // Size & position window
        window.setWidth("310px");
        window.setHeight("220px");
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
        createCommunityOptions = new OptionGroup();
        createCommunityOptions.addItem(IMPORT_OPTION);
        createCommunityOptions.addItem(MANUAL_OPTION);
        createCommunityOptions.select(IMPORT_OPTION);
        compVL.addComponent(createCommunityOptions);

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

        button = new Button("Next");
        button.addListener(new NextPageListener());
        bHL.addComponent(button);

        // Space
        bHL.addComponent(UILayoutUtil.createSpace("24px", null, true));
    }

    // Private internal event handlers -------------------------------------------
    private void onDiscardCommunityClicked() {
        setVisible(false);
    }

    private void onNextButtonClicked() {
        String createOption = (String)createCommunityOptions.getValue();
        
        if (createOption == null)
            return;

        System.out.println("Create community option: " + createOption);
        
        if (createOption.equals(IMPORT_OPTION)) {
            welcomeController.createDataSourcesAndCommunitiesView();
        }
        else { // manual option
            welcomeController.createCommunityCreateView();
        }
        
        setVisible(false);
    }

    private class DiscardCommunityListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onDiscardCommunityClicked();
        }
    }

    private class NextPageListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            onNextButtonClicked();
        }
    }
}