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

import java.net.URI;
import java.util.*;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.StreamDetails;

/**
 *
 * @author Vegard Engen
 */
public class PredictorServiceJobConfig
{
    private List<Parameter> configurationParams;
    private Set<Event> events;
    private CommunityDetails communityDetails; // change to SIOC stuff when available
    private Date startDate;
    private Parameter forecastPeriod; // OBS: should use unit of days!
    private boolean streaming;
    private StreamDetails streamDetails;
    private URI evaluationEngineServiceURI;

    /**
     * Default constructor which sets the isStreaming variable to false.
     */
    public PredictorServiceJobConfig()
    {
        streaming = false;
        configurationParams = new ArrayList<Parameter>();
        events = new HashSet<Event>();
    }
    
    /**
     * Constructor to set the basic information of a job configuration.
     * PS: assumes that the job is not for streaming data.
     * @param date
     * @param forecastPeriod
     * @param configParams
     * @param events
     * @param communityDetails
     */
    public PredictorServiceJobConfig(Date date, Parameter forecastPeriod, List<Parameter> configParams, Set<Event> events, CommunityDetails communityDetails)
    {
        this();
        this.startDate = date;
        this.forecastPeriod = forecastPeriod;
        this.configurationParams = configParams;
        this.events = events;
        this.communityDetails = communityDetails;
    }
    
    /**
     * Constructor to set all the information of a job configuration.
     * @param date
     * @param forecastPeriod
     * @param configParams
     * @param events
     * @param communityDetails
     * @param isStreaming
     * @param streamDetails
     */
    public PredictorServiceJobConfig(Date date, Parameter forecastPeriod, List<Parameter> configParams, Set<Event> events, CommunityDetails communityDetails, boolean isStreaming, StreamDetails streamDetails)
    {
        this(date, forecastPeriod, configParams, events, communityDetails);
        this.streaming = isStreaming;
        this.streamDetails = streamDetails;
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
        if (configurationParams == null)
            setConfigurationParams(new ArrayList<Parameter>());
        
        configurationParams.addAll(params);
    }
    
    /**
     * Add a configuration parameter.
     * @param param A Parameter object.
     */
    public void addConfigurationParam(Parameter param)
    {
        if (configurationParams == null)
            setConfigurationParams(new ArrayList<Parameter>());
        
        configurationParams.add(param);
    }

    /**
     * @return the event
     */
    public Set<Event> getEvents() {
        return events;
    }

    /**
     * @param event the event to set
     */
    public void setEvents(Set<Event> events) {
        this.events = events;
    }
    
    /**
     * @param evt The event to add to the event list
     */
    public void addEvent(Event evt)
    {
        if (events == null)
            events = new HashSet<Event>();
        
        events.add(evt);
    }
    
    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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
     * @return the streaming boolean
     */
    public boolean isStreaming()
    {
        return streaming;
    }

    /**
     * @param isStreaming the streaming boolean to set
     */
    public void setStreaming(boolean isStreaming)
    {
        this.streaming = isStreaming;
    }

    /**
     * @return the streamDetails
     */
    public StreamDetails getStreamDetails()
    {
        return streamDetails;
    }

    /**
     * @param streamDetails the streamDetails to set
     */
    public void setStreamDetails(StreamDetails streamDetails)
    {
        this.streamDetails = streamDetails;
    }

    /**
     * @return the evaluationEngineServiceURI
     */
    public URI getEvaluationEngineServiceURI()
    {
        return evaluationEngineServiceURI;
    }

    /**
     * @param evaluationEngineServiceURI the evaluationEngineServiceURI to set
     */
    public void setEvaluationEngineServiceURI(URI evaluationEngineServiceURI)
    {
        this.evaluationEngineServiceURI = evaluationEngineServiceURI;
    }
}
