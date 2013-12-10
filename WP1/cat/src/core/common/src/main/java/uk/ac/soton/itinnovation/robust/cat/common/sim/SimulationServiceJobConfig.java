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
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.ps.CommunityDetails;

/**
 * A configuration object for a simulation job.
 * @author Vegard Engen
 */
public class SimulationServiceJobConfig implements Serializable
{
	private CommunityDetails communityDetails;
    private Date startDate, endDate;
	private List<Parameter> configurationParameters; // should correspond to the parameters defined in the SimulationServiceDescription
	
	/**
	 * Default constructor which only initialises the configuration parameter
	 * list.
	 */
	public SimulationServiceJobConfig()
	{
		configurationParameters = new ArrayList<Parameter>();
	}
	
	/**
	 * Constructor to set all the required job configuration details.
	 * @param communityDetails Community details, specifying the name and ID of the community
	 * @param startDate The start date of the simulation
	 * @param endDate The end date of the simulation
	 * @param params Any configuration parameters
	 */
	public SimulationServiceJobConfig(CommunityDetails communityDetails, Date startDate, Date endDate, List<Parameter> params)
	{
		this();
		this.communityDetails = communityDetails;
		this.startDate = startDate;
		this.endDate = endDate;
		this.configurationParameters = params;
	}

	/**
	 * @return the communityDetails
	 */
	public CommunityDetails getCommunityDetails()
	{
		return communityDetails;
	}

	/**
	 * @param communityDetails the communityDetails to set
	 */
	public void setCommunityDetails(CommunityDetails communityDetails)
	{
		this.communityDetails = communityDetails;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate()
	{
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate()
	{
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	/**
	 * @return the configurationParameters
	 */
	public List<Parameter> getConfigurationParameters()
	{
		return configurationParameters;
	}

	/**
	 * @param configurationParameters the configurationParameters to set
	 */
	public void setConfigurationParameters(List<Parameter> configurationParameters)
	{
		this.configurationParameters = configurationParameters;
	}
	
	/**
	 * @param configurationParameters the configurationParameters to add
	 */
	public void addConfigurationParameters(List<Parameter> configurationParameters)
	{
		if (this.configurationParameters == null) {
			this.configurationParameters = new ArrayList<Parameter>();
		}
		this.configurationParameters.addAll(configurationParameters);
	}
	
	/**
	 * @param configurationParameter the configurationParameter to add
	 */
	public void addConfigurationParameter(Parameter configurationParameter)
	{
		if (this.configurationParameters == null) {
			this.configurationParameters = new ArrayList<Parameter>();
		}
		this.configurationParameters.add(configurationParameter);
	}
}
