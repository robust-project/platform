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
//      Created Date :          2012-10-26
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;
import uk.ac.soton.itinnovation.robust.riskmodel.Period;

/**
 * A class detailing the review schedule of a risk and when it has been evaluated.
 * 
 * @author Vegard Engen
 */
public class RiskEvaluationDetails
{
    private UUID riskUUID;
    private Period evaluationPeriod;
    private int evaluationFrequency;
    private Date lastEvaluatedDate;
    
    private Map<Period, Long> periodMilliSecMapping;

    /**
     * Default constructor, which initialises a mapping of Period to milliseconds.
     */
    public RiskEvaluationDetails()
    {
        periodMilliSecMapping = new HashMap<Period, Long>();
        periodMilliSecMapping.put(Period.HOUR,  3600000L);
        periodMilliSecMapping.put(Period.DAY,   86400000L);
        periodMilliSecMapping.put(Period.WEEK,  604800000L);
        // for MONTH and YEAR, will use calendar functionality as it will vary according to month...
        periodMilliSecMapping.put(Period.MONTH, 2630000000L);
        periodMilliSecMapping.put(Period.YEAR,  3156000000L);
    }
    
    /**
     * Copy constructor.
     * @param rrd The object this should be a copy of.
     */
    public RiskEvaluationDetails(RiskEvaluationDetails rrd)
    {
        this();
        
        if (rrd == null) {
            return;
        }
        
        this.riskUUID = rrd.getRiskUUID();
        this.evaluationPeriod = rrd.getEvaluationPeriod();
        this.evaluationFrequency = rrd.getEvaluationFrequency();
        this.lastEvaluatedDate = rrd.getLastEvaluatedDate();
    }
    
    /**
     * Constructor to set the review parameters.
     * @param riskUUID The risk UUID.
     * @param reviewPeriod The risk review period (day, week, month, etc)
     * @param reviewFrequency The risk review frequency; a multiplier of the review period, to define, e.g., 1 day.
     */
    public RiskEvaluationDetails(UUID riskUUID, Period reviewPeriod, int reviewFrequency)
    {
        this();
        this.riskUUID = riskUUID;
        this.evaluationPeriod = reviewPeriod;
        this.evaluationFrequency = reviewFrequency;
    }
    
    /**
     * Constructor to set the review parameters.
     * @param riskUUID The risk UUID.
     * @param reviewPeriod The risk review period (day, week, month, etc)
     * @param reviewFrequency The risk review frequency; a multiplier of the review period, to define, e.g., 1 day.
     * @param lastEvaluatedDate The date the risk was last evaluated.
     */
    public RiskEvaluationDetails(UUID riskUUID, Period reviewPeriod, int reviewFrequency, Date lastEvaluatedDate)
    {
        this(riskUUID, reviewPeriod, reviewFrequency);
        this.lastEvaluatedDate = lastEvaluatedDate;
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
     * @return the evaluationPeriod
     */
    public Period getEvaluationPeriod()
    {
        return evaluationPeriod;
    }
    
    /**
     * Note that if the Period is MONTH or YEAR, this will not be accurate as the
     * number of milliseconds in these cases depends on what month it is and whether
     * it is a leap year or not.
     * @return the evaluation period in milliseconds
     */
    public long getEvaluationPeriodInMilliSeconds()
    {
        if (evaluationPeriod == null) {
            return -1;
        }
        
        return periodMilliSecMapping.get(evaluationPeriod) * evaluationFrequency;
    }

    /**
     * @param evaluationPeriod the evaluationPeriod to set
     */
    public void setEvaluationPeriod(Period evaluationPeriod)
    {
        this.evaluationPeriod = evaluationPeriod;
    }

    /**
     * @return the evaluationFrequency
     */
    public int getEvaluationFrequency()
    {
        return evaluationFrequency;
    }

    /**
     * @param evaluationFrequency the evaluationFrequency to set
     */
    public void setEvaluationFrequency(int evaluationFrequency)
    {
        this.evaluationFrequency = evaluationFrequency;
    }

    /**
     * @return the lastEvaluatedDate
     */
    public Date getLastEvaluatedDate()
    {
        return lastEvaluatedDate;
    }

    /**
     * @param lastEvaluatedDate the lastEvaluatedDate to set
     */
    public void setLastEvaluatedDate(Date lastEvaluatedDate)
    {
        this.lastEvaluatedDate = lastEvaluatedDate;
    }
    
    public boolean isRiskDueEvaluation(Date currentDate)
    {
        if (this.lastEvaluatedDate == null) {
            return true;
        }
        
        Date nextEvalDate = null;
        
        // if MONTH or YEAR, use calendar logic
        if (this.evaluationPeriod == Period.MONTH)
        {
            nextEvalDate = DateUtil.getToDate(this.getLastEvaluatedDate(), FrequencyType.MONTHLY, this.evaluationFrequency);
        }
        else if (this.evaluationPeriod == Period.YEAR)
        {
            nextEvalDate = DateUtil.getToDate(this.getLastEvaluatedDate(), FrequencyType.ANNUALLY, this.evaluationFrequency);
        }
        else // HOUR/DAY/WEEK:
        {
            nextEvalDate = new Date(this.getLastEvaluatedDate().getTime() + this.getEvaluationPeriodInMilliSeconds());
        }
        
        // if less than a minute a part then ok
        if ( (nextEvalDate.getTime() - currentDate.getTime()) < 60000) {
            return true;
        }
        
        return false;
    }
}
