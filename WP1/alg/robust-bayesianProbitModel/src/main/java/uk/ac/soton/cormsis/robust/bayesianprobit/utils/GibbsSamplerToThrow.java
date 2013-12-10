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
//      Created Date :          2013-04-19
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.bayesianprobit.utils;

import java.util.ArrayList;
import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;

/**
 *
 * @author pah1g10
 */
public class GibbsSamplerToThrow {

    /**
     * Refine observed data according to whether observations are active
     *
     * @param rawObservations raw observed data (excluding observation
     * identification labels)
     * @param indexActive vector indices of active observations
     * @return matrix of active observation data
     */
    public static double[][] refineData(double[][] rawObservations, int[] indexActive) {
        double[][] refinedData = new double[indexActive.length][rawObservations[0].length];
        for (int j = 0; j < refinedData[0].length; j++) {
            for (int i = 0; i < refinedData.length; i++) {
                refinedData[i][j] = rawObservations[indexActive[i]][j];
            }
        }
        return refinedData;
    }

    /**
     * Find the indices of active observations
     *
     * @param rawObservations raw observed data (excluding observation
     * identification labels)
     * @param ActiveThreshold threshold for sum of variables for each
     * observation to be over for that observation to be defined as active
     * @return vector containing indices of active observations
     */
    public static int[] indexRefineData(double[][] rawObservations, double ActiveThreshold) {
        double[] totalPeriodActivity = GeneralMatrixOperation.matrixRowSum(rawObservations);
        ArrayList<Integer> arActiveIndex = new ArrayList<Integer>();
        int[] intActiveIndex;
        for (int i = 0; i < totalPeriodActivity.length; i++) {
            if (totalPeriodActivity[i] > ActiveThreshold) {
                arActiveIndex.add(i);
            }
        }
        intActiveIndex = new int[arActiveIndex.size()];
        for (int i = 0; i < intActiveIndex.length; i++) {
            intActiveIndex[i] = arActiveIndex.get(i);
        }
        return intActiveIndex;
    }

    /**
     * Refine observation identification labels according to whether
     * observations are active
     *
     * @param labels vector of observation identification labels
     * @param indexActive vector indices of active observations
     * @return vector of active observation identification labels
     */
    public static String[] refineLabels(String[] labels, int[] indexActive) {
        String[] refinedLabels = new String[indexActive.length];
        for (int i = 0; i < refinedLabels.length; i++) {
            refinedLabels[i] = labels[indexActive[i]];
        }
        return refinedLabels;
    }
    
}
