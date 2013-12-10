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
//      Created Date :          2011-09-22
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.elements;




/**
 * ISVGText provides basic text rendering functionality.
 * 
 * @author Simon Crowle
 */
public interface ISVGText
{
  /**
   * Sets the text to be rendered
   * 
   * @param value - text value
   */
  void setValue( String value );
  
  /**
   * Sets some basic presentation aspects of the text, including font family,
   * size and colour. See http://www.w3.org/TR/SVG/text.html for more information
   * 
   * @param fontFamily - name of the font to be used (for example, "Verdana")
   * @param size       - in pixels, percent or ems
   * @param colour     - alphanumeric colour string, for example #FF00FF
   */
  void setFontMetrics( String fontFamily, String size, String colour );
}