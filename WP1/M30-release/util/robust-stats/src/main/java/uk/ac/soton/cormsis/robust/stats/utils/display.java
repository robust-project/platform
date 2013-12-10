////////////////////////////////////////////////////////////////////////
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
//      Created Date :          2012-09-24
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.utils;

import java.util.List;
import java.util.Map;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * A class to print some the output of either a matrix or a vector on the screen
 * to save some typing
 *
 * @author Edwin
 */
public class display {

    /**
     * Prints the matrix
     *
     * @param A matrix
     */
    static public void print(double[][] A) {
        for (double[] Ai : A) {
            for (double Aij : Ai) {
                // for (int j = 0; j < Ai.length; j++) {
                // System.out.printf("%9.3f", Ai[j]);
                System.out.printf("%9.3f", Aij);
                System.out.print(" ");
            }
            System.out.println(" ");
        }
    }

    static public void print(double[][] A, int digit) {
	String printFormat = "%9."+digit+"f";
        for (double[] Ai : A) {
            for (double Aij : Ai) {
                // for (int j = 0; j < Ai.length; j++) {
                // System.out.printf("%9.3f", Ai[j]);
                System.out.printf(printFormat, Aij);
                System.out.print(" ");
            }
            System.out.println(" ");
        }
    }
    
    /**
     * Prints numeric list of map data
     * 
     * @param mapData 
     */
    static public void print(Map<String,List<Number>> mapData) {
        String id = mapData.keySet().iterator().next();
        int numCovariate = mapData.get(id).size();
        for (Map.Entry<String, List<Number>> e : mapData.entrySet()) {
            System.out.print(e.getKey());
            List<Number> x = e.getValue();
            for (int j = 0; j < numCovariate; j++) {
//                System.out.println(j);
                System.out.printf("%9.3f", x.get(j).doubleValue());
                System.out.print(" ");
            }
            System.out.println("");
        }
    }
    
    /**
     *
     * @param map
     */
    static public void printmSD(Map<String,Double> mapData) {
        for (Map.Entry<String, Double> e : mapData.entrySet()) {
            Double x = e.getValue();
                System.out.printf("%9.3f", x.doubleValue());
            System.out.println("");
        }
    }

    static public void printmSI(Map<String,Integer> mapData) {
        for (Map.Entry<String, Integer> e : mapData.entrySet()) {
            Integer x = e.getValue();
                System.out.print(x.intValue());
            System.out.println("");
        }
    }
    
    static public void print(RealMatrix A) {
        int numCovariate = A.getColumnDimension();
        int numObs = A.getRowDimension();
        double[][] B = new double[numObs][];
        for (int i = 0; i < numObs; i ++) {
            B[i] = A.getRow(i);
        }
        print(B);
    }
    
    /**
     * Prints the vector
     *
     * @param A vector
     */
    static public void print(double[] A) {
        for (double Ai : A) {
            System.out.println(Ai);
        }
    }

    static public void print(int[] A) {
        for (double Ai : A) {
            System.out.println(Ai);
        }
    }
    
    /**
     * Prints the vector
     *
     * @param A vector
     */
    static public void print(String[] A) {
        for (String Ai : A) {
            System.out.println(Ai);
        }
    }

    static public void printHorizontal(double[] A) {
        for (double Ai : A) {
            System.out.printf("%9.5f", Ai);
            System.out.print(" ");
        }
        System.out.println(" ");
    }
}
