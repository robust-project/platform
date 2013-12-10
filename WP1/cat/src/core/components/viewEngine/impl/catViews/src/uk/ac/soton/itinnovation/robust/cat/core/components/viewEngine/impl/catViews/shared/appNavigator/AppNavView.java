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
//      Created By :            Simon Crowle
//      Created Date :          16 Sep 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.shared.appNavigator;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.ICATAppViewNavListener;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

import com.vaadin.ui.*;
import java.util.List;




public class AppNavView extends SimpleView
{
  public AppNavView() 
  {
    super();
    
    createComponents();
  }
  
  // Private methods -----------------------------------------------------------  
  private void createComponents()
  {
    HorizontalLayout ccLayout = new HorizontalLayout();
    viewContents.addComponent( ccLayout );
 
    // Headroom
    ccLayout.addComponent( UILayoutUtil.createSpace("1px", "catNavPanel") );
    
    // Navigation content
    HorizontalLayout innerHL = new HorizontalLayout();
    innerHL.setHeight( "80px" );
    ccLayout.addComponent( innerHL );
    ccLayout.setComponentAlignment( innerHL, Alignment.BOTTOM_CENTER );
    
    // Buttons
    Button button = new Button();
    button.addStyleName( "borderless icon-on-top" );
    button.setIcon( ViewResources.CATAPPResInstance.getResource("dashWelcomeButton") );
    button.setDescription("Return to welcome page");
    button.addListener( new ButtonNavListener(ICATAppViewNavListener.eNavDest.WELCOME) );
    innerHL.addComponent( button );
    
    button= new Button();
    button.addStyleName( "borderless icon-on-top" );
    button.setIcon( ViewResources.CATAPPResInstance.getResource("dashRiskEditorButton") );
    button.setDescription("Show the risk editor. You can here specify your risks and opportunities for your chosen community.");
    button.addListener( new ButtonNavListener(ICATAppViewNavListener.eNavDest.RISK_EDITOR) );
    innerHL.addComponent( button );
    
    button = new Button();
    button.addStyleName( "borderless icon-on-top" );
    button.setIcon( ViewResources.CATAPPResInstance.getResource("dashComSimButton") );
    button.setDescription("Policy forum restriction simulator. Simulate the effects of policy changes on your community.");
    button.addListener( new ButtonNavListener(ICATAppViewNavListener.eNavDest.SIMULATOR_CENTRE) );
    innerHL.addComponent( button );
    
    button = new Button();
    button.addStyleName( "borderless icon-on-top" );
    button.setIcon( ViewResources.CATAPPResInstance.getResource("dashTreatEditorButton") );
    button.setDescription("Perform treatments on risks. Go through the treatment workflow to manage risks and opportunity.");
    button.addListener( new ButtonNavListener(ICATAppViewNavListener.eNavDest.TREATMENT_CENTRE) );
    button.setEnabled( true );
    innerHL.addComponent( button );
    ccLayout.setComponentAlignment( innerHL, Alignment.MIDDLE_CENTER );
    
    // Footroom
    ccLayout.addComponent( UILayoutUtil.createSpace("1px", "catNavPanel") );
  }
  
  private void onNavigationEvent( ICATAppViewNavListener.eNavDest net )
  {
    List<ICATAppViewNavListener> navListeners = getListenersByType();
    
    if ( !navListeners.isEmpty() )
      for ( ICATAppViewNavListener listener : navListeners )
        listener.onNavigateApp( net );
  }
  
  // Internal event handlers ---------------------------------------------------
  private class ButtonNavListener implements Button.ClickListener
  {
    private ICATAppViewNavListener.eNavDest navType;
    
    public ButtonNavListener( ICATAppViewNavListener.eNavDest net ) { navType = net; }
    
    @Override
    public void buttonClick( Button.ClickEvent ce )
    { onNavigationEvent( navType ); }
  }
}

