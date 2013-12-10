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
import java.util.Date;

/**
 *
 * @author Vegard Engen
 */
public class JobDetails implements Serializable
{
    private String jobRef;
    private JobStatus jobStatus;
    private Date jobRequestDate;
    private Date evaluationStartDate;
    private Date completionDate;
    
    public JobDetails(){}
    
    public JobDetails(JobDetails jd)
    {
        if (jd == null)
            return;
        
        this.jobRef = jd.getJobRef();
        this.jobStatus = new JobStatus(jd.getJobStatus());
        if (jd.getRequestDate() != null)
            this.jobRequestDate = new Date(jd.getRequestDate().getTime());
        if (jd.getStartDate() != null)
            this.evaluationStartDate = new Date(jd.getStartDate().getTime());
        if (jd.getCompletionDate() != null)
            this.completionDate = new Date(jd.getCompletionDate().getTime());
    }
    
    public JobDetails(String jobRef)
    {
        this.jobRef = jobRef;
    }
    
    public JobDetails(String jobRef, JobStatus jobStatus, Date requestDate)
    {
        this(jobRef);
        this.jobStatus = jobStatus;
        this.jobRequestDate = requestDate;
    }
    
    public JobDetails(String jobRef, JobStatus jobStatus, Date requestDate, Date startDate)
    {
        this(jobRef, jobStatus, requestDate);
        this.evaluationStartDate = startDate;
    }
    
    public JobDetails(String jobRef, JobStatus jobStatus, Date requestDate, Date startDate, Date completionDate)
    {
        this(jobRef, jobStatus, requestDate, startDate);
        this.completionDate = completionDate;
    }

    /**
     * @return the jobRef
     */
    public String getJobRef() {
        return jobRef;
    }

    /**
     * @param jobRef the jobRef to set
     */
    public void setJobRef(String jobRef) {
        this.jobRef = jobRef;
    }

    /**
     * @return the requestDate
     */
    public Date getRequestDate() {
        return jobRequestDate;
    }

    /**
     * @param requestDate the requestDate to set
     */
    public void setRequestDate(Date requestDate) {
        this.jobRequestDate = requestDate;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return evaluationStartDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.evaluationStartDate = startDate;
    }

    /**
     * @return the completionDate
     */
    public Date getCompletionDate() {
        return completionDate;
    }

    /**
     * @param completionDate the completionDate to set
     */
    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    /**
     * @return the jobStatus
     */
    public JobStatus getJobStatus() {
        return jobStatus;
    }

    /**
     * @param jobStatus the jobStatus to set
     */
    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }
    
    
}
