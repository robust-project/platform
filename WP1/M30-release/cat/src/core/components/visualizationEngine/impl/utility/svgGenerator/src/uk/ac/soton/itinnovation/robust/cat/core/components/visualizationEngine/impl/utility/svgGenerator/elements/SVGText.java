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
//      Created By :            sgc
//      Created Date :          2011-09-12
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator.elements;

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.elements.ISVGText;
import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator.*;




public class SVGText extends SVGVector implements ISVGText
{
  private String textValue;
  
  public SVGText( SVGElGroup parent )
  {
    super( parent );
  }
  
  @Override
  public String getXML()
  {
    // Write default values if none exist
    if ( getAttributeValue( "font-family") == null )
      setAttribute( "font-family", "Verdana" );
    
    if ( getAttributeValue( "font-size" ) == null )
      setAttribute( "font-size", "32" );
    
    if ( getAttributeValue( "fill" ) == null )
      setAttribute( "fill", "grey" );
    
    String xml = "";
    
    // Inject scaling and transform if required
    if ( isTransformRequired() ) xml += getTransformGroup();
    
    // Text X & Y not supplied positioning is applied using the transform group
    // so just add attributes
    xml += "<text " + getXMLAttributes() + ">";
    
    // Inject value
    xml += textValue;
    xml += "</text>";
    
    // .. and finalize transform
    if ( isTransformRequired() ) xml += "</g>";

    xml += "\n";
    
    return xml;
  }
  
  // ISVGText ------------------------------------------------------------------  
  @Override
  public void setValue( String value )
  {
    textValue = value;
  }
  
  @Override
  public void setFontMetrics( String fontFamily, String size, String colour )
  {
    setAttribute( "font-family", fontFamily );
    setAttribute( "font-size", size );
    setAttribute( "fill", colour );
  }
}