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
//      Created Date :          19-Jan-2012
//      Created for Project :   robust
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator.elements;

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.elements.ISVGVector;
import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator.*;

import java.util.UUID;
import java.awt.geom.Point2D;





public abstract class SVGVector extends SVGElement implements ISVGVector
{
  protected Point2D.Float renderCentre;
  protected Point2D.Float renderDimension;
  protected Point2D.Float tlPoint;
  protected Point2D.Float brPoint;
  
  public SVGVector( SVGElGroup parent )
  {
    super( UUID.randomUUID() );
    setParent( parent );
    
    tlPoint = new Point2D.Float();
    brPoint = new Point2D.Float();
    // renderCentre and dimension only created when setting position & dimension
  }
  
  @Override
  public void setVisible( boolean visible )
  {
    if ( visible )
      setAttribute( "display", "all" );
    else
      setAttribute( "display", "none" );
  }
  
  @Override
  public void setPosition( Point2D.Float pos )
  { if ( pos != null ) renderCentre = pos; }
  
  @Override
  public void setWidth( float w )
  {
    if ( renderDimension == null )
    {
      // Create and set height to original data boundary
      renderDimension = new Point2D.Float();
      renderDimension.y = brPoint.y - tlPoint.y;
    }
    
    renderDimension.x = w; 
  }
  
  @Override
  public void setHeight( float h )
  {
    if ( renderDimension == null )
    {
      // Create and set width to original data boundary
      renderDimension = new Point2D.Float();
      renderDimension.x = brPoint.x - tlPoint.x;
    }
    
    renderDimension.y = h;
  }
  
  @Override
  public abstract String getXML(); // Vector based classes to implement
  
  // Protected methods ---------------------------------------------------------
  protected boolean isTransformRequired()
  { return ( renderDimension != null || renderCentre !=null ); }
  
  protected String getTransformGroup()
  {
    String transform = "";
    
    if ( isTransformRequired() )
    {
      transform = "<g transform=\"";
      
      Point2D.Float relDim = new Point2D.Float( brPoint.x - tlPoint.x,
                                                brPoint.y - tlPoint.y );
        
      Point2D.Float vecCentre = new Point2D.Float( tlPoint.x + (relDim.x /2.0f),
                                                   tlPoint.y + (relDim.y /2.0f) );
      
      if ( renderDimension != null )
      {
        Point2D.Float scalar = new Point2D.Float( renderDimension.x / relDim.x,
                                                  renderDimension.y / relDim.y );
        
        transform += "scale(" + Float.toString( scalar.x ) + ", " + 
                     Float.toString( scalar.y ) + ")\" ";
      }
      
      if ( renderCentre != null )
      {
        Point2D.Float delta = new Point2D.Float( renderCentre.x - vecCentre.x,
                                                 renderCentre.y - vecCentre.y );
        
        transform += "translate(" + Float.toString( delta.x ) + ", " +
                     Float.toString( delta.y ) + ")\" ";
      }
      
      transform += ">";
    }
    
    return transform;
  }
}
