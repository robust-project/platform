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

import java.net.URI;
import java.util.List;
import java.util.UUID;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;

/**
 * A class that describes a simulation service, including any configuration parameters.
 * @author Vegard Engen
 */
public class SimulationServiceDescription
{
	private UUID uuid;
    private String name;
    private String version;
    private String description;
    private List<Parameter> configurationParameters;
    
    // the following is used by the WP1 framework only
    private URI serviceURI;
    private String serviceTargetNamespace;
    private String serviceName;
    private String servicePortName;
	
	public SimulationServiceDescription()
	{
		this.uuid = UUID.randomUUID();
	}
	
	/**
     * Constructor to set the basic information about the service.
     * @param name
     * @param version
     * @param desc 
     */
    public SimulationServiceDescription(String name, String version, String desc)
    {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.version = version;
        this.description = desc;
    }
    
    /**
     * Constructor to set the basic information about the service.
     * @param uuid
     * @param name
     * @param version
     * @param desc 
     */
    public SimulationServiceDescription(UUID uuid, String name, String version, String desc)
    {
        this.uuid = uuid;
        this.name = name;
        this.version = version;
        this.description = desc;
    }
	
	/**
     * Constructor to set all the information of a predictor (except for UUID and Service URI etc).
     * @param name
     * @param version
     * @param desc
     * @param configParams
     */
    public SimulationServiceDescription(String name, String version, String desc, List<Parameter> configParams)
    {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.version = version;
        this.description = desc;
        this.configurationParameters = configParams;
    }
	
	/**
     * Constructor to set all the information of a predictor (except for Service URI etc).
     * @param uuid
     * @param name
     * @param version
     * @param desc
     * @param configParams
     */
    public SimulationServiceDescription(UUID uuid, String name, String version, String desc, List<Parameter> configParams)
    {
        this.uuid = uuid;
        this.name = name;
        this.version = version;
        this.description = desc;
        this.configurationParameters = configParams;
    }

	/**
	 * @return the uuid
	 */
	public UUID getUuid()
	{
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(UUID uuid)
	{
		this.uuid = uuid;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the configurationParams
	 */
	public List<Parameter> getConfigurationParameters()
	{
		return configurationParameters;
	}

	/**
	 * @param configurationParams the configurationParams to set
	 */
	public void setConfigurationParameters(List<Parameter> configurationParams)
	{
		this.configurationParameters = configurationParams;
	}

	/**
	 * @return the serviceURI
	 */
	public URI getServiceURI()
	{
		return serviceURI;
	}

	/**
	 * @param serviceURI the serviceURI to set
	 */
	public void setServiceURI(URI serviceURI)
	{
		this.serviceURI = serviceURI;
	}

	/**
	 * @return the serviceTargetNamespace
	 */
	public String getServiceTargetNamespace()
	{
		return serviceTargetNamespace;
	}

	/**
	 * @param serviceTargetNamespace the serviceTargetNamespace to set
	 */
	public void setServiceTargetNamespace(String serviceTargetNamespace)
	{
		this.serviceTargetNamespace = serviceTargetNamespace;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName()
	{
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	/**
	 * @return the servicePortName
	 */
	public String getServicePortName()
	{
		return servicePortName;
	}

	/**
	 * @param servicePortName the servicePortName to set
	 */
	public void setServicePortName(String servicePortName)
	{
		this.servicePortName = servicePortName;
	}
}
