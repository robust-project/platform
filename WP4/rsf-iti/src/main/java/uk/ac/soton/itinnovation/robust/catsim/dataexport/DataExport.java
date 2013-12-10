/*
 * Copyright 2012 University of Southampton IT Innovation Centre 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Created by       	: Mariusz Jacyno
 *
 * Creation Time    	: 07.12.2011
 *
 * Created for Project  : ROBUST
 */ 
package uk.ac.soton.itinnovation.robust.catsim.dataexport;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import uk.ac.soton.itinnovation.prestoprime.iplatform.batch.ModelRunner;
import uk.ac.soton.itinnovation.prestoprime.iplatform.dataexport.interfaces.IDataExport;
import uk.ac.soton.itinnovation.robust.catsim.ModelInterface;

public class DataExport {
	
	static Logger logger = Logger.getLogger(ModelRunner.class);

	private ModelInterface modelInterface = null;
	
	public DataExport(ModelInterface modelInterfaceRef)
	{
		this.modelInterface = modelInterfaceRef;
		logger.info("successfully initialised data export...");
	}
	
	//checking if an integer needs a "0" in front or not
	private String correctFormat(int x)
	{
		if(x/10==0)
			return "0"+String.valueOf(x);
		else
			return String.valueOf(x);
	}
	
	private synchronized String getCurrentSimulationTime()
	{	
		String years = correctFormat(modelInterface.getSimulationClock().years);
		String months = correctFormat(modelInterface.getSimulationClock().months);
		String days = correctFormat(modelInterface.getSimulationClock().days);
		String hours = correctFormat(modelInterface.getSimulationClock().hours);
		String minutes = correctFormat(modelInterface.getSimulationClock().tenMinutes);
			
		//the new time format according to ISO8601 - excluding the seconds
		//T stands for Time and Z denotes the UTC time format
		String time = "[Current simulation time: "+years+"-"+months+"-"+days+"T"+hours+":"+minutes+"Z]";
		
		return time;
	}

	//============================== system-performance events ================================
	public synchronized void exportHourlyOutput(Hashtable table)
	{
	    Logger appLogger = Logger.getLogger("SystemPerformanceOutput");
	    appLogger.info("test hourly...");
	//store system performance output in file 
//    Logger appLogger = Logger.getLogger("SystemPerformanceOutput");
//	appLogger.info(getCurrentSimulationTime()+" [category: daily output" +
//			"] "+"[throughput: "+throughput+
//			"] [runningCost: "+runningCost+"]");
	}

	public synchronized void exportDailyOutput(Hashtable table)
	{
	    Logger appLogger = Logger.getLogger("SystemPerformanceOutput");
	    appLogger.info("test daily...");

//		//store system performance output in file 
//	    Logger appLogger = Logger.getLogger("SystemPerformanceOutput");
//		appLogger.info(getCurrentSimulationTime()+" [category: daily output" +
//				"] "+"[throughput: "+throughput+
//				"] [runningCost: "+runningCost+"]");
	}

	public synchronized void exportMonthlyOutput(Hashtable table)
	{
	    Logger appLogger = Logger.getLogger("SystemPerformanceOutput");
	    appLogger.info("test monthly...");

//		//store system performance output in file 
//		Logger appLogger = Logger.getLogger("SystemPerformanceOutput");
//		appLogger.info(getCurrentSimulationTime()+" [category: monthly output" +
//				"] "+"[throughput: "+throughput+
//				"] [runningCost: "+runningCost+"]");
	}
	
	public synchronized void exportYearlyOutput(Hashtable table)
	{
	    Logger appLogger = Logger.getLogger("SystemPerformanceOutput");
	    appLogger.info("test yearly...");

//		//store system performance output in file 
//		Logger appLogger = Logger.getLogger("SystemPerformanceOutput");
//		appLogger.info(getCurrentSimulationTime()+" [category: monthly output" +
//				"] "+"[throughput: "+throughput+
//				"] [runningCost: "+runningCost+"]");
	}
}
