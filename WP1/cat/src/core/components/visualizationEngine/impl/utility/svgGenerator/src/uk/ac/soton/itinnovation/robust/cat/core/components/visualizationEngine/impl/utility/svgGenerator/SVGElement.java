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
//      Created Date :          2011-09-12
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator;

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.*;

import java.util.*;




public abstract class SVGElement implements ISVGElement
{
  protected SVGElGroup              parentGroup;
  protected UUID                    elID;
  protected HashMap<String, String> xmlElAttrs;
  protected int                     elementIndex = 0;
  
  // ISVGElement ---------------------------------------------------------------
  @Override
  public UUID getID()
  { return elID; }
  
  @Override
  public ISVGElGroup getParent()
  { return parentGroup; }
  
  @Override
  public String getAttributeValue( String attr )
  {
    return xmlElAttrs.get( attr );
  }
  
  @Override
  public void setAttribute( String attr, String value )
  {
    if ( xmlElAttrs.containsKey(attr) )
      xmlElAttrs.remove( attr );
    
    xmlElAttrs.put( attr, value );
  }
  
  @Override
  public int getIndex()
  { return elementIndex; }

  @Override
  public abstract String getXML(); // Sub-classes to implement
    
  // Protected methods ---------------------------------------------------------
  protected SVGElement( UUID id )
  {
    parentGroup = null;
    elID = id;
   
    xmlElAttrs = new HashMap<String, String>();
  }
  
  protected SVGElement( UUID id, int index )
  {
    parentGroup = null;
    elID = id;
    elementIndex = index;
    
    xmlElAttrs = new HashMap<String, String>();
  }
  
  protected void setParent( SVGElGroup parent )
  {
    parentGroup = parent;
    
    if ( parentGroup != null )
      parentGroup.addElement( this );
  }
  
  protected String getXMLAttributes()
  {
    String xml = "";
    
    Set<String> keySet = xmlElAttrs.keySet();
    for ( String key : keySet )
    {
      String value = xmlElAttrs.get( key );
      
      xml += key + "=\"";
      xml += value + "\" ";
    }
    
    return xml;
  }
  
  protected void setIndex( int index )
  { elementIndex = index; }
}