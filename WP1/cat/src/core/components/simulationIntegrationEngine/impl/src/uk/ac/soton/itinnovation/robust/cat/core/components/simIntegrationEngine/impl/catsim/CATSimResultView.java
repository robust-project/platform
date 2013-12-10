/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2013
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
//      Created By :            Ken Meacham
//      Created Date :          17 Sep 2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.impl.catsim;

import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.impl.koblenz.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components.WindowView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

import com.vaadin.ui.*;
import org.vaadin.vaadinvisualizations.*;

import java.util.*;





class CATSimResultView extends WindowView
{
  private ColumnChart resultChart;
  
  
  public CATSimResultView( Component parentComp )
  {
    super( parentComp, "Forum Restriction simulation results" );
    
    window.setWidth( "900px" );
    window.setHeight( "500px" );
    window.setModal(true);
    centreWindow();
    
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
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    resultChart = new ColumnChart();
    resultChart.setOption( "width", 880 );
    resultChart.setOption( "height", 390 );
    resultChart.setWidth( "890px" );
    resultChart.setHeight( "400px" );
    resultChart.addXAxisLabel( "Threads" );
    resultChart.addColumn( "Frequency" );
    
    window.addComponent( resultChart );
    
    // Spacer
    Component spacer = UILayoutUtil.createSpace( "4px", "catBottomBorder", false );
    spacer.setWidth( "100%" );
    window.addComponent( spacer );
    window.addComponent( UILayoutUtil.createSpace( "4px", null) );
    
    // Close button
    Button closeButton = new Button( "Close" );
    closeButton.addListener( new CloseButtonListener() );
    window.addComponent( closeButton );
    
    // Space
    window.addComponent( UILayoutUtil.createSpace( "16px", null) );
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
