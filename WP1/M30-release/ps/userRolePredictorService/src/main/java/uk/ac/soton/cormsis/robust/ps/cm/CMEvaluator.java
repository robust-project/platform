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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.log4j.Logger;
import uk.ac.soton.cormsis.robust.compartmentmodel.impl.CompartmentModel;
import uk.ac.soton.cormsis.robust.compartmentmodel.spec.ICompartmentModel;
import uk.ac.soton.cormsis.robust.ps.spec.IEvaluationListener;
import uk.ac.soton.cormsis.robust.ps.spec.IEvaluator;
import uk.ac.soton.cormsis.robust.roleCollection.impl.RoleCollection;
import uk.ac.soton.cormsis.robust.roleCollection.spec.IRoleCollection;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.ps.CommunityDetails;
import uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus;
import uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceJobConfig;
import uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;

/**
 *
 * @author Edwin Tye
 */
public class CMEvaluator implements Runnable, IEvaluator {

    private double probability;
    private double currentObservation;
    private PredictorServiceJobConfig jobConfig;
    // Define the progression step
    private FrequencyType forecastPeriod;
    // Finds the number of condition for an event
    private int numCondition;
    // Which group we want to look at
    private ArrayList<String> roleName = new ArrayList<String>();
    private int[] groupIndex;
    // The threshold of change
    private ArrayList<Double> threshold = new ArrayList<Double>();
    private ArrayList<Boolean> hasThreshold = new ArrayList<Boolean>();
    private ArrayList<Boolean> isUnder = new ArrayList<Boolean>();
    private ArrayList<Boolean> isAndCondition = new ArrayList<Boolean>();
    // Abusing the variable name to make my life easier
    private double[] numUserFromThreshold;
    // The starting date
    private Date startDate;
    // Number of steps
    private int forecastStep;
    // Which community?
    private String communityID;
    // Ending date
    private Date endDate;
    private String platformName;
    private String behaviourServiceURI;
    private JobStatus status;
    private String jobRef;
    // listener to notify of new result or errors
    private IEvaluationListener evaluationListener;
    private Thread evaluatorThread;
    static Logger log = Logger.getLogger(CMEvaluator.class);

    /**
     * Default constructor that initialises the evaluator.
     */
    public CMEvaluator() {
        forecastPeriod = FrequencyType.WEEKLY;
        status = new JobStatus(JobStatusType.READY);
        getConfigs();
        evaluatorThread = new Thread((CMEvaluator) this, "CM Evaluator Thread");
    }

    /**
     * Gets configuration properties from 'service.properties' on the class
     * path.
     */
    private void getConfigs() {
        Properties prop = new Properties();

        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("service.properties"));
        } catch (Exception ex) {
            log.error("Error with loading configuration file service.properties. " + ex.getMessage(), ex);
            return;
        }

        try {
            behaviourServiceURI = prop.getProperty("behaviourServiceURI");
            if (behaviourServiceURI == null) {
                throw new IllegalArgumentException("'behaviourServiceURI' not set");
            }
        } catch (Exception ex) {
            log.warn("Error getting 'behaviourServiceURI' parameter from service.properties. " + ex.getMessage(), ex);
            log.warn("Using default service URI");
            behaviourServiceURI = "http://robust-www.softwaremind.pl/robust-behaviour-analysis-service-ws-1.0-SNAPSHOT/robustBehaviourAnalysisService";
            return;
        }
        log.debug("Behaviour Service URI:    " + behaviourServiceURI);
    }

    @Override
    public void run() {
        IRoleCollection RC = null;
        ArrayList<double[]> m = null;
        ArrayList<double[][]> r = null;
        this.numCondition = this.isUnder.size();

        try {
            // This is the part which I have to change!
            log.info("Starting to evaluate the risk with the Compartment Model");

            status = new JobStatus(JobStatusType.EVALUATING);

            //             String communityID = "http://forums.sdn.sap.com/uim/forum/44#id";
            //             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //             Date date = sdf.parse("2009-08-26");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            log.debug("The community given = RC = " + communityID);
            log.debug("And the start date = " + startDate);
            String s = sdf.format(startDate);
            Date sd = sdf.parse(s);
            log.debug("And the parsed start date = " + sd + " and the string date = " + s);

            log.debug("Creating RoleCollection object");
            RC = new RoleCollection();
            //RC.setBehaviourServiceURI(behaviourServiceURI);
            //RC.setCommunity(communityID);
            log.debug("The platform name in CM evaluator = " + platformName);
            log.debug("The community id = " + communityID);
            log.debug("The starting date = " + startDate);
            log.debug("The number of condition = " + numCondition);
            //RC.setPlatform(platformName);
            RC.initialize(behaviourServiceURI, communityID, platformName);
            RC.setDate(sd);

//            log.info("Getting role name index");
            Map<String, Integer> roleNameToIndex = RC.getRoleNameIndex();
            this.groupIndex = new int[this.numCondition];

//            log.info("Starting to loop the rolename with total = " +this.roleName.size());
//            for (Entry<String,Integer> e : roleNameToIndex.entrySet()) {
//                log.info(e.getKey() + " and " + e.getValue());
//            }
//            log.info("Values in roleName");
//            for (String j : roleName) {
//                log.info(j);
//            }

            log.info("Size of group index = " + groupIndex.length);
            for (int i = 0; i < this.numCondition; i++) {
                this.groupIndex[i] = roleNameToIndex.get(this.roleName.get(i));
            }
            log.info("Roles:");
            for (Map.Entry<String, Integer> entry : roleNameToIndex.entrySet()) {
                log.debug("  " + entry.getKey() + " and " + entry.getValue());
            }

            log.debug("Going through the list of roles that is of interest");
            for (int i = 0; i < this.numCondition; i++) {
                log.info("The role of interest is : " + this.roleName.get(i));
                log.info("And the corresponding index = " + roleNameToIndex.get(this.roleName.get(i)));
                // this.groupIndex = roleNameToIndex.get(this.roleName);
            }

            m = RC.getMass(communityID, sd);
            r = RC.getRate(communityID, sd);
            int lastIndex = r.size();
            r.remove(lastIndex - 1);

            // We go and get the name of the role and the corresponding index that
            // was mapped to the compartment model
            double[] currentMass = m.get(0);
            if (numCondition > currentMass.length) {
                throw new IllegalArgumentException("Too many conditions specified.  The total number of roles = " + currentMass.length);
            }

            // We need to find the current number of use
            for (Map.Entry<String, Integer> entry : roleNameToIndex.entrySet()) {
                log.debug(entry.getKey() + " with number of user = " + currentMass[entry.getValue()]);
            }

            // TODO: This is the start to start summing over all the mass to find the proportion of users
            double totalMass = VectorCalculation.sum(currentMass);
            // TODO: If we don't want to include the inactive users
            totalMass -= currentMass[roleNameToIndex.get("Inactive")];

            /* This is to find the proportion which is for a different event. i.e. if the question is
             * "What is the probability that role X is below 30% of the active User population.
             */



            // TODO: change the output to the currentObservation after consultation with Vegard
            // currentObservation = currentMassInGroup;

            /* Using a add here because the value that we really take in is the one
             * between [-1, \infinity].  Which means that a drop of 20% is actually
             * a value of -0.2.  So, the outcomes is 80% of the current number of
             * user, hence 1 + -0.2 will give the correct number to evaluate
             * 
             * This allow us to combine the two different "event" a drop in number
             * of user and the number of user below a certain point to become 
             * one single evaluation
             */
            numUserFromThreshold = new double[numCondition];
            for (int i = 0; i < this.numCondition; i++) {
                double currentMassInGroup = currentMass[this.groupIndex[i]];
                if (this.hasThreshold.get(i)) {
                    numUserFromThreshold[i] = currentMassInGroup * (1 + this.threshold.get(i) / 100);
                } else {
                    // Slightly abuse of the variable notation here
                    numUserFromThreshold[i] = this.threshold.get(i);
                }
                // }
            }
        } catch (Throwable ex) {
            log.error("Unable to get behavioural role data to run the CM: " + ex.toString(), ex);
            status = new JobStatus(JobStatusType.ERROR, "Unable to get behavioural role data to run the Compartment Model");

            if (evaluationListener != null) {
                log.debug("Notifying listener of error");
                evaluationListener.onError(jobRef, status);
            }
        }


        log.debug("Manage to get the information from behaviour service");
        if ((status.getStatus() != JobStatusType.ERROR) && (status.getStatus() != JobStatusType.CANCELLED)) {
            try {
                // Test to see whether a valid date was entered
                // long startTime = System.currentTimeMillis();
                if (m.size() > 5) {
                    // Running the compartment model
                    //System.gc();
                    log.debug("Now, start testing the compartment model element");
                    ICompartmentModel D = new CompartmentModel();
                    int iteration = (int) 1E5;
                    D.initialize(m, r);
                    // Should be getting the number of forecastep from the jobConfig
                    D.computeForecast(forecastStep, iteration);

                    // Getting the forcast
                    int totalGroup = D.getTotalRole();

                    // numUserFromThreshold = 180;
                    // GroupID, timeStep, threshold
                    if (numCondition == 1) {
                        for (int i = 0; i < totalGroup; i++) {
                            log.debug("Probability of " + i + "= " + D.getProbabilityOverForecast(i, forecastStep, numUserFromThreshold[0]));
                        }
                    }

                    // Group index should be predefined
                    log.debug("The group index that was read from the input = " + groupIndex);

                    boolean[] under = new boolean[numCondition];
                    boolean[] andCondition = new boolean[numCondition];
                    for (int i = 0; i < numCondition; i++) {
                        under[i] = isUnder.get(i);
                        andCondition[i] = isAndCondition.get(i);
                    }
                    // probability = D.getProbabilityForecast(groupIndex, forecastStep, numUserFromThreshold, under);
                    probability = D.getProbabilityForecast(groupIndex, forecastStep, numUserFromThreshold, under, andCondition);
                    log.info("The resulting probability from the evaluation is " + probability);
                    // currentObservation = D.getCurrentObservation(groupIndex);
                } else {
                    status = new JobStatus(JobStatusType.ERROR, "Not enough observation for with the given input parameter");
                    log.error("The number of observations (" + m.size() + ")are not sufficient to make a forecast");

                    if (evaluationListener != null) {
                        log.debug("Notifying listener of error");
                        evaluationListener.onError(jobRef, status);
                    }
                }

                if ((status.getStatus() != JobStatusType.ERROR) && (status.getStatus() != JobStatusType.CANCELLED)) {

                    EvaluationResult evalRes = getEvaluationResult(probability, currentObservation, startDate, endDate);
                    status = new JobStatus(JobStatusType.FINISHED);

                    if (evaluationListener != null) {
                        log.debug("Notifying evaluation listener of new result");
                        evaluationListener.onNewResult(jobRef, evalRes);
                    }
                    log.info("The CM finished running successfully");
                }
                // } catch (InterruptedException iex) {
                // log.error("Caught an interrupted exception in the Evaluator", iex);
                // status = new JobStatus(JobStatusType.ERROR, "Caught an exception in the Evaluator");
            } catch (Throwable ex) {
                log.error("Failed to run the Compartment Model: " + ex.toString(), ex);
                status = new JobStatus(JobStatusType.ERROR, "Failed to run the Compartment Model");

                if (evaluationListener != null) {
                    log.debug("Notifying listener of error");
                    evaluationListener.onError(jobRef, status);
                }
            }
        } // end if not ERROR or CANCELLED after getting group masses

        log.debug("Evaluation thread complete");
    }

    @Override
    public void startJob(String jobRef, PredictorServiceJobConfig config) throws Exception {
        this.jobRef = jobRef;
        this.jobConfig = config;
        this.startDate = jobConfig.getStartDate();
        this.endDate = DateUtil.getToDate(startDate, forecastPeriod);

        // TODO: Check whether identical roles exist in different event

        try {
            // First, get the job config
            this.jobConfig = config;

            // Getting the config file
            Parameter forecastPeriodParam = config.getForecastPeriod();
            this.forecastStep = Integer.valueOf(forecastPeriodParam.getValue().getValue());
            log.info("The forecast step obtained = " + this.forecastStep);

            // First, we obtain the list of the parameters
            List<Parameter> configParams = jobConfig.getConfigurationParams();

            // Here we declare the parameter for the role which will be \emph{properly} initialzed
            // when it gets matched up to the correct use case
            boolean hasUseCase = false;
            // this.platformName = "IBM";

            // Load the properties from the service, where it defines which data source it comes from
            Properties prop = new Properties();
            
            try {
                prop.load(this.getClass().getClassLoader().getResourceAsStream("service.properties"));
            } catch (Exception ex) {
                log.error("Error with loading configuration file service.properties. " + ex.getMessage(), ex);
                return;
            }

            try {
                this.platformName = prop.getProperty("platformName");
            } catch (Exception ex) {
                log.error("Error getting and parsing 'platformName' parameter from service.properties. " + ex.getMessage(), ex);
                return;
            }
            log.info("The data belong to the " + this.platformName + " platform");

            for (Event e : jobConfig.getEvents()) {
                for (EventCondition evtCond : e.getEventConditions()) {
                    /*
                     * Here we are working on a reduced set of events. Namely, cutting it down from the original 6 to the current 4.
                     */

                    String eventName = evtCond.getName();
                    if (eventName.equals("Increase in percentage of number of users in a role")) {
                        this.threshold.add(Double.valueOf(evtCond.getPostConditionValue().getValue()));
                        this.hasThreshold.add(true);
                        this.isUnder.add(false);
                        this.isAndCondition.add(true);
                    }
                    if (eventName.equals("Decrease in percentage of number of users in a role")) {
                        log.info("Post condition in string form = " + evtCond.getPostConditionValue().getValue());
                        this.threshold.add(Double.valueOf(evtCond.getPostConditionValue().getValue()));
                        // log.info("has threshold and the value = " + this.threshold.get(this.threshold.size() - 1) + " and in string form = " + evtCond.getPostConditionValue().getValue());
                        this.hasThreshold.add(true);
                        this.isUnder.add(true);
                        this.isAndCondition.add(false);
                    }
                    if (eventName.equals("Number of Users below the specified value")) {
                        this.threshold.add(Double.valueOf(evtCond.getPostConditionValue().getValue()));
                        this.hasThreshold.add(false);
                        this.isUnder.add(true);
                        this.isAndCondition.add(false);
                    }
                    if (eventName.equals("Number of Users above the specified value")) {
                        this.threshold.add(Double.valueOf(evtCond.getPostConditionValue().getValue()));
                        this.isUnder.add(false);
                        this.isAndCondition.add(true);
                    }
                }
                // Now, because this is going to be generic.  We won't be able to test for the role name anymore
                for (Parameter roleParam : e.getConfigParams()) {
                    if (roleParam.getName().equals("Roles for the use case")) {
                        log.info("Name of the role = " + roleParam.getValue().getValue());
                        roleName.add(roleParam.getValue().getValue());
                        hasUseCase = true;
                    } else {
                        status = new JobStatus(JobStatusType.ERROR, "Role not in the platform specified");
                        throw new IllegalArgumentException("role not in the platform specified");
                    }
                }
            }

            if (hasUseCase) {
            } else {
                status = new JobStatus(JobStatusType.ERROR, "Use case platform is not defined");
                throw new IllegalArgumentException("Use case platform is not defined");
            }

            if (roleName.isEmpty()) {
                status = new JobStatus(JobStatusType.ERROR, "Role name was not entered");
                throw new IllegalArgumentException("Role name was not entered");
            }

            //this.groupIndex = Integer.valueOf(roleParam.getValue().getValue());
            // Lets just use a role index to fill the gap for now until we can get
            // the behaviour service from WP3 running
            // this.groupIndex = 3;

            this.startDate = jobConfig.getStartDate();
            this.endDate = DateUtilCM.getToDate(jobConfig.getStartDate(), forecastPeriod, forecastStep);

            // Then the community id
            CommunityDetails a = jobConfig.getCommunityDetails();
            this.communityID = a.getCommunityID();

            evaluatorThread.start();
        } catch (Exception e) {
            status = new JobStatus(JobStatusType.ERROR, "Error in start job");
            log.error("Error in start job", e);
        }
    }

    @Override
    public void cancelJob() {
        status = new JobStatus(JobStatusType.CANCELLED);
        log.debug("Cancelled job");
    }

    @Override
    public JobStatus getJobStatus() {
        return status;
    }

    @Override
    public void setEvaluationListener(IEvaluationListener evaluationListener) {
        this.evaluationListener = evaluationListener;
    }

    /**
     * Creates an evaluation result and notifies the listener.
     *
     * @param probability The calculated probability.
     * @param currentObservation The current observed value.
     * @param currentDate The date from which the result is a forecast.
     * @param forecastDate The date of the forecast.
     */
    private EvaluationResult getEvaluationResult(double probability, double currentObservation, Date currentDate, Date forecastDate) {
        EvaluationResult evalRes = new EvaluationResult();
        evalRes.addResultItem(new ResultItem("probability", probability));
        evalRes.setCurrentDate(currentDate);
        evalRes.setForecastDate(forecastDate);
        //evalRes.setCurrentObservation(String.valueOf(currentObservation));

        return evalRes;
    }
}
