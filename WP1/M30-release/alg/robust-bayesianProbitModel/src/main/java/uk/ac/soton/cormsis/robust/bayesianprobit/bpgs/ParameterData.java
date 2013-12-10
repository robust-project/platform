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
package uk.ac.soton.cormsis.robust.bayesianprobit.bpgs;

public class ParameterData {
    
    private double[][] beta;
    private double[][] gamma;
    
    public ParameterData() {}
    
    public ParameterData(double[][] b) {
        this.beta = b;
    }
    
    public ParameterData(double[][] b, double[][] g) {
        this.beta = b;
        this.gamma = g;
    }
    
    //beta
    public void setBeta(double[][] b) {
        this.beta = b;
    }
    
    public double[][] getBeta(){
        return this.beta;
    }
    
    //gamma
    public void setGamma(double[][] g) {
        this.gamma = g;
    }
    
    public double[][] getGamma(){
        return this.gamma;
    }
}
