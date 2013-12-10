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
//      Created Date :          26-Jan-2012
//      Created for Project :   robust
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator;



/**
 * ISVGDefinition is a container for any kind of SVG definition (such as a
 * Linear Gradient). See http://www.w3.org/TR/SVG/struct.html#DefsElement for
 * further information
 * 
 * @author sgc
 */
public interface ISVGDefinition
{
  /**
   * Creates a parameter of named SVG type
   * 
   * @param typeName - SVG parameter type
   * @return         - the parameter instance (as an ISVGElement)
   */
  ISVGElement createParam( String typeName );
  
  /**
   * Removes a parameter instance from the definition
   * 
   * @param param - parameter to be removed
   */
  void removeParam( ISVGElement param );
}
