/*
 * Copyright 2012 WeST Institute - University of Koblenz-Landau
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
 * Created by       	: Felix Schwagereit 
 *
 * Creation Time    	: 07.12.2011
 *
 * Created for Project  : ROBUST
 */
 
package eu.robust.simulation.ukob.sv.forumrp;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uk.ac.soton.itinnovation.prestoprime.iplatform.batch.ModelRunner;
import uk.ac.soton.itinnovation.prestoprime.iplatform.config.ToolConfigurationManager;
import uk.ac.soton.itinnovation.prestoprime.iplatform.model.interfaces.SimulationClockEvent;
import eu.robust.simulation.dbconnection.*;
import eu.robust.simulation.entities.ContentItem;
import eu.robust.simulation.io.OutputWriter;
import eu.robust.simulation.ukob.config.ModelConfiguration;
	

public class EventWatcherEndSimulation implements SimulationClockEvent{

	static Logger logger = Logger.getLogger(ModelRunner.class);

	private int id = 0;
	//this is class used to configure the model
	private MIMForumRestriction modelInterface = null;
	private ModelConfiguration modelConfiguration = null;
	
	public EventWatcherEndSimulation(MIMForumRestriction modelInterfaceRef, int idRef)
	{
		this.id = idRef;
		
		this.modelInterface = modelInterfaceRef;
		this.modelConfiguration = modelInterface.getModelConfiguration();
	}
	
	private void writeDetailedStatisticsToDisk() {
		
		Map<Integer,Integer> timeSum = new HashMap<Integer,Integer>();
		Map<Integer,Integer> countThreads = new HashMap<Integer,Integer>();
		Map<Integer,Integer> countThreadsWithout = new HashMap<Integer,Integer>();
		
		OutputWriter outTimeQualityReply = new OutputWriter(modelConfiguration.templateName+"TTQR");
		OutputWriter outTimeReply = new OutputWriter(modelConfiguration.templateName+"TTR");
		
		for (ContentItem ci : modelInterface.getManagerContent().getAllItems().getAll()) {
			Thread thread = (Thread) ci;
			int forum = thread.getForum();

			// get time to reply for all replies
			int threadCreateTime = thread.getCreationtime();
			for (ContentItem posting : thread.getReplies()) {
				outTimeReply.write((Integer.toString(posting.getCreationtime()-threadCreateTime)));
			}
			
			// get time to quality reply
			if (thread.hasQualityReply()) {
				// add time
				int oldtime = 0;
				if (timeSum.containsKey(forum)) {
					oldtime = timeSum.get(forum);
				} 
				timeSum.put(forum, thread.timeToQualityReply()+oldtime);
				// add count
				int oldcount = 0;
				if (countThreads.containsKey(forum)) {
					oldcount = countThreads.get(forum);
				} 
				countThreads.put(forum, 1+oldcount);
				outTimeQualityReply.write((Integer.toString(thread.timeToQualityReply())));
			} else {
				// count non-quality reply threads
				int oldcountWithout = 0;
				if (countThreadsWithout.containsKey(forum)) {
					oldcountWithout = countThreadsWithout.get(forum);
				} 
				countThreadsWithout.put(forum, 1+oldcountWithout);
			}
		}
		
		outTimeQualityReply.close();
		outTimeReply.close();

		OutputWriter out = new OutputWriter(modelConfiguration.templateName);
		
		out.write("forum;threadsWith;SumTime;threadsWithout");
		for (int forum : getForums()) {
			out.write(forum+";"+countThreads.get(forum)+";"+timeSum.get(forum)+";"+countThreadsWithout.get(forum));
		}
		out.close();

		
	}
	
	private void writeResultObject() {
		
		Map<Integer,Integer> timeSum = new HashMap<Integer,Integer>();
		Map<Integer,Integer> countThreads = new HashMap<Integer,Integer>();
		Map<Integer,Integer> countThreadsWithout = new HashMap<Integer,Integer>();
		
		
		for (ContentItem ci : modelInterface.getManagerContent().getAllItems().getAll()) {
			Thread thread = (Thread) ci;
			int forum = thread.getForum();

			
			// get time to quality reply
			if (thread.hasQualityReply()) {
				// add time
				int oldtime = 0;
				if (timeSum.containsKey(forum)) {
					oldtime = timeSum.get(forum);
				} 
				timeSum.put(forum, thread.timeToQualityReply()+oldtime);
				// add count
				int oldcount = 0;
				if (countThreads.containsKey(forum)) {
					oldcount = countThreads.get(forum);
				} 
				countThreads.put(forum, 1+oldcount);
			} else {
				// count non-quality reply threads
				int oldcountWithout = 0;
				if (countThreadsWithout.containsKey(forum)) {
					oldcountWithout = countThreadsWithout.get(forum);
				} 
				countThreadsWithout.put(forum, 1+oldcountWithout);
			}
		}
		
		logger.error(modelInterface.getToolConfiguration().getResultsFolderLocation());

		Map<String, String> avgReplyTimePerForum = new HashMap<String, String>();
		
		
		
		for (int forum : getForums()) {
			if ((timeSum.containsKey(forum) && countThreads.containsKey(forum))) {
				double avgReplyTime = (double)timeSum.get(forum)/(double)countThreads.get(forum);
				avgReplyTimePerForum.put(String.valueOf(forum), String.valueOf(avgReplyTime));
			}
		}

		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		
		logger.error("Fehler "+gson);
		 
		String json = gson.toJson(avgReplyTimePerForum);
		 
		try {
			//write converted json data to a file named "file.json"
			//FileWriter writer = new FileWriter(modelConfiguration.  .templateName+"file.json");
			FileWriter writer = new FileWriter("C:\\Users\\schwagereit\\Documents\\file.json");
			writer.write(json);
			writer.close();
	 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void writeInDatabase() {
		
		Map<Integer,Integer> timeSum = new HashMap<Integer,Integer>();
		Map<Integer,Integer> countThreads = new HashMap<Integer,Integer>();
		Map<Integer,Integer> countThreadsWithout = new HashMap<Integer,Integer>();
		
		
		for (ContentItem ci : modelInterface.getManagerContent().getAllItems().getAll()) {
			Thread thread = (Thread) ci;
			int forum = thread.getForum();

			
			// get time to quality reply
			if (thread.hasQualityReply()) {
				// add time
				int oldtime = 0;
				if (timeSum.containsKey(forum)) {
					oldtime = timeSum.get(forum);
				} 
				timeSum.put(forum, thread.timeToQualityReply()+oldtime);
				// add count
				int oldcount = 0;
				if (countThreads.containsKey(forum)) {
					oldcount = countThreads.get(forum);
				} 
				countThreads.put(forum, 1+oldcount);
			} else {
				// count non-quality reply threads
				int oldcountWithout = 0;
				if (countThreadsWithout.containsKey(forum)) {
					oldcountWithout = countThreadsWithout.get(forum);
				} 
				countThreadsWithout.put(forum, 1+oldcountWithout);
			}
		}
		


		Map<String, String> avgReplyTimePerForum = new HashMap<String, String>();
		
		for (int forum : getForums()) {
			if ((timeSum.containsKey(forum) && countThreads.containsKey(forum))) {
				double avgReplyTime = (double)timeSum.get(forum)/(double)countThreads.get(forum);
				avgReplyTimePerForum.put("Forum"+String.valueOf(forum), String.valueOf(avgReplyTime));
			}
		}
		
		logger.debug("Writing simulation ("+modelInterface.getModelConfiguration().jobRefId+") results for "+avgReplyTimePerForum.size()+" forums.");

		Connectdb db = new Connectdb("jdbc:oracle:thin:@//robustdb:1521/orcl", "results", "none");
		

		
		db.writeSimulationResults(	modelInterface.getModelConfiguration().jobRefId,				
									avgReplyTimePerForum);
		
		db.disconnectdb();

	}
	
	public void triggerEvent() {
		logger.info("---> end simulation event triggered by simplatform clock...");
		
		//writeDetailedStatisticsToDisk();
		
		//writeResultObject();
		
		writeInDatabase();
		
		//exit the simulation as we received end simulation call
		System.exit(0);
		
		
//		exportSimulationLogs();
	}
	
	private Set<Integer> getForums() {
		Set<Integer> set = new HashSet<Integer>();
		set.add(	44	);
		set.add(	50	);
		set.add(	56	);
		set.add(	101	);
		set.add(	161	);
		set.add(	197	);
		set.add(	198	);
		set.add(	200	);
		set.add(	201	);
		set.add(	210	);
		set.add(	226	);
		set.add(	252	);
		set.add(	256	);
		set.add(	264	);
		set.add(	265	);
		set.add(	270	);
		set.add(	281	);
		set.add(	319	);
		set.add(	353	);
		set.add(	354	);
		set.add(	400	);
		set.add(	411	);
		set.add(	412	);
		set.add(	413	);
		set.add(	414	);
		set.add(	418	);
		set.add(	419	);
		set.add(	420	);
		set.add(	468	);
		set.add(	470	);
		set.add(	482	);
		set.add(	485	);
		set.add(	486	);
		return set;
	}
	
	private Set<Integer> getBusyForums() {
		Set<Integer> set = new HashSet<Integer>();
		set.add(	56	);
		set.add(	264	);
		set.add(	50	);
		set.add(	353	);
		set.add(	44	);
		set.add(	413	);
		set.add(	256	);
		set.add(	418	);
		set.add(	412	);
		set.add(	419	);
		return set;
	}

	//finalise any necessary actions to export the simulation logs
//	private void exportSimulationLogs()
//	{
//
//		
//    	//set that we are in 'exit mode' and we want to stop the batch restart process
//    	try 
//    	{
//    		//export model simulation logs
//    		ExportManager exportManager = new ExportManager(modelInterface);
//    		exportManager.exportData();	
//		} 
//    	catch (Exception e1) 
//    	{
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	}

	public int getEventId() {
		// TODO Auto-generated method stub
		return id;
	}
}
