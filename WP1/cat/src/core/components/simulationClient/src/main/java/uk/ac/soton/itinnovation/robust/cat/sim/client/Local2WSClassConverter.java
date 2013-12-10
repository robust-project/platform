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
//      Created Date :          2013-10-13
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.sim.client;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;

/**
 *
 * @author Vegard Engen
 */
public class Local2WSClassConverter
{
    static Logger log = Logger.getLogger(Local2WSClassConverter.class);
    
	/**
     * Get a WS instance of the predictor service job configuration object from
     * the local object.
     *
     * @param config The local predictor service job configuration object.
     * @return
     */
    public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.SimulationServiceJobConfig getSimulationSServiceJobConfig(uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceJobConfig config)
    {
        if (config == null)
        {
            log.debug("The local SimulationServiceJobConfig object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.SimulationServiceJobConfig jobConfig;
        jobConfig = new uk.ac.soton.itinnovation.robust.cat.sim.client.gen.SimulationServiceJobConfig();
        
        try {
            if (config.getStartDate() != null)
                jobConfig.setStartDate(DateUtil.getXMLGregorianCalendar(config.getStartDate()));
        } catch (Exception ex) {
            log.error("An exception was caught when coverting the start date into an XMLGregorianCalendar format", ex);
        }
		
		try {
            if (config.getEndDate() != null)
                jobConfig.setEndDate(DateUtil.getXMLGregorianCalendar(config.getEndDate()));
        } catch (Exception ex) {
            log.error("An exception was caught when coverting the end date into an XMLGregorianCalendar format", ex);
        }
        
        jobConfig.setCommunityDetails(getCommunityDetails(config.getCommunityDetails()));
        
        if (config.getConfigurationParameters() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.sim.client.gen.Parameter> configParams = new ArrayList<uk.ac.soton.itinnovation.robust.cat.sim.client.gen.Parameter>();
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter param : config.getConfigurationParameters())
                configParams.add(getParameter(param));
            
            jobConfig.setConfigurationParameters(configParams);
        }
        
        return jobConfig;
    }
	
	public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.CommunityDetails getCommunityDetails(uk.ac.soton.itinnovation.robust.cat.common.ps.CommunityDetails cd)
    {
        if (cd == null)
        {
            log.debug("The local CommunityDetails object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.CommunityDetails communityDetails = new uk.ac.soton.itinnovation.robust.cat.sim.client.gen.CommunityDetails();
        
        communityDetails.setCommunityName(cd.getCommunityName());
        communityDetails.setCommunityID(cd.getCommunityID());
        
        return communityDetails;
    }
	
	public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.Parameter getParameter(uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter localParam)
    {
        if (localParam == null)
        {
            log.debug("The local Parameter object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.Parameter param;
        param = new uk.ac.soton.itinnovation.robust.cat.sim.client.gen.Parameter();
        
        param.setUUID(localParam.getUUID().toString());
        param.setName(localParam.getName());
        param.setDescription(localParam.getDescription());
        param.setUnit(localParam.getUnit());
        param.setType(getParameterValueType(localParam.getType()));
        param.setValue(getParameterValue(localParam.getValue()));
        param.setValuesAllowedType(getValuesAllowedType(localParam.getValuesAllowedType()));
        
        // ValueConstraint list
        if (localParam.getValueConstraints() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValueConstraint> valueConstrList;
            valueConstrList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValueConstraint>();
            
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint vc : localParam.getValueConstraints())
                valueConstrList.add(getValueConstraint(vc));
            
            param.setValueConstraints(valueConstrList);
        }
        
        if (localParam.getAllowedEvaluationTypes() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.sim.client.gen.EvaluationType> valueConstrList;
            valueConstrList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.sim.client.gen.EvaluationType>();
            
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType et : localParam.getAllowedEvaluationTypes()) {
                valueConstrList.add(getEvaluationType(et));
            }
            
            param.setAllowedEvaluationTypes(valueConstrList);
        }
        
        return param;
    }
	
	public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ParameterValue getParameterValue(uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue localParameterValue)
    {
        if (localParameterValue == null)
        {
            log.debug("The local ParameterValue object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ParameterValue parameterValue;
        parameterValue = new uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ParameterValue();
        
        parameterValue.setValue(localParameterValue.getValue());
        parameterValue.setValueEvaluationType(getEvaluationType(localParameterValue.getValueEvaluationType()));
        
        return parameterValue;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ParameterValueType getParameterValueType (uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType localParamValueType)
    {
        if (localParamValueType == null)
        {
            log.debug("The local ParameterValueType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ParameterValueType paramValueType;
        paramValueType = uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ParameterValueType.fromValue(localParamValueType.name());
        
        return paramValueType;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValueConstraint getValueConstraint(uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint localValueConstraint)
    {
        if (localValueConstraint == null)
        {
            log.debug("The local ValueConstraint object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValueConstraint valueConstraint;
        valueConstraint = new uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValueConstraint();
        
        valueConstraint.setValue(localValueConstraint.getValue());
        valueConstraint.setConstraintType(getValueConstraintType(localValueConstraint.getConstraintType()));
        
        return valueConstraint;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValueConstraintType getValueConstraintType (uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType localValueConstraintType)
    {
        if (localValueConstraintType == null)
        {
            log.debug("The local ValueConstraintType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValueConstraintType valueConstraintType;
        valueConstraintType = uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValueConstraintType.fromValue(localValueConstraintType.name());
        
        return valueConstraintType;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValuesAllowedType getValuesAllowedType (uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType localValuesAllowedType)
    {
        if (localValuesAllowedType == null)
        {
            log.debug("The local ParameterValueType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValuesAllowedType valuesAllowedType;
        valuesAllowedType = uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValuesAllowedType.fromValue(localValuesAllowedType.name());
        
        return valuesAllowedType;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.EvaluationType getEvaluationType(uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType localEvaluationType)
    {
        if (localEvaluationType == null)
        {
            log.debug("The local ParameterValueType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.EvaluationType evaluationType;
        evaluationType = uk.ac.soton.itinnovation.robust.cat.sim.client.gen.EvaluationType.fromValue(localEvaluationType.name());
        
        return evaluationType;
    }
	
	/**
     * Get a local instance of the job details WS object.
     * @param job Web service job details object.
     * @return 
     */
    public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobDetails getJobDetails(uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails job)
    {
        if (job == null)
        {
            log.debug("The job details object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobDetails jobDetails;
        jobDetails = new uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobDetails();
        
        if (job.getJobRef() != null)
            jobDetails.setJobRef(job.getJobRef());
        
        if (job.getJobStatus() != null)
            jobDetails.setJobStatus(getJobStatus(job.getJobStatus()));
        
        try {
            if (job.getRequestDate() != null) jobDetails.setRequestDate(DateUtil.getXMLGregorianCalendar(job.getRequestDate()));
            if (job.getStartDate() != null) jobDetails.setStartDate(DateUtil.getXMLGregorianCalendar(job.getStartDate()));
            if (job.getCompletionDate() != null) jobDetails.setCompletionDate(DateUtil.getXMLGregorianCalendar(job.getCompletionDate()));
        } catch (Exception ex) {
            log.error("An exception was caught when coverting the job status dates into Date objects", ex);
        }
        
        return jobDetails;
    }
    
    /**
     * Get a local instance of the job status from the WS object.
     * @param job Web service job status object.
     * @return 
     */
    public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobStatus getJobStatus(uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus job)
    {
        if (job == null)
        {
            log.debug("The job status object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobStatus jobStatus;
        jobStatus = new uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobStatus();
        
        if (job.getStatus() != null)
            jobStatus.setStatus(getJobStatusType(job.getStatus()));
        
        if (job.getMetaData() != null)
            jobStatus.setMetaData(job.getMetaData());
        
        return jobStatus;
    }
    
    /**
     * Get a local instance of the status type from the WS object.
     * @param job Web service job status enum.
     * @return 
     */
    public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobStatusType getJobStatusType(uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType job)
    {
        if (job == null)
        {
            log.debug("The job status type object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
		return uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobStatusType.fromValue(job.value());
    }

    /**
     * Get a local instance of the simulation result from the WS object.
     *
     * @param res Web service simulation result object.
     * @return
     */
    public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.SimulationResult getSimulationResult(uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationResult res)
    {
        if (res == null)
        {
            log.debug("The result object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.SimulationResult simResult;
        simResult = new uk.ac.soton.itinnovation.robust.cat.sim.client.gen.SimulationResult();
        
        if (res.getSimulationDate() != null)
        {
            try {
                simResult.setSimulationDate(DateUtil.getXMLGregorianCalendar(res.getSimulationDate()));
            } catch (Exception ex) {
                log.error("An exception was caught when coverting the current date into a Date object", ex);
            }
        }
              
        if (res.getResultGroups() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ResultGroup> resultGroups = new ArrayList<uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ResultGroup>();
            for (uk.ac.soton.itinnovation.robust.cat.common.sim.ResultGroup resGrp : res.getResultGroups())
                resultGroups.add(getResultGroup(resGrp));
            
            simResult.setResultGroups(resultGroups);
        }
        
        simResult.setJobDetails(getJobDetails(res.getJobDetails()));
        
        
        
        return simResult;
    }
    
    /**
     * Get a local instance of the result group from a WS object.
     * @param resGrp The WS ResultGroup object.
     * @return A local instance of a ResultGroup.
     */
    public static uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ResultGroup getResultGroup(uk.ac.soton.itinnovation.robust.cat.common.sim.ResultGroup resGrp)
    {
        if (resGrp == null)
        {
            log.debug("The ResultGroup object from the WS was NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ResultGroup resultGroup;
        resultGroup = new uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ResultGroup();
        
        if (resGrp.getName() != null) {
            resultGroup.setName(resGrp.getName());
        }
		
		if (resGrp.getResults() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.sim.client.gen.KeyValuePair> results = new ArrayList<uk.ac.soton.itinnovation.robust.cat.sim.client.gen.KeyValuePair>();
            
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.KeyValuePair kvp : resGrp.getResults())
            {
                uk.ac.soton.itinnovation.robust.cat.sim.client.gen.KeyValuePair kvp_ws = new uk.ac.soton.itinnovation.robust.cat.sim.client.gen.KeyValuePair();
                kvp_ws.setKey(kvp.getKey());
                kvp_ws.setValue(kvp.getValue());
                results.add(kvp_ws);
            }
            
            resultGroup.setResults(results);
        }

        return resultGroup;
    }

}
