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
//      Created Date :          2013-10-10
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.sim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;

/**
 * A simulation result class.
 * @author Vegard Engen
 */
public class SimulationResult implements Serializable
{
	private Date simulationDate; // to be set by the Simulation Service
    private JobDetails jobDetails; // to be set by the Simulation Service
    private List<ResultGroup> resultGroups; // to be set by the Simulation Service
	
	/**
	 * Default constructor, which initialises the result groups list.
	 */
	public SimulationResult()
	{
		this.resultGroups = new ArrayList<ResultGroup>();
	}
	
	/**
	 * A constructor to set all the required parameters of the simulation result object.
	 * @param jDetails JobDetails object
	 * @param simDate Simulation date (the end date of the simulation that took place)
	 * @param res The result groups from the simulation
	 */
	public SimulationResult(JobDetails jDetails, Date simDate, List<ResultGroup> res)
	{
		this.jobDetails = jDetails;
		this.simulationDate = simDate;
		this.resultGroups = res;
	}

	/**
	 * @return the simulationDate
	 */
	public Date getSimulationDate()
	{
		return simulationDate;
	}

	/**
	 * @param simulationDate the simulationDate to set
	 */
	public void setSimulationDate(Date simulationDate)
	{
		this.simulationDate = simulationDate;
	}

	/**
	 * @return the jobDetails
	 */
	public JobDetails getJobDetails()
	{
		return jobDetails;
	}

	/**
	 * @param jobDetails the jobDetails to set
	 */
	public void setJobDetails(JobDetails jobDetails)
	{
		this.jobDetails = jobDetails;
	}

	/**
	 * @return the result groups
	 */
	public List<ResultGroup> getResultGroups()
	{
		return resultGroups;
	}

	/**
	 * @param results the result groups to set
	 */
	public void setResultGroups(List<ResultGroup> results)
	{
		this.resultGroups = results;
	}
	
	/**
	 * @param results the result groups to add
	 */
	public void addResultGroups(List<ResultGroup> results)
	{
		if (this.resultGroups == null) {
			this.resultGroups = new ArrayList<ResultGroup>();
		}
		this.resultGroups.addAll(results);
	}
	
	/**
	 * @param result the result group to add
	 */
	public void addResultGroup(ResultGroup result)
	{
		if (this.resultGroups == null) {
			this.resultGroups = new ArrayList<ResultGroup>();
		}
		this.resultGroups.add(result);
	}
}
