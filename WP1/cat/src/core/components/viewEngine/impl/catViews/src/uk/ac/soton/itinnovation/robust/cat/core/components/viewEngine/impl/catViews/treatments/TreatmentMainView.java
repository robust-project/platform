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
//      Created Date :          18-Jul-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.treatments;


import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.events.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.treatments.subViews.*;

import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.ROTreatmentGroup;

import com.vaadin.ui.*;







class TreatmentMainView extends SimpleView
                        implements ActiveTitleBarListener
{ 
  private VerticalLayout       subviewHolder;
  private TreatmentMonitorView monitorView;
  private TreatmentEditorView  editorView;
  private boolean              isMonitorViewActive;
  
  @Override
  public void updateView()
  {
    //TODO
  }
  
  public TreatmentMonitorView getMonitorView()
  { return monitorView; }
  
  public void addTreatmentGroup( ROTreatmentGroup group )
  {
    if ( group != null && monitorView != null )
      monitorView.addTreatmentGroup( group );
  }
  
  public void removeTreatmentGroup( ROTreatmentGroup group )
  {
    if ( group != null && monitorView != null )
      monitorView.removeTreatmentGroup( group );
  }
  
  public void setCurrentTreatmentGroup( ROTreatmentGroup group )
  {
    if ( group != null && monitorView != null )
      monitorView.setCurrentTreatmentGroup( group );
  }
  
  // ActiveTitleBarListener ----------------------------------------------------
  @Override
  public void onActiveTitleBarSelection( String itemName )
  {
    if ( itemName.equals( "Treatment monitor") && !isMonitorViewActive )
      switchView();
    else if ( itemName.equals( "Treatment editor") && isMonitorViewActive )
      switchView();
  }
  
  // Protected methods ---------------------------------------------------------
  protected TreatmentMainView()
  {
    super();
    
    viewContents.setWidth( "100%" );
    
    createComponents();
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    VerticalLayout vl = (VerticalLayout) viewContents;
    
    ActiveTitleBar atb = new ActiveTitleBar( "CAT Treatment Centre" );
    atb.addMenuOption( "Treatment monitor" );
    atb.addMenuOption( "Treatment editor" );
    atb.addListener( this );
    
    vl.addComponent( (Component) atb.getImplContainer() );
    
    // Sub-view holder
    subviewHolder = new VerticalLayout();
    subviewHolder.setWidth( "100%" );
    vl.addComponent( subviewHolder );
    
    // Sub-views
    monitorView = new TreatmentMonitorView();
    editorView  = new TreatmentEditorView();
    
    // Default view: monitor
    isMonitorViewActive = true;
    Component comp = (Component) monitorView.getImplContainer();
    subviewHolder.addComponent( comp );
    subviewHolder.setComponentAlignment( comp, Alignment.TOP_LEFT );
  }
  
  private void switchView()
  {
    subviewHolder.removeAllComponents();
    
    Component targetView;
    
    if ( isMonitorViewActive )
    {
      targetView = (Component) editorView.getImplContainer();
      isMonitorViewActive = false;
    }
    else
    {
      targetView = (Component) monitorView.getImplContainer();
      isMonitorViewActive = true;
    }
    
    subviewHolder.addComponent( targetView );
    subviewHolder.setComponentAlignment( targetView, Alignment.TOP_LEFT );
  }
   
  // Internal event handling ---------------------------------------------------
  
}
