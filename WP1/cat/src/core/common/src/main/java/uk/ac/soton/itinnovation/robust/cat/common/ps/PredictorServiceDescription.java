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
//      Created By :            Vegard Engen
//      Created Date :          2012-01-11
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.ps;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;

/**
 *
 * @author Vegard Engen
 */
public class PredictorServiceDescription implements Serializable
{
    private UUID uuid;
    private String name;
    private String version;
    private String description;
    private List<Parameter> configurationParams;
    private List<Event> events;
    private Parameter forecastPeriod; // OBS: should use unit of days!
    
    // the following is used by the WP1 framework only
    private URI serviceURI;
    private String serviceTargetNamespace;
    private String serviceName;
    private String servicePortName;

    /**
     * Default constructor which sets the UUID
     */
    public PredictorServiceDescription()
    {
        uuid = UUID.randomUUID();
    }

        /**
     * Constructor to set the basic information about the predictor.
     * @param name
     * @param version
     * @param desc 
     */
    public PredictorServiceDescription(String name, String version, String desc)
    {
        uuid = UUID.randomUUID();
        this.name = name;
        this.version = version;
        this.description = desc;
    }
    
    /**
     * Constructor to set the basic information about the predictor.
     * @param uuid
     * @param name
     * @param version
     * @param desc 
     */
    public PredictorServiceDescription(UUID uuid, String name, String version, String desc)
    {
        this.uuid = uuid;
        this.name = name;
        this.version = version;
        this.description = desc;
    }
    
        /**
     * Constructor to set the basic information about the predictor.
     * @param uuid
     * @param name
     * @param version
     * @param desc 
     */
    public PredictorServiceDescription(UUID uuid, String name, String version, String desc, URI serviceURI)
    {
        this.uuid = uuid;
        this.name = name;
        this.version = version;
        this.description = desc;
        this.serviceURI=serviceURI;
    }
    
    /**
     * Constructor to set all the information of a predictor (except for Service URI).
     * @param uuid
     * @param name
     * @param version
     * @param desc
     * @param configParams
     * @param inputParams 
     */
    public PredictorServiceDescription(UUID uuid, String name, String version, String desc, List<Parameter> configParams, List<Event> events, Parameter forecastPeriod)
    {
        this.uuid = uuid;
        this.name = name;
        this.version = version;
        this.description = desc;
        this.configurationParams = configParams;
        this.events = events;
        this.forecastPeriod = forecastPeriod;
    }
    
    /**
     * Constructor to set all the information of a predictor.
     * @param uuid
     * @param name
     * @param version
     * @param desc
     * @param configParams
     * @param inputParams
     * @param sURI 
     */
    public PredictorServiceDescription(UUID uuid, String name, String version, String desc, List<Parameter> configParams, List<Event> events, Parameter forecastPeriod, URI sURI)
    {
        this.uuid = uuid;
        this.name = name;
        this.version = version;
        this.description = desc;
        this.configurationParams = configParams;
        this.events = events;
        this.forecastPeriod = forecastPeriod;
        this.serviceURI = sURI;
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
    public List<Parameter> getConfigurationParams()
    {
        return configurationParams;
    }

    /**
     * @param configurationParams the configurationParams to set
     */
    public void setConfigurationParams(List<Parameter> configurationParams)
    {
        this.configurationParams = configurationParams;
    }
    
    /**
     * Add configuration parameters.
     * @param params List of Parameter objects.
     */
    public void addConfigurationParams(List<Parameter> params)
    {
        if (params != null) {
            if (configurationParams == null) {
                configurationParams = new ArrayList<Parameter>();
            }

            configurationParams.addAll(params);
        }
    }
    
    /**
     * Add a configuration parameter.
     * @param param A Parameter object.
     */
    public void addConfigurationParam(Parameter param)
    {
        if (configurationParams == null)
            configurationParams = new ArrayList<Parameter>();
        
        configurationParams.add(param);
    }

    /**
     * @return the events
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * @param events the events to set
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }
    
    /**
     * Add events.
     * @param events List of Event objects.
     */
    public void addEvents(List<Event> events)
    {
        if (this.events == null)
            this.events = new ArrayList<Event>();
        
        this.events.addAll(events);
    }
    
    /**
     * Add an event.
     * @param event An Event object.
     */
    public void addEvent(Event event)
    {
        if (this.events == null)
            this.events = new ArrayList<Event>();
        
        this.events.add(event);
    }
    
    /**
     * @return the forecastPeriod
     */
    public Parameter getForecastPeriod() {
        return forecastPeriod;
    }

    /**
     * @param forecastPeriod the forecastPeriod to set
     */
    public void setForecastPeriod(Parameter forecastPeriod) {
        this.forecastPeriod = forecastPeriod;
    }
    
    /**
     * @return the serviceURI
     */
    public URI getServiceURI() {
        return serviceURI;
    }

    /**
     * @param serviceURI the serviceURI to set
     */
    public void setServiceURI(URI serviceURI) {
        this.serviceURI = serviceURI;
    }
 


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 47 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 47 * hash + (this.serviceURI != null ? this.serviceURI.hashCode() : 0);
        hash = 47 * hash + (this.serviceTargetNamespace != null ? this.serviceTargetNamespace.hashCode() : 0);
        hash = 47 * hash + (this.serviceName != null ? this.serviceName.hashCode() : 0);
        hash = 47 * hash + (this.servicePortName != null ? this.servicePortName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PredictorServiceDescription other = (PredictorServiceDescription) obj;
        if (this.uuid != other.uuid && (this.uuid == null || !this.uuid.equals(other.uuid))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version)) {
            return false;
        }
        if (this.serviceURI != other.serviceURI && (this.serviceURI == null || !this.serviceURI.equals(other.serviceURI))) {
            return false;
        }
        if ((this.serviceTargetNamespace == null) ? (other.serviceTargetNamespace != null) : !this.serviceTargetNamespace.equals(other.serviceTargetNamespace)) {
            return false;
        }
        if ((this.serviceName == null) ? (other.serviceName != null) : !this.serviceName.equals(other.serviceName)) {
            return false;
        }
        if ((this.servicePortName == null) ? (other.servicePortName != null) : !this.servicePortName.equals(other.servicePortName)) {
            return false;
        }
        return true;
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
