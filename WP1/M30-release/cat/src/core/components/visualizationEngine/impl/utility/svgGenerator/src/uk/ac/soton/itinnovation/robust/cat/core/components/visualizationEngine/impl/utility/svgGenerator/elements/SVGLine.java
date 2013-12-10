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
//      Created Date :          2011-09-12
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator.elements;

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.elements.ISVGLine;
import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator.*;

import java.awt.geom.Point2D;




public class SVGLine extends SVGVector implements ISVGLine
{
  private Point2D.Float startPoint;
  private Point2D.Float endPoint;
  
  public SVGLine( SVGElGroup parent )
  {
    super( parent );
  }
  
  @Override
  public String getXML()
  {
    String xml = "";
    
    // Inject scaling and transform if required
    if ( isTransformRequired() )
      xml += getTransformGroup();
    
    xml = "<line x1=\"" + startPoint.x + "\" " +
          "y1=\"" + startPoint.y + "\" " +
          "x2=\"" + endPoint.x + "\" " +
          "y2=\"" + endPoint.y + "\" ";
    
    // Inject attributes
    xml += getXMLAttributes();
    xml += "/>";
    
    // .. and finalize transform
    if ( isTransformRequired() )
      xml += "</g>";

    xml += "\n";
    
    return xml;
  }
  
  // ISVGLine ------------------------------------------------------------------
  @Override
  public void setPoints( Point2D.Float start, Point2D.Float end )
  {
    startPoint = start;
    endPoint   = end;
  }
}