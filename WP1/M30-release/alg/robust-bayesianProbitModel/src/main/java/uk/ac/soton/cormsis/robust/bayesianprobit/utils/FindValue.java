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
//      Created Date :          2013-04-18
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.bayesianprobit.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pah1g10
 */
public class FindValue {
    
    /**
     * Utility method to find vector of possible unique categories from input
     * vector a.
     *
     * @param a categorical vector
     * @return Vector of unique possible categories (length = numCat)
     */
    public static double[] findUnique(double[] a) {
        double[] dblUnique;
        double[] temp = new double[a.length - 1];
        boolean blnUnique = true;
        temp[0] = a[0];
        int count = 1;
        for (double tempDbl : a) {
            for (double tempTempDbl : temp) {
                if (tempDbl == tempTempDbl) {
                    blnUnique = false;
                }
            }
            if (blnUnique) {
                temp[count++] = tempDbl;
            }
            blnUnique = true;
        }
        dblUnique = new double[count];
        for (int i = 0; i < count; i++) {
            dblUnique[i] = temp[i];
        }
        return dblUnique;
    }

    /**
     * Utility method to count the number of observations from y in each of the
     * observed unique possible categories of cat.
     *
     * @param cat Vector of unique possible categories
     * @param y categorical vector of observed responses for learning/prediction
     * phase
     * @return Vector of counts (length = numCat)
     */
    public static double[] countUnique(double[] cat, double[] y) {
        double[] numInCat = new double[cat.length];
        for (int c = 0; c < cat.length; c++) {
            for (int i = 0; i < y.length; i++) {
                if (y[i] == cat[c]) {
                    numInCat[c] = numInCat[c] + 1;
                }
            }
        }
        return numInCat;
    }
    
    /**
     * Searches for matches to argument pattern within each element of a vector
     *
     * @param pattern double to be matched
     * @param x vector in which match is sought
     * @return ArrayList of index values
     */
    public static List<Integer> grep(double pattern, double[] x) {
        List<Integer> index = new ArrayList<Integer>();
        
        for (int i = 0; i < x.length; i++) {
            double xi = x[i];
            if (xi == pattern) {
                index.add(i);
            }
        }
        
        return index;
    }
    
    
    public static int minVectorIndex(double[] data) {
        int minValueIndex = 0;
        
        for (int i = 1; i < data.length; i++) {
            if (data[i] < data[minValueIndex]) {
                minValueIndex = i;
            }
        }
        
        return(minValueIndex);
    }
    
    public static int maxVectorIndex(double[] data) {
        int maxValueIndex = 0;
        
        for (int i = 1; i < data.length; i++) {
            if (data[i] > data[maxValueIndex]) {
                maxValueIndex = i;
            }
        }
        
        return(maxValueIndex);
    }
    
    public static double minVectorValue(double[] x) {
        double min = x[0];
        
        for (int i = 1; i < x.length; i++) {
            double xi = x[i];
            if (xi < min) {
                min = xi;
            }
        }
        
        return(min);
    }
    
    public static double maxVectorValue(double[] x) {
        double max = x[0];
        
        for (int i = 1; i < x.length; i++) {
            double xi = x[i];
            if (xi > max) {
                max = xi;
            }
        }
        
        return(max);
    }
    
    public static double minValue(double a, double b) {
        double min = a;
        if (a > b) min = b;
            
        return(min);
    }
    
    public static double maxValue(double a, double b) {
        double max = a;
        if (a < b) max = b;
            
        return(max);
    }

    
}
