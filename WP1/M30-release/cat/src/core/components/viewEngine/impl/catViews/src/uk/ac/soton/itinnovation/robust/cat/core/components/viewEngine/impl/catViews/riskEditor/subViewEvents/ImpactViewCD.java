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
//      Created Date :          21 Oct 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UIChangeData;

import java.util.*;





public class ImpactViewCD extends UIChangeData
{
  public enum eImpact { UNDEFINED,
                        NEG_VHIGH,
                        NEG_HIGH,
                        NEG_MEDIUM,
                        NEG_LOW,
                        NEG_VLOW,
                        POS_VLOW,
                        POS_LOW,
                        POS_MEDIUM,
                        POS_HIGH,
                        POS_VHIGH };
  
  private HashMap<UUID, eImpact> objImpacts;
  
  public ImpactViewCD()
  { 
    super();
    objImpacts = new HashMap<UUID, eImpact>();
  }
  
  @Override
  public void reset()
  {
    super.reset();
    objImpacts.clear();
  }
  
  public void removeImpact( UUID objID )
  { objImpacts.remove(objID); dataChanged = true; }
  
  public void modifyImpact( UUID objID, eImpact impact )
  {
    objImpacts.remove( objID );
    objImpacts.put( objID, impact );
    dataChanged = true;
  }
  
  public Map<UUID, eImpact> getChanges()
  { return objImpacts; }
}