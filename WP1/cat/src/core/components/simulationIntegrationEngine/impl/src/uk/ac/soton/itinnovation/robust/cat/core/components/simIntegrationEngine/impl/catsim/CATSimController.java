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
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFView;

import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.spec.*;

import uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.impl.base.*;

import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;

import eu.robust.simulation.service.interfaces.RobustService;
import eu.robust.simulation.service.parameters.*;
        
import com.vaadin.ui.Component;
import java.io.UnsupportedEncodingException;

import java.util.*;
import java.net.URL;
import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;




public class CATSimController extends IntegratedSimControllerBase
{  
  private transient RobustService   catSimService;
  private transient SimulationInput catSimServerParameters;
  private transient Timer           simCheckTimer;
  
  private String communityData;
  private String subCommunityID;
  //private boolean useDemoResultID;
  private String param1;
  
  private String  simExeID;
  private String  simModelName;
  private String  simModelNameDefault;
  private boolean simServiceOK;
  private boolean executingSimulation;
  
  private static Logger log = Logger.getLogger(CATSimController.class);
  
  
  public CATSimController( IUFView parent )
  {
    super( parent );
    
    simulatorName = "CATSim simulator";
    simulatorDescription = "N.B. This description is still to be defined!";
    simModelNameDefault = "MIMListOrders";
  }
  
  
  // IntegratedSimControllerBase abstract method implementations ---------------
  @Override
  public SimulationStatus executeSimulation() throws Exception
  {
    executingSimulation = false;
    simExeID            = null;
    simStatus           = null;
    
    if ( !simServiceOK ) throw new Exception( "Could not execute simulation service; set-up failed" );
    
    mapParamsToCATSim();
    
    throw new Exception("CATSim service not available");
    
    /*
    simExeID = catSimService.startSimulation( catSimServerParameters );
    
    executingSimulation = true;
    
    Date startDate = new Date();
    Date endDate = null;
    SimulationStatus simulationStatus = new SimulationStatus(simExeID, "started", startDate, endDate);

    this.simStatus = simulationStatus;

    return simulationStatus;
    */
  }
  
  // Protected methods ---------------------------------------------------------
  @Override
  protected void initialiseConfiguration( Properties props, String currPlatformID, String currCommunityID ) throws Exception
  {    
    String serviceURL = props.getProperty( "serviceURL" );
    String queueName  = props.getProperty( "queueName" );
    String implName   = props.getProperty( "implName" );
    
    //useDemoResultID = props.getProperty( "usePrebakedSim" ).equals("true");
    simModelName = props.getProperty( "simModelName" );
    
    if ( simModelName == null ) {
        simModelName = simModelNameDefault;
    }
    log.debug("simModelName = " + simModelName);
    
    if ( serviceURL == null || queueName == null || implName == null )
      throw new Exception( "CATSim Service configuration is incomplete" );
    
    simServiceOK        = false;
    catSimServerParameters = null;
    
    Service catService = Service.create( new URL( serviceURL + "?wsdl" ),
                                         new QName( queueName,
                                         implName) );
      
		catSimService = catService.getPort( RobustService.class );
    
    if ( catService != null && catSimService != null )
    {
      catSimServerParameters = new SimulationInput();
      
      // Get pre-configured parameters
      //communityData  = props.getProperty( "communityData" );
      //subCommunityID = props.getProperty( "subCommunityID" );
      
      communityData = currPlatformID;
      subCommunityID = currCommunityID;
      
      simServiceOK = true;
    }
  }
  
  @Override
  protected void initialiseParameterGroups()
  {
    // Parameters ----------------------------------------------------
    SimulationParamGroup simGroup = new SimulationParamGroup( "Parameters" );
    
    // Param1
    Parameter param = new Parameter( ParameterValueType.STRING, "param1", "Parameter1 description", "unit" );
    param.setValue( new ParameterValue( "1.0" ) );
    simGroup.addParameter( param.getName(), param );
        
    simulationParams.put( simGroup.getName(), simGroup );
  }
  
  // Private methods -----------------------------------------------------------
  private void mapParamsToCATSim()
  {
    HashMap<String, String> catSimParams = new HashMap<String,String>();
    
    // Parameters ----------------------------------------------------
    ISimulationParamGroup group = simulationParams.get( "Parameters" );
    
    catSimParams.put( "community", communityData );
    catSimParams.put( "subCommunity", subCommunityID );
    
    Parameter param = group.getParameter( "param1" );
    catSimParams.put( "param1", param1 );
    
    catSimServerParameters.setCommunityParameters( catSimParams ); 
  }
  
  private HashMap<String, String> convertToDate( Parameter param )
  {
    HashMap<String, String> dateInfo = new HashMap<String,String>();
    SimpleDateFormat             sdf = new SimpleDateFormat( "yyyy.MM.d.H" );
    
    try 
    { 
      Date date = sdf.parse( param.getValue().getValue() );
      
      sdf = new SimpleDateFormat( "yyyy" );
      dateInfo.put( "year", sdf.format(date) );
      
      sdf = new SimpleDateFormat( "MM" );
      dateInfo.put( "month", sdf.format(date) );
      
      sdf = new SimpleDateFormat( "d" );
      dateInfo.put( "day", sdf.format(date) );
      
      sdf = new SimpleDateFormat( "H" );
      dateInfo.put( "hour", sdf.format(date) );
      
    }
    catch ( Exception e ) {}
    
    return dateInfo;    
  }
  
  private HashMap<String,String> convertBE( HashMap<String,String> inMap )
  {
    HashMap<String, String> outMap = new HashMap<String, String>();
    
    Iterator<String> keyIt = inMap.keySet().iterator();
    while ( keyIt.hasNext() )
    {
      String key = keyIt.next();
      String val = inMap.get( key );
      
      String newKey = convertBE( key );
      String newVal = convertBE( val );
      
      outMap.put( newKey, newVal );
    }
    
    return outMap;
  }
  
  private String convertBE( String in )
  {
    String out = null;
    
    try { out = new String( in.getBytes(), "ISO8859_1" ); }
    catch ( UnsupportedEncodingException uee ) {}
    
    return out;
  }
  
  @Override
  public void onExecutionComplete()
  {
    executingSimulation = false;
    stopExecutionMonitor();

    viewResults(simExeID);
    
    // Notify simulation has completed (result or not)
    notifySimulationCompleted();
  }
  
  @Override
  public void viewResults(String simulationID) {
    //SimulationResult simResult = catSimService.getSimulationResult( simExeID ); 
    SimulationResult simResult = catSimService.getSimulationResult( simulationID ); 

    if ( simResult != null )
    {
      // Prepare results
      TreeMap<Double, Double> procData = processResults( simResult.getResult() );
      
      // Present results
      CATSimResultView krv = new CATSimResultView( (Component) parentView.getImplContainer() );
      krv.setData( procData );
    }
  }
  
  private TreeMap<Double, Double> processResults( Map<String, String> rawData )
  {
    TreeMap<Double, Double> procResults = new TreeMap<Double, Double>();
    
    if ( rawData != null )
    {
      Iterator<String> rawDataIt = rawData.keySet().iterator();
      while ( rawDataIt.hasNext() )
      {
        String thSizeStr = rawDataIt.next();

        Double threadSize = Double.parseDouble( thSizeStr );
        Double frequency = Double.parseDouble( rawData.get(thSizeStr) );

        procResults.put( threadSize, frequency );
      }
    }
    
    return procResults;
  }
  
    @Override
    public boolean simulationHasFinished() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
