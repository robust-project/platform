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
//      Created Date :          2012-10-25
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec;

import java.io.Serializable;
import java.util.UUID;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails;

/**
 * A Job Details class that adds risk and community UUID in addition to the
 * "standard" JobDetails class in cat common.
 * @author Vegard Engen
 */
public class EEJobDetails extends JobDetails implements Serializable
{
    private UUID riskUUID;
    private UUID communityUUID;
    private boolean streaming;
    private PredictorConnectionDetails predictorConnectionDetails;
    
    public EEJobDetails()
    {
        super();
    }
    
    public EEJobDetails(JobDetails jd)
    {
        super(jd);
    }
    
    public EEJobDetails(UUID riskUUID, UUID communityUUID, boolean streaming, PredictorConnectionDetails psConnectionDetails)
    {
        super();
        this.riskUUID = riskUUID;
        this.communityUUID = communityUUID;
        this.streaming = streaming;
        this.predictorConnectionDetails = psConnectionDetails;
    }
    
    public EEJobDetails(JobDetails jd, UUID riskUUID, UUID communityUUID, boolean streaming, PredictorConnectionDetails psConnectionDetails)
    {
        super(jd);
        this.riskUUID = riskUUID;
        this.communityUUID = communityUUID;
        this.streaming = streaming;
        this.predictorConnectionDetails = psConnectionDetails;
    }

    /**
     * @return the riskUUID
     */
    public UUID getRiskUUID()
    {
        return riskUUID;
    }

    /**
     * @param riskUUID the riskUUID to set
     */
    public void setRiskUUID(UUID riskUUID)
    {
        this.riskUUID = riskUUID;
    }

    /**
     * @return the communityUUID
     */
    public UUID getCommunityUUID()
    {
        return communityUUID;
    }

    /**
     * @param communityUUID the communityUUID to set
     */
    public void setCommunityUUID(UUID communityUUID)
    {
        this.communityUUID = communityUUID;
    }

    /**
     * @return the streaming
     */
    public boolean isStreaming()
    {
        return streaming;
    }

    /**
     * @param streaming the streaming to set
     */
    public void setStreaming(boolean streaming)
    {
        this.streaming = streaming;
    }

    /**
     * @return the predictorConnectionDetails
     */
    public PredictorConnectionDetails getPredictorConnectionDetails()
    {
        return predictorConnectionDetails;
    }

    /**
     * @param predictorConnectionDetails the predictorConnectionDetails to set
     */
    public void setPredictorConnectionDetails(PredictorConnectionDetails predictorConnectionDetails)
    {
        this.predictorConnectionDetails = predictorConnectionDetails;
    }
}
