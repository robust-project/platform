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

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.elements.ISVGPolygon;
import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator.*;

import java.awt.geom.Point2D;
import java.util.*;





public class SVGPolygon extends SVGVectorList implements ISVGPolygon
{
  public SVGPolygon( SVGElGroup parent )
  {
    super( parent );
  }
  
  @Override
  public String getXML()
  {
    String xml = "";
    
    if ( !vecList.isEmpty() )
    {
      // Inject scaling and transform if required
      if ( isTransformRequired() )
        xml += getTransformGroup();

      // Write in polygon data
      xml = "<polygon points=\"";

      for ( Point2D.Float vec : vecList )
        xml += Float.toString( vec.x ) + "," + Float.toString( vec.y ) + " ";

      xml += "\" ";

      // Inject attributes
      xml += getXMLAttributes();
      xml += "/>";

      // .. and finalize transform
      if ( isTransformRequired() )
        xml += "</g>";

      xml += "\n";
    }
    
    return xml;
  }
  
  // ISVGPolygon ---------------------------------------------------------------
  @Override
  public void appendPoint( Point2D.Float point )
  { if ( point != null ) addPointToList( point ); }
  
  @Override
  public void appendPoints( List<Point2D.Float> points )
  {
    if ( points != null )
      for ( Point2D.Float p : points )
        if ( p != null ) addPointToList( p );
  }
}
