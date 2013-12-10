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
//      Created Date :          09-Nov-2012
//      Created for Project :   robust-cat-core-components-simulationIntegrationEngine-impl
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.simIntegrationEngine.impl.koblenz;


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




public class KoblenzForumRestrictionController extends IntegratedSimControllerBase
{  
  private transient RobustService   kobSimService;
  private transient SimulationInput kobServerParameters;
  private transient Timer           simCheckTimer;
  
  private String communityData;
  private String subCommunityID;
  private boolean useDemoResultID;
  
  private String  simExeID;
  private String  simModelName;
  private String  simModelNameDefault;
  private boolean simServiceOK;
  private boolean executingSimulation;
  
  private static Logger log = Logger.getLogger(KoblenzForumRestrictionController.class);
  
  
  public KoblenzForumRestrictionController( IUFView parent )
  {
    super( parent );
    
    simulatorName = "Policy Forum Restriction simulator";
    simulatorDescription = "It can be simulated how the limitation of new threads in specific forums (e.g., the number of new threads in Forum #56 is limited to 10 per day) affects the time to a quality reply to a thread in all forums. The idea is that the attention of users is steered to other forums if the activity in a specific forum is restricted";
    simModelNameDefault = "MIMListOrders";
  }
  
  
  // IntegratedSimControllerBase abstract method implementations ---------------
  @Override
  public void executeSimulation() throws Exception
  {
    executingSimulation = false;
    simExeID            = null;
    
    if ( !simServiceOK ) throw new Exception( "Could not execute simulation service; set-up failed" );
    
    mapParamsToKoblenz();
    simExeID = kobSimService.startSimulation( kobServerParameters );
    
    executingSimulation = true;
    simCheckTimer = new Timer();
    simCheckTimer.scheduleAtFixedRate( new SimExeCheckTask(), 250, 5000 );
  }
  
  @Override
  public boolean isExecutingSimulation()
  { return executingSimulation; }
  
  // Protected methods ---------------------------------------------------------
  @Override
  protected void initialiseConfiguration( Properties props ) throws Exception
  {    
    String serviceURL = props.getProperty( "serviceURL" );
    String queueName  = props.getProperty( "queueName" );
    String implName   = props.getProperty( "implName" );
    
    useDemoResultID = props.getProperty( "usePrebakedSim" ).equals("true");
    simModelName = props.getProperty( "simModelName" );
    
    if ( simModelName == null ) {
        simModelName = simModelNameDefault;
    }
    log.debug("simModelName = " + simModelName);
    
    if ( serviceURL == null || queueName == null || implName == null )
      throw new Exception( "Forum Restriction Service configuration is incomplete" );
    
    simServiceOK        = false;
    kobServerParameters = null;
    
    Service kobService = Service.create( new URL( serviceURL + "?wsdl" ),
                                         new QName( queueName,
                                         implName) );
      
		kobSimService = kobService.getPort( RobustService.class );
    
    if ( kobService != null && kobSimService != null )
    {
      kobServerParameters = new SimulationInput();
      
      // Get pre-configured parameters
      communityData  = props.getProperty( "communityData" );
      subCommunityID = props.getProperty( "subCommunityID" );
      
      simServiceOK = true;
    }
  }
  
  @Override
  protected void initialiseParameterGroups()
  {
    // Community parameters ----------------------------------------------------
    SimulationParamGroup simGroup = new SimulationParamGroup( "Community parameters" );
    
    // Community model
    Parameter param = new Parameter( ParameterValueType.STRING, "communityModel", "Community model to use", "model ID" );
    param.setValue( new ParameterValue( simModelName ) );
    simGroup.addParameter( param.getName(), param );
    
    // Start time of simulation
    param = new Parameter( ParameterValueType.STRING, "StartTime", "Start time of simulation", "date" );
    param.setValue( new ParameterValue( "2009.12.1.8" ) ); // yyyy.MM.d.H
    simGroup.addParameter( param.getName(), param );
    
    // End time of simulation
    param = new Parameter( ParameterValueType.STRING, "EndTime", "End time of simulation", "date" );
    param.setValue( new ParameterValue( "2010.12.1.8" ) ); // yyyy.MM.d.H
    simGroup.addParameter( param.getName(), param );
    
    simulationParams.put( simGroup.getName(), simGroup );
    
    // Simulation parameters ---------------------------------------------------
    simGroup = new SimulationParamGroup( "Policy parameters" );
    
    // PolicyLatestActivityCreationDate
    ArrayList<ValueConstraint> constraints = new ArrayList<ValueConstraint>();
    param = new Parameter( ParameterValueType.FLOAT, "PolicyLatestActivityCreationDate", "Latest activity creation date probability", "probability (0-1)" );
    
    constraints.add( new ValueConstraint("0.0", ValueConstraintType.MIN) );
    constraints.add( new ValueConstraint("1.0", ValueConstraintType.MAX) );
    param.setValueConstraints(constraints);
    param.setValue( new ParameterValue( "1.0" ) );
    simGroup.addParameter( param.getName(), param );
    
    // PolicyThreadCreationDate
    param = new Parameter( ParameterValueType.FLOAT, "PolicyThreadCreationDate", "Policy thread creation date probability", "probability (0-1)" );
    
    constraints = new ArrayList<ValueConstraint>();
    constraints.add( new ValueConstraint("0.0", ValueConstraintType.MIN) );
    constraints.add( new ValueConstraint("1.0", ValueConstraintType.MAX) );
    param.setValueConstraints(constraints);
    param.setValue( new ParameterValue( "0" ) );
    simGroup.addParameter( param.getName(), param );
    
    // PolicyThreadSize
    param = new Parameter( ParameterValueType.FLOAT, "PolicyThreadSize", "Policy thread size probability", "rep(s)" );
    
    constraints = new ArrayList<ValueConstraint>();
    constraints.add( new ValueConstraint("0.0", ValueConstraintType.MIN) );
    constraints.add( new ValueConstraint("1.0", ValueConstraintType.MAX) );
    param.setValueConstraints(constraints);
    param.setValue( new ParameterValue( "0" ) );
    simGroup.addParameter( param.getName(), param );
    
    simulationParams.put( simGroup.getName(), simGroup );
  }
  
  // Private methods -----------------------------------------------------------
  private void mapParamsToKoblenz()
  {
    HashMap<String, String> kobParams = new HashMap<String,String>();
    
    // Community parameters ----------------------------------------------------
    ISimulationParamGroup group = simulationParams.get( "Community parameters" );
    
    Parameter param = group.getParameter( "communityModel" );
    kobServerParameters.setCommunityModel( param.getValue().getValue() );
    
    kobParams.put( "communityData", communityData );
    kobParams.put( "subcommunityId", subCommunityID );
    
    // Start time
    param = group.getParameter( "StartTime" );
    HashMap<String, String> dateInfo = convertToDate( param );
    kobParams.put("startHour" , dateInfo.get("hour") );
    kobParams.put("startDay"  , dateInfo.get("day") );
    kobParams.put("startMonth", dateInfo.get("month") );
    kobParams.put("startYear" , dateInfo.get("year") );
    
    // End time
    param = group.getParameter( "EndTime" );
    dateInfo = convertToDate( param );
    kobParams.put("endHour" , dateInfo.get("hour") );
    kobParams.put("endDay"  , dateInfo.get("day") );
    kobParams.put("endMonth", dateInfo.get("month") );
    kobParams.put("endYear" , dateInfo.get("year") );
    
    // Policy parameters -------------------------------------------------------
    group = simulationParams.get( "Policy parameters" );
    
    param = group.getParameter( "PolicyLatestActivityCreationDate" );
    kobParams.put( "PolicyLatestActivityCreationDate", param.getValue().getValue() );
    
    param = group.getParameter( "PolicyThreadCreationDate" );
    kobParams.put( "PolicyThreadCreationDate", param.getValue().getValue() );
    
    param = group.getParameter( "PolicyThreadSize" );
    kobParams.put( "PolicyThreadSize", param.getValue().getValue() );
   
    // Set up all parameters ---------------------------------------------------
    //HashMap<String,String> convParams = convertBE( kobParams );
    
    kobServerParameters.setCommunityParameters( kobParams ); 
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
  
  // Private event methods -----------------------------------------------------
  private void onExecutionComplete()
  {
    executingSimulation = false;
    simCheckTimer.cancel();

    SimulationResult simResult = kobSimService.getSimulationResult( simExeID ); 

    if ( simResult != null )
    {
      // Prepare results
      TreeMap<Double, Double> procData = processResults( simResult.getResult() );
      
      // Present results
      KoblenzFRResultView krv = new KoblenzFRResultView( (Component) parentView.getImplContainer() );
      krv.setData( procData );
    }
    
    // Notify simulation has completed (result or not)
    notifySimulationCompleted();
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
  
  
  // Private TimerTask class ---------------------------------------------------
  private class SimExeCheckTask extends TimerTask
  {
    public SimExeCheckTask()
    {
      super();
    }
    
    @Override
      public void run()
    {      
      // Seem to need a hard-coded ID here
      if ( kobSimService.simulationHasFinished( simExeID ) )
        onExecutionComplete();
    }
  }
}
