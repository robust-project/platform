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
import java.util.UUID;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;

/**
 * @author Vegard Engen
 */
public class WS2LocalClassConverter
{
    private static final Logger log = Logger.getLogger(WS2LocalClassConverter.class);
    
	/**
     * Get a local instance of the simulation service description from the WS
     * object.
     *
     * @param desc Web service simulation service description object.
     * description.
     * @return
     */
    public static uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceDescription getSimulationServiceDescription(uk.ac.soton.itinnovation.robust.cat.sim.client.gen.SimulationServiceDescription desc)
    {
        if (desc == null)
        {
            log.debug("The SimulationServiceDescription object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceDescription simServiceDesc;
        simServiceDesc = new uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationServiceDescription();

        simServiceDesc.setUuid(UUID.fromString(desc.getUuid()));
        simServiceDesc.setName(desc.getName());
        simServiceDesc.setDescription(desc.getDescription());
        simServiceDesc.setVersion(desc.getVersion());
        
        if (desc.getConfigurationParameters() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter> paramList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter>();
            
            for (uk.ac.soton.itinnovation.robust.cat.sim.client.gen.Parameter p : desc.getConfigurationParameters())
                paramList.add(getParameter(p));
            
            simServiceDesc.setConfigurationParameters(paramList);
        }

        return simServiceDesc;
    }
    
    /**
     * Get a local instance of the job details WS object.
     * @param job Web service job details object.
     * @return 
     */
    public static uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails getJobDetails(uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobDetails job)
    {
        if (job == null)
        {
            log.debug("The job details object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails jobDetails;
        jobDetails = new uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails();
        
        if (job.getJobRef() != null)
            jobDetails.setJobRef(job.getJobRef());
        
        if (job.getJobStatus() != null)
            jobDetails.setJobStatus(getJobStatus(job.getJobStatus()));
        
        try {
            if (job.getRequestDate() != null) jobDetails.setRequestDate(DateUtil.getDate(job.getRequestDate()));
            if (job.getStartDate() != null) jobDetails.setStartDate(DateUtil.getDate(job.getStartDate()));
            if (job.getCompletionDate() != null) jobDetails.setCompletionDate(DateUtil.getDate(job.getCompletionDate()));
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
    public static uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus getJobStatus(uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobStatus job)
    {
        if (job == null)
        {
            log.debug("The job status object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus jobStatus;
        jobStatus = new uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus();
        
        if (getJobStatusType(job.getStatus()) != null)
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
    public static uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType getJobStatusType(uk.ac.soton.itinnovation.robust.cat.sim.client.gen.JobStatusType job)
    {
        if (job == null)
        {
            log.debug("The job status type object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        return uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType.fromValue(job.value());
    }

    /**
     * Get a local instance of the simulation result from the WS object.
     *
     * @param res Web service simulation result object.
     * @return
     */
    public static uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationResult getSimulationResult(uk.ac.soton.itinnovation.robust.cat.sim.client.gen.SimulationResult res)
    {
        if (res == null)
        {
            log.debug("The result object from the WS is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationResult simResult;
        simResult = new uk.ac.soton.itinnovation.robust.cat.common.sim.SimulationResult();
        
        if (res.getSimulationDate() != null)
        {
            try {
                simResult.setSimulationDate(DateUtil.getDate(res.getSimulationDate()));
            } catch (Exception ex) {
                log.debug("An exception was caught when coverting the simulation date into a Date object", ex);
            }
        }
        
        if (res.getResultGroups() != null)
        {
            for (uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ResultGroup resGrp : res.getResultGroups()) {
                simResult.addResultGroup(getResultGroup(resGrp));
            }
        }
        
        simResult.setJobDetails(getJobDetails(res.getJobDetails()));
        
        return simResult;
    }
    
    /**
     * Get a local instance of the result group from a WS object.
     * @param resGrp The WS ResultGroup object.
     * @return A local instance of a ResultGroup.
     */
    public static uk.ac.soton.itinnovation.robust.cat.common.sim.ResultGroup getResultGroup(uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ResultGroup resGrp)
    {
        if (resGrp == null)
        {
            log.debug("The ResultGroup object from the WS was NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.sim.ResultGroup resultGroup;
        resultGroup = new uk.ac.soton.itinnovation.robust.cat.common.sim.ResultGroup();
        
        if (resGrp.getName() != null) {
            resultGroup.setName(resGrp.getName());
        }
		
		if (resGrp.getResults() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.KeyValuePair> results = new ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.KeyValuePair>();
            
            for (uk.ac.soton.itinnovation.robust.cat.sim.client.gen.KeyValuePair kvp : resGrp.getResults())
            {
                uk.ac.soton.itinnovation.robust.cat.common.datastructures.KeyValuePair kvp_local = new uk.ac.soton.itinnovation.robust.cat.common.datastructures.KeyValuePair();
                kvp_local.setKey(kvp.getKey());
                kvp_local.setValue(kvp.getValue());
                results.add(kvp_local);
            }
            
            resultGroup.setResults(results);
        }

        return resultGroup;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter getParameter(uk.ac.soton.itinnovation.robust.cat.sim.client.gen.Parameter wsParam)
    {
        if (wsParam == null)
        {
            log.debug("The WS Parameter object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter param;
        param = new uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter();
        
        param.setUUID(UUID.fromString(wsParam.getUUID()));
        param.setName(wsParam.getName());
        param.setDescription(wsParam.getDescription());
        param.setUnit(wsParam.getUnit());
        param.setType(getParameterValueType(wsParam.getType()));
        param.setValue(getParameterValue(wsParam.getValue()));
        param.setValuesAllowedType(getValuesAllowedType(wsParam.getValuesAllowedType()));
        
        // ValueConstraint list
        if (wsParam.getValueConstraints() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint> valueConstrList;
            valueConstrList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint>();
            
            for (uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValueConstraint vc : wsParam.getValueConstraints())
                valueConstrList.add(getValueConstraint(vc));
            
            param.setValueConstraints(valueConstrList);
        }
        
        if (wsParam.getAllowedEvaluationTypes() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType> evaluationTypeList;
            evaluationTypeList = new ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType>();
            
            for (uk.ac.soton.itinnovation.robust.cat.sim.client.gen.EvaluationType et : wsParam.getAllowedEvaluationTypes()) {
                evaluationTypeList.add(getEvaluationType(et));
            }
            
            param.setAllowedEvaluationTypes(evaluationTypeList);
        }
        
        return param;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue getParameterValue(uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ParameterValue wsParameterValue)
    {
        if (wsParameterValue == null)
        {
            log.debug("The WS ParameterValue object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue parameterValue;
        parameterValue = new uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue();
        
        parameterValue.setValue(wsParameterValue.getValue());
        parameterValue.setValueEvaluationType(getEvaluationType(wsParameterValue.getValueEvaluationType()));
        
        return parameterValue;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType getParameterValueType (uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ParameterValueType wsParamValueType)
    {
        if (wsParamValueType == null)
        {
            log.debug("The WS ParameterValueType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType paramValueType;
        paramValueType = uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType.fromValue(wsParamValueType.name());
        
        return paramValueType;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint getValueConstraint(uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValueConstraint wsValueConstraint)
    {
        if (wsValueConstraint == null)
        {
            log.debug("The WS ValueConstraint object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint valueConstraint;
        valueConstraint = new uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint();
        
        valueConstraint.setValue(wsValueConstraint.getValue());
        valueConstraint.setConstraintType(getValueConstraintType(wsValueConstraint.getConstraintType()));
        
        return valueConstraint;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType getValueConstraintType (uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValueConstraintType wsValueConstraintType)
    {
        if (wsValueConstraintType == null)
        {
            log.debug("The WS ValueConstraintType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType valueConstraintType;
        valueConstraintType = uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType.fromValue(wsValueConstraintType.name());
        
        return valueConstraintType;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType getValuesAllowedType (uk.ac.soton.itinnovation.robust.cat.sim.client.gen.ValuesAllowedType wsValuesAllowedType)
    {
        if (wsValuesAllowedType == null)
        {
            log.debug("The WS ParameterValueType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType valuesAllowedType;
        valuesAllowedType = uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType.fromValue(wsValuesAllowedType.name());
        
        return valuesAllowedType;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType getEvaluationType(uk.ac.soton.itinnovation.robust.cat.sim.client.gen.EvaluationType wsEvaluationType)
    {
        if (wsEvaluationType == null)
        {
            log.debug("The WS ParameterValueType object is NULL - which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType evaluationType;
        evaluationType = uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType.fromValue(wsEvaluationType.name());
        
        return evaluationType;
    }
}
