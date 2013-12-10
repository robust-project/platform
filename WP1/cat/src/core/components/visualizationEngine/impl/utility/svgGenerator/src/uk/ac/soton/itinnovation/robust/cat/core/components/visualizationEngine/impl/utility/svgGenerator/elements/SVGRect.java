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
//      Created Date :          19-Jan-2012
//      Created for Project :   robust
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator.elements;

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.elements.ISVGRect;
import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator.*;

import java.awt.geom.Point2D;




public class SVGRect extends SVGVector implements ISVGRect
{
  private Point2D.Float topLeft;
  private Point2D.Float dimension;
  
  public SVGRect( SVGElGroup parent )
  {
    super( parent );
    
    topLeft   = new Point2D.Float();
    dimension = new Point2D.Float();
  }
  
  @Override
  public String getXML()
  {
    String xml = "";
    
    // Inject scaling and transform if required
    if ( isTransformRequired() )
      xml += getTransformGroup();
      
    xml = "<rect x=\"" + topLeft.x + "\" " +
          "y=\"" + topLeft.y + "\" " +
          "width=\"" + dimension.x + "\" " +
          "height=\"" + dimension.y + "\" ";
    
    // Inject attributes
    xml += getXMLAttributes();
    xml += "/>";
    
    // .. and finalize transform
    if ( isTransformRequired() )
      xml += "</g>";

    xml += "\n";
    
    return xml;
  }
    
  // ISVGRect ------------------------------------------------------------------
  @Override
  public void setTL( Point2D.Float tl )
  { if ( tl != null ) topLeft = tl; }
  
  @Override
  public void setDimension( Point2D.Float dim )
  { if ( dim != null ) dimension = dim; }
}
