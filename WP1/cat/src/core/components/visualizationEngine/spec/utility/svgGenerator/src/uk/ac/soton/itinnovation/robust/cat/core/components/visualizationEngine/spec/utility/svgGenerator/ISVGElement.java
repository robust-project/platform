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

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator;

import java.util.UUID;




/**
 * ISVGElement is a high-level abstraction of a wide range of SVG elements that
 * can be manipulated in the SVG document. Use this type to get high-level
 * information about the current element as well as the SVG rendering.
 * 
 * Any element can be qualified with further SVG attributes that are not directly
 * supported by the interfaces provided in this library by using the
 * getAttributeValue(..) and setAttribute(..) methods. For example:
 * 
 * myElement.setAttribute( "stroke", "none" );
 * 
 * will set the stoke attribute of the element (this may or may not be actually
 * used depending on the actual element type).
 * 
 * @author Simon Crowle
 */
public interface ISVGElement
{
  /**
   * Returns the UUID associated with this element. Most SVG elements have a
   * UUID generated internally.
   * 
   * @return - the unique ID for this element
   */
  UUID getID();
  
  /**
   * Returns the SVG components associated with this element.
   * 
   * @return - String representation of the SVG
   */
  String getXML();
  
  /*
   * Returns the parent of this element
   * 
   */
  ISVGElGroup getParent();
  
  /**
   * Returns the value of a named SVG attribute (if it exists)
   * 
   * @param attr - name of the attribute value to be returned
   * @return     - returns the value of the attribute if it exists, or null
   */
  String getAttributeValue( String attr );
  
  /**
   * Sets the value of a named SVG attribute
   * 
   * @param attr  - name of attribute to set
   * @param value - value of the attribute
   */
  void setAttribute( String attr, String value );
  
  /**
   * Returns an automatically generated index value for the element. The meaning
   * of this value varies, depending on the type of the element; for example, a
   * SVGParam type uses this index to specify the parameter's order, which is
   * sometimes important.
   * 
   * @return - numerical index value.
   */
  int getIndex();
}