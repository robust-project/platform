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
//      Created Date :          17-Aug-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel;

import java.util.*;




public class ROTreatmentGroup
{
  private String riskOpTitle;
  private UUID   riskOpID;
  
  private HashMap<UUID, Treatment> treatments;
  private List<TreatmentRecord> historicTreatments;

  public ROTreatmentGroup( String roTitle, UUID roID )
  {
    riskOpTitle = roTitle;
    riskOpID    = roID;
    
    treatments = new HashMap<UUID, Treatment>();
    historicTreatments = new ArrayList<TreatmentRecord>();
  }
  
  public String getROTitle()
  { return riskOpTitle; }
  
  public UUID getROID()
  { return riskOpID; }
  
  public boolean isCurrentlyActive()
  { return ( !treatments.isEmpty() ); }
  
  public void addTreatment( Treatment tmt )
  {
    if ( tmt != null )
    {
      UUID id = tmt.getID();
      
      if ( !treatments.containsKey(id) )
        treatments.put( tmt.getID(), tmt );
    }
  }
  
  public void addTreatments( Set<Treatment> treatments )
  {
    if ( treatments != null )
    {
      Iterator<Treatment> tmtIt = treatments.iterator();
      while ( tmtIt.hasNext() )
        addTreatment( tmtIt.next() );
    }
  }
  
  public Treatment getTreatment( UUID tmtID )
  { return treatments.get( tmtID ); }
  
  public List<Treatment> getOrderedTreatments()
  {
    ArrayList<Treatment> orderedTreatments = new ArrayList<Treatment>();
    
    TreeMap<Float, Treatment> tmtTree = new TreeMap<Float, Treatment>();
    Iterator<Treatment> groupIt = treatments.values().iterator();
    while ( groupIt.hasNext() )
    {
      Treatment tmt = new Treatment( groupIt.next() );
      Float index = tmt.getCopyOfIndex();
      while (tmtTree.containsKey(index)) {
          index += new Float(0.001); // fudge to ensure that indices are still unique // TODO: is there a better solution?
      }
      tmtTree.put( index , tmt );
    }
    
    Set<Float> orderSet = tmtTree.descendingKeySet();
    Iterator<Float> orderIt = orderSet.iterator();
    while ( orderIt.hasNext() )
      orderedTreatments.add( tmtTree.get(orderIt.next()) );
    
    return orderedTreatments;
  }
  
  public void deleteTreatment( UUID tmtID )
  { treatments.remove( tmtID ); }
  
  public void addHistoricTreatment( TreatmentRecord tmtRec )
  { if ( tmtRec != null ) historicTreatments.add( tmtRec ); }
  
  public void setHistoricTreatments( List<TreatmentRecord> treatments )
  { if ( treatments != null ) historicTreatments = treatments; }
  
  public List<TreatmentRecord> getCopyOfHistoricTreatments()
  {
    ArrayList<TreatmentRecord> histList = new ArrayList<TreatmentRecord>();
    
    Iterator<TreatmentRecord> recIt = historicTreatments.iterator();
    while ( recIt.hasNext() )
    { histList.add( recIt.next() ); }
    
    return histList;
  }
}
