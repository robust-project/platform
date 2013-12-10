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
//      Created Date :          2012-08-05
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.compartmentmodel.spec;

import java.util.ArrayList;

public interface ICompartmentModel {

    //public void initialize();
    // 
    /**
     * This is the default initialization method
     * @param mass Array list of Real Matrix of size [group x 1]
     * @param rate Array list of Real matrix of size [group x group]
     */
    public void initialize(ArrayList<double[]> mass, ArrayList<double[][]> rate);

    /**
     * Computes the forecast via simulation up to the entered parameter
     * @param forecastStep number of time steps to be forecasted
     */
    public void computeForecast(int forecastStep);

    /**
     * Computes the forecast via simulation up to the entered parameter
     * @param forecastStep number of time steps to be forecasted
     * @param iter number of iteration to be used
     */
    public void computeForecast(int forecastStep, int iter);


    /**
     * Computes the forecast via simulation up to the entered parameter
     * @param forecastStep number of time steps to be forecasted
     * @param iter number of iteration to be used
     * @param penalty discount factor \in [0,1]
     */
    public void computeForecast(int forecastStep, int iter, double penalty);

    /**
     * Get the current number of user in each of the roles
     * @return the current observation for all roles
     */
    public double[] getCurrentObservation();

    /**
     * Get the current number of user in one of the roles
     * @param groupIndex Index of the role
     * @return current observation for role given argument
     */
    public double getCurrentObservation(int groupIndex);

    /**
     * Find the mean forecast of each role over all the number of steps as specified 
     * 
     * @return A 2d array of double that contains the mean forecast of roles x num of steps
     */
    public double[][] getForecastMean();

    /**
     * Find the mean forecast for a particular role and time step
     * 
     * @param forecastStep Number of forecast step
     * @param groupIndex Index for the role
     * @return a double for the specified input argument
     */
    public double getForecastMean(int forecastStep, int groupIndex);

    /**
     * Find the upper confidence interval for the forecast of each role over all the number of steps as specified 
     * 
     * @return A 2d array of double that contains the upper confidence interval of roles x num of steps
     */
    public double[][] getForecastUCI();

    /**
     * Find the upper confidence interval forecast for a particular role and time step
     * 
     * @param forecastStep Number of forecast step
     * @param groupIndex Index for the role
     * @return a double for the specified input argument
     */
    public double getForecastUCI(int forecastStep, int groupIndex);
    
    /**
     * Find the lower confidence interval for the forecast of each role over all the number of steps as specified 
     * 
     * @return A 2d array of double that contains the lower confidence interval of roles x num of steps
     */
    public double[][] getForecastLCI();

    /**
     * Find the lower confidence interval forecast for a particular role and time step
     * 
     * @param forecastStep Number of forecast step
     * @param groupIndex Index for the role
     * @return a double for the specified input argument
     */
    public double getForecastLCI(int forecastStep, int groupIndex);

    /**
     * To provide the optimized discount factor according to the number of forecast steps 
     * required by the user
     * @param forecastStep number of time steps to be forecasted
     * @return A value \in [0,1]
     */
    public double getOptimiseWeight(int forecastStep);

    /**
     * The total forecast steps computed
     * 
     * @return number of forecast
     */
    public int getForecastStep();

    /**
     * The total number of roles in the compartment model
     * 
     * @return number of groups
     */
    public int getTotalRole();

    // public double getProbabilityUnderThreshold(int groupid, int timestep, double threshold);

    /**
     * Find the value in the group that is under the probability, i.e. x = F^{-1}(\alpha) if F(\cdot) defines
     * the cumulative distribution function, \alpha \in \left[0,1\right]
     * @param groupid the group index of a role
     * @param timeStep the time step of the forecast
     * @param probability probability value \in [0,1]
     * @return the value that is expected to be under the entered probability
     */
    public double getForecastUnderProbability(int groupid, int timeStep, double probability);

    /**
     * 
     * Find the value in the group that is over the probability, i.e. x = F^{-1}(1-\alpha)
     * @param groupid the group index of a role
     * @param timeStep the time step of the forecast
     * @param probability probability value \in [0,1]
     * @return the value that is expected to be over the entered probability
     */
    public double getForecastOverProbability(int groupid, int timeStep, double probability);

    /**
     * Finds the probability that the specific group has a value below the entered parameter
     * at the given time step of the forecast i.e. \P(X \le x)
     * @param groupid the group index
     * @param timeStep the time step of the forecast
     * @param numuser number of user
     * @return A value \in [0,1]
     */
    public double getProbabilityUnderForecast(int groupid, int timeStep, double numuser);

    /**
     * Finds the probability that the specific group has a value over the entered parameter
     * at the given time step of the forecast i.e. \P(X \le x)
     * @param groupid the group index
     * @param timeStep the time step of the forecast
     * @param numuser number of user
     * @return A value \in [0,1]
     */
    public double getProbabilityOverForecast(int groupid, int timeStep, double numuser);

    /**
     * AND condition. To find the probabiliy of a set of events happening at the same time
     * 
     * @param groupid array of role index
     * @param timestep number of timestep forward in time
     * @param threshold array of absolute change
     * @param under array of equality sign, true means it is using P(X \le x)
     * @return probabiliy \in [0,1]
     */
    public double getProbabilityForecast(int[] groupid, int timestep, double[] threshold,boolean[] under);

    /**
     * Both "and" and "or" condition. To find the probabiliy of a set of events happening at the same time
     * 
     * @param groupid array of role index
     * @param timestep number of timestep forward in time
     * @param threshold array of absolute change
     * @param under array of equality sign, true means it is using P(X \le)
     * @param logicalCondition true is and condition, false is or condition
     * @return probabiliy \in [0,1]
     */

    public double getProbabilityForecast(int[] groupid, int timeStep, double[] threshold,boolean[] under, boolean[] logicalCondition);

   
}
