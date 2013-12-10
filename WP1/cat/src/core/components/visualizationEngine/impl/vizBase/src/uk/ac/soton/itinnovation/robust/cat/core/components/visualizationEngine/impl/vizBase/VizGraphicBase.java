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
//      Created Date :          2011-10-17
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.vizBase;

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.vf.entities.*;


public abstract class VizGraphicBase implements IVizGraphic,
                                                IVizTraitsListener
              
{
  protected ISelectable   selectable;
  protected VizTraitsBase gfxTraits;
  
  public VizGraphicBase()
  {
    gfxTraits = new VizTraitsBase( this );
  }
  
  public void setSelectable( ISelectable select )
  { selectable = select; }
  
  // IVizGraphic ---------------------------------------------------------------  
  @Override
  public ISelectable isSelectable()
  { return selectable; }
  
  @Override
  public IVizTraits getTraits()
  { return gfxTraits; }
  
  // Sub-classes to derive -----------------------------------------------------
  // IVizTraitsListener
  @Override
  public abstract void onTraitsChanged();
  
  // Render implementation object (perhaps an SVG doc/component)
  @Override
  public abstract Object getRenderImpl();
}