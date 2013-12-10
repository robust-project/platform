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
//      Created Date :          29 Dec 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.dashboard.indicators.IROMatrixController;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UUIDItem;

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.vizLibrary.roMatrix.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.engine.CATVisualizationEngine;

import uk.ac.soton.itinnovation.robust.riskmodel.*;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.ICATAppViewNavListener;





public class ROMatrixController extends UFAbstractEventManager
                                implements Serializable,
                                           IROMatrixController,
                                           ROMatrixViewListener
{
  private ROMatrixView          roIndicatorView;
  private LinkedHashSet<UUID>       matRiskIDs;
  private HashMap<UUID, Double> matRiskProbabilities;
  
  private transient IROMatrix                     roMatrix;
  private transient HashMap<UUID, RiskEvalResult> matRiskEvaluations;
  private Set<UUID> selectedRisks;
  private ICATAppViewNavListener    navListener;
  
  public ROMatrixController(ICATAppViewNavListener navListener)
  {
    super();
    this.navListener = navListener;
    
    initialiseROMatrix();
  }

  public AbstractDashIndicatorView getIndicatorView()
  { return roIndicatorView; }

  private void initialiseROMatrix() {
    matRiskIDs = new LinkedHashSet<UUID>();
    matRiskEvaluations   = new HashMap<UUID, RiskEvalResult>();
    matRiskProbabilities = new HashMap<UUID, Double>();
    
    roMatrix = CATVisualizationEngine.createROMatrix();
    
    // Create matrix view and display empty
    roIndicatorView = new ROMatrixView();
    roIndicatorView.applySVGViz( roMatrix.getXMLResult() );
    roIndicatorView.addListener( this );
  }
  
  // IROMatrixController -------------------------------------------------------  
  @Override
  public final void resetROMatrix() {
      matRiskIDs = new LinkedHashSet<UUID>();
      matRiskEvaluations = new HashMap<UUID, RiskEvalResult>();
      matRiskProbabilities = new HashMap<UUID, Double>();
      
      roMatrix.resetMatrix();
      
      // Update list view
      List<UUID> descProbList = createDescendingROIDList(false);
      updateViewProbList(descProbList);

      // Update matrix view
      updateMatrixIndices(descProbList);
      
      roIndicatorView.applySVGViz(roMatrix.getXMLResult());
      roIndicatorView.setCurrentEvaluationInfo("No risk selected", null, null);
  }

  private void addROEvaluationResult( Risk risk, 
                                     Date currentDate,
                                     Date forecastDate, 
                                     //double probability
                                     List<ResultItem> results
          )
  {
    if ( risk != null )
    {
      UUID rID = risk.getId();
      
      // Remove old instance data, if it exists
      matRiskEvaluations.remove( rID );
      matRiskProbabilities.remove( rID );
      
      double probability = findHighestProbability(results);
      
      // Add new evaluation
      RiskEvalResult rer = new RiskEvalResult( risk, currentDate, forecastDate, 
                                               findMostNegativeImpact(risk),
                                               probability,
                                               results
              );
      
      matRiskIDs.add(rID);
      matRiskEvaluations.put( rID, rer );
      matRiskProbabilities.put( rID, probability );
      
      // Update list view
      //List<UUID> descProbList = createDescendingROIDList(false);
      //updateViewProbList( descProbList );
      Integer riskIndex = addOrUpdateProbListItem( rID );

      // Update risk matrix visualization data
      addEvalResultToMatrix( rer, riskIndex );

      // Update matrix view
      // Now done within addOrUpdateProbListItem()
      //updateMatrixIndices( descProbList );
      roIndicatorView.applySVGViz( roMatrix.getXMLResult() );   
      
        String riskTitle = null;

        /*
        if (selectedRisks != null) {
            if (selectedRisks.size() == 1) {

                UUID selectedRiskID = selectedRisks.iterator().next();

                if (rID.equals(selectedRiskID)) {
                    riskTitle = rer.getRiskTitle();
                }
            }
        }
        */

        roIndicatorView.setCurrentEvaluationInfo(riskTitle,
                rer.getCurrentDate(),
                rer.getForecastDate());
       
    }
  }
  
    @Override
    public void addROEvaluationResult(Risk riskTarget, EvaluationResult res) {
        List<ResultItem> results = res.getResultItems();
        //List<ResultItem> results = getDummyResults(); // Use this to get multiple dummy results

        if (results != null && !results.isEmpty()) {
            //ResultItem ri = results.get(0);

            this.addROEvaluationResult(riskTarget,
                    res.getCurrentDate(),
                    res.getForecastDate(),
                    //ri.getProbability()
                    results
                    );

        }
    }

    @Override
    public void removeEvaluationResultsForRisk(Risk risk) {
        if (risk != null) {
            UUID rID = risk.getId();

            // Remove old instance data, if it exists
            matRiskIDs.remove(rID);
            matRiskEvaluations.remove(rID);
            matRiskProbabilities.remove(rID);
            
            removeEvalResultFromMatrix(rID);
            
            // Update list view
            List<UUID> descProbList = createDescendingROIDList(false);
            updateViewProbList( descProbList );

            // Update matrix view
            updateMatrixIndices( descProbList );
            roIndicatorView.applySVGViz( roMatrix.getXMLResult() );     
        }
    }
  
  // ROMatrixViewListener ------------------------------------------------------
  @Override
  public void onROSelected( Set<UUID> ids )
  { 
    selectedRisks = ids;
    roMatrix.highlightEntities( ids );
    roIndicatorView.applySVGViz( roMatrix.getXMLResult() );
    
    // Also update the current and forecast info on the view
    if ( !ids.isEmpty() )
    {
      if ( ids.size() == 1 )
      {
        UUID id = ids.iterator().next();
        
        RiskEvalResult rer = matRiskEvaluations.get( id );
        if ( rer != null )
          roIndicatorView.setCurrentEvaluationInfo( rer.getRiskTitle(), 
                                                    rer.getCurrentDate(),
                                                    rer.getForecastDate() );
      }
      else
        roIndicatorView.setCurrentEvaluationInfo( "Multiple risks selected", 
                                                  null, null );
    }
    else {
        roIndicatorView.setCurrentEvaluationInfo( "No risk selected", null, null);
    }
  }

    @Override
    public void onViewResultsButtonClicked(Set<UUID> ids) {
        //TODO: ids is not required here, as risks have already been selected
        viewResults();
    }
    
    private void viewResults() {
        if (selectedRisks == null)
            return;
        
        List<RiskEvalResult> riskEvalResults = new ArrayList<RiskEvalResult>();
        
        for (UUID riskID : selectedRisks) {
            RiskEvalResult rer = matRiskEvaluations.get( riskID );
            riskEvalResults.add(rer);
        }
        
        roIndicatorView.viewResults(riskEvalResults);
    }

    @Override
    public void onSimulateButtonClicked(Set<UUID> ids) {
        // Navigate to Simulation Centre
        navListener.onNavigateApp(ICATAppViewNavListener.eNavDest.SIMULATOR_CENTRE);
    }
  
  // Private methods -----------------------------------------------------------  
    private List<UUID> createDescendingROIDList(boolean sortList) {
        ArrayList<UUID> probList = new ArrayList<UUID>();
        
        if (sortList) {
            TreeSet<SortableRiskID> sortedIDs = new TreeSet<SortableRiskID>(new SortableRiskID());

            Set<Entry<UUID, Double>> idProbs = matRiskProbabilities.entrySet();
            for (Entry<UUID, Double> ip : idProbs) {
                sortedIDs.add(new SortableRiskID(ip.getKey(), ip.getValue()));
            }

            Iterator<SortableRiskID> probIt = sortedIDs.descendingIterator();
            
            while (probIt.hasNext()) {
                SortableRiskID srID = probIt.next();
                probList.add(srID.getID());
            }
        }
        else
        {
            for (UUID riskID: matRiskIDs)
            {
                probList.add(riskID);
            }
        }

        return probList;
    }
  
  private void updateViewProbList( List<UUID> descProbList )
  {
    ArrayList<UUIDItem> viewProbList = new ArrayList<UUIDItem>();
    Integer riskIndex = 1;
    
    for ( UUID roID : descProbList )
    {
      UUIDItem newItem = createProbListItem(riskIndex, roID);
      viewProbList.add( newItem );
      riskIndex++;
    }
    
    // Update the view
    roIndicatorView.updateProbList( viewProbList );
  }
    
  private Integer addOrUpdateProbListItem( UUID roID )
  {
      Integer riskIndex = -1; // indicates unknown index
      UUIDItem newItem = createProbListItem(riskIndex, roID);
      riskIndex = roIndicatorView.addOrUpdateProbListItem(newItem);
      return riskIndex;
  }
  
  private UUIDItem createProbListItem( Integer riskIndex, UUID roID )
  {
      RiskEvalResult rer = matRiskEvaluations.get(roID);
      double riskProb = matRiskProbabilities.get(roID);
      String label = null;
      
      if (riskIndex > 0)
          label = "RO" + riskIndex.toString();

      UUIDItem newItem = new UUIDItem(label, roID);
      newItem.setData(rer);
      newItem.setData("Prob", riskProb);

      return newItem;
  }
  
  private void updateMatrixIndices( List<UUID> descProbList )
  {
    if ( !descProbList.isEmpty() )
    {
      Integer index = 1;
      HashMap<UUID, Integer> entityIndicies = new HashMap<UUID, Integer>();
      
      for ( UUID id : descProbList )
      {
        entityIndicies.put( id, index );
        index++;
      }
      
      roMatrix.updateEntityIndicies( entityIndicies );
    }
  }
  
  /* Not used
  private void addOrUpdateMatrixIndex(UUID id, Integer index)
  {
      HashMap<UUID, Integer> entityIndicies = new HashMap<UUID, Integer>();
      entityIndicies.put( id, index );
      roMatrix.updateEntityIndicies( entityIndicies );
      //roMatrix.addOrUpdateMatrixIndex(id, index);
  }
  */
  
  private void addEvalResultToMatrix( RiskEvalResult rer, Integer riskIndex)
  {    
    IROMRiskEntity re = roMatrix.addEntity( rer.getRiskID(), riskIndex );
    re.setTitle( rer.getRiskTitle() );
    
    // For now, 'pre-configure' the probability range to a linear scale
    IROMRiskEntity.eROMProb probCat = IROMRiskEntity.eROMProb.VHIGH;
    
    double probability = rer.getProbability();
    
    if ( probability < 0.2 ) probCat = IROMRiskEntity.eROMProb.VLOW;
    else if ( probability < 0.4 ) probCat = IROMRiskEntity.eROMProb.LOW;
    else if ( probability < 0.6 ) probCat = IROMRiskEntity.eROMProb.MEDIUM;
    else if ( probability < 0.8 ) probCat = IROMRiskEntity.eROMProb.HIGH;
    
    re.setProbCategory( probCat );
    
    // Compress some of the levels to fit in the matrix
    ImpactLevel expectedImpact = rer.getImpact();
    switch ( expectedImpact )
    {
      case NEG_VHIGH  :
      case NEG_HIGH   : re.addImpactLevel( IROMRiskEntity.eROMImpact.NEG_HIGH );   break;
      case NEG_MEDIUM : re.addImpactLevel( IROMRiskEntity.eROMImpact.NEG_MEDIUM ); break; 
      case NEG_LOW    :
      case NEG_VLOW   : re.addImpactLevel( IROMRiskEntity.eROMImpact.NEG_LOW );    break;

      case POS_VHIGH  :
      case POS_HIGH   : re.addImpactLevel( IROMRiskEntity.eROMImpact.POS_HIGH );   break;
      case POS_MEDIUM : re.addImpactLevel( IROMRiskEntity.eROMImpact.POS_MEDIUM ); break; 
      case POS_LOW    :
      case POS_VLOW   : re.addImpactLevel( IROMRiskEntity.eROMImpact.POS_LOW );    break;      
    }
  }
  
  private void removeEvalResultFromMatrix(UUID rID)
  {
      roMatrix.removeEntity(rID);
  }
  
  private ImpactLevel findMostNegativeImpact( Risk risk )
    {
        // Start optimistically and work down
        ImpactLevel targetImpact = ImpactLevel.POS_VHIGH;//????


            // Find most negative impact and add it
            Map<Objective, ImpactLevel> impMap = (risk.getImpact()==null ? null : risk.getImpact().getImpactMap());
            
            if (impMap != null) {
                for (ImpactLevel level : impMap.values()) {
                    if (level.isLessThan(targetImpact)) {
                        targetImpact = level;
                    }
                }
            }


        return targetImpact;
    }

    private double findHighestProbability(List<ResultItem> results) {
        double highestProbability = 0.0;
        
        for (ResultItem resultItem : results) {
            double prob = resultItem.getProbability();
            if (prob > highestProbability) {
                highestProbability = prob;
            }
        }
        
        return highestProbability;
    }

    public static List<ResultItem> getDummyResults() {
        List<ResultItem> items = new LinkedList<ResultItem>();
        
        ResultItem itm1 = new ResultItem("user1", 0.1);
        ResultItem itm2 = new ResultItem("user2", 0.8);
        ResultItem itm3 = new ResultItem("user3", 0.5);

        items.add(itm1);
        items.add(itm2);
        items.add(itm3);

        return items;
    }

  // Private class for sortable RiskIDs using probability
  private class SortableRiskID implements Comparator
  {
    private UUID   riskID;
    private Double probability;
    
    SortableRiskID()
    { /* Empty constructor used as comparator */ }
    
    SortableRiskID( UUID id, Double prob )
    {
      riskID = id;
      probability = prob;
    }
    
    public UUID getID() { return riskID; }
    
    public Double getProbability() { return probability; }
    
    // Comparator --------------------------------------------------------------
    @Override
    public int compare( Object lhs, Object rhs )
    {
      SortableRiskID srLHS = (SortableRiskID) lhs;
      SortableRiskID srRHS = (SortableRiskID) rhs;
      
      int eval = 1;
      
      if ( srLHS.getProbability() < srRHS.getProbability() ) eval = -1;
      else if ( srLHS.getProbability() == srRHS.getProbability() ) eval = 0;
      
      return eval;
    }
  }
  
}