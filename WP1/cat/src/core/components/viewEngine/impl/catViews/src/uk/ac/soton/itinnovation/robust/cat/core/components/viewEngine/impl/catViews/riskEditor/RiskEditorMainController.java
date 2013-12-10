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
//      Created Date :          19 Sep 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor;


import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.riskEditor.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.spec.*;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents.*;

import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.riskmodel.*;

import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.*;

import java.io.Serializable;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;




public class RiskEditorMainController extends    UFAbstractEventManager
                                      implements Serializable,
                                                 IUFController,
                                                 IRiskEditorController,
                                                 RiskEditorMainViewListener,
                                                 RiskEditCommitListener
{
  private RiskEditorMainView   riskEditorMainView;
 
  private transient Map<UUID, Risk>                            currCommunityROs;
  private transient HashMap<UUID, Objective>                   currCommunityObjectives;
  private transient HashMap<UUID, PredictorServiceDescription> currAvailablePSDs;
  private transient HashMap<UUID, PredictorServiceDescription> currEventPSDMappings;
  private transient Risk                                       currRO;
  private transient ITreatmentRepository                       treatmentRepository;
  
  private boolean currRODataHasChanged;
  
  public RiskEditorMainController()
  {
    riskEditorMainView = new RiskEditorMainView( this );
    riskEditorMainView.addListener( this );
    
    currCommunityObjectives = new HashMap<UUID, Objective>();
    currAvailablePSDs = new HashMap<UUID, PredictorServiceDescription>();
    currEventPSDMappings = new HashMap<UUID, PredictorServiceDescription>(); 
  }
  
  public Map<UUID, String> getCommunityRONames()
  {
    HashMap<UUID, String> idNames = new HashMap<UUID,String>();
    
    if ( currCommunityROs != null )
    {
      for ( Risk r : currCommunityROs.values() )
        idNames.put( r.getId(), r.getTitle() );
    }
    
    return idNames;
  }
  
  @Override
  public void selectRiskByID( UUID targetID )
  {
    if ( currCommunityROs != null )
    {
      Risk target = currCommunityROs.get( targetID );
      if ( target != null )
      {
        currRO = target;
        setCurrRODataHasChanged(false);
        //riskEditorMainView.onROSelected(targetID);
        //refreshEditorUI(); doesn't seem to help...
      }
      else {
          resetEditorUI();
      }
    }
  }
  
  public boolean currentROSelected()
  {
    return ( currRO != null );
  }
  
  public String getCurrentROTitle()
  {    
    if ( currRO != null )
    {
      String title = currRO.getTitle();
      if ( title != null ) return title;
    }
    
    return "Unknown title";
  }
  
  public String getCurrentROOwnerInfo()
  {
    // TO DO: selectable from group later
    if ( currRO != null )
    {
      String owner = currRO.getOwner();
      if ( owner != null ) return owner;
    }
    
    return "Unknown owner";
  }
  
  public boolean getCurrentROType()
  {
    if ( currRO != null )
    {
      Boolean type = currRO.getType();
      if ( type != null ) return type;
    }
    
    return true;
  }
  
  public Scope getCurrentROScope()
  {
    if ( currRO != null )
      return currRO.getScope();
    
    return null;
  }
  
  public RiskState getCurrentROState()
  {
    if ( currRO != null )
      return currRO.getState();
     
    return null;
  }
  
  public boolean getCurrentROIsNotifyingUser()
  {
    if ( currRO != null )
      return currRO.isNotification();
    
    return false;
  }
  
  public String getCurrentROCATReviewSummaryInfo()
  {
    if ( currRO != null )
    {
      int      freq = currRO.getCat_review_freq();
      Period period = currRO.getCat_review_period();
      
      String info = generateFreqPeriodText( freq, period );
      return info;
    }
    
    return "No schedule set";
  }
  
  public String getCurrentROHumanReviewSummaryInfo()
  {
    if ( currRO != null )
    {
      int      freq = currRO.getAdmin_review_freq();
      Period period = currRO.getAdmin_review_period();
      
      String info = generateFreqPeriodText( freq, period );
      return info;
    }
    
    return "No scheudle set";
  }
  
  public Entry<Integer, Period> getCurrentROCATReview()
  {
    if ( currRO != null )
    {
      Integer freq = new Integer( currRO.getCat_review_freq() );
      Period  per  = currRO.getCat_review_period();
      
      return new SimpleEntry<Integer, Period>( freq, per );
    }
    
    return null;
  }
  
  public Entry<Integer, Period> getCurrentROHumanReview()
  {
    if ( currRO != null )
    {
      Integer freq = new Integer( currRO.getAdmin_review_freq() );
      Period  per  = currRO.getAdmin_review_period();
      
      return new SimpleEntry<Integer, Period>( freq, per );
    }
    
    return null;
  }
  
  public Date getCurrentROExpiryDate()
  {
    if ( currRO != null )
    {
      Date date = currRO.getExpiryDate();
      if ( date != null ) return date;
    }
    
    return new Date();
  }
  
  public Event getEventFromCurrentRO( UUID id )
  {
    Event event = null;
    
    if ( id != null )
    {
      Iterator<Event> evIt = currRO.getSetEvent().iterator();
      while ( evIt.hasNext() )
      {
        Event e = evIt.next();
        if ( e.getUuid().equals(id) )
        {
          event = e;
          break;
        }
      }
    }
    
    return event;
  }
  
  public Event createCopyOfEventFromCurrentRO( UUID id )
  {
    Event eventCopy   = null;
    Event targetEvent = getEventFromCurrentRO( id );
    
    if ( targetEvent != null )
    {
      eventCopy = new Event( targetEvent );
      eventCopy.setUuid( UUID.randomUUID() );
    }
    
    return eventCopy;
  }
  
  public Collection<Event> getAllEventsFromCurrentRO()
  {
    if ( currRO != null ) return currRO.getSetEvent(); 
    
    return null;   
  }
  
  public Map<UUID, Objective> getAllCommunityObjectives()
  { return currCommunityObjectives; }
  
  public Objective getObjective( UUID objID )
  { return currCommunityObjectives.get(objID); }
  
  public Map<UUID, ImpactViewCD.eImpact> getCurrentROImpacts()
  {
    HashMap<UUID, ImpactViewCD.eImpact> currImpacts = new HashMap<UUID, ImpactViewCD.eImpact>();
    
    if ( currRO != null )
    {
      Impact impactGroup = currRO.getImpact();
      if ( impactGroup != null )
      {
        Map<Objective, ImpactLevel> im = impactGroup.getImpactMap();
        if ( im != null )
        {
          for ( Objective obj : im.keySet() )
          {
            ImpactLevel il = im.get( obj );
            ImpactViewCD.eImpact impact = ImpactViewCD.eImpact.UNDEFINED;
            
            // TO DO: Refactor ImpactViewCD to use model data now polarization fixed
            switch ( il )
            {
              case NEG_VHIGH  : impact = ImpactViewCD.eImpact.NEG_VHIGH;  break;
              case NEG_HIGH   : impact = ImpactViewCD.eImpact.NEG_HIGH;   break;
              case NEG_MEDIUM : impact = ImpactViewCD.eImpact.NEG_MEDIUM; break;
              case NEG_LOW    : impact = ImpactViewCD.eImpact.NEG_LOW;    break;
              case NEG_VLOW   : impact = ImpactViewCD.eImpact.NEG_VLOW;   break;
              case POS_VLOW   : impact = ImpactViewCD.eImpact.POS_VLOW;   break;
              case POS_LOW    : impact = ImpactViewCD.eImpact.POS_LOW;    break;
              case POS_MEDIUM : impact = ImpactViewCD.eImpact.POS_MEDIUM; break;
              case POS_HIGH   : impact = ImpactViewCD.eImpact.POS_HIGH;   break;
              case POS_VHIGH  : impact = ImpactViewCD.eImpact.POS_VHIGH;  break;
            }
            
            currImpacts.put( obj.getIdAsUUID(), impact );
          }
        }
      }
    }
    
    return currImpacts;
  }
  
  public Map<UUID,TreatmentTemplate> getRepositoryTreatmentTemplates()
  {
    HashMap<UUID, TreatmentTemplate> treatments = new HashMap<UUID,TreatmentTemplate>();
    
    if ( treatmentRepository != null )
    {
      Iterator<TreatmentTemplate> ttIt = treatmentRepository.getCopyofTreatmentTemplates().iterator();
      while ( ttIt.hasNext() )
      {
        TreatmentTemplate tt = ttIt.next();
        treatments.put( tt.getID(), tt );
      }
    }
    
    return treatments;
  }
  
  public IProcessRender getTreatmentTemplateRender( TreatmentTemplate tt )
  {
    IProcessRender render = null;
    
    if ( treatmentRepository != null )
      try { render = treatmentRepository.createTemplateRender( tt.getID() ); }
      catch ( Exception e ) {}
    
    return render;
  }
  
  public List<Treatment> getCurrentROTreatments()
  {
    ArrayList<Treatment> treatmentList = new ArrayList<Treatment>();
    
    if ( currRO != null && treatmentRepository != null )
    {
      ROTreatmentGroup rotg = treatmentRepository.getTreatmentsForRO( currRO.getTitle(),
                                                                      currRO.getId(),
                                                                      currRO.getTreatProcIDS() );
      // Get treatments for this risk
      if ( rotg != null )
        treatmentList.addAll( rotg.getOrderedTreatments() );
    }
    
    return treatmentList;
  }
  
  public Map<UUID, String> getPredictorSummaryInfo()
  {
    HashMap<UUID, String> info = new HashMap<UUID, String>();
    
    for ( PredictorServiceDescription psd : currAvailablePSDs.values() )
      info.put( psd.getUuid(), psd.getName() );
    
    return info;
  }
  
  public PredictorServiceDescription getPSDByID( UUID predID )
  { return currAvailablePSDs.get( predID ); }
  
  public PredictorServiceDescription getPSDByEvent( Event event )
  { 
    // Try an early retrieval first, but if we can't find, go ask the data layer
    PredictorServiceDescription targetPSD = currEventPSDMappings.get( event.getUuid() );
    
    // No good. Try the data layer
    if ( targetPSD == null )
    {
      List<IRiskEditorListener> listeners = getListenersByType();
      if ( !listeners.isEmpty() )
        listeners.get(0).onGetPSD( event );
      
      // Try again
      targetPSD = currEventPSDMappings.get( event.getUuid() );
    }

    return targetPSD;
  }
  
  // IUFController -------------------------------------------------------------
  @Override
  public IUFModelRO getModel( UUID id ) throws UFException
  {
    // TO DO: need to return specific risk model
    return null;
  }
  
  @Override
  public IUFView getView( IUFModelRO model ) throws UFException
  {
    return riskEditorMainView;
  }
  
  @Override
  public IUFView getView( String ID )
  {
    return riskEditorMainView;
  }
  
  // IRiskEditorController -----------------------------------------------------  
  @Override
  public void resetEditorUI()
  {
    // TO DO: check for unsaved data etc
    
    currRO = null;
    riskEditorMainView.resetView();
  }

  @Override
  public void refreshEditorUI() {
      riskEditorMainView.updateView();
  }
  
  @Override
  public Map<UUID, Risk> getCopyOfCurrentRisks()
  { return new HashMap<UUID, Risk>( currCommunityROs ); }
  
  @Override
  public void setCurrentRisks( Map<UUID, Risk> risks, Risk selectedRisk )
  {
    currRO = selectedRisk;
    
    if ( risks != null )
    {
      currCommunityROs = risks;
      riskEditorMainView.setCurrRO(currRO);
      riskEditorMainView.updateView();
    }   
  }
  
  @Override
  public void setCurrentObjectives( Set<Objective> objectives )
  {
    currCommunityObjectives.clear();
    
    if ( objectives != null )
    {
      Iterator<Objective> objIt = objectives.iterator();
      while ( objIt.hasNext() )
      {
        Objective obj = objIt.next();
        currCommunityObjectives.put( obj.getIdAsUUID(), obj );
      }
    }
  }
  
  @Override
  public void setCurrentPSDs( Set<PredictorServiceDescription> psds )
  {
    currAvailablePSDs.clear();
    
    if ( psds != null )
    {
      Iterator<PredictorServiceDescription> psdIt = psds.iterator();
      while ( psdIt.hasNext() )
      {
        PredictorServiceDescription predictor = psdIt.next();
        currAvailablePSDs.put( predictor.getUuid(), predictor );
      }
    }
  }
  
  @Override
  public void setTreatmentRepository( ITreatmentRepository rep )
  { treatmentRepository = rep; }
  
  @Override
  public void addPSDEventMapping( UUID eventID, PredictorServiceDescription psd )
  {
    if ( eventID != null && psd != null )
    {
      // Check that the PSD actually exists in our known collection
      if ( currAvailablePSDs.containsKey( psd.getUuid() ) )
      {
        currEventPSDMappings.remove( eventID ); // Remove old event mapping first
        currEventPSDMappings.put ( eventID, psd );
      }
    }
  }

  // RiskEditorMainViewListener ------------------------------------------------
  @Override
  public void onDeleteCurrentRisk()
  {
    if ( currRO != null )
    {
      List<IRiskEditorListener> listeners = getListenersByType();
    
      for ( IRiskEditorListener listener : listeners )
        listener.onDeleteRisk( currRO );
      
      resetEditorUI();
    }
  }
  
  // RiskEditCommitListener ----------------------------------------------------
  @Override
  public void onDataChanged() {
      setCurrRODataHasChanged(true);
  }
  
  @Override
  public void onCommitCreateViewChanges( CreateViewCD changeData )
  {
    List<IRiskEditorListener> listeners = getListenersByType();
    for ( IRiskEditorListener listener : listeners )
      listener.onCreateNewRO( changeData.getTitle(),
                              changeData.getType(),
                              changeData.getOwner(),
                              changeData.getGroup() );
  }
  
  @Override
  public void onCommitBrowseViewChanges( BrowseViewCD changeData )
  {
    if ( changeData!=null && changeData.isDataChanged() )
    {
      currRO.setTitle( changeData.getROTitle() );
      currRO.setType( changeData.getROType() );
      currRO.setGroup( changeData.getROGroup() );
      currRO.setOwner( changeData.getROOwner() );
      currRO.setScope( changeData.getROScope() );
      currRO.setExpiryDate( changeData.getROExpiration() );
      
      setCurrRODataHasChanged(true);
      //riskEditorMainView.updateView();
      //riskEditorMainView.onROSelected(currRO.getId());
    }
  }
  
  @Override
  public void onCommitEventPredictorChanges( PredictorViewCD changeData )
  {   
    if ( changeData!=null && changeData.isDataChanged() )
    {
      // Create current event set
      HashMap<UUID, Event> currEvents = new HashMap<UUID, Event>();
      Iterator<Event>      eventSetIt = currRO.getSetEvent().iterator();
      
      while ( eventSetIt.hasNext() )
      {
        Event ev = eventSetIt.next();
        currEvents.put( ev.getUuid(), ev );
      }
      
      // Remove any events that shouldn't be there any more
      Iterator<UUID> delUUIDIt = changeData.getEventsToRemove().iterator();
      while ( delUUIDIt.hasNext() )
      { currEvents.remove( delUUIDIt.next() ); }
      
      // Now either modifying an existing event or add a new one
      Iterator<Event> chEvIt = changeData.getEventsToUpdate().iterator();
      while ( chEvIt.hasNext() )
      {
        Event updatedEvent = chEvIt.next();
        UUID upID = updatedEvent.getUuid();
        
        // Remove the old event instance, if it exists
        if ( currEvents.containsKey(upID) ) currEvents.remove( upID );
        
        // Add in the updated/new event
        currEvents.put( upID, updatedEvent );
      }
      
      // Update the risk
      HashSet<Event> updatedEvents = new HashSet<Event>();
      updatedEvents.addAll( currEvents.values() );
      currRO.setSetEvent( updatedEvents );
      
      
      if ( updatedEvents.isEmpty() )
      {
        currRO.setState( RiskState.INACTIVE );
        currRO.setNotification( false );
        
        riskEditorMainView.displayWarning( "Risk " + currRO.getTitle() + "was set inactive",
                                           "No events are currently defined for this risk" );
      }
      else // Otherwise, just set as appropriate
      {
        if ( changeData.isPredictionActive() )
          currRO.setState( RiskState.ACTIVE );
        else
          currRO.setState( RiskState.INACTIVE );
        
        currRO.setNotification( changeData.isPredictionActive() );
      }
      
      setCurrRODataHasChanged(true);
      riskEditorMainView.updateView();
      //riskEditorMainView.onROSelected(currRO.getId());
    }
  }
  
  @Override
  public void onCommitImpactChanges( ImpactViewCD changeData )
  {
    if ( changeData!=null && changeData.isDataChanged() )
    {
      Impact riskImpactGroup = currRO.getImpact();
      
//      if ( riskImpactGroup != null )//bmn this is preventing from filling in an empty risk!!
//      {
        HashMap<Objective, ImpactLevel> newIM = new HashMap<Objective, ImpactLevel>();
        
        Map<UUID, ImpactViewCD.eImpact> impacts = changeData.getChanges();
        for ( UUID objID : impacts.keySet() )
        {
          Objective obj = currCommunityObjectives.get( objID );
          if ( obj != null )
          {
            ImpactViewCD.eImpact impact = impacts.get( objID );
            ImpactLevel dataIL = null;
            
            // TO DO: Use direct mapping - refactor ImpactViewCD to use model
            switch ( impact )
            {
              case NEG_VHIGH  : dataIL = ImpactLevel.NEG_VHIGH;  break;
              case NEG_HIGH   : dataIL = ImpactLevel.NEG_HIGH;   break;
              case NEG_MEDIUM : dataIL = ImpactLevel.NEG_MEDIUM; break;
              case NEG_LOW    : dataIL = ImpactLevel.NEG_LOW;    break;
              case NEG_VLOW   : dataIL = ImpactLevel.NEG_VLOW;   break;
              case POS_VLOW   : dataIL = ImpactLevel.POS_VLOW;   break;
              case POS_LOW    : dataIL = ImpactLevel.POS_LOW;    break;
              case POS_MEDIUM : dataIL = ImpactLevel.POS_MEDIUM; break;
              case POS_HIGH   : dataIL = ImpactLevel.POS_HIGH;   break;
              case POS_VHIGH  : dataIL = ImpactLevel.POS_VHIGH;  break; 
            }
            
            // Problem here: enumeration is a reference :(
            newIM.put( obj, dataIL );
          }
        }
       
        if(riskImpactGroup!=null)
            riskImpactGroup.setImpactMap( newIM );//bmn
        else 
            currRO.setImpact(new Impact(newIM));
        
        
        setCurrRODataHasChanged(true);
        riskEditorMainView.updateView();
        //riskEditorMainView.onROSelected(currRO.getId());
//      }bmn
    }
  }
  
  @Override
  public void onCommitScheduleChanges( ScheduleViewCD changeData )
  {
    if ( changeData!=null && changeData.isDataChanged() )
    {
      currRO.setCat_review_freq( changeData.getCatReviewFrequency() );
      currRO.setCat_review_period( changeData.getCatReviewPeriod() );
      currRO.setAdmin_review_freq( changeData.getHumanReviewFrequency() );
      currRO.setAdmin_review_period( changeData.getHumanReviewPeriod() );
      currRO.setNotification( changeData.getNotifyUser() );
      
      setCurrRODataHasChanged(true);
      riskEditorMainView.updateView();
      //riskEditorMainView.onROSelected(currRO.getId());
    }
  }
  
  @Override
  public void onCommitTreatmentChanges( TreatmentViewCD changeData )
  {
    if ( changeData!=null && changeData.isDataChanged() )
    {
      // Clear old treatment data
      TreatmentWFs wfs = currRO.getTreatment();      
      wfs.clearAllTemplates();
      
      treatmentRepository.destroyAllTreatmentsForRisk( currRO.getId() );
      
      // Re-write templates for this risk
      HashMap<Float, TreatmentTemplate> ttChanges = changeData.getTemplates();
      Iterator<Float> indIt = ttChanges.keySet().iterator();
      while ( indIt.hasNext() )
      {
        Float index = indIt.next();
        TreatmentTemplate tt = ttChanges.get( index );
        
        try
        {
          treatmentRepository.createTreatment( tt.getID(), 
                                               currRO.getId(), 
                                               index );
        }
        catch ( Exception e ) {}
        
        wfs.addTreatmentTemplate( tt, index );
      }
      
      // Signal update
      currRO.setTreatment( wfs );
      setCurrRODataHasChanged(true);
      
      riskEditorMainView.updateView();
      //riskEditorMainView.onROSelected(currRO.getId());
    }
  }
  
  @Override
  public void onCommitAllChanges()
  {
    // Make a full commitment to any changes here
    if ( currRODataHasChanged ) {
      notifyOfRiskUpdate();
      setCurrRODataHasChanged(false); // resets status and disable Save button, etc
    }
    else
      // Notify user that no data has changed
      riskEditorMainView.displayMessage( "Did not save", "No changes to save" );
  }
  
  @Override
  public void onDiscardAllChanges()
  {
    // Just get a refreshed view of all R/Os..
    List<IRiskEditorListener> listeners = getListenersByType();
    
    for ( IRiskEditorListener listener : listeners )
      listener.onRiskEditorWantsFreshRiskData();
    
    setCurrRODataHasChanged(false);
    riskEditorMainView.updateView();
    
    // .. and then re-select the current risk
    if ( currRO != null )
      selectRiskByID( currRO.getId() );
    
  }
  
  // Private methods -----------------------------------------------------------  
  private String generateFreqPeriodText( int freq, Period per )
  {
    String freqText = "review not set";
    
    if ( freq > 0 )
    {
      freqText = "every ";
      if ( freq > 1 ) freqText += new Integer(freq).toString() + " ";
      
      freqText += per.toString();
      
      if ( freq > 1 ) freqText += "s";
    }
    
    return freqText;
  }
  
  private void setCurrRODataHasChanged(boolean value) {
      currRODataHasChanged = value;
      riskEditorMainView.getBrowseView().onRODataHasChanged(value);
  }
  
  private void notifyOfRiskUpdate()
  {
    List<IRiskEditorListener> listeners = getListenersByType();
    
    for ( IRiskEditorListener listener : listeners )
      listener.onUpdateRisk( currRO );
  }
}