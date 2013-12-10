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
//      Created Date :          18-Jan-2012
//      Created for Project :   robust
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.vizLibrary.roMatrix;


import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.vizLibrary.roMatrix.IROMRiskEntity;

import java.util.*;




public class ROMRiskEntity implements IROMRiskEntity
{
  private UUID                riskID;
  private String              reTitle;
  private String              reDescription;
  private int                 reIndex;
  private eROMProb            reProbability;
  private EnumSet<eROMImpact> reImpacts;
  
  public UUID getRiskID()
  { return riskID; }
  
  public String getTitle()
  { return reTitle; }
  
  public String getDescription()
  { return reDescription; }
  
  public int getRiskIndex()
  { return reIndex; }
  
  public int getIndexValue()
  { return reIndex; }
  
  public eROMProb getProbability()
  { return reProbability; }
  
  public List<eROMImpact> getImpacts()
  {
    ArrayList<eROMImpact> list = new ArrayList<eROMImpact>();
    Iterator<eROMImpact> iIt = reImpacts.iterator();
    while ( iIt.hasNext() )
      list.add( iIt.next() );
    
    return list;
  }

  // IROMRiskEntity ------------------------------------------------------------
  @Override
  public void setTitle( String title )
  { reTitle = title; }
  
  @Override
  public void setDescription( String desc )
  { reDescription = desc; }
  
  @Override
  public void setRiskIndex( int index )
  { reIndex = index; }
  
  @Override
  public void setProbCategory( eROMProb prob )
  { reProbability = prob; }
  
  @Override
  public void addImpactLevel( eROMImpact impact )
  { reImpacts.add(impact); }
  
  // Protected methods ---------------------------------------------------------
  protected ROMRiskEntity( UUID id )
  {
    riskID = id;
    reImpacts = EnumSet.noneOf( eROMImpact.class );
    reTitle = "";
    reDescription = "";
    reIndex = 0;
  }
  
  protected ROMRiskEntity( ROMRiskEntity src )
  {
    riskID        = src.riskID;
    reTitle       = src.reTitle;
    reDescription = src.reDescription;
    reIndex       = src.reIndex;
    reProbability = src.reProbability;
    reImpacts     = src.reImpacts.clone();
  }
}
