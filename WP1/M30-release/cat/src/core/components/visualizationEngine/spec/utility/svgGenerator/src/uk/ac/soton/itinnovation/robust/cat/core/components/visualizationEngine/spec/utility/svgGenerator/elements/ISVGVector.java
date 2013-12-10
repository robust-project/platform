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

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.elements;

import java.awt.geom.Point2D;




/**
 * ISVGVector represents a high-level abstraction of a graphic that is visually
 * observable in the final render. Use this to access common visual attributes.
 * 
 * @author sgc
 */
public interface ISVGVector
{
  // TODO: Getters!
  
  /**
   * Sets the visibility of the vector
   * 
   * @param visible - it is either visible or not
   */
  void setVisible( boolean visible );
  
  /**
   * Sets the position of the graphic as either TL or centroid coordinates
   * (depending on actual shape)
   * 
   * @param pos - x,y coordinates (pixels or percentage)
   */
  void setPosition( Point2D.Float pos );
  
  /**
   * Sets the width of the graphic (some shapes currently ignore this,
   * such as ISVGText)
   * 
   * @param w - width (pixels or percentage)
   */
  void setWidth( float w );
  
  /**
   * Sets the height of the graphic
   * 
   * @param h - height (pixels or percentage)
   */
  void setHeight( float h );
}
