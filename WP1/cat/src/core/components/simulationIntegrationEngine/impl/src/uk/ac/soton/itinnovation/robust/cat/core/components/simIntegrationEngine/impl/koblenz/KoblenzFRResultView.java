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
//      Created Date :          20-Nov-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.impl.koblenz;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.WindowView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

import com.vaadin.ui.*;
import org.vaadin.vaadinvisualizations.*;

import java.util.*;





class KoblenzFRResultView extends WindowView
{
  public static final String X_AXIS_LABEL = "Thread size";
  public static final String COLUMN_LABEL = "Frequency";
  
  private ColumnChart resultChart;  
  
  public KoblenzFRResultView( Component parentComp, String simulationID )
  {
    super( parentComp, "Forum Restriction simulation results: " + simulationID );
    
    window.setWidth( "900px" );
    window.setHeight( "500px" );
    window.setModal(true);
    //centreWindow();
    window.setPositionX(100);
    window.setPositionY(100);
    
    createComponents();    
  }
  
  public void setData( TreeMap<Double, Double> values )
  {
    if ( values != null )
    {
      Iterator<Double> tsIt = values.keySet().iterator();
      while ( tsIt.hasNext() )
      {
        Double tsSize = tsIt.next();
        Double freq   = values.get( tsSize );
        
        resultChart.add( tsSize.toString(), new double[] { freq } );
      }
    }
    this.updateView();
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    VerticalLayout vl = new VerticalLayout();
    window.addComponent( vl );
    
    resultChart = new ColumnChart();
    resultChart.setOption( "width", 880 );
    resultChart.setOption( "height", 390 );
    resultChart.setWidth( "890px" );
    resultChart.setHeight( "380px" );
    resultChart.addXAxisLabel( X_AXIS_LABEL ); //does not work :(
    resultChart.addColumn( COLUMN_LABEL );
    
    vl.addComponent( resultChart );
    
    HorizontalLayout hl = new HorizontalLayout();
    Label xAxisLabel = new Label( X_AXIS_LABEL );
    xAxisLabel.setWidth("100%");
    hl.addComponent(xAxisLabel);
    hl.setComponentAlignment(xAxisLabel, Alignment.MIDDLE_CENTER);
    vl.addComponent(hl);
    vl.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
    
    // Spacer
    Component spacer = UILayoutUtil.createSpace( "4px", "catBottomBorder", false );
    spacer.setWidth( "100%" );
    vl.addComponent( spacer );
    vl.addComponent( UILayoutUtil.createSpace( "4px", null) );
    
    // Close button
    Button closeButton = new Button( "Close" );
    closeButton.addListener( new CloseButtonListener() );
    vl.addComponent( closeButton );
    
    // Space
    vl.addComponent( UILayoutUtil.createSpace( "16px", null) );
  }
  
  // Internal event handlers ---------------------------------------------------
  private void onCloseButtonClicked()
  {
      destroyView();
  }
  
  private class CloseButtonListener implements Button.ClickListener
  {
    @Override
    public void buttonClick(Button.ClickEvent ce) { onCloseButtonClicked(); }
  }
}
