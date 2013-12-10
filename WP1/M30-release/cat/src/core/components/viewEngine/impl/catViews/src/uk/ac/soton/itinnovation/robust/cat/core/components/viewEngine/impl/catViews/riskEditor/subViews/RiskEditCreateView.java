/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2011
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
//      Created By :            sgc
//      Created Date :          21 Oct 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViews;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.LabelledComponent;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.WindowView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents.*;

import com.vaadin.ui.*;
import java.util.*;



public class RiskEditCreateView extends WindowView
{
  private TextField    roTitleField;
  private ComboBox     roTypeCB;
  private NativeSelect roOwnerSelect;
  private NativeSelect roGroupSelect;
  
  
  public RiskEditCreateView( Component parent )
  {
    super( parent, "Create new risk/opportunity" );
    
    // Size & position window
    window.setWidth( "300px" );
    window.setHeight( "360px" );
    centreWindow();
    
    createComponents();
  }
  
  public void setOwners( List<String> owners )
  {
    roOwnerSelect.removeAllItems();
    
    if ( (owners != null) && owners.size() > 0 )
    {
      for ( String owner : owners )
        roOwnerSelect.addItem( owner );
    
      roOwnerSelect.select( owners.get( owners.size() -1) );
    }
  }
  
  public void setGroups( List<String> groups )
  {
    roGroupSelect.removeAllItems();
    
    if ( (groups != null) && groups.size() > 0  )
    {
      for ( String group: groups )
        roGroupSelect.addItem( group );
      
      roGroupSelect.select( groups.get( groups.size() -1) );
    }
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    VerticalLayout vl = (VerticalLayout) window.getContent();
    
    // Headline
    vl.addComponent( createHeadline("Please enter initial R/O information") );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("48px", null) );
    
    // RO components
    HorizontalLayout hl = new HorizontalLayout();
    vl.addComponent( hl );
    hl.addComponent( UILayoutUtil.createSpace("24px", null, true) );
    VerticalLayout compVL = new VerticalLayout();
    hl.addComponent( compVL );
    
    // Title
    roTitleField = new TextField();
    roTitleField.setWidth( "160px" );
    LabelledComponent lc = new LabelledComponent( "Title", "64px", roTitleField );
    compVL.addComponent( (Component) lc.getImplContainer() );
    compVL.addComponent( UILayoutUtil.createSpace("10px", null));
    
    // Owner
    roOwnerSelect = new NativeSelect();
    roOwnerSelect.setWidth( "160px" );
    lc = new LabelledComponent( "Owner", "64px", roOwnerSelect );
    compVL.addComponent( (Component) lc.getImplContainer() );
    compVL.addComponent( UILayoutUtil.createSpace("8px", null));
    
    // Group
    roGroupSelect = new NativeSelect();
    roGroupSelect.setWidth( "160px" );
    lc = new LabelledComponent( "Group", "64px", roGroupSelect );
    compVL.addComponent( (Component) lc.getImplContainer() );
    compVL.addComponent( UILayoutUtil.createSpace("8px", null));
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("40px", null) );
    
    // Buttons
    HorizontalLayout bHL = new HorizontalLayout();
    vl.addComponent( bHL );
    vl.setComponentAlignment( bHL, Alignment.BOTTOM_RIGHT );
    
    Button button = new Button( "Cancel" );
    button.addListener( new DiscardROListener() );
    bHL.addComponent( button );
    
    bHL.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    button = new Button( "Save R/O" );
    button.addListener( new SaveROListener() );
    bHL.addComponent( button );
    
    // Space
    bHL.addComponent( UILayoutUtil.createSpace("24px", null, true) );
  }
  
  // Private internal event handlers -------------------------------------------
  private void onDiscardROClicked()
  { setVisible(false); }
  
  private void onSaveROClicked()
  {
    String tf = (String) roTitleField.getValue();
    String ow = (String) roOwnerSelect.getValue();
    String gp = (String) roGroupSelect.getValue();
    
    if ( tf != null && ow != null && gp != null )
    {
      CreateViewCD cd = new CreateViewCD( tf, (boolean) (tf.equals("Risk")),
                                        ow, gp );
    
      Collection<RiskEditCommitListener> listeners = getListenersByType();
      for ( RiskEditCommitListener listener : listeners )
        listener.onCommitCreateViewChanges( cd );
      
      setVisible( false );
    }
  }
  
  private class DiscardROListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onDiscardROClicked(); }
  }
  
  private class SaveROListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onSaveROClicked(); }
  }
}