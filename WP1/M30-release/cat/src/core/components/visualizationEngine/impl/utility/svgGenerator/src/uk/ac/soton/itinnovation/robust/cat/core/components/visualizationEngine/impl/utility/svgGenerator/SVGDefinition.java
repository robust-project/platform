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

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator;

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.*;

import java.util.*;



public class SVGDefinition extends SVGElement implements ISVGDefinition
{
  protected String                     defTypeName;
  protected String                     defName;
  protected HashMap<UUID, ISVGElement> defParameters;
  protected TreeMap<Integer, UUID>     orderedParameters; // Ordering can be important for some SVG elements
  protected int                        paramIndex = 0;
  
  // SVGElement ----------------------------------------------------------------
  @Override
  public String getXML()
  {
    String xml = "<" + defTypeName + " id=\"" + defName + "\" ";
    xml += getXMLAttributes() + ">\n";
    
    // Insert parameters (in order, as this can be important sometimes)
    if ( !orderedParameters.isEmpty() )
    {
      Collection<UUID> ids = orderedParameters.values();
      Iterator<UUID> ascList = ids.iterator();
      while ( ascList.hasNext() )
      {
        ISVGElement el = defParameters.get( ascList.next() );
        if ( el != null ) xml += el.getXML();
      }
    }
    
    xml += "</" + defTypeName + ">\n";
    
    return xml;
  }

  // ISVGDefinition ------------------------------------------------------------
  @Override
  public ISVGElement createParam( String typeName )
  {
    SVGParam newParam = new SVGParam( typeName, paramIndex );
    defParameters.put( newParam.getID(), (ISVGElement) newParam );
    orderedParameters.put( paramIndex, newParam.getID() );
    
    paramIndex++;
    
    return (ISVGElement) newParam;
  }
  
  @Override
  public void removeParam( ISVGElement param )
  { 
    if ( param != null )
    {
      defParameters.remove( param.getID() );
      orderedParameters.remove( param.getIndex() );
      
      // Don't modify index, just keep counting upwards
    }
  }
  
  // Protected methods ---------------------------------------------------------
  protected SVGDefinition( String typeName, String dName )
  {
    super( UUID.randomUUID() ); // definition name will be actually be used instead 
                                // of the UUID value when outputting XML as definitions
                                // are only used to qualify other elements
    defTypeName   = typeName;
    defName       = dName;
    defParameters = new HashMap<UUID, ISVGElement>();
    orderedParameters = new TreeMap<Integer, UUID>();
  }
}
