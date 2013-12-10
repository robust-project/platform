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
//      Created Date :          26-Jan-2012
//      Created for Project :   robust-cat-core-components-visualizationEngine-impl-utility-svgGenerator
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator;

import java.util.UUID;



/**
 * SVGParam is protected member as it is only to be used internally to qualify
 * other elements such as SVG definitions
 * 
 * @author Simon Crowle
 */
class SVGParam extends SVGElement
{
  private String paramType;
  
  // SVGElement ----------------------------------------------------------------
  @Override
  public String getXML()
  {
    String xml = "<" + paramType + " ";
    
    xml += getXMLAttributes();
    
    xml += "/>";
    
    return xml;
  }
  
  // Protected methods ---------------------------------------------------------
  protected SVGParam( String type )
  {
    super( UUID.randomUUID() );
    paramType = type;
  }
  
  protected SVGParam( String type, int index )
  {
    super ( UUID.randomUUID(), index );
    paramType = type;
  }
}
