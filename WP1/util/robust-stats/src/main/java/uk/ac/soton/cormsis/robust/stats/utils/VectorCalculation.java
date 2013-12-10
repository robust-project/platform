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
//      Created Date :          2012-09-21
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.utils;

/**
 * A static class to calculate standard operation on a vector
 *
 * @author Edwin
 * @version 4th
 */
public class VectorCalculation {

    public VectorCalculation() {};

    /**
     * Adding a constant to the vector
     * 
     * @param x vector
     * @param s constant
     * @return vector of x+s
     */
    static public double[] add(final double[] x, final double s) {
        int max = x.length;
        double[] y = new double[max];
        for (int i = 0; i < max; i++) {
            y[i] = x[i] + s;
        }
        return y;
    }

    /**
     * Adding two vector element wise
     *
     * @param x vector
     * @param y vector
     * @return new vector
     */
    static public double[] add(final double[] x, final double[] y) {
        checkDimension(x, y);
        int max = x.length;
        double[] z = new double[max];
        for (int i = 0; i < max; i++) {
            z[i] = x[i] + y[i];
        }
        return z;
    }

    static private void checkDimension(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Vector: Wrong Dimension");
        }
    }

    /**
     * Return the Pearson correlation between two vector
     *
     * @param X First input vector
     * @param Y Second input vector
     * @return \rho
     */
    static public double correlation(double[] X, double[] Y) {
        checkDimension(X, Y);
        // int max = X.length;
        double muX = mean(X);
        double muY = mean(Y);
        double cov = covariance(X, muX, Y, muY);
	double sd = Math.sqrt(variance(X,muX)) * Math.sqrt(variance(Y,muY));
        return cov /= sd;
    }

    /**
     * Finds the covariance between two vector given the mean
     *
     * @param X vector 1
     * @param muX mean of vector 1
     * @param Y vector 2
     * @param muY mean of vector 2
     * @return \sigma_{x,y}
     */
    static public double covariance(double[] X, double muX, double[] Y, double muY) {
        double value = 0;
        checkDimension(X, Y);
        int max = X.length;
        for (int i = 0; i < max; i++) {
            value += (X[i] - muX) * (Y[i] - muY);
        }
        return value /= (double) max;
    }

    static public double[] divide(final double[] x, final double s) {
        int max = x.length;
        double[] y = new double[max];
        for (int i = 0; i < max; i++) {
            y[i] = x[i] / s;
        }
        return y;
    }

    static public double[] divide(final double[] x, final double[] y) {
        checkDimension(x, y);
        int max = x.length;
        double[] z = new double[max];
        for (int i = 0; i < max; i++) {
            z[i] = x[i] / y[i];
        }
        return z;
    }

    /**
     * Naive norm of a vector
     *
     * @param x vector
     * @return ( \sum_{i} x_{i}^{2} )^{\frac{1}{2}}
     */
    static public double fastNorm(double[] x) {
        double a = 0;
        for (double i : x) {
            a += i * i;
        }
        return Math.sqrt(a);
    }

    /**
     * Compute \| x \| using hypot, very slow
     *
     * @param x vector
     * @return (\sum_{i=1}^{n} x_{i}^{2})^{\frac{1}{2})}
     */
    static public double hypotNorm(double[] x) {
        double a = 0;
        for (double s : x) {
            a = Math.hypot(a, s);
        }
        return a;
    }

    /**
     * The inner product of two vector
     *
     * @param x vector
     * @param y vector
     * @return scalar
     */
    static public double innerProduct(final double[] x, final double[] y) {
        checkDimension(x, y);
        int max = x.length;
        double s = 0;
        for (int i = 0; i < max; i++) {
            s += x[i] * y[i];
        }
        return s;
    }

    /**
     * Something simple to calculate the population mean
     *
     * @param x a double vector
     * @return mean value
     */
    static public double mean(final double[] x) {
	return sum(x) / (double)x.length;
    }

    /**
     * Something simple to calculate mean given weights
     * 
     * @param x a double vector
     * @param w a double wegith vector
     * @return mean value
     */
    static public double weightedMean(final double[] x,final double[] w) {
	return mean(multiply(x,w));
    }

    /**
     * Multiply a vector with by a constant
     *
     * @param x vector
     * @param s constant
     * @return x*s
     */
    static public double[] multiply(final double[] x, final double s) {
        int max = x.length;
        double[] z = new double[max];
        for (int i = 0; i < max; i++) {
            z[i] = x[i] * s;
        }
        return z;
    }

    static public double[] multiply(final double[] x, final double[] y) {
	checkDimension(x,y);
        int numRow = x.length;
        double[] z = new double[numRow];
        for (int i = 0; i < numRow; i++) {
            z[i] = x[i] * y[i];
        }
        return z;
    }

    /**
     * Implementation of nrm2 in blas
     *
     * @param x vector
     * @return \| x \|
     */
    static public double norm(final double[] x) {
        double a;
        double absX;
        double scale = 0;
        double ssq = 1;

        for (double s : x) {
            if (s != 0) {
                absX = Math.abs(s);
                if (scale < absX) {
                    ssq *= (scale / absX) * (scale / absX);
                    ssq++;
                    scale = absX;
                } else {
                    ssq += (absX / scale) * (absX / scale);
                }
            }
        }
        a = scale * Math.sqrt(ssq);
        return a;
    }

    /**
     * What is this doing here?
     * 
     * @param x input vector
     * @return normalized vector
     */
    static public double[] normalize(final double[] x) {
        int max = x.length;
        double[] y = new double[max];
        double s = 0;
        for (int i = 0; i < max; i++) {
            s += x[i];
        }
        for (int i = 0; i < max; i++) {
            y[i] = x[i] / s;
        }
        return y;
    }

    /**
     * The outer product of two vector
     *
     * @param x vector
     * @param y vector
     * @return x*y^{T}
     */
    static public double[][] outerProduct(final double[] x, final double[] y) {
        checkDimension(x, y);
        int max = x.length;
        double[][] z = new double[max][max];
        for (int i = 0; i < max; i++) {
            for (int j = i + 1; j < max; j++) {
                z[i][j] = x[i] * y[j];
                z[j][i] = z[i][j];
            }
            z[i][i] = x[i] * y[i];
        }
        return z;
    }

    /**
     * Return the Spearman rank correlation between two vector
     *
     * @param X First input vector
     * @param Y Second input vector
     * @return \rho
     */
    static public double rankCorrelation(final double[] X, final double[] Y) {
	checkDimension(X,Y);
	return correlation(ranking.orderedRank(X),ranking.orderedRank(Y));
    }

    /**
     * Subtract a vector from a constant
     * 
     * @param s constant
     * @param x vector
     * @return vector of s-x
     */
    static public int[] subtract(int s, int[] x) {
        int max = x.length;
        int[] y = new int[max];
        for (int i = 0; i < max; i++) {
            y[i] = s - x[i];
        }
        return y;
    }
    
    /**
     * Subtract a vector from a constant
     *
     * @param x vector
     * @param s constant
     * @return vector of s-x
     */
    static public double[] subtract(final double s, final double[] x) {
        int max = x.length;
        double[] y = new double[max];
        for (int i = 0; i < max; i++) {
            y[i] = s - x[i];
        }
        return y;
    }

    /**
     * Subtract a constant from a vector
     *
     * @param x vector
     * @param s constant
     * @return vector of x-s
     */
    static public double[] subtract(final double[] x, final double s) {
        int max = x.length;
        double[] y = new double[max];
        for (int i = 0; i < max; i++) {
            y[i] = x[i] - s;
        }
        return y;
    }

    /**
     * Subtract two vector element wise
     *
     * @param x vector
     * @param y vector
     * @return new vector
     */
    static public double[] subtract(final double[] x, final double[] y) {
        checkDimension(x, y);
        int max = x.length;
        double[] z = new double[max];
        for (int i = 0; i < max; i++) {
            z[i] = x[i] - y[i];
        }
        return z;
    }

    /**
     * Sum over a vector
     *
     * @param x vector
     * @return \sum_{i} x_{i}
     */
    static public double sum(final double[] x) {
        double value = 0;
        for (double s : x) {
            value += s;
        }
        return value;
    }

    /**
     * Return the variance of the vector
     *
     * @param X the input vector
     * @return variance
     */
    static public double variance(final double[] X) {
	return variance(X,mean(X));
    }

    /**
     * Finds the variance of the vector given the mean of it
     *
     * @param x vector
     * @param mu mean of the vector
     * @return variance
     */
    static public double variance(final double[] x, final double mu) {
        double value = 0;
        for (double s : x) {
            value += (s - mu) * (s - mu);
        }
        return value /= (double) x.length;
    }
}
