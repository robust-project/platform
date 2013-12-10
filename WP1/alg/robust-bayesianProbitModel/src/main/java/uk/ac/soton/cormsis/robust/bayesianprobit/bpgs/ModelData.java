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
//      Created Date :          2012-11-08
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.bayesianprobit.bpgs;

import java.util.List;

public class ModelData {

    
    private String[] refinedLabels;
    private double[][] refinedObservations;
    private double[][] xLearn;
    private double[][] xPred;
    private double[] yLearn;
    private double[] yPred;
    private double[] yLearnProportion;
    private boolean getHistorical;
    private double[][] pLearn;
    private double[][] pPred;
    private int[] catLearn;
    private int[] catPred;
    private double[][] epLearn;
    private double[][] epPred;

    public ModelData() {
    }

    public ModelData(String[] rLabels,
                     double[][] rObs,
                     double[][] xL,
                     double[][] xP,
                     double[] yL) {
        this.refinedLabels = rLabels;
        this.xLearn = xL;
        this.xPred = xP;
        this.yLearn = yL;
    }

    public ModelData(String[] rLabels,
                     double[][] rObs,
                     double[][] xL,
                     double[][] xP,
                     double[] yL,
                     double[] yP) {
        this.refinedLabels = rLabels;
        this.xLearn = xL;
        this.xPred = xP;
        this.yLearn = yL;
        this.yPred = yP;
    }

    // Refined labels
    protected void setRefinedLabels(String[] rLabels) {
        this.refinedLabels = rLabels;
    }
    public String[] getRefinedLabels() {
        return this.refinedLabels;
    }

    // Refined observations
    protected void setRefinedObservations(double[][] rObs) {
        this.refinedObservations = rObs;
    }
    public double[][] getRefinedObservations() {
        return this.refinedObservations;
    }
    
    // xLearn
    protected void setXLearn(double[][] xL) {
        this.xLearn = xL;
    }
    public double[][] getXLearn() {
        return this.xLearn;
    }

    // xPred
    protected void setXPred(double[][] xP) {
        this.xPred = xP;
    }
    public double[][] getXPred() {
        return this.xPred;
    }

    // yLearn
    protected void setYLearn(double[] yL) {
        this.yLearn = yL;
    }
    public double[] getYLearn() {
        return this.yLearn;
    }

    // yPred
    protected void setYPred(double[] yP) {
        this.yPred = yP;
    }
    public double[] getYPred() {
        return this.yPred;
    }
    
    // yLearnProportion
    protected void setYLearnProp(double[] yLProp) {
        this.yLearnProportion = yLProp;
    }
    public double[] getYLProp() {
        return this.yLearnProportion;
    }
    
//    // getHistorical;
//    protected void setGetHistorical(boolean gHist) {
//        this.getHistorical = gHist;
//    }
//    public boolean getGetHistorical() {
//        return this.getHistorical;
//    }
    
    // pLearn;
    protected void setPLearn(double[][] pL) {
        this.pLearn = pL;
    }
    public double[][] getPLearn() {
        return this.pLearn;
    }
    
    // pPred;
    protected void setPPred(double[][] pP) {
        this.pPred = pP;
    }
    public double[][] getPPred() {
        return this.pPred;
    }
    
    // catLearn;
    protected void setCatLearn(int[] cL) {
        this.catLearn = cL;
    }
    public int[] getCatLearn() {
        return this.catLearn;
    }
    
    // catPred;
    protected void setCatPred(int[] cP) {
        this.catPred = cP;
    }
    public int[] getCatPred() {
        return this.catPred;
    }
    
    // epLearn;
    protected void setEPLearn(double[][] epL) {
        this.epLearn = epL;
    }
    public double[][] getEPLearn() {
        return this.epLearn;
    }
    
    // epPred;
    protected void setEPPRed(double[][] epP) {
        this.epPred = epP;
    }
    public double[][] getEPPred() {
        return this.epPred;
    }
}
