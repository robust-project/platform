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
//      Created Date :          18-Jul-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.treatments;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.treatment.ITreatmentCentreController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.*;

import uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.spec.*;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.common.CATAppViewController;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.treatments.subViews.*;

import uk.ac.soton.itinnovation.robust.riskmodel.*;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.*;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;




public class TreatmentCentreController extends UFAbstractEventManager
                                       implements Serializable,
                                                  IUFController,
                                                  ITreatmentCentreController,
                                                  ITreatmentProcessListener,
                                                  TreatmentMainViewListener,
                                                  TreatmentMonitorViewListener
                                                  
{
  private CATAppViewController appViewCtrl;
  private TreatmentMainView    treatmentMainView;
  
  private transient ITreatmentRepository          treatmentRepo;
  private transient HashMap<UUID, IProcessRender> templateRenderCache;
  
  private transient HashMap<UUID, ROTreatmentGroup> currentTreatmentGroups;
  private transient ROTreatmentGroup                currentROGroup;
  
  
  public TreatmentCentreController( CATAppViewController avc )
  {
    appViewCtrl = avc;
    
    treatmentMainView = new TreatmentMainView();
    treatmentMainView.addListener( this );
    treatmentMainView.getMonitorView().addListener( this );
    
    templateRenderCache    = new HashMap<UUID, IProcessRender>();
    currentTreatmentGroups = new HashMap<UUID, ROTreatmentGroup>();
  }
  
  // IUFController -------------------------------------------------------------
  @Override
  public IUFModelRO getModel( UUID id ) throws UFException
  { return null; }
  
  @Override
  public IUFView getView( IUFModelRO model ) throws UFException
  { return treatmentMainView; /*Return the main view only*/ }
  
  @Override
  public IUFView getView( String ID )
  { return treatmentMainView; /* Return the main view only */ }
  
  // ITreatmentController ------------------------------------------------------
  @Override
  public ITreatmentRepository getRepository()
  { return treatmentRepo; }
  
  @Override
  public void setRepository( ITreatmentRepository repo )
  { treatmentRepo = repo; }
  
  /*
  @Override
  public boolean handleTreatmentOfRisk( Risk risk )
  {
    String failMessage = null;
    
    if ( treatmentRepo == null ) failMessage = "Treatment repository unavailable";
    if ( risk == null )          failMessage = "Risk is NULL";
    
    UUID riskID = risk.getId();
    
    // Remove the old group if it already exists
    if ( currentTreatmentGroups.containsKey(riskID) )
      currentTreatmentGroups.remove( riskID );
    else
      // Otherwise, create treatments for the new risk
      if ( !createTreatmentsForNewRisk(risk) )
        failMessage = "Could not create treatments for risk " + risk.getTitle();
    
    // Get updated treatments from risk repository first
    ROTreatmentGroup newGroup = treatmentRepo.getTreatmentsForRO( risk.getTitle(), 
                                                                  riskID,
                                                                  risk.getTreatProcIDS() );
    currentTreatmentGroups.put( riskID, newGroup );

    // Update the view
    treatmentMainView.addTreatmentGroup( newGroup );
   
    // Notify the user of problems
    if ( failMessage != null )
    {
      appViewCtrl.getCurrentView().displayWarning( "Treatment Centre problem", 
                                                   failMessage );
      return false;
    }
    
    return true;
  }
  */
  
    @Override
    public boolean handleTreatmentOfRisk(Risk risk) {
        try {
            if (treatmentRepo == null) {
                throw new Exception("Treatment repository unavailable");
            }
            
            if (risk == null) {
                throw new Exception("Risk is NULL");
            }

            UUID riskID = risk.getId();

            // Remove the old group if it already exists
            if (currentTreatmentGroups.containsKey(riskID)) {
                currentTreatmentGroups.remove(riskID);
            } else // Otherwise, create treatments for the new risk
            if (!createTreatmentsForNewRisk(risk)) {
                throw new Exception("Could not create treatments for risk " + risk.getTitle());
            }

            // Get updated treatments from risk repository first
            ROTreatmentGroup newGroup = treatmentRepo.getTreatmentsForRO(risk.getTitle(),
                    riskID,
                    risk.getTreatProcIDS());
            currentTreatmentGroups.put(riskID, newGroup);

            // Update the view
            treatmentMainView.addTreatmentGroup(newGroup);

            return true;
        } catch (Exception e) {
            // Notify the user of problems
            appViewCtrl.getCurrentView().displayWarning("Treatment Centre problem", e.getMessage());
            return false;
        }
    }

  @Override
  public void handleRiskRemoval( Risk risk )
  {
    if ( risk != null )
    {
      ROTreatmentGroup tmtGroup = treatmentRepo.getTreatmentsForRO( risk.getTitle(), 
                                                                    risk.getId(),
                                                                    risk.getTreatProcIDS() );
      if ( tmtGroup != null )
      {
        treatmentMainView.removeTreatmentGroup( tmtGroup );
        currentTreatmentGroups.remove( tmtGroup.getROID() );
        
        if ( currentROGroup == tmtGroup ) currentROGroup = null;
        
        treatmentRepo.destroyAllTreatmentsForRisk( risk.getId() );
      }
    }
  }
  
  // ITreatmentProcessListener -------------------------------------------------
  @Override
  public void onProcessStarted( Treatment tmt )
  {
    //TODO: Nothing here right now
  }
  
  @Override
  public void onProcessCompleted( Treatment tmt )
  {
    // Try to update 'live' treatments just finished (will not update if treatment
    // does not relate to currently monitored risk in monitor view)
    treatmentMainView.
            getMonitorView().addLiveHistoricTreatment( tmt );
  }
  
  @Override
  public void onTaskCreated( TreatmentTask task )
  {
    TreatmentMonitorView tmv = treatmentMainView.getMonitorView();
    
    IProcessRender pr = getLiveTreatmentRender( task.getTreatment() );
    if ( pr != null ) tmv.updateBPMNRender( pr );
    
    tmv.updateTreatmentTask( task );
  }
  
  @Override
  public void onTaskCompleted( TreatmentTask task )
  {
    // Don't re-render the treatment view here
    treatmentMainView.getMonitorView().updateTreatmentTask( task );
  }
  
  // TreatmentMainViewListener -------------------------------------------------
  
  // TreatmentMonitorViewListener ----------------------------------------------
  @Override
  public void onROTreatmentGroupSelected( UUID groupID )
  {
    if ( groupID != null )
    {
      ROTreatmentGroup target = currentTreatmentGroups.get( groupID );
      if ( target != null )
      {
        currentROGroup = target;
        treatmentMainView.setCurrentTreatmentGroup( currentROGroup );
      }
    }
  }
  
  @Override
  public void onMonitorStartTreatment( Treatment tmt )
  {
    if ( tmt != null && treatmentRepo != null )
    {
      try
      {
        treatmentRepo.startTreatmentProcess( tmt );
        IProcessRender pr = getLiveTreatmentRender( tmt );
        
        TreatmentMonitorView tmv = treatmentMainView.getMonitorView();
        if ( pr != null ) tmv.updateBPMNRender( pr );
        
        tmv.updateTreatmentStatus( tmt, null );
      }
      catch (Exception e)
      {
        treatmentMainView.getMonitorView().
                updateTreatmentStatus( tmt, "Could not start treatment" );
      }
    }
  }
  
  @Override
  public void onMonitorStopTreatment( Treatment tmt )
  {
    if ( tmt != null && treatmentRepo != null )
    {
      try
      { treatmentRepo.forceStopTreatmentProcess( tmt ); }
      catch ( Exception e )
      {
        treatmentMainView.getMonitorView().
                updateTreatmentStatus( tmt, "Could not stop treatment" );
      }
    }
  }
  
  @Override
  public void onMonitorRequireRenderForTreatment( Treatment tmt )
  {
    IProcessRender render = null;
    
    try
    {
      if ( tmt.isTreatmentRunning() )
        render = treatmentRepo.createTreatmentRender( tmt.getID() );
      else
        render = treatmentRepo.createTemplateRender( tmt.getActivitiProcResourceID() );
    }
    catch (Exception e) {}
    
    if ( render != null )
      treatmentMainView.getMonitorView().updateBPMNRender( render );
  }
  
  @Override
  public void onTreatmentTaskAction( TreatmentTask task, boolean completed )
  {
    // For now, don't worry about skip/completed status
    if ( task != null )
      treatmentRepo.notifyTreatmentTaskCompleted( task );
  }
  
  private boolean createTreatmentsForNewRisk( Risk risk )
  {
    boolean success = true;
    
    TreatmentWFs workflows = risk.getTreatment();
    if ( workflows != null )
    {
      List<Entry<Float,TreatmentTemplate>> treatments =
              workflows.getOrderedCopyOfTreatmentTemplates();

      Iterator<Entry<Float,TreatmentTemplate>> tmtIt = treatments.iterator();
      while ( tmtIt.hasNext() && success )
      {
        Entry<Float,TreatmentTemplate> entry = tmtIt.next();
        Float order                          = entry.getKey();
        TreatmentTemplate tt                 = entry.getValue();

        // Try create the treatment from the template
        try
        { treatmentRepo.createTreatment( tt.getID(), risk.getId(), order ); }
        catch ( Exception e )
        {
          appViewCtrl.getCurrentView().displayWarning( "Could not create treatment: ",
                                                       e.getMessage() );
          success = false; 
        }
      }
    }
    else success = false;
    
    return success;
  }
  
  private IProcessRender getLiveTreatmentRender( Treatment tmt )
  {
    IProcessRender pr = null;
    
    try { pr = treatmentRepo.createTreatmentRender( tmt.getID() ); }
    catch (Exception e ) {}
        
    return pr;
  }
}
