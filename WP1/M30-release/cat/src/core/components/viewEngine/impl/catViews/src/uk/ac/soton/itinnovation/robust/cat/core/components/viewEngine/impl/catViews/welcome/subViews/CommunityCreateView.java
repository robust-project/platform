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
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.WelcomeViewListener;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;

public class CommunityCreateView extends WindowView {

    private TextField communityNameField;
    private TextField communityIdField;
    private TextField communityUriField;
    private CheckBox isStreamCheckbox;
    private TextField communityStreamNameField;

    public CommunityCreateView(Component parent) {
        super(parent, "Create new community");
        
        // Size & position window
        window.setWidth("310px");
        window.setHeight("450px");
        centreWindow();

        createComponents();
    }

    // Private methods -----------------------------------------------------------
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
        
        // Is stream? 
        HorizontalLayout hl2 = new HorizontalLayout();
        hl2.addComponent(UILayoutUtil.createSpace("80px", null, true));

        isStreamCheckbox = new CheckBox("Stream data source");
        isStreamCheckbox.setValue(false);
        isStreamCheckbox.setImmediate(true);
        isStreamCheckbox.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                final boolean isChecked = (Boolean)event.getProperty().getValue();
                communityStreamNameField.setEnabled( isChecked );
                communityUriField.setEnabled( isChecked );
            }
        });
        hl2.addComponent(isStreamCheckbox);
        
        compVL.addComponent(hl2);
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));
        
        // Stream name
        communityStreamNameField = new TextField();
        communityStreamNameField.setWidth("180px");
        communityStreamNameField.setEnabled(false);
        lc = new LabelledComponent("Stream Name", "80px", communityStreamNameField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));
        
        // URI
        communityUriField = new TextField();
        communityUriField.setWidth("180px");
        communityUriField.setEnabled(false);
        lc = new LabelledComponent("URI", "80px", communityUriField);
        compVL.addComponent((Component) lc.getImplContainer());
        compVL.addComponent(UILayoutUtil.createSpace("10px", null));

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
        String commUri = (String)communityUriField.getValue();
        Boolean isStream = (Boolean)isStreamCheckbox.getValue();
        String commStreamName = (String)communityStreamNameField.getValue();

        if (commName != null) {
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
                Community comm = new Community(commName, new URI(commUri), isStream, commId, commStreamName);

                Collection<WelcomeViewListener> listeners = getListenersByType();
                for (WelcomeViewListener listener : listeners) {
                    listener.onAddCommunity(comm);
                }

            } catch (URISyntaxException ex) {
                Logger.getLogger(CommunityCreateView.class.getName()).log(Level.SEVERE, null, ex);
            }

            setVisible(false);
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
}