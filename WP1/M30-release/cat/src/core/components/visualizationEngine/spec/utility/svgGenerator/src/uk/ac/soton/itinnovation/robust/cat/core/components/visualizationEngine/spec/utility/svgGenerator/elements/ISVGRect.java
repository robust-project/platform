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
//      Created for Project :   robust-cat-core-components-visualizationEngine-spec-utility-svgGenerator
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.elements;

import java.awt.geom.Point2D;




/**
 * ISVGRect represents a rectangle
 * 
 * @author sgc
 */
public interface ISVGRect
{
  /**
   * Sets the top-left co-ordinate of the rectangle
   * 
   * @param tl - (x,y) in pixels or percent
   */
  void setTL( Point2D.Float tl );
  
  /**
   * Sets the width and height of the rectangle
   * 
   * @param dim - in pixels or percent
   */
  void setDimension( Point2D.Float dim );
}
