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
//      Created By :            Philippa Hiscock
//      Created Date :          2012-11-13
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.bayesianprobit.bpgs;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class RunModel {

    static Logger log = Logger.getLogger(RunModel.class);
    ModelData md = new ModelData();

    /**
     * Constructor to run pre-processing, Bayesian probit model with Gibbs
     * sampler, and post-processing (including making predictions)
     *
     * @param mapData input data
     * @param isHistorical true if the prediction phase is observed
     * @param isPercentile true if response variable is peer based
     * @param isActiveSum whether to use sum or average statistic when
     * determining observations as active
     * @param activeThreshold threshold for sum of variables for each
     * observation to be over for that observation to be defined as active
     * @param dropThreshold vector of thresholds for changes in activity - used
     * to define categories
     * @param b number of iterations in burn-in period
     * @param r number of iterations in retained period
     * @throws Exception
     */
    public RunModel(Map<String, List<Number>> mapData, boolean isHistorical, boolean isPercentile, boolean isActiveSum, double activeThreshold, double[] dropThreshold, int b, int r) throws Exception {

        try {
            ////-------------------- Refine/PreProcess Data --------------------////
            PreProcessData pd = new PreProcessData(md, mapData, isHistorical, isPercentile, isActiveSum, activeThreshold, dropThreshold);
        } catch (Throwable t) {
            log.error("Failed to preprocess the data before executing the GS: " + t.toString(), t);
        }


        try {
            ////----------------------- Run Gibbs Sampler ----------------------////
            BayesianProbitwGS gs = new BayesianProbitwGS(md.getXLearn(), md.getYLearn(), md.getYLProp(), b, r);
            ParameterData paramData = gs.returnParamData();
            ////------------------------ Make Prediction -----------------------////
            PostProcess.Predict(md, paramData);
        } catch (Throwable t) {
            log.error("Failed to execute the GS: " + t.toString(), t);
        }


    }

    /**
     * Constructor to run pre-processing, Bayesian probit model with Gibbs
     * sampler, and post-processing (including making predictions)
     *
     * @param mapData input data
     * @param isHistorical true if the prediction phase is observed
     * @param isPercentile true if response variable is peer based
     * @param isActiveSum whether to use sum or average statistic when
     * determining observations as active
     * @param activeThreshold threshold for sum of variables for each
     * observation to be over for that observation to be defined as active
     * @param dropThreshold vector of thresholds for changes in activity - used
     * to define categories
     * @throws Exception
     */
    public RunModel(Map<String, List<Number>> mapData, boolean isHistorical, boolean isActiveSum, boolean isPercentile, double activeThreshold, double[] dropThreshold) throws Exception {

        try {
            ////-------------------- Refine/PreProcess Data --------------------////
            PreProcessData pd = new PreProcessData(md, mapData, isHistorical, isPercentile, isActiveSum, activeThreshold, dropThreshold);
        } catch (Throwable t) {
            log.error("Failed to preprocess the data before executing the GS: " + t.toString(), t);
        }


        try {
            ////----------------------- Run Gibbs Sampler ----------------------////
            BayesianProbitwGS gs = new BayesianProbitwGS(md.getXLearn(), md.getYLearn(), md.getYLProp());
            ParameterData paramData = gs.returnParamData();
            ////------------------------ Make Prediction -----------------------////
            PostProcess.Predict(md, paramData);
        } catch (Throwable t) {
            log.error("Failed to execute the GS: " + t.toString(), t);
        }


    }

//    /**
//     * Constructor to run pre-processing, Bayesian probit model with Gibbs
//     * sampler, and post-processing (including making predictions)
//     *
//     * @param mapData input data
//     * @param isHistorical true if the prediction phase is observed
//     * @param yLearn binary response for learning phase wrt mapData observations
//     * @throws Exception
//     */
//    public RunModel(Map<String, List<Number>> mapData, boolean isHistorical, int[] yLearn) throws Exception {
//
//        try {
//            ////-------------------- Refine/PreProcess Data --------------------////
//            PreProcessData pd = new PreProcessData(md, mapData, isHistorical, yLearn);
//        } catch (Throwable t) {
//            log.error("Failed to preprocess the data before executing the GS: " + t.toString(), t);
//        }
//
//        try {
//            ////----------------------- Run Gibbs Sampler ----------------------////
//            BayesianProbitwGS gs = new BayesianProbitwGS(md.getXLearn(), md.getYLearn(), md.getYLProp());
//            ParameterData paramData = gs.returnParamData();
//
//            ////------------------------ Make Prediction -----------------------////
//            PostProcess.Predict(md, paramData);
//        } catch (Throwable t) {
//            log.error("Failed to execute the GS: " + t.toString(), t);
//        }
//    }

    /**
     * Method to return model data
     *
     * @return model data filled according to RunModel constructor
     */
    public ModelData returnModelData() {
        return md;
    }
}
