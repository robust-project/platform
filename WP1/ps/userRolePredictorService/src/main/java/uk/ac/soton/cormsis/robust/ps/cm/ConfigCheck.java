/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton
// CORMSIS
// Centre of Operational Research, Management Science and Information Systems
// 2012
//
// Copyright in this software belongs to University of Southampton
// CORMSIS,
// University of Soutampton,
// Highfield Campus,
// Southampton,
// SO17 1BJ,
// UK
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
//      Created By :            Edwin Tye
//      Created Date :          2012-10-26
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.ps.cm;

import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceJobConfig;

/**
 *
 * @author Edwin
 */
public class ConfigCheck {

    static Logger log = Logger.getLogger(ConfigCheck.class);

    static public boolean checkJobValid(PredictorServiceJobConfig jobConfig,
            PredictorServiceDescription desc) throws Exception {

        // Currently there are no mechanism that checks whether the jobConfig that is being
        // entered are for which particular description
        try { // begins everything
            log.debug("Beginning to check the configurations from the jobConfig against the description");
            // The parameter that is being read from the jobConfig
            Parameter forecastPeriod = jobConfig.getForecastPeriod();
            // The forecasting value that was entered by the end user that needs to be verified
            int forecastValue = Integer.valueOf(forecastPeriod.getValue().getValue());

            /* This is the parameter that is being read from the original service description
             * Can be tested outside of the loop because forecastPeriod is not limited to the particular event 
             * but to the whole PredictorService
             */
            Parameter descForecastPeriod = desc.getForecastPeriod();

            /* Using the isSet() method which basically does the check .isEmpty() and == null
             */
            if (forecastPeriod.getValue().isSet()) {
                if (descForecastPeriod.getMax().isEmpty() || descForecastPeriod.getMax() == null) {
                    log.debug("No max value was set");
                } else {
                    int forecastMax = Integer.valueOf(descForecastPeriod.getMax());
                    if (forecastValue > forecastMax) {
                        log.error("forecast period not allowed");
                        new JobStatus(JobStatusType.ERROR, "forecast period not allowed");
                        return false;
                    }
                }

                if (descForecastPeriod.getMin().isEmpty() || descForecastPeriod.getMin() == null) {
                    log.debug("No min value was set");
                } else {
                    int forecastMin = Integer.valueOf(descForecastPeriod.getMin());
                    if (forecastValue < forecastMin) {
                        new JobStatus(JobStatusType.ERROR, "forecast period not allowed");
                        log.error("forecast period not allowed");
                        return false;
                    }
                }
            } else {
                // Fails the test  *if (forecastPeriod.getValue().isSet()) *
                log.error("The forecast period is not set");
                throw new IllegalArgumentException("The Parameter forecastPeriod is not set");
            }

            log.debug(" We are here! testing the validity of events");
            // Begin looping the events for each of the events from the jobConfig
            for (Event jobConfigEvent : jobConfig.getEvents()) {
                String strTitle = jobConfigEvent.getTitle();
                log.debug("    Event: " + strTitle);
                boolean validEvent = false;
                // Going through the list of available events in the description and check whether any one of them matches the jobConfig event name
                for (Event descEvent : desc.getEvents()) {
                    if (strTitle.equals(descEvent.getTitle())) {
                        validEvent = true;
                        log.debug("We got the correct title");
                        log.debug("Lets check the jobConfig param");
                        for (Parameter configParamEvent : jobConfigEvent.getConfigParams()) {
                            log.info("Name of the event parameter = " + configParamEvent.getName());
                            if (configParamEvent.isValueSet()) {
                                boolean validParam = false;
                                for (Parameter descParamEvent : descEvent.getConfigParams()) {
                                    if (descParamEvent.getName().equals(configParamEvent.getName())) {
                                        log.debug("Found an equal name in the description");
                                        for (ValueConstraint descValueConstraint : descParamEvent.getValueConstraints()) {
                                            ValueConstraintType constraintType = descValueConstraint.getConstraintType();
                                            if (constraintType.value().equals("EXACT")) {
                                                if (descValueConstraint.getValue().equals(configParamEvent.getValue().getValue())) {
                                                    validParam = true;
                                                } else {
                                                    log.error("Parameter input value does not match up exactly");
                                                    new JobStatus(JobStatusType.ERROR, "Parameter input value does not match up exactly");
                                                    // validParameter = false;
                                                    return false;
                                                }
                                            } else if (constraintType.value().equals("MAX")) {

                                                if (Double.valueOf(descValueConstraint.getValue()) <= Double.valueOf(configParamEvent.getValue().getValue())) {
                                                    validParam = true;
                                                } else {
                                                    log.error("Parameter input value over the max value");
                                                    new JobStatus(JobStatusType.ERROR, "Parameter input value over the max value");
                                                    // validParameter = false;
                                                    return false;
                                                }
                                            } else if (constraintType.value().equals("MIN")) {
                                                log.debug("Value of min" + Double.valueOf(configParamEvent.getValue().getValue()));
                                                if (Double.valueOf(descValueConstraint.getValue()) >= Double.valueOf(configParamEvent.getValue().getValue())) {
                                                    validParam = true;
                                                } else {
                                                    log.error("Parameter input value over the min value");
                                                    new JobStatus(JobStatusType.ERROR, "Parameter input value over the min value");
                                                    // validParameter = false;
                                                    return false;
                                                }
                                            } else if (constraintType.value().equals("VALUESALLOWED")) {
                                                log.debug("Type of constraint = VALUESALLOWED");
                                                log.debug("desc = " + descValueConstraint.getValue() + " and input = " + configParamEvent.getValue().getValue());
                                                if (descValueConstraint.getValue().equals(configParamEvent.getValue().getValue())) {
                                                    validParam = true;
                                                }
                                                // if it doesn't match up to any of the allowed value, then validParameter will remain false which return an error
                                            } else {
                                                // it doesn't match up to the four different type of constraint... we have no flipping idea what to do now
                                                // the other types of constraint can be STEP or DEFAULT							
                                            }
                                        }
                                    }
                                }
                                if (validParam) {
                                } else {
                                    log.error("Parameter in Event is not valid");
                                    new JobStatus(JobStatusType.ERROR, "Parameter in Event is not valid");
                                    return false;
                                }
                            } else {
                                log.error("Event Parameter value not set for: " + configParamEvent.getName());
                                new JobStatus(JobStatusType.ERROR, "Event Parameter value not set for: " + configParamEvent.getName());
                                return false;
                            }
                        }

                        // looping through the job event conditions
                        /* TODO: Do we need to go through and find all the event conditions?  Not sure why we need to go through the list of event 
                         * in order to find out what the PostCondition value is
                         */
                        for (EventCondition jobConfigEventCondition : jobConfigEvent.getEventConditions()) {
                            // Check whether the post condition is set
                            if (jobConfigEventCondition.isPostConditionValueSet()) {
                                log.debug("The Post condition was set");
                                log.debug("The value of post condition is " + jobConfigEventCondition.getPostConditionValue().getValue());
                                // Go through the list of event conditions in the description
                                boolean validCondition = false;
                                for (EventCondition descEventCondition : descEvent.getEventConditions()) {

                                    // Find the mataching name
                                    if (jobConfigEventCondition.getName().equals(descEventCondition.getName())) {
                                        log.debug("Found the matching name for the event condition");
                                        // We have got the correct name!!!  Now we go through the bounds
                                        for (ValueConstraint descValueConstraint : descEventCondition.getValueConstraints()) {
                                            ValueConstraintType constraintType = descValueConstraint.getConstraintType();
                                            /* Currently not checking ParameterValueType, whether it is a string or a float or an integer
                                             * In theory, the boundary checks should be enough because we shouldn't be using max/min
                                             * when we are expecting to have a string input anyway
                                             */
                                            log.debug("The type of the constarint = ");
                                            log.debug(constraintType.value());
                                            if (constraintType.value().equals("EXACT")) {
                                                if (descValueConstraint.getValue().equals(jobConfigEventCondition.getPostConditionValue().getValue())) {
                                                    validCondition = true;
                                                } else {
                                                    log.error("Post condition value does not match up exactly");
                                                    new JobStatus(JobStatusType.ERROR, "Post condition value does not match up exactly");
                                                    // validCondition = false;
                                                    return false;
                                                }
                                            } else if (constraintType.value().equals("MAX")) {
                                                if (Double.valueOf(descValueConstraint.getValue()) >= Double.valueOf(jobConfigEventCondition.getPostConditionValue().getValue())) {
                                                    validCondition = true;
                                                } else {
                                                    log.debug("Max constraint = " + descValueConstraint.getValue());
                                                    log.debug("Post condition value over the max value");
                                                    new JobStatus(JobStatusType.ERROR, "Post condition value over the max value");
                                                    // validCondition = false;
                                                    return false;
                                                }
                                            } else if (constraintType.value().equals("MIN")) {
                                                if (Double.valueOf(descValueConstraint.getValue()) <= Double.valueOf(jobConfigEventCondition.getPostConditionValue().getValue())) {
                                                    validCondition = true;
                                                    // valid
                                                } else {
                                                    log.debug("Min constraint = " + descValueConstraint.getValue());
                                                    log.debug("Post condition value over the min value");
                                                    new JobStatus(JobStatusType.ERROR, "Post condition value over the min value");
                                                    // validCondition = false;
                                                    return false;
                                                }
                                            } else if (constraintType.value().equals("VALUESALLOWED")) {
                                                if (descValueConstraint.getValue().equals(jobConfigEventCondition.getPostConditionValue().getValue())) {
                                                    // Only need to make it valid once if it falls into the set of allow values
                                                    // here we are assuming that if VALUESALLOWED will only be use for inputs that we are expecting to be a string
                                                    validCondition = true;
                                                }
                                            } else {
                                                // it doesn't match up to the four different type of constraint... we have no flipping idea what to do now
                                                // the other types of constraint can be STEP or DEFAULT							
                                            }
                                        }                   // looping through all the constraints
                                        if (validCondition) {
                                            // happy
                                        } else {
                                            log.error("Name of event condition does not exist in the description");
                                            new JobStatus(JobStatusType.ERROR, "Name of event condition does not exist in the description");
                                            return false;
                                        }
                                    }                           // comparing the name of the event conditions 
                                }                               // looping through all the event conditions
                                if (validEvent) {
                                } else {
                                    new JobStatus(JobStatusType.ERROR, "The entered event does not match up to any of the existing events");
                                    log.error("The entered event doesn't match up to any of the existing events");
                                    return false;
                                }
                            } else {				// below is to test whether the post condition has been set
                                log.error("The Post Condition value is not set");
                                return false;
                            }
                        }                                       // loop jobConfigEventCondition
                    }                                           // desc event tile is the same as jobConfig title
                }                                               // desc events
            }                                                   // jobConfig events

            // Starting looping the jobConfig parameter
            if (jobConfig.getConfigurationParams().isEmpty()) {
                log.debug("Starting to check the parameters");
                for (Parameter jobConfigParameter : jobConfig.getConfigurationParams()) {
                    // check whether the parameter has been set
                    if (jobConfigParameter.getValue().isSet()) {
                        log.debug("The parameter input value has been set");
                        String jobConfigParameterName = jobConfigParameter.getName();
                        log.debug("Name of the parameter = " + jobConfigParameterName);
                        boolean validParameter = false;
                        // looping through the parameters in the predictor service description
                        for (Parameter descParameter : desc.getConfigurationParams()) {
                            String descParameterName = descParameter.getName();
                            // if the name of the parameter between jobConfig and desc matches up, start testing
                            if (descParameterName.equals(jobConfigParameterName)) {
                                log.debug("Found an equal name in the description");
                                for (ValueConstraint descValueConstraint : descParameter.getValueConstraints()) {
                                    ValueConstraintType constraintType = descValueConstraint.getConstraintType();
                                    if (constraintType.value().equals("EXACT")) {
                                        if (descValueConstraint.getValue().equals(jobConfigParameter.getValue().getValue())) {
                                            validParameter = true;
                                        } else {
                                            log.error("Parameter input value does not match up exactly");
                                            new JobStatus(JobStatusType.ERROR, "Parameter input value does not match up exactly");
                                            // validParameter = false;
                                            return false;
                                        }
                                    } else if (constraintType.value().equals("MAX")) {

                                        if (Double.valueOf(descValueConstraint.getValue()) <= Double.valueOf(jobConfigParameter.getValue().getValue())) {
                                            validParameter = true;
                                        } else {
                                            log.error("Parameter input value over the max value");
                                            new JobStatus(JobStatusType.ERROR, "Parameter input value over the max value");
                                            // validParameter = false;
                                            return false;
                                        }
                                    } else if (constraintType.value().equals("MIN")) {
                                        log.debug("Value of min" + Double.valueOf(jobConfigParameter.getValue().getValue()));
                                        if (Double.valueOf(descValueConstraint.getValue()) >= Double.valueOf(jobConfigParameter.getValue().getValue())) {
                                            validParameter = true;
                                        } else {
                                            log.error("Parameter input value over the min value");
                                            new JobStatus(JobStatusType.ERROR, "Parameter input value over the min value");
                                            // validParameter = false;
                                            return false;
                                        }
                                    } else if (constraintType.value().equals("VALUESALLOWED")) {
                                        log.debug("Type of constraint = VALUESALLOWED");
                                        log.debug("desc = " + descValueConstraint.getValue() + " and input = " + jobConfigParameter.getValue().getValue());
                                        if (descValueConstraint.getValue().equals(jobConfigParameter.getValue().getValue())) {
                                            validParameter = true;
                                        }
                                        // if it doesn't match up to any of the allowed value, then validParameter will remain false which return an error
                                    } else {
                                        // it doesn't match up to the four different type of constraint... we have no flipping idea what to do now
                                        // the other types of constraint can be STEP or DEFAULT							
                                    }
                                }                   // looping through all the constraints
                            }
                        }
                        if (validParameter) {
                            // happy
                        } else {
                            log.error("Parameter is not valid");
                            new JobStatus(JobStatusType.ERROR, "Parameter is not valid");
                            return false;
                        }
                    } else {
                        log.error("Parameter not set");
                        new JobStatus(JobStatusType.ERROR, "Parameter not set");
                        return false;
                    }
                }
            } else { // ~ jobconfig parameters are empty, which means that the object cannot be valid
            }
            /*
            log.debug("Starting to check the parameters");
            for (Parameter jobConfigParameter : jobConfig.getConfigurationParams()) {
                // check whether the parameter has been set
                if (jobConfigParameter.getValue().isSet()) {
                    log.debug("The parameter input value has been set");
                    String jobConfigParameterName = jobConfigParameter.getName();
                    log.debug("Name of the parameter = " + jobConfigParameterName);
                    boolean validParameter = false;
                    // looping through the parameters in the predictor service description
                    for (Parameter descParameter : desc.getConfigurationParams()) {
                        String descParameterName = descParameter.getName();
                        // if the name of the parameter between jobConfig and desc matches up, start testing
                        if (descParameterName.equals(jobConfigParameterName)) {
                            log.debug("Found an equal name in the description");
                            for (ValueConstraint descValueConstraint : descParameter.getValueConstraints()) {
                                ValueConstraintType constraintType = descValueConstraint.getConstraintType();
                                if (constraintType.value().equals("EXACT")) {
                                    if (descValueConstraint.getValue().equals(jobConfigParameter.getValue().getValue())) {
                                        validParameter = true;
                                    } else {
                                        log.error("Parameter input value does not match up exactly");
                                        new JobStatus(JobStatusType.ERROR, "Parameter input value does not match up exactly");
                                        // validParameter = false;
                                        return false;
                                    }
                                } else if (constraintType.value().equals("MAX")) {

                                    if (Double.valueOf(descValueConstraint.getValue()) <= Double.valueOf(jobConfigParameter.getValue().getValue())) {
                                        validParameter = true;
                                    } else {
                                        log.error("Parameter input value over the max value");
                                        new JobStatus(JobStatusType.ERROR, "Parameter input value over the max value");
                                        // validParameter = false;
                                        return false;
                                    }
                                } else if (constraintType.value().equals("MIN")) {
                                    log.debug("Value of min" + Double.valueOf(jobConfigParameter.getValue().getValue()));
                                    if (Double.valueOf(descValueConstraint.getValue()) >= Double.valueOf(jobConfigParameter.getValue().getValue())) {
                                        validParameter = true;
                                    } else {
                                        log.error("Parameter input value over the min value");
                                        new JobStatus(JobStatusType.ERROR, "Parameter input value over the min value");
                                        // validParameter = false;
                                        return false;
                                    }
                                } else if (constraintType.value().equals("VALUESALLOWED")) {
                                    log.debug("Type of constraint = VALUESALLOWED");
                                    log.debug("desc = " + descValueConstraint.getValue() + " and input = " + jobConfigParameter.getValue().getValue());
                                    if (descValueConstraint.getValue().equals(jobConfigParameter.getValue().getValue())) {
                                        validParameter = true;
                                    }
                                    // if it doesn't match up to any of the allowed value, then validParameter will remain false which return an error
                                } else {
                                    // it doesn't match up to the four different type of constraint... we have no flipping idea what to do now
                                    // the other types of constraint can be STEP or DEFAULT							
                                }
                            }                   // looping through all the constraints
                        }
                    }
                    if (validParameter) {
                        // happy
                    } else {
                        log.error("Parameter is not valid");
                        new JobStatus(JobStatusType.ERROR, "Parameter is not valid");
                        return false;
                    }
                } else {
                    log.error("Parameter not set");
                    new JobStatus(JobStatusType.ERROR, "Parameter not set");
                    return false;
                }
            }
            */
            
        } catch (Exception e) {
            log.error("Caught an exception when validating configuration object: " + e.getMessage(), e);
            return false;
        }
        
        log.info("Returning true");
        return true;
    }                                                             // end of class
}
