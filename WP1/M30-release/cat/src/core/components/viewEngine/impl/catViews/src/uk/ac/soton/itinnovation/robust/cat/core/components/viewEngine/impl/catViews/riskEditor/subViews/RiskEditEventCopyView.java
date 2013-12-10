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
//      Created Date :          13-Nov-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViews;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

import com.vaadin.ui.*;
import java.util.List;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents.RiskEditEventCopyViewListener;



public class RiskEditEventCopyView extends WindowView
{
  private TextField subTitleField;
  

  public RiskEditEventCopyView( Component parent,
                                String defaultName )
  {
    super( parent, "Event info" );
    
    // Size & position window
    window.setWidth( "300px" );
    window.setHeight( "270px" );
    centreWindow();
    
    window.setModal( true );
    
    createComponents( defaultName );
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents( String defaultName )
  {
    VerticalLayout vl = (VerticalLayout) window.getContent();
    
    // Headline
    vl.addComponent( createHeadline("Please provide event sub-title") );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("48px", null) );
    
    subTitleField = new TextField();
    subTitleField.setWidth( "160px" );
    subTitleField.setValue( defaultName );
    subTitleField.setImmediate( true );
    
    // Event sub-title field
    LabelledComponent lc = new LabelledComponent( "Sub-title", "64px", subTitleField );
    Component comp = (Component) lc.getImplContainer();
    vl.addComponent( comp );
    vl.setComponentAlignment( comp, Alignment.MIDDLE_CENTER );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("48px", null) );
    
    // Space & bar
    Component spacer = UILayoutUtil.createSpace("4px", null);
    spacer.setWidth( "100%" );
    spacer.addStyleName( "catBorderBottom" );
    vl.addComponent( spacer );
    
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // OK Button
    Button okButton = new Button( "OK" );
    okButton.setWidth( "80px" );
    okButton.addListener( new OKListener() );
    vl.addComponent( okButton );
    vl.setComponentAlignment( okButton, Alignment.MIDDLE_CENTER );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("24px", null) );
  }
  
  // Private event handling ----------------------------------------------------
  private void onOKButtonClicked()
  {
    // Notify of event
    List<RiskEditEventCopyViewListener> listeners = getListenersByType();
    for ( RiskEditEventCopyViewListener listener : listeners )
      listener.onEventSubTitleViewClosed( (String) subTitleField.getValue() );
    
    // Tidy up and go away
    setVisible( false );
    Window parentWindow = window.getParent();
    parentWindow.removeComponent( window );
  }
  
  private class OKListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onOKButtonClicked(); }
  }
}
