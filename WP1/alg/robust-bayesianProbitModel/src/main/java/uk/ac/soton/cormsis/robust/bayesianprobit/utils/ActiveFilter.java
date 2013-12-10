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
//      Created Date :          2013-04-22
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.bayesianprobit.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;

/**
 *
 * @author pah1g10
 */
public class ActiveFilter {
    
    
    /**
     * Filtering of observations by sum activity over covariates included.
     * @param mapData original data.
     * @param activeThreshold threshold for activity.
     * @param numLeadFeature number of covariates.
     * @return subset of mapData.
     */
    public static Map<String, List<Number>> activeSum(Map<String, List<Number>> mapData, double activeThreshold, int numLeadFeature) {
        Map<String, List<Number>> mapRefData = new HashMap<String, List<Number>>();

        //Find number of covariate
        String id = mapData.keySet().iterator().next();
        int numCov = mapData.get(id).size();

        //Fill mapRefData with observations which are defined to be active by activeThreshold
        double[] X = new double[numCov]; int startI = numCov-numLeadFeature; 
        for (Map.Entry<String, List<Number>> e : mapData.entrySet()) {
            List<Number> x = e.getValue();
            for (int j = startI; j < numCov; j++) {
                X[j] = x.get(j).doubleValue();
            }
            if (VectorCalculation.sum(X) > activeThreshold) {
                mapRefData.put(e.getKey(), e.getValue());
//                System.out.println("User " + e.getKey() + " ACCEPTED");
//                display.print(X);
//            } else {
//                System.out.println("User " + e.getKey() + " REJECTED");
            }
        }
        return mapRefData;
    }
    
    /**
     * Filtering of observations by average activity over covariates included.
     * @param mapData original data.
     * @param activeThreshold threshold for activity.
     * @param numLeadFeature number of covariates.
     * @return subset of mapData.
     */
    public static Map<String, List<Number>> activeAv(Map<String, List<Number>> mapData, double activeThreshold, int numLeadFeature) {
        Map<String, List<Number>> mapRefData = new HashMap<String, List<Number>>();

        //Find number of covariate
        String id = mapData.keySet().iterator().next();
        int numCov = mapData.get(id).size();

        //Fill mapRefData with observations which are defined to be active by activeThreshold
        double[] X = new double[numCov]; int startI = numCov-numLeadFeature; 
        for (Map.Entry<String, List<Number>> e : mapData.entrySet()) {
            List<Number> x = e.getValue();
            for (int j = 0; j < numCov; j++) {
                X[j] = x.get(j).doubleValue();
            }
            if (VectorCalculation.mean(X) > activeThreshold) {
                mapRefData.put(e.getKey(), e.getValue());
//                System.out.println("User " + e.getKey() + " ACCEPTED");
//                display.print(X);
//            } else {
//                System.out.println("User " + e.getKey() + " REJECTED");
            }
        }
        return mapRefData;
    }
}
