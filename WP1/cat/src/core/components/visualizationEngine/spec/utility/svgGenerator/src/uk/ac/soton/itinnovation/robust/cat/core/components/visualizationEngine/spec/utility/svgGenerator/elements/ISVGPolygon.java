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
//      Created for Project :   robust-cat-core-components-visualizationEngine-spec-utility-svgGenerator
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.elements;

import java.awt.geom.Point2D;
import java.util.List;



/**
 * ISVGPolygon represents a closed polygon with an arbitrary number of points
 * 
 * @author Simon Crowle
 */
public interface ISVGPolygon
{
  /**
   * Adds a point to the end of the polygon point list
   * 
   * @param point - (x,y) in pixels or percent
   */
  void appendPoint( Point2D.Float point );
  
  /**
   * Adds a list of points to the end of the polygon list
   * 
   * @param points - List of (x,y) in pixels or percent
   */
  void appendPoints( List<Point2D.Float> points );
}
