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
//      Created Date :          2012-11-09
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.bayesianprobit.utils;

import org.apache.commons.math3.linear.RealVector;
import uk.ac.soton.cormsis.robust.bayesianprobit.bpgs.ExceptionPositiveDefiniteMatrix;
import uk.ac.soton.cormsis.robust.stats.utils.VectorCalculation;

/**
 *
 * @author pah1g10
 */
public class InputChecks {

    /**
     * Check whether varCov is posDef
     *
     * @param A square symmetric matrix
     * @return Boolean
     */
    public static boolean checkMatrixMayPosDef(double[][] A) {
        boolean valid = true;

        int numCov = A.length;

        //check all Aii > 0:
        for (int i = 0; i < numCov; i++) {
            double Aii = A[i][i];
            if (Aii <= 0) {
                System.out.println(Aii);
                valid = false;
                System.out.println(valid);
                return valid;
            }
        }

        return valid;
    }

    public static boolean checkMatrixRankValid(double[][] x, boolean isHist) {
        Boolean xValid = false;

        int numObs = x.length;
        int numCov = x[0].length;
        int numVar;
        if (isHist == true) {
            numVar = numCov - 2;
        } else {
            numVar = numCov - 1;
        }

        if (numObs > numVar) {
            xValid = true;
        }

        return xValid;
    }

    /**
     * Check on whether the Y vector created is neither all 0 or all 1
     *
     * @param y vector of observed binary responses
     * @return Boolean
     */
    public static boolean checkYValid(double[] y, int numCls) {
        boolean yValid = false;
        double[] uniqueCls = FindValue.findUnique(y);
        
        if (uniqueCls.length == numCls) {
            yValid = true;
        } else {
            yValid = false;
        }

        return (yValid);
    }

    /**
     * Check on whether the Y vector created is neither all 0 or all 1
     *
     * @param y vector of observed binary responses
     * @return Boolean
     */
    public static boolean checkBinaryYValid(RealVector y) {
        boolean yValid = false;

        double[] dblY = y.toArray();

        double ySum = VectorCalculation.sum(dblY);
        if (ySum != (double) y.getDimension() && ySum != (double) 0) {
            yValid = true;
        } else {
            yValid = false;
        }

        return (yValid);
    }
}
