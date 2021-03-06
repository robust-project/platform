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
//      Created By :            sgc
//      Created Date :          21-Mar-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.dashboard.indicators.IPolecatGfxLizerController;

import java.io.Serializable;
import java.util.Map;




public class GfxLizerController extends UFAbstractEventManager
                                implements Serializable,
                                           IPolecatGfxLizerController
{
  private GfxLizerView lizerView;
  
  public GfxLizerController()
  {
    super();
    
    lizerView = new GfxLizerView();
  }
  
  public AbstractDashIndicatorView getIndicatorView()
  { return lizerView; }
  
  // IGfxLizerController -------------------------------------------------------
  @Override
  public void setLizerURL( String url )
  { lizerView.updateURL( url ); }
}
