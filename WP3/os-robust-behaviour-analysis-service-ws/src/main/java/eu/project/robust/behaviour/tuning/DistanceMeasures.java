/**
 *Copyright 2013 Knowledge Media Institute
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
package eu.project.robust.behaviour.tuning;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

public class DistanceMeasures {
    /*
     * Calculates the euclidean distance between the two vectors
     */
    public static double calcEuclidean(DoubleMatrix1D v1, DoubleMatrix1D v2) {
        double total = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            double diff = v1.get(i) - v2.get(i);
            diff = diff * diff;
            total += diff;
        }

        total = Math.sqrt(total);
        return total;
    }

    /*
     * Calculates the euclidean distance between the two vectors
     */
    public static double calcManhattan(DoubleMatrix1D v1, DoubleMatrix1D v2) {
        double total = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            double diff = v1.get(i) - v2.get(i);
            diff = Math.abs(diff);
            total += diff;
        }

//        total = Math.sqrt(total);
        return total;
    }

    public static double calcMahalanobis(DoubleMatrix2D sample, DoubleMatrix1D v1, DoubleMatrix1D v2) {

        double total = 0.0;
        return total;
    }

    public static double calcCosine(DoubleMatrix1D v1, DoubleMatrix1D v2) {
        double cosine = v1.zDotProduct(v2)/Math.sqrt(v1.zDotProduct(v1)*v2.zDotProduct(v2));
        return cosine;
    }

    public static double calcChebyshev(DoubleMatrix1D v1, DoubleMatrix1D v2) {
        double max = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            double diff = Math.abs(v1.get(i) - v2.get(i));
            if(diff > max)
                max = diff;
        }

        return max;
    }

    public static double calcCanberra(DoubleMatrix1D v1, DoubleMatrix1D v2) {
        double total = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            double top = v1.get(i) - v2.get(i);
            top = Math.abs(top);
            double bottom = v1.get(i) + v2.get(i);

            double result = top / bottom;
            String resultStr = "" + result;
            if(resultStr.contains("NaN"))
                result = 0.0;
            total += result;
        }

        return total;
    }
}

