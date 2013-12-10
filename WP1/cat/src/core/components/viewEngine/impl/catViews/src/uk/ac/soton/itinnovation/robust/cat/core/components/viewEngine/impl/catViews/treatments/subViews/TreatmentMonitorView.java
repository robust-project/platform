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
//      Created Date :          20-Jul-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.treatments.subViews;

//import com.github.wolfie.refresher.Refresher;
//import com.github.wolfie.refresher.Refresher.RefreshListener;
import uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.spec.IProcessRender;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.*;

import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.*;

import com.vaadin.ui.*;
import com.vaadin.data.Property;

import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;




public class TreatmentMonitorView extends SimpleView
{
  private ListSelect activeROList;
  private ListSelect activeTreatmentsList;
  private Button     treatmentStartButton;
  private TextArea   planSummaryField;
  
  private Table treatmentHistoryTable;
  
  private ListSelect        tasksWaitingList;
  private TabSheet sheet;
  private Panel bpmnView;
  private StreamedImageView bpmnImage;
  
  private Label    taskWaitingTitle;
  private TextArea taskWaitingInfoField;
  private Button   skipTaskButton;
  private Button   completedTaskButton;

  private final String tableTmtNameID  = "Treatment name";
  private final String tableTmtStartID = "Date started";
  private final String tableTmtEndID   = "Date stopped";
  
  private transient HashMap<UUID, UUIDItem> currentROGroupsByID;
  private transient ROTreatmentGroup        currentROGroup;
  private transient Treatment               currentTreatment;
  
  private transient UUIDItem      currentTaskItem;
  private transient TreatmentTask currentTask;
  
  public TreatmentMonitorView()
  {
    super();
    
    VerticalLayout vl = (VerticalLayout) viewContents;
    vl.setWidth( "100%" );
    vl.addStyleName( "catBkgnd" );
    
    createComponents();
    resetComponents();
  }
  
    public Treatment getCurrentTreatment() {
        return currentTreatment;
    }

    public TreatmentTask getCurrentTask() {
        return currentTask;
    }
  
  public void addTreatmentGroup( ROTreatmentGroup group )
  {
    UUID groupID = group.getROID();
    
    if ( !currentROGroupsByID.containsKey(groupID) )
    {
      UUIDItem newROItem = new UUIDItem( group.getROTitle(), group.getROID());
      currentROGroupsByID.put( groupID, newROItem );
      
      activeROList.addItem( newROItem );
    }
    
    /*
    // If this is the only entry, make it the current one
    if ( currentROGroupsByID.size() == 1 )
      setCurrentTreatmentGroup( group );
    */
  }
  
  public void removeTreatmentGroup( ROTreatmentGroup group )
  {
    UUIDItem itemToGo = currentROGroupsByID.get( group.getROID() );
    if ( itemToGo != null )
      activeROList.removeItem( itemToGo );
    
    if ( currentROGroup == group )
    {
      currentROGroup   = null;
      currentTreatment = null;
      currentTask      = null;
      currentTaskItem  = null;
    }
    
    currentROGroupsByID.remove( group.getROID() );
    
    updateView();
  }
  
  public void setCurrentTreatmentGroup( ROTreatmentGroup group )
  {    
    if ( currentROGroupsByID.containsKey(group.getROID()) )
    {
      currentROGroup   = group;
      currentTreatment = null;
      
      updateView();
    }
  }
  
  public void updateTreatmentTask( TreatmentTask task )
  {
      if ((currentTreatment != null) && currentTreatment.getID().equals(task.getTreatment().getID())) {
          Set<String> taskIDs = currentTreatment.getCurrentTaskIDs();
          
          
          if (taskIDs.size() > 0) {
              // if we have at least 1 task in the list at this point, we must refresh
              // (e.g. for inclusive gateway, where several tasks need to be completed)
              updateCurrentTreatmentTaskList();
          }
          else {
              // KEM - no need to update tasks list at this point, as completed tasks will be removed
              // when next task is created. This gives a better look&feel through the workflow
          }
      }
  }
  
  public void updateTreatment( Treatment tmt )
  {
    if ( (currentTreatment != null) && currentTreatment.getID().equals( tmt.getID()) )
      updateCurrentTreatmentTaskList();
  }
  
  @Override
  public void updateView()
  {
    updateCurrentROGroupInfo();
    updateCurrentROTreatmentInfo();
    updateTreatmentHistoryTable();
    updateCurrentTreatmentTaskList();
  }
  
  public void updateBPMNRender( IProcessRender render )
  {
    if ( render != null ) {
      float width = sheet.getWidth();
      float height = sheet.getHeight();
      int centreX = render.getCentreX();
      int centreY = render.getCentreY();
      int scrollLeft = (int) (centreX - width/2);
      int scrollTop = (int) (centreY - height/2);
      
      if (scrollLeft > 0) {
          bpmnView.setScrollLeft(scrollLeft);
      }
      
      if (scrollTop > 0) {
          bpmnView.setScrollTop(scrollTop);
      }
      
      bpmnImage.updateImage( (InputStream) render.getImpl(),
                             "png" );
    }
  }
  
  public void updateTreatmentStatus( Treatment tmt, String statusMessage )
  {    
    // Update the display if it's the current treatment
    if ( (currentTreatment != null) && currentTreatment.getID().equals( tmt.getID()) )
    {
      /*
      if ( currentTreatment.isTreatmentRunning() )
        treatmentStartButton.setCaption( "Stop treatment" );
      else
        treatmentStartButton.setCaption( "Start treatment" );
      */
      updateTreatmentStartButton();
    }
    
    if ( statusMessage != null ) displayMessage( "Treatment message:", statusMessage );
  }
  
  public void addLiveHistoricTreatment( Treatment tmt )
  {
    if ( tmt != null && currentROGroup != null )
    {
      // Update the ROGroup with a TreatmentRecord
      UUID riskID = tmt.getLinkedRiskID();
      
      TreatmentRecord tr = new TreatmentRecord( riskID,
                                                tmt.getTitle(),
                                                tmt.getDescription(),
                                                tmt.getActivitiProcResourceID(),
                                                tmt.getCopyofStartDate(),
                                                tmt.getCopyofStopDate(),
                                                "Risk manager stopped this treatment" );
      currentROGroup.addHistoricTreatment( tr );
      
      // Only add recent historic treatments to current risk
      if ( riskID.equals(currentROGroup.getROID()) )
        addHistoricTreatmentToTable( tr );
      
      // Update the UI for this treatment, if appropriate
      if ( (currentTreatment != null) && tmt.getID().equals( currentTreatment.getID()) )
      {
        updateTreatmentStartButton();
        tasksWaitingList.removeAllItems();
        taskWaitingTitle.setValue( "No action currently selected" );
        taskWaitingInfoField.setReadOnly( false );
        taskWaitingInfoField.setValue( "" );
        taskWaitingInfoField.setReadOnly( true );
        bpmnImage.updateImage( ViewResources.CATAPPResInstance.getResource("noWorkflow") );
        displayMessage( "Treatment completed", tmt.getTitle() );
      }
    }
  }
  
    public TreatmentTask getCurrentTreatmentTask() {
        if (currentTask == null) {
            return waitForSelectedTask();
        } else {
            return currentTask;
        }
    }

    private synchronized TreatmentTask waitForSelectedTask() {
        while(currentTask == null) {
            try {
                wait();
            } catch (InterruptedException ex) {
                return null;
            }
        }
        
        return currentTask;
    }

  // Protected methods ---------------------------------------------------------
  
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    HorizontalLayout hl = new HorizontalLayout();
    viewContents.addComponent( hl );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace( "4px", null, true ) );
    
    // Side bar
    Component comp = createMonitorSideBar();
    hl.addComponent( comp );
    hl.setComponentAlignment(comp, Alignment.TOP_RIGHT );
    
    // Spaces
    hl.addComponent( UILayoutUtil.createSpace( "4px", null, true ) );
    comp = (Component) UILayoutUtil.createSpace( "2px", 
                                                 "catVertRule",
                                                 true );
    comp.setHeight( "100%" );
    hl.addComponent( comp );
    
    // Monitor console
    comp = createMonitorConsole();
    hl.addComponent( comp );
    hl.setComponentAlignment( comp, Alignment.TOP_CENTER );
    
    /* KEM - uncomment following code to provide automatic page refreshes every 5 secs.
     * This fixes the problem that the workflow image is not refreshed after selecting its
     * treatment from the Current Treatements list (i.e. one that is already running).
     * It should scroll automatically to the currently active task.
     * This problem is not apparent when running a treatment from the start.
     * 
     * TODO: fix problem that widget set needs to be recompiled, which causes an error:
     * Widgetset does not contain implementation for com.github.wolfie.refresher.Refresher
     */
    /* final Refresher refresher = new Refresher();
    refresher.setRefreshInterval(5000);
    refresher.addListener(new PageRefreshListener());
    viewContents.addComponent(refresher);
    */
  }
  
    public void resetComponents() {
        currentROGroupsByID = new HashMap<UUID, UUIDItem>();
        currentROGroup = null;
        currentTreatment = null;
        currentTask = null;
        currentTaskItem = null;
        activeROList.removeAllItems();
        activeTreatmentsList.removeAllItems();
        tasksWaitingList.removeAllItems();
        planSummaryField.setReadOnly( false );
        planSummaryField.setValue("");
        planSummaryField.setReadOnly( true );
        updateTreatmentStartButton();
        updateTreatmentHistoryTable();
        bpmnImage.updateImage( ViewResources.CATAPPResInstance.getResource("noWorkflow") );
    }


  private Component createMonitorSideBar()
  {
    VerticalLayout vl = new VerticalLayout();
    vl.addStyleName( "catBkgnd" );
    
    // Active plan space & heading ---------------------------------------------
    vl.addComponent( UILayoutUtil.createSpace("6px", null) );
    
    // Current Risk/Opportunities
    Label label = new Label( "Active Risk/Opportunities" );
    label.addStyleName( "catSubSectionFont" );
    vl.addComponent( label );
    
    activeROList = new ListSelect();
    activeROList.setWidth( "188px" );
    activeROList.setHeight( "130px" );
    activeROList.addListener( new ROListSelectListener() );
    vl.addComponent( activeROList );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Plans for each R/O
    label = new Label( "Current treatments:" );
    label.addStyleName( "catSubSectionFont" );
    vl.addComponent( label );
    
    activeTreatmentsList = new ListSelect();
    activeTreatmentsList.setWidth( "188px" );
    activeTreatmentsList.setHeight( "130" );
    activeTreatmentsList.addListener( new ActiveTreatmentListSelectListener() );
    vl.addComponent( activeTreatmentsList );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    treatmentStartButton = new Button( "Start treatment" );
    treatmentStartButton.addStyleName( "big default" );
    treatmentStartButton.setWidth( "188px" );
    treatmentStartButton.setEnabled( false );
    treatmentStartButton.addListener( new StartTreatmentListener() );
    vl.addComponent( treatmentStartButton );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Plan info
    label = new Label( "Plan summary: " );
    label.addStyleName( "catSubSectionFont" );
    vl.addComponent( label );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Info
    planSummaryField = new TextArea();
    planSummaryField.setWidth( "188px" );
    planSummaryField.setHeight( "160px" );
    planSummaryField.setReadOnly( true );
    vl.addComponent( planSummaryField );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("8px", null) );
    
    return vl;
  }
  
  private Component createMonitorConsole()
  {
    VerticalLayout vl = new VerticalLayout();
    
    vl.addComponent( createActionNavView() );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace( "4px", null, true ) );
    
    vl.addComponent( createActionInfoView() );
    
    return vl;
  }
  
  private Component createActionNavView()
  {
    HorizontalLayout mainHL = new HorizontalLayout();
    mainHL.addStyleName( "catBkgnd" );
    
    // Space
    mainHL.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    
    // Awaiting actions
    VerticalLayout vl = new VerticalLayout();
    mainHL.addComponent( vl );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("6px", null) );
    
    Label label = new Label( "Actions waiting" );
    label.addStyleName( "catSubSectionFont" );
    vl.addComponent( label );
    
    // Actions waiting
    tasksWaitingList = new ListSelect();
    tasksWaitingList.addStyleName( "small" );
    tasksWaitingList.setWidth( "200px" );
    tasksWaitingList.setHeight( "300px" );
    tasksWaitingList.setImmediate( true );
    tasksWaitingList.addListener( new TreatmentTaskListListener() );
    vl.addComponent( tasksWaitingList );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("8px", null) );
    
    // Actions
    label = new Label( "Actions:" );
    label.addStyleName( "small" );
    vl.addComponent( label );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Skip button
    skipTaskButton = new Button( "Refresh" );
    skipTaskButton.addStyleName( "small" );
    skipTaskButton.setWidth( "200px" );
    skipTaskButton.setEnabled( false );
    skipTaskButton.addListener( new SkipTaskListener() );
    vl.addComponent( skipTaskButton );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Completed button
    completedTaskButton = new Button( "Completed" );
    completedTaskButton.addStyleName( "small" );
    completedTaskButton.setWidth( "200px" );
    completedTaskButton.setEnabled( false );
    completedTaskButton.addListener( new CompletedTaskListener() );
    vl.addComponent( completedTaskButton );
    
    // Horizontal spacer
    mainHL.addComponent( UILayoutUtil.createSpace("4px", null, true) );
    Component comp = UILayoutUtil.createSpace("4px", "catVertRule", true);
    comp.setHeight( "100%" );
    mainHL.addComponent( comp );
    
    // Treatment history view/current BPMN view
    sheet = new TabSheet();
    sheet.setWidth( "600px" );
    sheet.setHeight( "400px" );
    sheet.addStyleName( "borderless" );
    mainHL.addComponent( sheet );
    
    // Historical view
    Panel panel = new Panel();
    panel.addStyleName( "borderless" );
    panel.setImmediate( true );
    //sheet.addTab( panel, "Treatment history" ); //reorder tabs
    
    treatmentHistoryTable = new Table( "Previous treatments" );
    treatmentHistoryTable.setWidth( "570px" );
    treatmentHistoryTable.setHeight( "330px" );
    treatmentHistoryTable.addContainerProperty( tableTmtNameID, String.class, null );
    treatmentHistoryTable.addContainerProperty( tableTmtStartID, String.class, null );
    treatmentHistoryTable.addContainerProperty( tableTmtEndID, String.class, null );
    treatmentHistoryTable.setImmediate( true );
    panel.addComponent( treatmentHistoryTable );
    
    // BPMN view & image target
    bpmnView = new Panel();
    bpmnView.setWidth( "100%" );
    bpmnView.setHeight( "100%" );
    bpmnView.addStyleName( "borderless" );
    bpmnView.setScrollable( true );
    bpmnView.getContent().setSizeUndefined(); // Enforces scrollbar usage
    bpmnView.setImmediate( true );
    sheet.addTab(bpmnView, "Treatment work-flow");
    
    bpmnImage = new StreamedImageView();
    bpmnImage.updateImage( ViewResources.CATAPPResInstance.getResource("noWorkflow") );
    bpmnView.addComponent( (Component) bpmnImage.getImplContainer() );
    
    sheet.addTab( panel, "Treatment history" );
    
    return mainHL;
  }
  
  private Component createActionInfoView()
  {
    // Action info sub-view
    VerticalLayout vl = new VerticalLayout();
    vl.addStyleName( "catBorder" );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
    
    // Action title
    taskWaitingTitle = new Label( "No action currently selected" );
    taskWaitingTitle.addStyleName( "catSectionFont" );
    vl.addComponent( taskWaitingTitle );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace("4px", null) );
            
    taskWaitingInfoField = new TextArea();
    taskWaitingInfoField.setWidth( "820px" );
    taskWaitingInfoField.setHeight( "124px" );
    taskWaitingInfoField.addStyleName( "small" );
    taskWaitingInfoField.setReadOnly( true );
    vl.addComponent( taskWaitingInfoField );
        
    return vl;
  }
  
  private void updateCurrentROGroupInfo()
  {
    if ( currentROGroup != null )
    {
      activeTreatmentsList.removeAllItems();
      planSummaryField.setReadOnly( false );
      planSummaryField.setValue( "No plans set" );
      planSummaryField.setReadOnly( true );
      tasksWaitingList.removeAllItems();
      bpmnImage.updateImage( ViewResources.CATAPPResInstance.getResource("noWorkflow") );
        
      List<Treatment> treatments = currentROGroup.getOrderedTreatments();
      for ( Treatment tmt : treatments )
        activeTreatmentsList.addItem( new UUIDItem( tmt.getTitle(), tmt.getID()) );
    }
  }
  
  private void updateCurrentROTreatmentInfo()
  {
    if ( currentTreatment != null )
    {
      // reset selections first
      currentTask = null;
      currentTaskItem = null;
      
      updateCurrentTreatmentTaskList();

      // Control start/stop interactions
      treatmentStartButton.setEnabled( true );

      updateTreatmentStartButton();

      planSummaryField.setReadOnly( false );
      planSummaryField.setValue( currentTreatment.getDescription() );
      planSummaryField.setReadOnly( true );

      List<TreatmentMonitorViewListener> listeners = getListenersByType();
      for ( TreatmentMonitorViewListener listener : listeners )
        listener.onMonitorRequireRenderForTreatment( currentTreatment );
    }
  }
  
  private void updateTreatmentStartButton()
  {
    if (currentTreatment == null) {
        treatmentStartButton.setCaption( "Start treatment" );
        treatmentStartButton.setEnabled(false);
        return;
    }
    
    treatmentStartButton.setEnabled(true);
    
    if ( currentTreatment.isTreatmentRunning() )
        treatmentStartButton.setCaption( "Stop treatment" );
      else
        treatmentStartButton.setCaption( "Start treatment" );
  }
  
  private synchronized void updateCurrentTreatmentTaskList()
  {
    if ( currentTreatment != null )
    {
      // Update current tasks
      //tasksWaitingList.removeAllItems();
      Set<String> taskIDs = currentTreatment.getCurrentTaskIDs();
      
      // Reset currentTask if it has been removed
      if (currentTask != null) {
          String currentTaskID = currentTask.getTaskID();
          if (! taskIDs.contains(currentTaskID)) {
              currentTask = null;
              currentTaskItem = null;
          }
      }
      
      ArrayList<UUIDItem> tasksToRemove = new ArrayList<UUIDItem>();
      
      for (Object itemId : tasksWaitingList.getItemIds()) {
          UUIDItem taskItem = (UUIDItem) itemId;
          String taskId = (String)taskItem.getData();
          if (! taskIDs.contains(taskId)) {
              TreatmentTask tt = currentTreatment.getTaskForTaskDefId( taskId );
              if (tt == null)
                tasksToRemove.add(taskItem);
          }
      }
      
      for (UUIDItem itemId : tasksToRemove) {
          tasksWaitingList.removeItem(itemId);
      }

      UUIDItem selectedTaskItem = null;
      
      Iterator<String> idIt = taskIDs.iterator();
      while ( idIt.hasNext() )
      {
        TreatmentTask tt = currentTreatment.getTask( idIt.next() );
        String taskId = tt.getTaskID();
        String taskDefId = tt.getTaskDefinitionKey();
        
        UUIDItem taskItem = null;
        boolean alreadyInList = false;
        int taskItemIndex = 0;
        
        for (Object itemId : tasksWaitingList.getItemIds()) {
            taskItem = (UUIDItem) itemId;
            String itemTaskId = (String) taskItem.getData();
            if (taskId.equals(itemTaskId)) {
                alreadyInList = true;
                break;
            }
            else if (taskDefId.equals(itemTaskId)) {
                taskItem.setData(taskId);
                selectedTaskItem = taskItem;
                alreadyInList = true;
                break;
            }
            
            taskItemIndex++;
        }
        
        if (! alreadyInList) {
            taskItem = new UUIDItem(tt.getName());
            taskItem.setData(tt.getTaskID());
            tasksWaitingList.addItem(taskItem);
        }
        
        if (currentTask == null) {
            // if there is one new task in the list, select it (if not after exclusive gateway)
            if (! tt.getTaskDefinitionKey().equals( tt.getTaskID())) {
                //if (tasksWaitingList.size() == 1) {
                //    selectedTaskItem = taskItem;
                //}
                if (taskItemIndex == 0)
                    selectedTaskItem = taskItem; // select first item in list by default
            }
        }
        //else {
        //    // if we have a current (selected) task and its id's match, then it is for an exclusive gateway, so ensure it gets reselected below
        //    if (tt.getTaskDefinitionKey().equals( currentTask.getTaskID() )) {
        //          selectedTaskItem = taskItem;
        //    }
        //}
        else {
            String currentTaskType = currentTask.getType();
            if ("subProcess".equals(currentTaskType)) {
                if (tasksWaitingList.size() == 1) {
                    selectedTaskItem = taskItem;
                    currentTaskItem = null; // ensure new selection below
                }
            }
        }
      }
            
      if (selectedTaskItem != null) {
          if ( (currentTaskItem == null) || (! currentTaskItem.equals(selectedTaskItem)) ) {
              // select any newly selected item
              tasksWaitingList.select(selectedTaskItem);
          }
          else {
              // reselect item
              onTreatmentTaskSelected(selectedTaskItem);
          }
      }
    }
  }
  
  private void addHistoricTreatmentToTable( TreatmentRecord tr )
  {
    treatmentHistoryTable.addItem(
              new Object[] { tr.getTreatmentTitle(), 
                             tr.getTreatmentStartDate().toString(),
                             tr.getTreatmentStopDate().toString() }, null );
  }
  
  private void updateTreatmentHistoryTable()
  {
    treatmentHistoryTable.removeAllItems();
    
    if ( currentROGroup != null )
    {
      List<TreatmentRecord> historicTmts = currentROGroup.getCopyOfHistoricTreatments();
      Iterator<TreatmentRecord> histIt   = historicTmts.iterator();
      while ( histIt.hasNext() )
      { addHistoricTreatmentToTable( histIt.next() ); }
    }
  }
  
  // Internal event management -------------------------------------------------
  private void onROItemSelected( UUIDItem item )
  {
    if ( item != null )
    {
      List<TreatmentMonitorViewListener> listeners = getListenersByType();
      for ( TreatmentMonitorViewListener listener : listeners )
        listener.onROTreatmentGroupSelected( item.getID() );
    }
  }
  
  private void onActiveTreatmentSelected( UUIDItem item )
  {
    if ( item != null && currentROGroup != null )
    {
      currentTreatment = currentROGroup.getTreatment( item.getID() );
      updateCurrentROTreatmentInfo();
    }
  }
  
  private synchronized void onTreatmentTaskSelected( UUIDItem item )
  {
    if ( item != null && currentTreatment != null )
    {
      currentTaskItem = item;
      
      String taskID = (String) currentTaskItem.getData();
      currentTask = currentTreatment.getTask( taskID );
      
      if ( currentTask != null )
      {
        taskWaitingTitle.setValue( currentTask.getName() );
        
        taskWaitingInfoField.setReadOnly( false );
        taskWaitingInfoField.setValue( currentTask.getDescription() );
        taskWaitingInfoField.setReadOnly( true );
        
        skipTaskButton.setEnabled( true );
        completedTaskButton.setEnabled( true );
        
        if ( currentTask.getTaskID().equals( currentTask.getTaskDefinitionKey() ) ) {
            for (Object itemId : tasksWaitingList.getItemIds()) {
                UUIDItem taskItem = (UUIDItem)itemId;
                String taskid = (String)taskItem.getData();
                
                if (taskid.equals(taskID)) {
                    currentTreatment.selectGatewayTask(currentTask);
                        
                    String type = currentTask.getType();
                    // If task is a subProcess, we must complete it here, as its subTask will be created next
                    if ((type != null) && (type.equals("subProcess"))) {
                        //currentTask = null;
                        //currentTaskItem = null; // deselect it, as subtask needs to be selected
                        onTaskAction(taskItem, true);
                    }
                }
                else {
                    onTaskAction(taskItem, true);
                }
            }
            
            //updateCurrentTreatmentTaskList();
        }
      }
    }
    
    notifyAll();
  }
  
  private void onStartTreatmentClicked()
  {
    if ( currentTreatment != null )
    {
      boolean stopTmt = currentTreatment.isTreatmentRunning();
      
      List<TreatmentMonitorViewListener> listeners = getListenersByType();
      for ( TreatmentMonitorViewListener listener : listeners )
        if ( stopTmt ) listener.onMonitorStopTreatment( currentTreatment );
          else listener.onMonitorStartTreatment( currentTreatment );
    }
  }
  
  private void onTaskAction( boolean completed )
  {
    if (! completed) {
        viewContents.requestRepaintAll();
        return;
    }
    
      // Ensure that we have the currently selected item
      for (Object item : tasksWaitingList.getItemIds()) {
          if (tasksWaitingList.isSelected(item)) {
              currentTaskItem = (UUIDItem)item;
              String taskID = (String) currentTaskItem.getData();
              currentTask = currentTreatment.getTask(taskID);
              break;
          }
      }
    
    if ( currentTask != null && currentTaskItem != null )
    {
      TreatmentTask notifyTask = currentTask;
      
      //tasksWaitingList.removeItem( currentTaskItem );
      currentTask     = null;
      currentTaskItem = null;
      
      List<TreatmentMonitorViewListener> listeners = getListenersByType();
        for ( TreatmentMonitorViewListener listener : listeners )
          listener.onTreatmentTaskAction( notifyTask, completed );
    }
  }
  
    private void onTaskAction(UUIDItem taskItem, boolean completed) {
        if (taskItem != null) {
            String taskID = (String) taskItem.getData();
            TreatmentTask task = currentTreatment.getTask( taskID );

            //tasksWaitingList.removeItem(taskItem);
            //currentTask = null;
            //currentTaskItem = null;

            List<TreatmentMonitorViewListener> listeners = getListenersByType();
            for (TreatmentMonitorViewListener listener : listeners) {
                listener.onTreatmentTaskAction(task, completed);
            }
        }
    }

  private class ROListSelectListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onROItemSelected( (UUIDItem) vce.getProperty().getValue() ); }
  }
  
  private class ActiveTreatmentListSelectListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onActiveTreatmentSelected( (UUIDItem) vce.getProperty().getValue() ); }
  }
  
  private class StartTreatmentListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event) { onStartTreatmentClicked(); }
  }
  
  private class TreatmentTaskListListener implements Property.ValueChangeListener
  {
    @Override
    public void valueChange( Property.ValueChangeEvent vce )
    { onTreatmentTaskSelected( (UUIDItem) vce.getProperty().getValue() ); }
  }
  
  private class SkipTaskListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event)
    {  
        //onTaskAction( false );
        Thread ctnt = new Thread(new CompletedTaskNotifierThread(false));
        ctnt.start();
    }
  }
  
  private class CompletedTaskListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent event)
    {
        //onTaskAction( true );
        Thread ctnt = new Thread(new CompletedTaskNotifierThread(true));
        ctnt.start();
    }
  }
  
  private class CompletedTaskNotifierThread implements Runnable
  {
      private boolean completed;

      private CompletedTaskNotifierThread(boolean b) {
          completed = b;
      }
      
      @Override
      public void run() {
          try {
            onTaskAction(completed);
          }
          catch (Exception e) {
              e.printStackTrace();
              displayMessage( "Treatment message:", e.getMessage() );
          }
      }
  }
  
  /* KEM: class not yet used - see comments about Refresher in createComponents() method
  private class PageRefreshListener implements RefreshListener {

        @Override
        public void refresh(final Refresher source) {
            System.out.println("Refreshed");
        }
      
  }
  */
}
