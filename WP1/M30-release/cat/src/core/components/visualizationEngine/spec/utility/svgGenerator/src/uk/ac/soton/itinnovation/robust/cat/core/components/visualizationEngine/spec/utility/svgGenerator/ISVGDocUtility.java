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

import java.util.List;
import java.util.Map.Entry;



/**
 * ISVGDocUtility provides functionality to assist SVG composition.
 * 
 * @author sgc
 */
public interface ISVGDocUtility
{
  /**
   * Use createGradient to create a gradient definition for use with other SVG elements.
   * Once you have created the gradient, you will subsequently need to add attributes
   * x1, x2, y1 and y2 (using the generic ISVGElement interface).
   * 
   * @param name  - The definition ID: refer to this in other SVG elements
   * @param stops - A list of entries for 'stop-color' and 'offset' stop elements respectively
   * @return      - Returns the generic ISVGDefinition which should be added to the ISVGDoc instance
   */
  ISVGDefinition createGradient( String name, List<Entry<String, String>> stops );
}
