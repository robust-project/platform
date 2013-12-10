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
//      Created Date :          09-Nov-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.impl.base;

import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.spec.ISimulationParamGroup;

import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;

import java.util.*;




public class SimulationParamGroup implements ISimulationParamGroup
{
  private final String               groupName;
  private LinkedHashMap<String, Parameter> groupParams;  
  
  public SimulationParamGroup( String name )
  {
    groupName   = name;
    groupParams = new LinkedHashMap<String, Parameter>();
  }
  
  public SimulationParamGroup( SimulationParamGroup group )
  {
    groupName   = group.groupName;
    groupParams = new LinkedHashMap<String, Parameter>( group.groupParams );
  }
  
  public void removeParameter( String name )
  { groupParams.remove( name ); }
  
  // ISimulationParamGroup -----------------------------------------------------
  @Override
  public String getName()
  { return groupName; }
  
  @Override
  public Set<String> getParameterNames()
  { return groupParams.keySet(); }
  
  @Override
  public boolean addParameter( String name, Parameter param )
  {
    // Safety first
    if ( name == null || param == null ) return false;
    if ( groupParams.containsKey(name) ) return false;
    
    groupParams.put( name, param );
    
    return true;
  }
  
  @Override
  public Parameter getParameter( String name )
  { return groupParams.get( name ); }
}
