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

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator.*;

import java.awt.geom.Point2D;
import java.util.*;





/**
 * SVGVectorList is an abstract class that deals with an array of points. Sub-
 * classes derive from this class to make polylines and polygons.
 * 
 * @author sgc
 */
abstract class SVGVectorList extends SVGVector
{
  protected ArrayList<Point2D.Float> vecList;
  
  public SVGVectorList( SVGElGroup parent )
  {
    super( parent );
    
    vecList = new ArrayList<Point2D.Float>();
  }
  
  @Override
  public abstract String getXML(); // Vector based classes to implement
  
  // Protected methods ---------------------------------------------------------
  protected void addPointToList( Point2D.Float point )
  {
    if ( point.x < tlPoint.x ) tlPoint.x = point.x;
    if ( point.x > brPoint.x ) brPoint.x = point.x;
    if ( point.y < tlPoint.y ) tlPoint.y = point.y;
    if ( point.y > brPoint.y ) brPoint.y = point.y;
    
    vecList.add( point );
  }
}
