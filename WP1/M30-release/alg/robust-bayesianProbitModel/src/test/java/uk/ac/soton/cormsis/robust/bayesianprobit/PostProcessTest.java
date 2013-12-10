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
//      Created Date :          2012-10-31
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.bayesianprobit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import uk.ac.soton.cormsis.robust.bayesianprobit.bpgs.BayesianProbitwGS;
import uk.ac.soton.cormsis.robust.bayesianprobit.bpgs.ModelData;
import uk.ac.soton.cormsis.robust.bayesianprobit.bpgs.ParameterData;
import uk.ac.soton.cormsis.robust.bayesianprobit.bpgs.PostProcess;
import uk.ac.soton.cormsis.robust.bayesianprobit.bpgs.PreProcessData;

/**
 *
 * @author pah1g10
 */
public class PostProcessTest {

    public static void main(String[] args) throws Exception {

        ModelData md = new ModelData();
        
        double[] dropThreshold = {0.2D};
        double activeThreshold = 0D;

        Map<String, List<Number>> mapData = new HashMap<String, List<Number>>();

        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        File file = new File("/home/pah1g10/Dropbox/TestData/testData.csv");

        BufferedReader bufRdr = null;
        bufRdr = new BufferedReader(new FileReader(file));
        String line = null;

        while ((line = bufRdr.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line, ",");
            ArrayList<String> listTemp = new ArrayList<String>();
            while (st.hasMoreTokens()) {
                listTemp.add(st.nextToken());
            }
            data.add(listTemp);
        }

        bufRdr.close();

        int numRow = data.size();
        for (ArrayList<String> x : data) {
            List<Number> b = new ArrayList<Number>();
            for (int i = 1; i < x.size(); i++) {
                b.add(Double.valueOf(x.get(i)));
            }
            mapData.put(x.get(0), b);
        }


        ////------------------------- Get Data -------------------------////
        PreProcessData pd = new PreProcessData(md, mapData, true, false, false, activeThreshold, dropThreshold);
        
//        CholeskyDecomposition cholDecomp = new CholeskyDecomposition(md.getXLearn(),false);
//        double[][] Sigma = cholDecomp.getAinverse();
//        double[][] ActSigma = new CholeskyDecomposition(Sigma).getL();
//        writeCSV(ActSigma,"ActSigma.csv");

//        RealMatrix invSigma = new Array2DRowRealMatrix(MatrixMultiplication.innerProduct(md.getXLearn()));
//        RealMatrix Sigma = new CholeskyDecomposition(invSigma).getSolver().getInverse();
//        double[][] dblSigma = Sigma.getData();
//        writeCSV(dblSigma,"TobySigma.csv");


        ////--------------------- Run Gibbs Sampler --------------------////
        BayesianProbitwGS gs = new BayesianProbitwGS(md.getXLearn(), md.getYLearn(), md.getYLProp());
        ParameterData paramData = gs.returnParamData();


        PostProcess.Predict(md, paramData);
        writeCSV(md.getPLearn(), "PLearn.csv");
        writeCSV(md.getPPred(), "PPred.csv");
        writeCSV(md.getCatLearn(), "CLearn.csv");
        writeCSV(md.getCatPred(), "CPred.csv");
    }

    public static void writeCSV(double[][] data, String fileName) throws IOException {
        String directoryPath = "/home/pah1g10/Dropbox/TestData/";
        String fullPathName = directoryPath + fileName;
        FileWriter writer = new FileWriter(fullPathName);

        for (int i = 0; i < data.length; i++) {
            double[] Datai = data[i];

            for (int j = 0; j < data[0].length; j++) {
                Datai[j] = data[i][j];
            }
            for (double d : Datai) {
                writer.append(",");
                writer.append(Double.toString(d));
            }
            writer.append("\n");
            writer.flush();
        }
        //close file
        writer.close();
    }

    public static void writeCSV(int[] data, String fileName) throws IOException {
        String directoryPath = "/home/pah1g10/Dropbox/TestData/";
        String fullPathName = directoryPath + fileName;
        FileWriter writer = new FileWriter(fullPathName);

        for (int i = 0; i < data.length; i++) {
            writer.append(Integer.toString(data[i]));
            writer.append("\n");
            writer.flush();
        }
        //close file
        writer.close();
    }
}
