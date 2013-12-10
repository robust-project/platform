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
//      Created Date :          2012-08-14
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.impl;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.log4j.Logger;
import pl.swmind.robust.streaming.activemq.topic.ActiveMQPublisher;
import pl.swmind.robust.streaming.topic.RobustPublisher;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.StreamDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.spec.EvaluationEngineStatusType;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

/**
 * A class to manage streams and publishing events on the streams set up.
 * Two types of streams are set up, one for general events such as engine starting
 * and stopping. The other type of stream is for evaluation results, and a
 * separate stream is set up for each community.
 * 
 * @author Vegard Engen
 */
public class StreamManager
{
    private Map<String, StreamDetails> streamDetailsMap;
    private Map<UUID, UUID> risk2communityMap;
    private IDataLayer dataLayer;
    boolean streamDetailsInitialised;
    private String generalTopicName = "general";
    
    // parameters set by 'evalEngService.properties'
    private boolean publishToLocalhost;
    private String brokerHostname;
    private int brokerPort;
    
    // broker URI to be set according to the above parameters
    private URI brokerURI;
    private URI localBrokerURI;
    
    static Logger log = Logger.getLogger(StreamManager.class);
    
    public StreamManager()
    {
        streamDetailsMap = new HashMap<String, StreamDetails>();
        risk2communityMap = new HashMap<UUID, UUID>();
        streamDetailsInitialised = false;
    }
    
    public StreamManager(boolean publishToLocalhost, String brokerHostname, int brokerPort, IDataLayer dataLayer) throws Exception
    {
        this();
        this.publishToLocalhost = publishToLocalhost;
        this.brokerHostname = brokerHostname;
        this.brokerPort = brokerPort;
        
        if (setUpBrokerURI(brokerHostname, brokerPort))
            streamDetailsInitialised = true;
        
        this.dataLayer = dataLayer;
        setUpRiskToCommunityMap();
    }
    
    public void init(boolean publishToLocalhost, String brokerHostname, int brokerPort, IDataLayer dataLayer) throws Exception
    {
        this.publishToLocalhost = publishToLocalhost;
        this.brokerHostname = brokerHostname;
        this.brokerPort = brokerPort;
        
        if (setUpBrokerURI(brokerHostname, brokerPort))
            streamDetailsInitialised = true;
        
        this.dataLayer = dataLayer;
        setUpRiskToCommunityMap();
    }
    
    private boolean setUpBrokerURI(String brokerHostname, int brokerPort) throws Exception
    {
        try {
            brokerURI = new URI("tcp://" + brokerHostname + ":" + String.valueOf(brokerPort));
            localBrokerURI = new URI("tcp://localhost:" + String.valueOf(brokerPort));
        } catch (URISyntaxException ex) {
            log.error("Failed to set up Broker URI: " + ex.getMessage());
            throw ex;
        }
        return true;
    }
    
    private void setUpRiskToCommunityMap() throws Exception
    {
        for (Community c : dataLayer.getCommunities())
        {
            for (Risk r : dataLayer.getRisks(c.getUuid()))
            {
                if ((r.getId() != null) && (c.getUuid() != null))
                    risk2communityMap.put(r.getId(), c.getUuid());
            }
        }
    }
    
    private void updateRiskToCommunityMap()
    {
        for (Community c : dataLayer.getCommunities())
        {
            for (Risk r : dataLayer.getRisks(c.getUuid()))
            {
                if (!risk2communityMap.containsKey(r.getId()))
                    risk2communityMap.put(r.getId(), c.getUuid());
            }
        }
    }
    
    private void logRiskToCommunityMap()
    {
        if ((risk2communityMap == null) || risk2communityMap.isEmpty())
            return;
        
        log.debug("Risk to community map:");
        
        for (UUID riskUUID : risk2communityMap.keySet())
        {
            log.debug("  - R [" + riskUUID.toString() + "]\t C [" + risk2communityMap.get(riskUUID) + "]");
        }
    }
    
    public void setUpGeneralStream() throws Exception
    {
        if (!streamDetailsInitialised)
            throw new NullPointerException("The Broker details have not been set, so cannot set up the general stream");
        
        addStream(generalTopicName, brokerURI);
    }
    
    public void setUpCommunityStreams(Set<Community> communities) throws Exception
    {
        if (!streamDetailsInitialised)
            throw new NullPointerException("The Broker details have not been set, so cannot set up the community streams");
        
        if (communities == null)
            throw new NullPointerException("The set of communities is NULL, so cannot set up the community streams");
        
        for (Community c : communities)
        {
            addStream(c.getUuid().toString(), brokerURI);
        }
    }
    
    private boolean isCommunityValid(UUID communityUUID)
    {
        try {
            Community c = dataLayer.getCommunityByUUID(communityUUID);
            if (c != null)
                return true;
            else
                return false;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public void setUpCommunityStream(UUID communityUUID) throws Exception
    {
        if (!streamDetailsInitialised)
        {
            log.error("The Broker details have not been set, so the community stream cannot be created for community: " + communityUUID.toString());
            throw new NullPointerException("The Broker details have not been set, so the community stream cannot be created");
        }
        
        addStream(communityUUID.toString(), brokerURI);
    }
    
    private void addStream(String name, URI brokerURI)
    {
        streamDetailsMap.put(name, new StreamDetails(name, brokerURI));
        log.info("Set up stream: " + streamDetailsMap.get(name));
    }
    
    public StreamDetails getGeneralStreamDetails() throws Exception
    {
        return streamDetailsMap.get(generalTopicName);
    }
    
    public StreamDetails getCommunityResultsStreamDetails(UUID communityUUID) throws Exception
    {
        if (!streamDetailsInitialised)
        {
            log.error("The Broker details have not been set, so the community streams have not been created");
            throw new NullPointerException("The Broker details have not been set, so the community streams have not been created");
        }
        
        if (communityUUID == null)
        {
            log.error("The Community UUID is NULL, can't get any stream details for that...");
            throw new NullPointerException("The Community UUID is NULL, can't get any stream details for that...");
        }
        
        if (!streamDetailsMap.containsKey(communityUUID.toString()))
        {
            if (isCommunityValid(communityUUID))
            {
                setUpCommunityStream(communityUUID);
                this.updateRiskToCommunityMap();
            }
            else
            {
                log.error("There is no community with the given UUID (" + communityUUID.toString() + "), so no stream available!");
                throw new RuntimeException("There is no community with the given UUID (" + communityUUID.toString() + "), so no stream available!");
            }
        }
        
        return streamDetailsMap.get(communityUUID.toString());
    }
    
    public void publishEngineStatus(EvaluationEngineStatusType status) throws Exception
    {
        publish(status, streamDetailsMap.get(generalTopicName));
    }
    
    public void publishEvaluationResult(EvaluationResult evalRes) throws Exception
    {
        UUID riskUUID = evalRes.getRiskUUID();
        UUID communityUUID = null;
        
        if (risk2communityMap.containsKey(riskUUID)) {
            communityUUID = risk2communityMap.get(riskUUID);
        }
        
        if (communityUUID == null)
        {
            updateRiskToCommunityMap();
            logRiskToCommunityMap();
        }
        
        if (risk2communityMap.containsKey(riskUUID)) {
            communityUUID = risk2communityMap.get(riskUUID);
        }
        
        if (communityUUID == null)
        {
            log.error("Unable to get the community UUID for the risk with UUID: " + riskUUID.toString());
            return;
        }
        
        if (streamDetailsMap.get(communityUUID.toString()) == null) {
            setUpCommunityStream(communityUUID);
        }
        
        publish(evalRes, streamDetailsMap.get(communityUUID.toString()));
    }
    
    private void publish(Object obj, StreamDetails sd) throws Exception
    {
        if (obj == null)
        {
            log.error("The object to be streamed is NULL");
            return;
        }
        
        if (sd == null)
        {
            log.error("The Stream Details object is NULL");
        }
        
        String streamName = sd.getStreamName();
        String streamURL = null;
        if (publishToLocalhost)
            streamURL = localBrokerURI.toString();
        else
            streamURL = sd.getStreamURI().toString();
        
        try {
            //Thread pThread = new Thread(new PublisherThread(streamName, streamURL, obj));
            //pThread.start();
            PublisherThread pt = new PublisherThread(streamName, streamURL, obj);
            pt.publish();
        } catch (Exception ex) {
            log.error("Failed to publish to " + streamName + ". Error msg: " + ex.getMessage());
        }
    }
    
    public void setDataLayer(IDataLayer dataLayer)
    {
        this.dataLayer = dataLayer;
    }
    
    class PublisherThread implements Runnable
    {
        String streamName;
        String streamURL;
        Object obj;
        boolean init = false;
        
        public PublisherThread(String streamName, String streamURL, Object obj)
        {
            this.streamName = streamName;
            this.streamURL = streamURL;
            this.obj = obj;
            
            if ( (streamName != null) && (streamURL != null) && (obj != null))
                init = true;
        }
        
        @Override
        public void run()
        {
            publish();
        }
        
        public void publish()
        {
            if (!init)
                return;
            
            RobustPublisher publisher = null;
            try {
                log.debug("Publishing message on ActiveMQ");
                log.debug("  - " + streamName + " @ " + streamURL);
                if (obj instanceof EvaluationEngineStatusType) {
                    log.debug("  - " + obj.getClass().getSimpleName() + ": " + ((EvaluationEngineStatusType)obj).name());
                } else {
                    log.debug("  - " + obj.getClass().getSimpleName());
                }
                publisher = new ActiveMQPublisher(streamName, streamURL);
                publisher.publish((Serializable)obj);
            } catch (Exception ex) {
                log.error("Failed to publish to " + streamURL + ". Error msg: " + ex.toString(), ex);
            } finally {
                if (publisher != null) {
                    try { publisher.close(); } catch (Exception ex2) { }
                }
            }
        }
    }
}


