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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.KeyValuePair;

/**
 * An Evaluation Result class, which encompasses the results produced by
 * a Predictor Service after finishing an evaluation job. 
 * @author Vegard Engen
 */
public class EvaluationResult implements Serializable
{
    private UUID resultUUID;
    private UUID riskUUID;
    private Date currentDate; // to be set by Predictor Service
    private Date forecastDate; // to be set by Predictor Service
    private JobDetails jobDetails; // to be set by Predictor Service
    private List<ResultItem> resultItems; // to be set by Predictor Service
    private List<KeyValuePair> metaData; // to be set by Predictor Service - used instead of Map<String, String> due to WS
    
    public EvaluationResult()
    {
        this.resultUUID = UUID.randomUUID();
        this.metaData = new ArrayList<KeyValuePair>();
    }
    
    public EvaluationResult(UUID resultuuid, Date currentDate, Date forecastDate, List<ResultItem> resultItems)
    {
        this();
        this.resultUUID = resultuuid;
        this.currentDate = currentDate;
        this.forecastDate = forecastDate;
        this.resultItems = resultItems;
    }

    public EvaluationResult(UUID resultuuid, Date currentDate, Date forecastDate, List<ResultItem> resultItems, List<KeyValuePair> metaData)
    {
        this(resultuuid, currentDate, forecastDate, resultItems);
        this.metaData = metaData;
    }
    
    public EvaluationResult(JobDetails jobDetails, Date currentDate, Date forecastDate, List<ResultItem> resultItems)
    {
        this();
        this.jobDetails = jobDetails;
        this.currentDate = currentDate;
        this.forecastDate = forecastDate;
        this.resultItems = resultItems;
    }
    
    public EvaluationResult(JobDetails jobDetails, Date currentDate, Date forecastDate, List<ResultItem> resultItems, List<KeyValuePair> metaData)
    {
        this(jobDetails, currentDate, forecastDate, resultItems);
        this.metaData = metaData;
    }

    /**
     * @return the Risk UUID
     */
    public UUID getRiskUUID() {
        return riskUUID;
    }

    /**
     * Set the Risk UUID
     * @param riskuuid the Risk UUID to set
     */
    public void setRiskUUID(UUID riskuuid) {
        this.riskUUID = riskuuid;
    }

    /**
     * @return the EvaluationResult UUID
     */
    public UUID getResultUUID() {
        return resultUUID;
    }

    /**
     * Set the EvaluationResult UUID
     * @param uuid the EvaluationResult UUID to set
     */
    public void setResultUUID(UUID uuid) {
        this.resultUUID = uuid;
    }

    /**
     * @return the jobDetails
     */
    public JobDetails getJobDetails() {
        return jobDetails;
    }

    /**
     * @param jobDetails the JobDetails to set
     */
    public void setJobDetails(JobDetails jobDetails) {
        this.jobDetails = jobDetails;
    }

    /**
     * @return the resultItems
     */
    public List<ResultItem> getResultItems() {
        return resultItems;
    }

    /**
     * @param resultItems the resultItems to set
     */
    public void setResultItems(List<ResultItem> resultItems) {
        this.resultItems = resultItems;
    }

    /**
     * @return the metaData
     */
    public List<KeyValuePair> getMetaData() {
        if (metaData == null) {
            this.metaData = new ArrayList<KeyValuePair>();
        }
        return metaData;
    }

    /**
     * @param metaData the metaData to set
     */
    public void setMetaData(List<KeyValuePair> metaData) {
        if (metaData != null) {
            this.metaData = metaData;
        }
    }
    
    /**
     * @param metaData the metaData to set
     */
    public void setMetaData(Map<String, String> metaData) {
        if (metaData != null) {
            if (!this.metaData.isEmpty()) {
                this.metaData.clear();
            }
            addMetaData(metaData);
        }
    }
    
    /**
     * @param metaData the metaData to set
     */
    public void addMetaData(String key, String value) {
        if (metaData == null) {
            this.metaData = new ArrayList<KeyValuePair> ();
        }
        this.metaData.add(new KeyValuePair(key, value));
    }
    
    /**
     * @param metaData the metaData to set
     */
    public void addMetaData(List<KeyValuePair> metaData) {
        if (this.metaData == null) {
            this.metaData = new ArrayList<KeyValuePair>();
        }
        if (metaData != null) {
            this.metaData.addAll(metaData);
        }
    }
    
    /**
     * @param metaData the metaData to set
     */
    public void addMetaData(Map<String, String> metaData) {
        if (this.metaData == null) {
            this.metaData = new ArrayList<KeyValuePair>();
        }
        if (metaData != null)
        {
            for (String key : metaData.keySet()) {
                this.metaData.add(new KeyValuePair(key, metaData.get(key)));
            }
        }
    }
    
    /**
     * @param resultItem the ResultItem to add
     */
    public void addResultItem(ResultItem resultItem) {
        if (this.resultItems == null) {
            this.resultItems = new ArrayList<ResultItem>();
        }
        
        this.resultItems.add(resultItem);
    }
    
    /**
     * @param resultItem the ResultItem to add
     */
    public void addResultItems(List<ResultItem> resultItems) {
        if (this.resultItems == null) {
            this.setResultItems(new ArrayList<ResultItem>());
        }
        
        if(resultItems != null) {
            this.resultItems.addAll(resultItems);
        }
    }
    
    /**
     * @return the forecastDate
     */
    public Date getForecastDate() {
        return forecastDate;
    }

    /**
     * @param forecastDate the forecastDate to set
     */
    public void setForecastDate(Date forecastDate) {
        this.forecastDate = forecastDate;
    }

    /**
     * @return the currentDate
     */
    public Date getCurrentDate()
    {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(Date currentDate)
    {
        this.currentDate = currentDate;
    }
}