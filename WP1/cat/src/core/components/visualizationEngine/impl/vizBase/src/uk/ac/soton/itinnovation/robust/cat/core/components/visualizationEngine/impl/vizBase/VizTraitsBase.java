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

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.vf.entities.*;

import java.awt.*;


public class VizTraitsBase extends    UFAbstractEventManager 
                           implements IVizTraits
{
  private boolean   isVisible     = true;
  private boolean   isHighlighted = false;
  private boolean   isEmphasized  = false;
  private Point     gfxPos;
  private Dimension gfxBounds;
  
  private IVizTraitsListener traitListener;
  
  public VizTraitsBase( IVizTraitsListener listener )
  {
    gfxPos        = new Point();
    gfxBounds     = new Dimension();
    traitListener = listener;
  }
  
  // IVizTraits ----------------------------------------------------------------
  @Override
  public boolean isVisible()     { return isVisible; }
  
  @Override
  public boolean isHighlighted() { return isHighlighted; }
  
  @Override
  public boolean isEmphasized()  { return isEmphasized; }
  
  @Override
  public Point getCopyOfPosition() { return new Point(gfxPos); }
  
  @Override
  public Dimension getCopyOfBounds() { return new Dimension(gfxBounds); }
  
  @Override
  public void setVisible( boolean visible )
  {
    if ( isVisible != visible )
    {
      isVisible = visible;
      if ( traitListener != null ) traitListener.onTraitsChanged();
    }
    else
      isVisible = visible;
  }
  
  @Override
  public void setHighlighted( boolean highlighted )
  {
    if ( isHighlighted != highlighted )
    {
      isHighlighted = highlighted;
      if ( traitListener != null ) traitListener.onTraitsChanged();
    }
    else
      isHighlighted = highlighted;
  }
  
  @Override
  public void setEmphasized( boolean emphasized )
  {
    if ( isEmphasized != emphasized )
    {
      isEmphasized = emphasized;
      if ( traitListener != null ) traitListener.onTraitsChanged();
    }
    else
      isEmphasized = emphasized;
  }
  
  @Override
  public void setPosition( Point pos )
  {
    gfxPos = pos;
    if ( traitListener != null ) traitListener.onTraitsChanged();
  }
  
  @Override
  public void setBounds( Dimension bounds )
  {
    gfxBounds = bounds;
    if ( traitListener != null ) traitListener.onTraitsChanged();
  }
  
  
}