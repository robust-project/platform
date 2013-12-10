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
//      Created By :            Vegard Engen
//      Created Date :          2013-10-28
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.sim.ws.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.KeyValuePair;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;
import uk.ac.soton.itinnovation.robust.cat.common.sim.ResultGroup;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationResult;
import uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceJobConfig;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;

/**
 * 
 * @author Vegard Engen
 */
public class ResultProcessor
{
	private String modelOutputFileName;
	private File resultsDir;
	private String jobRef;
	private SimulationConfigurer simConfigurer;
	private SimulationResult simulationResult;
	
	private static Logger logger = Logger.getLogger(ResultProcessor.class);
	
	public ResultProcessor()
	{
		this.modelOutputFileName = "outputServiceModelPerformance.log";
	}
	
	public void initialise(SimulationConfigurer simConfigurer, File resultsDir) throws Exception
	{
		this.simConfigurer = simConfigurer;
		this.resultsDir = resultsDir;
	}
	
	public SimulationResult getSimulationResult() throws Exception
	{
		if (simulationResult != null) {
			return simulationResult;
		}
		
		simulationResult = new SimulationResult();
		logger.debug("Starting to read results from " + resultsDir.getAbsolutePath());
		ForumStats historicalForumStats = getHistoricalForumStats();
		
//		logger.debug("Historical forum stats:");
//		logForumStats(historicalForumStats);
		
		List<ForumStats> simForumStats = getSimulationForumStats();
		logger.debug("Got simulation results for " + simForumStats.size() + " iterations");
//		
//		for (ForumStats stats : simForumStats) {
//			logForumStats(stats);
//			logger.debug("");
//		}
		
		ResultGroup generalGrp = getGeneralStatistics(simForumStats);
		simulationResult.addResultGroup(generalGrp);
		
		if (simConfigurer.isIsTRTprob())
		{
			ResultGroup trtGrp = getTRTStatistics(historicalForumStats, simForumStats, simConfigurer.getTrtThreshold());
			simulationResult.addResultGroup(trtGrp);
		}
		
		if (simConfigurer.isIsCommunityActivityProb())
		{
			ResultGroup comDropGrp = getCommunityActivityStatistics(historicalForumStats, simForumStats, simConfigurer.getCommunityActivityThreshold());
			simulationResult.addResultGroup(comDropGrp);
		}
		
		JobDetails jobDetails = new JobDetails();
		jobDetails.setJobRef(this.jobRef);
		simulationResult.setSimulationDate(simConfigurer.getConfig().getEndDate());
		
		return simulationResult;
	}
	
	private ResultGroup getGeneralStatistics(List<ForumStats> simForumStats)
	{
		ResultGroup generalGrp = new ResultGroup("General community statistics");
		
		double posts = 0;
		double newThreads = 0;
		double answeredThreads = 0;
		double trt_hrs = 0;
		
		for (ForumStats stats : simForumStats)
		{
			posts += stats.getPosts();
			newThreads += stats.getNewThreads();
			answeredThreads += stats.getAnsweredThreads();
			trt_hrs += stats.getMeanTRT();
		}
		
		double mean_trt_days = (trt_hrs/(double)simForumStats.size()) * 24.0;
		
		generalGrp.addResult("Mean number of posts", String.valueOf(posts/(double)simForumStats.size()));
		generalGrp.addResult("Mean number of new threads", String.valueOf(newThreads/(double)simForumStats.size()));
		generalGrp.addResult("Mean number of threads resolved", String.valueOf(answeredThreads/(double)simForumStats.size()));
		generalGrp.addResult("Mean thread resolution time (TRT) in days", String.valueOf(mean_trt_days));
		
		return generalGrp;
	}
	
	private ResultGroup getTRTStatistics(ForumStats historicalForumStats, List<ForumStats> simForumStats, double threshold)
	{
		ResultGroup trtGrp = new ResultGroup("Thread resolution time");
		
		int thresholdExceededCount = 0;
		double minChange = 0;
		double maxChange = 0;
		double totChange = 0;
		
		for (int i = 0; i < simForumStats.size(); i++)
		{
			double change = (((simForumStats.get(i).getMeanTRT()*24.0) - historicalForumStats.getMeanTRT()) / historicalForumStats.getMeanTRT()) * 100.0;
			totChange += change;
			if (change >= threshold) {
				thresholdExceededCount++;
			}
			
			if (i == 0)
			{
				minChange = change;
				maxChange = change;
			}
			else
			{
				if (change < minChange) {
					minChange = change;
				}
				
				if (change > maxChange) {
					maxChange = change;
				}
			}
		}
		
		double prob = (double)thresholdExceededCount / (double)simForumStats.size();
		double mean = totChange / (double)simForumStats.size();
		
		trtGrp.addResult("P(TRT incr >= " + threshold + ")", String.valueOf(prob));
		trtGrp.addResult("TRT change min", String.valueOf(minChange));
		trtGrp.addResult("TRT change max", String.valueOf(maxChange));
		trtGrp.addResult("TRT change mean", String.valueOf(mean));
		
		return trtGrp;
	}
	
	private ResultGroup getCommunityActivityStatistics(ForumStats historicalForumStats, List<ForumStats> simForumStats, double threshold)
	{
		ResultGroup comDropGrp = new ResultGroup("Community activity drop");
		
		int thresholdExceededCount = 0;
		double minChange = 0;
		double maxChange = 0;
		double totChange = 0;
		
		for (int i = 0; i < simForumStats.size(); i++)
		{
			double change = (((double)historicalForumStats.getPosts()-(double)simForumStats.get(i).getPosts()) / (double)historicalForumStats.getPosts()) * 100.0;
			totChange += change;
			if (change >= threshold) {
				thresholdExceededCount++;
			}
			
			if (i == 0)
			{
				minChange = change;
				maxChange = change;
			}
			else
			{
				if (change < minChange) {
					minChange = change;
				}
				
				if (change > maxChange) {
					maxChange = change;
				}
			}
		}
		
		double prob = (double)thresholdExceededCount / (double)simForumStats.size();
		double mean = totChange / (double)simForumStats.size();
		
		comDropGrp.addResult("P(community_activity_drop >= " + threshold + ")", String.valueOf(prob));
		comDropGrp.addResult("Num posts drop min", String.valueOf(minChange));
		comDropGrp.addResult("Num posts drop max", String.valueOf(maxChange));
		comDropGrp.addResult("Num posts drop mean", String.valueOf(mean));

		return comDropGrp;
	}
	
	private ForumStats getHistoricalForumStats() throws Exception
	{
		logger.debug("Getting historical forum status");
		ForumStats stats = new ForumStats();
		
		File[] files = resultsDir.listFiles();
		
		if (files.length == 0) {
			throw new RuntimeException("No result folders for simulation runs in " + resultsDir.getAbsolutePath());
		}
		
		File modelOutputFile = new File(files[0].getAbsolutePath() + File.separator + this.modelOutputFileName);
		if (!modelOutputFile.exists()) {
			throw new RuntimeException("The model output file does not exist: " + modelOutputFile.getAbsolutePath());
		}
		
//		logger.debug(" - Reading from " + modelOutputFile.getAbsolutePath());
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(modelOutputFile));
			String line = null;
			boolean historyFlag = false;
			
			while ((line = br.readLine()) != null)
			{
				if (line.isEmpty()) {
					continue;
				}
				
				if (!historyFlag && line.contains("HISTORY")) {
					historyFlag = true;
				} else if (historyFlag && !line.contains("HISTORY")) {
					break;
				}
				
				if (historyFlag)
				{
					if (line.contains("NewPostsInSamplingPeriod")) {
						stats.setPosts(getHistoryStatsValue(line).intValue());
					} else if (line.contains("NewThreadsInSamplingPeriod")) {
						stats.setNewThreads(getHistoryStatsValue(line).intValue());
					} else if (line.contains("AnsweredThreadsInSamplingPeriod")) {
						stats.setAnsweredThreads(getHistoryStatsValue(line).intValue());
					} else if (line.contains("UnansweredThreadsInSamplingPeriod")) {
						stats.setUnansweredThreads(getHistoryStatsValue(line).intValue());
					} else if (line.contains("MeanThreadResponseTime")) {
						stats.setMeanTRT(getHistoryStatsValue(line).doubleValue());
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Exception caught when getting historical forum stats: " + ex);
			throw new RuntimeException("Exception caught when getting historical forum stats: " + ex);
		} finally {
			if (br!=null) {
				br.close();
			}
		}
		
		return stats;
	}
	
	private Number getHistoryStatsValue(String line) throws Exception
	{
		String[] strings = line.split(" ");
		return Double.parseDouble(strings[strings.length-1]);
	}
	
	private List<ForumStats> getSimulationForumStats() throws Exception
	{
		List<ForumStats> statsList = new ArrayList<ForumStats>();
		
		// iterate over each folder in the results dir (each run) and get stats
		File[] files = resultsDir.listFiles();
		for (File f : files) {
			statsList.add(getSimulationIterationForumStats(new File(f.getAbsolutePath() + File.separator + this.modelOutputFileName)));
		}
		
		return statsList;
	}
	
	private ForumStats getSimulationIterationForumStats(File modelOutputFile) throws Exception
	{
		ForumStats stats = new ForumStats();
		
		if (!modelOutputFile.exists()) {
			throw new RuntimeException("The model output file does not exist: " + modelOutputFile.getAbsolutePath());
		}
		
//		logger.debug(" - Reading from " + modelOutputFile.getAbsolutePath());
		BufferedReader br = null;
		String lastDataLine = null;
		
		try {
			br = new BufferedReader(new FileReader(modelOutputFile));
			String line = null;
			
			while ((line = br.readLine()) != null)
			{
				if (line.isEmpty()) {
					continue;
				}
				
				lastDataLine = line;
			}
		} catch (Exception ex) {
			logger.error("Exception caught when getting historical forum stats: " + ex);
			throw new RuntimeException("Exception caught when getting historical forum stats: " + ex);
		} finally {
			if (br!=null) {
				br.close();
			}
		}
		
		// extract the stats from the line
		if (lastDataLine == null) {
			throw new RuntimeException("Did not get any lines with content in the model output file: " + modelOutputFile.getAbsolutePath());
		}
		
		// | day | trt | posts | new threads | resolved threads |
		String[] sArray = lastDataLine.trim().split(" ");
		stats.setPosts(Integer.parseInt(sArray[2]));
		stats.setNewThreads(Integer.parseInt(sArray[3]));
		stats.setAnsweredThreads(Integer.parseInt(sArray[4]));
		stats.setMeanTRT(Double.parseDouble(sArray[1]));
		
		return stats;
	}
	
	public static void printResults(SimulationResult simRes) throws Exception
	{
		if (simRes == null) {
			return;
		}
		logger.debug("Simulation Results");
		if ((simRes.getJobDetails() != null) && (simRes.getJobDetails().getJobRef() != null)) {
			logger.debug(" * Job ref: " + simRes.getJobDetails().getJobRef());
		}
		logger.debug(" * Simulation date: " + DateUtil.getDateString(simRes.getSimulationDate()));
		logger.debug(" * " + simRes.getResultGroups().size() + " Result Groups:");

		for (ResultGroup resGrp : simRes.getResultGroups())
		{
			if (resGrp == null) {
				logger.debug("   - Got a NULL group :-(");
				continue;
			}
			
			logger.debug("   - " + resGrp.getName());
			for (KeyValuePair kp : resGrp.getResults())
			{
				logger.debug("      - " + kp.getKey() + ": " + kp.getValue());
			}
		}
	}
	
	private void logForumStats(ForumStats stats)
	{
		logger.debug(" - Num posts: " + stats.getPosts());
		logger.debug(" - New threads: " + stats.getNewThreads());
		logger.debug(" - Answered threads: " + stats.getAnsweredThreads());
		logger.debug(" - Unanswered threads: " + stats.getUnansweredThreads());
		logger.debug(" - Mean TRT: " + stats.getMeanTRT());
	}
	
	
	
	
	
}
