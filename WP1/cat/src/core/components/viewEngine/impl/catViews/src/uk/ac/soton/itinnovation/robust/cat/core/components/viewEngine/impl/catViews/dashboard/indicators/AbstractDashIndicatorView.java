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
//      Created Date :          21 Sep 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators;


import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.dashboard.IDashIndicatorView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.SimpleView;

import com.vaadin.terminal.ThemeResource;
import java.util.Properties;




public abstract class AbstractDashIndicatorView extends SimpleView
                                                implements IDashIndicatorView
{
  protected Properties props;
  
  // IDashIndicatorView --------------------------------------------------------
  @Override
  public void setProperties(Properties props) {
      this.props = props;
  }
    
  @Override
  public abstract String getIndicatorName();
  
  public abstract ThemeResource getIndicatorIcon();
  
  public abstract String getDescription();
  
  public void refreshView() {
      refreshView(false);
  }
  
  public void refreshView(boolean resizing) {
      //override refresh behaviour if required
  }
  
  // Protected methods ---------------------------------------------------------
  protected AbstractDashIndicatorView()
  {
    super();
  }
}