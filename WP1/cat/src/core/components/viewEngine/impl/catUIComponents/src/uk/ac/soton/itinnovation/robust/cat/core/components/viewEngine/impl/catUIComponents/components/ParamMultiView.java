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
//      Created Date :          22-Mar-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import com.vaadin.ui.*;
import com.vaadin.ui.TabSheet.Tab;

import java.util.HashMap;




public class ParamMultiView extends AbstractParamView
{
  private TabSheet             paramSheet;
  private HashMap<String, Tab> currentTabs;
  
  public ParamMultiView( String title, String desc )
  {
    super( title, desc );
    
    paramType = eParamType.MULTI_VIEW_TYPE;
    createComponents();
  }
  
  public void addParamView( String title, AbstractParamView view )
  {
    if ( title != null && view != null )
    {
      removeParamView( title ); // Remove old view if required

      Tab newTab = paramSheet.addTab( (Component) view.getImplContainer(), title );
      newTab.setStyleName( "tiny" );
      currentTabs.put( title, newTab );
    }
  }
  
  public void removeParamView( String title )
  {
    if ( title != null )
    {
      Tab tab = currentTabs.get( title );
      if ( tab != null )
      {
        paramSheet.removeTab( tab );
        currentTabs.remove( title );
      }
    }
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    paramSheet = new TabSheet();
    paramSheet.addStyleName( "borderless" );
    paramSheet.addStyleName( "small" );
    paramSheet.setWidth( "100%" );
    paramContainer.addComponent( paramSheet );
    
    currentTabs = new HashMap<String, Tab>();
  }
}
