/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton
// CORMSIS
// Centre of Operational Research, Management Science and Information Systems
// 2012
//
// Copyright in this software belongs to University of Southampton
// CORMSIS
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
//      Created Date :          2012-08-30
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.lossfunction.impl;

import uk.ac.soton.cormsis.robust.stats.lossfunction.spec.IRegressionLoss;
import uk.ac.soton.cormsis.robust.stats.utils.ArrayOperation;
import uk.ac.soton.cormsis.robust.stats.utils.GeneralMatrixOperation;

// Not sure if this should be a static class or not
public class RegressionLoss implements IRegressionLoss{

    double[] data1D;
    double[][] data2D;
    int numRow;
    int numCol;
    int intTotalData;
    double dblTotalData;

    boolean isData2D = false;
    boolean isData1D = false;


    public RegressionLoss(double[] obv) {
	this.data1D = ArrayOperation.copyArray(obv);
	this.numRow = obv.length;
	this.intTotalData = numRow;;
	this.dblTotalData = (double)this.intTotalData;
	this.isData1D = true;
    }

    public RegressionLoss(double[][] obv) {
	this.data2D = GeneralMatrixOperation.copyMatrix(obv);
	this.numRow = obv.length;
	this.numCol = obv[0].length;
	this.intTotalData = numRow * numCol;
	this.dblTotalData = (double)this.intTotalData;
	this.isData2D = true;
    }

@Override
    public double getMSE(double[] pred) {
	if (this.isData1D == true) {
            double SE = 0;
        if (this.numRow != pred.length) {
            throw new IllegalArgumentException("Number of rows are not equal, cannot get MSE");
        } else {
            for (int i = 0; i < this.numRow; i++) {
                    SE += Math.pow(data1D[i] - pred[i], 2);
            }
        }
        return SE / this.dblTotalData;	
	} else {
            throw new IllegalArgumentException("Data initialized was 1 dimensional");
	}	    
    }    

@Override
    public double getMSE(double[][] pred) {
	if (this.isData2D == true) {
            double SE = 0;
        if (this.numRow != pred.length) {
            throw new IllegalArgumentException("Number of rows are not equal, cannot get MSE");
        } else if (this.numCol != pred[0].length) {
            throw new IllegalArgumentException("Number of column are not equal, cannot get MSE");
        } else {
            for (int i = 0; i < this.numRow; i++) {
                double[] data2Di = data2D[i];
                double[] predi = pred[i];
                for (int j = 0; j < this.numCol; j++) {
                    // SE += Math.pow(data2D[i][j] - pred[i][j], 2);
                    SE += (data2Di[j] - predi[j]) * (data2Di[j] - predi[j]);
                }
            }
        }
        return SE / this.dblTotalData;	
	} else {
            throw new IllegalArgumentException("Data initialized was 2 dimensional");
	}	    
    }
    
}