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
package uk.ac.soton.cormsis.robust.stats.utils;

import java.util.Arrays;
import java.util.Comparator;

public class ranking{
    
    public static double[][] rankSort(double[] X) {
                double[][] Y = new double[X.length][2];
        //System.arraycopy(X, 0, Y, 0, X.length);
        //double[] rank = new double[X.length];
        Double[][] S = new Double[X.length][2];
        for (int i = 0; i < X.length; i++) {
            S[i][0] = X[i];
            S[i][1] = (double) i;
        }

        // Need to sort the array, according to one column only
        Arrays.sort(S, new Comparator<Double[]>() {
            @Override
            public int compare(Double[] o1, Double[] o2) {
                return Double.compare(o1[0], o2[0]);
                //if (o1[0] > o2[0]) return 1;
                //if (o1[0] < o2[0]) return -1;
                //return Double.valueOf(o1[0]).compareTo(Double.valueOf(o2[0]));
            }
        });
        
        double[][] R = new double[X.length][2];
        
        for (int i = 0; i < X.length; i++) {
            R[i][0] = S[i][0].doubleValue();
            R[i][1] = S[i][1].doubleValue();
        }
        return R;
    }
       
    public static double[] orderedRank(double[] X) {
        double[][] Y = new double[X.length][2];
        //System.arraycopy(X, 0, Y, 0, X.length);
        //double[] rank = new double[X.length];
        Double[][] S = new Double[X.length][2];
        for (int i = 0; i < X.length; i++) {
            S[i][0] = X[i];
            S[i][1] = (double) i;
        }

        // Need to sort the array, according to one column only
        Arrays.sort(S, new Comparator<Double[]>() {
            @Override
            public int compare(Double[] o1, Double[] o2) {
                return Double.compare(o1[0], o2[0]);
                //if (o1[0] > o2[0]) return 1;
                //if (o1[0] < o2[0]) return -1;
                //return Double.valueOf(o1[0]).compareTo(Double.valueOf(o2[0]));
            }
        });
        
        // Now S[i][1] has the index of the unsorted array
        // Putting it into Y, and then use it to generate the rank while preserving
        // the index in Y[i][1].  To make it more explicit that we are change type
        // between Double and double
        for (int i = 0; i < X.length; i++) {
            Y[i][0] = S[i][0];
            Y[i][1] = S[i][1];
        }
        // The following code follows the routine "crank" of Numerical Recipe
        int jt = 1;
        int ji = 1;
        int j = 1;
        double t = 1;
        double rank = 0;
        while (j < X.length) {
            if (Y[j] != Y[j - 1]) {
                Y[j - 1][0] = j;
                ++j;
            } else {
                for (jt = j + 1; jt <= Y.length && Y[jt - 1][0] == Y[j - 1][0]; jt++) {
                };
                rank = 0.5 * (j + jt - 1);
                for (ji = j; ji <= (jt - 1); ji++) {
                    Y[ji - 1][0] = rank;
                }
                t = jt - j;
                j = jt;
            }
        }

        if (j == X.length) {
            Y[X.length - 1][0] = X.length;
        }

        // Now Y[i][0] has the ranks
        // Y[i][1] has the original index.  Rearrange it back
        double[] R = new double[X.length];
        for (int i = 0; i < X.length; i++) {
            // Obtain the index number
            int e = (int) Y[i][1];
            // And put it into R
            R[e] = Y[i][0];
        }
        /*
        for (int i = 0; i < X.length; i++) {
        System.out.println("R " +i+ " = " +R[i]);
        }
         * 
         */
        return R;
    }
    
    public static double[] naturalRank(double[] X) {
        double[][] Y = new double[X.length][2];
        //System.arraycopy(X, 0, Y, 0, X.length);
        //double[] rank = new double[X.length];
        Double[][] S = new Double[X.length][2];
        for (int i = 0; i < X.length;i++) {
            S[i][0] = X[i];
            S[i][1] = (double)i;
        }
    
    Arrays.sort(S, new Comparator<Double[]>() {        
        @Override
        public int compare(Double[] o1, Double[] o2) {
            return Double.compare(o1[0],o2[0]);
            //if (o1[0] > o2[0]) return 1;
            //if (o1[0] < o2[0]) return -1;
            //return Double.valueOf(o1[0]).compareTo(Double.valueOf(o2[0]));
        }
    });
    // Now S[i][1] has the index of the unsorted array
    // Putting it into Y, and then use it to generate the rank while preserving
    // the index in Y[i][1].  To make it more explicit that we are change type
    // between Double and double
    for (int i = 0; i < X.length; i++) {
        Y[i][0] = S[i][0];
        Y[i][1] = S[i][1];
    }
           
        int jt = 1;
        int ji = 1;
        int j = 1;
        double t;
        //double s = 0;
        double rank = 0;
        while (j < X.length) {
            if (Y[j] != Y[j - 1]) {
                Y[j - 1][0] = j;
                ++j;
            } else {
                for (jt = j + 1; jt <= Y.length && Y[jt - 1][0] == Y[j - 1][0]; jt++);
                rank = 0.5 * (j + jt - 1);
                for (ji = j; ji <= (jt - 1); ji++) {
                    Y[ji - 1][0] = rank;
                }
                t = jt - j;
                //s += (t*t*t-t);
                j = jt;
            }
        }

        if (j == X.length) {
            Y[X.length - 1][0] = X.length;
        }       
         
        // Now Y[i][0] has the ranks
        // Y[i][1] has the original index.  Rearrange it back
        double[] R = new double[X.length];
        for (int i = 0; i < X.length; i++) {
            // Obtain the index number
            int e = (int)Y[i][1];
            // And put it into R
            R[e] = Y[i][0];
        }
        /*
        for (int i = 0; i < X.length; i++) {
            System.out.println("R " +i+ " = " +R[i]);
        }
         * 
         */
    return R;
    }


    // public static double[] orderedRank(double[] X) {
    //     double[][] Y = new double[X.length][2];
    //     //System.arraycopy(X, 0, Y, 0, X.length);
    //     //double[] rank = new double[X.length];
    //     Double[][] S = new Double[X.length][2];
    //     for (int i = 0; i < X.length; i++) {
    //         S[i][0] = X[i];
    //         S[i][1] = (double) i;
    //     }

    //     // Need to sort the array, according to one column only
    //     Arrays.sort(S, new Comparator<Double[]>() {
    //         @Override
    //         public int compare(Double[] o1, Double[] o2) {
    //             return Double.compare(o1[0], o2[0]);
    //             //if (o1[0] > o2[0]) return 1;
    //             //if (o1[0] < o2[0]) return -1;
    //             //return Double.valueOf(o1[0]).compareTo(Double.valueOf(o2[0]));
    //         }
    //     });
        
    //     // Now S[i][1] has the index of the unsorted array
    //     // Putting it into Y, and then use it to generate the rank while preserving
    //     // the index in Y[i][1].  To make it more explicit that we are change type
    //     // between Double and double
    //     for (int i = 0; i < X.length; i++) {
    //         Y[i][0] = S[i][0];
    //         Y[i][1] = S[i][1];
    //     }
    //     // The following code follows the routine "crank" of Numerical Recipe
    //     int jt = 1;
    //     int ji = 1;
    //     int j = 1;
    //     double t = 1;
    //     double rank = 0;
    //     while (j < X.length) {
    //         if (Y[j] != Y[j - 1]) {
    //             Y[j - 1][0] = j;
    //             ++j;
    //         } else {
    //             for (jt = j + 1; jt <= Y.length && Y[jt - 1][0] == Y[j - 1][0]; jt++) {
    //             };
    //             rank = 0.5 * (j + jt - 1);
    //             for (ji = j; ji <= (jt - 1); ji++) {
    //                 Y[ji - 1][0] = rank;
    //             }
    //             t = jt - j;
    //             j = jt;
    //         }
    //     }

    //     if (j == X.length) {
    //         Y[X.length - 1][0] = X.length;
    //     }

    //     // Now Y[i][0] has the ranks
    //     // Y[i][1] has the original index.  Rearrange it back
    //     double[] R = new double[X.length];
    //     for (int i = 0; i < X.length; i++) {
    //         // Obtain the index number
    //         int e = (int) Y[i][1];
    //         // And put it into R
    //         R[e] = Y[i][0];
    //     }
    //     /*
    //     for (int i = 0; i < X.length; i++) {
    //     System.out.println("R " +i+ " = " +R[i]);
    //     }
    //      * 
    //      */
    //     return R;
    // }
    
}
