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
//      Created Date :          2011-10-18
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.vizBase;

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.vf.entities.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.vf.types.VizException;

import java.util.*;


public abstract class VizDataObjectBase implements IDataObject
{
  protected String             objectLabel;
  protected IVizGraphic        vizGraphic;
  protected HashSet<IRelation> inRelations;
  protected HashSet<IRelation> outRelations;
  
  // IDataObject ---------------------------------------------------------------  
  @Override
  public IVizGraphic getGraphic() { return vizGraphic; }
  
  @Override
  public String getLabel() { return objectLabel; }
  
  @Override
  public Collection<IRelation> getCopyOfInRelations()
  {
    HashSet<IRelation> copy = new HashSet<IRelation>( inRelations );
    return copy;
  }
  
  @Override
  public Collection<IRelation> getCopyOfOutRelations()
  {
    HashSet<IRelation> copy = new HashSet<IRelation>( outRelations );
    return copy;
  }
  
  @Override
  public void addInRelation( IRelation relation ) throws VizException
  {
    // Do not allow this relationship if it already exists as an out-going relation
    if ( outRelations.contains(relation) )
      throw( new VizException("Cannot add IN relation: already exists as OUT") );
    
    inRelations.add( relation );
  }
  
  @Override
  public void addOutRelation( IRelation relation ) throws VizException
  {
    // Do not allow this relationship if it already exists as an in-coming relation
    if ( inRelations.contains(relation) )
      throw( new VizException("Cannot add OUT relation: already exists as IN") );
    
    outRelations.add( relation );
  }
  
  @Override
  public void removeRelation( IRelation relation )
  {
    // Try removing this relationship (could be IN or OUT)
    if ( !inRelations.remove(relation) )
      outRelations.remove( relation );
  }
  
  @Override
  public void setLabel( String label )
  { objectLabel = label; }
  
  // Protected methods ---------------------------------------------------------
  protected VizDataObjectBase()
  {
    inRelations = new HashSet<IRelation>();
    outRelations = new HashSet<IRelation>();
  }
}