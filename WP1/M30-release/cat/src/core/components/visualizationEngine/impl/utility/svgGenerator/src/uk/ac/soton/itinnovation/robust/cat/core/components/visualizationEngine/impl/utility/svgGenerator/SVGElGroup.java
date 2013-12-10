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

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator;

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.elements.ISVGVector;
import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator.elements.*;

import java.util.*;





public class SVGElGroup extends SVGElement implements ISVGElGroup
{
  private HashMap<UUID, ISVGElement> vectorElements;
  private HashMap<UUID, ISVGElGroup> subGroups;
  
  // Render ordering (note: this is INDEPENDENT of any index property the vector
  // may have (since we may wish to include the same vector instance in multiple groups)
  private LinkedHashSet<UUID> renderOrder;
  
  // ISVGElGroup ---------------------------------------------------------------
  @Override
  public void clearAllElements()
  {
    subGroups.clear();
    vectorElements.clear();
    renderOrder.clear();
  }
  
  @Override
  public ISVGVector addVector( eShapeType type )
  {
    ISVGVector newVec = null;
    
    switch ( type )
    {
      case LINE    : newVec = (ISVGVector) new SVGLine( parentGroup );    break;
      case RECT    : newVec = (ISVGVector) new SVGRect( parentGroup );    break;
      case POLYGON : newVec = (ISVGVector) new SVGPolygon( parentGroup ); break;
      case CIRCLE  : newVec = (ISVGVector) new SVGCircle( parentGroup );  break;
      case TEXT    : newVec = (ISVGVector) new SVGText( parentGroup );    break;
    }
    
    if ( newVec != null )
    {
      // Add to elements list
      ISVGElement el = (ISVGElement) newVec;
      UUID newID = el.getID();
      vectorElements.put( newID, el );
    
      // Include in render order
      renderOrder.add( newID );
    }
    
    return newVec;
  }
  
  @Override
  public ISVGVector addVector( eShapeType type, boolean visible )
  {
    ISVGVector newVec = addVector( type );
    
    if ( newVec != null )
      newVec.setVisible( visible );
    
    return newVec;
  }
  
  @Override
  public ISVGElement getElement( UUID id )
  {
    ISVGElement target = null;
    
    // Try vector elements first
    target = vectorElements.get( id );
    
    if ( target != null ) return target;
    
    // then sub-groups
    return (ISVGElement) subGroups.get( id );
  }
  
  @Override
  public void removeElement( ISVGElement el )
  {
    UUID id = el.getID();
    
    // Try removing the element or sub-group
    if ( id != null )
    {
      if ( vectorElements.remove( id ) != null )
        renderOrder.remove( id );
      else
        subGroups.remove( id );
    }
  }
  
  @Override
  public ISVGElGroup addSubGroup( UUID id )
  {
    if ( subGroups.containsKey(id) )
      subGroups.remove( id );
    
    // Add to sub-groups
    SVGElGroup sg = new SVGElGroup( this, id );
    subGroups.put( id, sg );
    
    // Add in render order
    renderOrder.add( id );
    
    return sg;
  }
  
  @Override
  public ISVGElGroup addSubGroup( UUID id, boolean visible )
  {
    ISVGElGroup newGroup = addSubGroup( id );
    ISVGElement el = (ISVGElement) newGroup;
    el.setAttribute( "display", visible ? "all" : "none" );
    
    return newGroup;
  }
  
  @Override
  public String getXML()
  {
    String xml = "<g id=\"" + elID.toString() +"\" ";
    
    // Inject attributes
    xml += getXMLAttributes();
    
    xml += ">\n";
    
    // Render in order (try UUID as element first, then group)
    for ( UUID id : renderOrder )
    {
      ISVGElement el = vectorElements.get( id );
      if ( el != null )
        xml += el.getXML();
      else
      {
        ISVGElGroup groupEl = subGroups.get( id );
        if ( groupEl != null )
          xml += el.getXML();
      }
    }
    
    // Finalize
    xml += "</g>\n";
    return xml;
  }
  
  // Protected methods ---------------------------------------------------------
  protected SVGElGroup( SVGElGroup parent, UUID svgDocID )
  {
    super( svgDocID );
            
    subGroups   = new HashMap<UUID, ISVGElGroup>();
    vectorElements    = new HashMap<UUID, ISVGElement>();
    renderOrder = new LinkedHashSet<UUID>();
  }
  
  protected void addElement( SVGElement el )
  {
    UUID thisID = el.getID();
    
    // Don't duplicate elements
    if ( vectorElements.get(thisID) == null )
    {
      // Add to element list
      vectorElements.put( thisID, el );
    
      // Include in render order
      renderOrder.add( thisID );
    }
  }
}