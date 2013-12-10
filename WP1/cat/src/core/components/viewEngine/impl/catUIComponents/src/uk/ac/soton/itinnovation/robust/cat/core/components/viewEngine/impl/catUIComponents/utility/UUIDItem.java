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
//      Created Date :          2011-11-08
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility;

import java.io.Serializable;
import java.util.*;




public class UUIDItem implements Cloneable, Serializable
{
  private static final String defaultDataKey = "defaultUUIDItemKey";
  
  private String itemLabel;
  private UUID   itemID;
  private HashMap<String, Object> dataObjects;
  
  public UUIDItem( String label )
  {
    itemLabel = label;
    itemID = UUID.randomUUID();
    dataObjects = new HashMap<String,Object>();
  }
  
  public UUIDItem( String label, UUID id )
  {
    itemLabel = label;
    itemID    = id;
    dataObjects = new HashMap<String,Object>();
  }
  
  public UUIDItem( String label, UUID id, Object data )
  {
    itemLabel = label;
    itemID    = id;
    dataObjects = new HashMap<String,Object>();
    
    dataObjects.put( defaultDataKey, data );
  }

  @Override
  public String toString() { return itemLabel; }

  public UUID   getID()    { return itemID; }

  public void   setLabel( String label ) { itemLabel = label; }
  public String getLabel()               { return itemLabel; }

  public void setData( Object data )
  {
    dataObjects.remove( defaultDataKey );
    dataObjects.put( defaultDataKey, data );
  }
  
  public void setData( String key, Object data )
  {
    dataObjects.remove( key );
    dataObjects.put( key, data );
  }
  
  public Object getData() 
  { return dataObjects.get( defaultDataKey ); }
  
  public Object getData( String key )
  { return dataObjects.get( key ); }
  
  // Cloneable -----------------------------------------------------------------
  @Override
  public UUIDItem clone() throws CloneNotSupportedException
  {
    UUIDItem clone = null;
    
    try { clone = (UUIDItem) super.clone(); }
    catch (CloneNotSupportedException cnse) { throw cnse; }
    
    clone.itemID = itemID;
    clone.itemLabel = itemLabel;
    clone.dataObjects = new HashMap<String,Object>( dataObjects );
    
    return clone;
  }
}
