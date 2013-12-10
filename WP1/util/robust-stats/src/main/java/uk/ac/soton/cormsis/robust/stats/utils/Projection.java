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
//      Created Date :          2013-01-22
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.utils;

/**
 * @author Edwin
 */
 public class Projection {

     static public double[] Leverage(double[][] x) {
	 int numRow = x.length;
	 int numCol = x[0].length;
	 double[] h = new double[numRow];
	 double[][] e = decomposedX(x);	 

	 for (int i = 0; i < numRow; i++) {
	     double[] ei = e[i];
	     for (int j = 0; j < numCol; j++) {
		 h[i] += ei[j] * ei[j];
	     }
	 }
	 return h;
     }

     static public double[][] HatMatrix(double[][] x) {
	 double[][] e = decomposedX(x);
	 return MatrixMultiplication.multiply(e,e,false,true);
     }

     static private double[][] decomposedX(double[][] x) {
	 QRDecomposition qr = new QRDecomposition(x);
	 return MatrixMultiplication.multiply(x,qr.getRInverse());
     }
    
}
