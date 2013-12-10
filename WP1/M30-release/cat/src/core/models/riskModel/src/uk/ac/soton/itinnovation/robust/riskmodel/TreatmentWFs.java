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
//      Created By :            bmn
//      Created Date :          03-Nov-2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.riskmodel;

import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.TreatmentTemplate;

import java.util.*;
import java.util.Map.Entry;





public class TreatmentWFs
{
  private HashMap<UUID, TreatmentTemplate> treatmentsTemplatesByID;
  private TreeMap<Float, UUID>             templatesByOrder;
  

  public TreatmentWFs()
  {
    treatmentsTemplatesByID = new HashMap<UUID, TreatmentTemplate>();
    templatesByOrder        = new TreeMap<Float, UUID>();
  }

  public TreatmentWFs( Set<TreatmentTemplate> treatments )
  {
    treatmentsTemplatesByID = new HashMap<UUID, TreatmentTemplate>();
    
    if ( treatments != null )
    {
      Iterator<TreatmentTemplate> treatIt = treatments.iterator();
      while ( treatIt.hasNext() )
      {
        TreatmentTemplate tt = treatIt.next();
        
        treatmentsTemplatesByID.put( tt.getID(), tt );
      }
    }
  }
  //bmn

    public TreeMap<Float, UUID> getTemplatesByOrder() {
        return templatesByOrder;
    }

    public void setTemplatesByOrder(TreeMap<Float, UUID> templatesByOrder) {
        this.templatesByOrder = templatesByOrder;
    }

    public HashMap<UUID, TreatmentTemplate> getTreatmentsTemplatesByID() {
        return treatmentsTemplatesByID;
    }

    public void setTreatmentsTemplatesByID(HashMap<UUID, TreatmentTemplate> treatmentsTemplatesByID) {
        this.treatmentsTemplatesByID = treatmentsTemplatesByID;
    }
  
  
  public List<Entry<Float,TreatmentTemplate>> getOrderedCopyOfTreatmentTemplates()
  {
    List<Entry<Float,TreatmentTemplate>> tmtTemplateList 
            = new ArrayList<Entry<Float,TreatmentTemplate>>();
    
    Set<Float> orderSet = templatesByOrder.descendingKeySet();
    Iterator<Float> orderIt = orderSet.iterator();
    while ( orderIt.hasNext() )
    {
      Float order          = orderIt.next();
      UUID ttID            = templatesByOrder.get( order );
      TreatmentTemplate tt = treatmentsTemplatesByID.get( ttID );
      
      if ( tt != null )
        tmtTemplateList.add( new HashMap.SimpleEntry<Float, TreatmentTemplate>( order, tt) );
    }
    
    return tmtTemplateList;
  }

  public void addTreatmentTemplate( TreatmentTemplate tmpl, Float order )
  {
    if ( tmpl != null )
    {
      UUID tmtID = tmpl.getID();
      
      if ( !treatmentsTemplatesByID.containsKey(tmtID) )
        treatmentsTemplatesByID.put( tmtID, tmpl );
      
      templatesByOrder.put( order, tmtID );
    }
  }

  public void removeTreatment( UUID treatmentID )
  {
    treatmentsTemplatesByID.remove( treatmentID );
    
    // Search through ordered list removing all instances of treatment ID
    TreeMap<Float, UUID> updatedList = new TreeMap<Float, UUID>();
    
    Set<Entry<Float, UUID>> existingSet = templatesByOrder.entrySet();
    Iterator<Entry<Float, UUID>> exIt = existingSet.iterator();
    while ( exIt.hasNext() )
    {
      // Keep all other entries
      Entry<Float, UUID> entry = exIt.next();
      if ( !entry.getValue().equals(treatmentID) )
        updatedList.put( entry.getKey(), entry.getValue() );
    }
    
    templatesByOrder = updatedList;
  }
  
  public void clearAllTemplates()
  {
    treatmentsTemplatesByID.clear();
    templatesByOrder.clear();
  }

  @Override
  public boolean equals( Object rhsObj )
  {
    if ( rhsObj == null )                  return false;
    if ( getClass() != rhsObj.getClass() ) return false;
    
    final TreatmentWFs rhsTWF = (TreatmentWFs) rhsObj;
    
    if ( (treatmentsTemplatesByID != rhsTWF.treatmentsTemplatesByID ) &&
         (treatmentsTemplatesByID == null || !hasEqualTemplateMap(rhsTWF.treatmentsTemplatesByID)) )
      return false;
    
    if ( (templatesByOrder != rhsTWF.templatesByOrder) &&
         (templatesByOrder == null || !hasEqualOrderMap(rhsTWF.templatesByOrder)) )
      return false;
      
      
    return true;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 23 * hash + (this.treatmentsTemplatesByID != null ? this.treatmentsTemplatesByID.hashCode() : 0);
    
    return hash;
  }

  public boolean containUUID(UUID uuid)
  {
    if ( treatmentsTemplatesByID != null && 
         treatmentsTemplatesByID.get(uuid) != null ) return true;
    
    return false;
  }

  private boolean hasEqualTemplateMap( HashMap<UUID, TreatmentTemplate> rhs )
  {
    if ( treatmentsTemplatesByID.size() == rhs.size() )
    {
      for ( Entry<UUID, TreatmentTemplate> entry : rhs.entrySet() )
        if ( !containsTemplateEntry(entry) )
          return false;
    
      return true;
    }
    
    return false;
  }

  private boolean containsTemplateEntry( Entry<UUID, TreatmentTemplate> rhs )
  {
    if ( treatmentsTemplatesByID != null && 
         treatmentsTemplatesByID.get(rhs.getKey()) != null )
    {
      TreatmentTemplate lhs = treatmentsTemplatesByID.get( rhs.getKey() );
      return lhs.isEqual( rhs.getValue() );
    }

    return false;
  }
  
  private boolean hasEqualOrderMap( TreeMap<Float, UUID> rhs )
  {
    if ( templatesByOrder.size() == rhs.size() )
    {
      for ( Entry<Float,UUID> entry : rhs.entrySet() )
        if ( !containsOrderEntry(entry) )
          return false;
    
      return true;
    }
    
    return false;
  }
  
  private boolean containsOrderEntry( Entry<Float, UUID> rhsEntry )
  {
    if ( templatesByOrder != null && 
         templatesByOrder.get(rhsEntry.getKey()) != null ) 
    {
      UUID lhsID = templatesByOrder.get( rhsEntry.getKey() );
      return ( lhsID.equals( rhsEntry.getValue()) );
    }

      return false;
  }
}
