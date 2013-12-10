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
//      Created Date :          2012-11-13
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.bayesianprobit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
//import pl.swmind.robust.gibbs.buffer.utils.SnapshotPeriod;
import uk.ac.soton.cormsis.robust.bayesianprobit.bpgs.ModelData;
import uk.ac.soton.cormsis.robust.bayesianprobit.bpgs.RunModel;
import uk.ac.soton.cormsis.robust.bayesianprobit.utils.WriteCSV;
//import uk.ac.soton.cormsis.robust.sap.postgresql.userfeature.impl.GetUserFeature;

/**
 *
 * @author pah1g10
 */
public class RunModelTest {

    public static void main(String[] args) throws Exception {

        //Variables currently required from UI:  
        boolean isHistorical = true;
        boolean isPercentile = true;
        boolean isActiveSum = false;
        double activeThreshold = 10D;
        double[] dropThreshold = {0.33D, 0.67D};
        int b = 500000;
        int r = 10000;

//        String wd = "C:/Users/Philippa/Dropbox/"; 
        String wd = "/home/pahiscock/Dropbox/";

        //Create map data:
        Map<String, List<Number>> mapData = new HashMap<String, List<Number>>();

        // Read data from CSV file:
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
//        File file = new File("/home/pah1g10/Dropbox/Data_TestJavaGS/testData.csv");
        File file = new File(wd + "/Data_TestJavaGS/testData.csv");
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
            List<Number> a = new ArrayList<Number>();
            for (int i = 1; i < x.size(); i++) {
                a.add(Double.valueOf(x.get(i)));
            }
            mapData.put(x.get(0), a);
        }

        ////--------------------- Initialise Probit GS ---------------------////
        RunModel pgs = new RunModel(mapData, isHistorical, isPercentile, isActiveSum, activeThreshold, dropThreshold, b, r);
        ////------------------- Get Resulting Model Data -------------------////
        ModelData md = pgs.returnModelData();
//        System.out.println("Refined labels: ");display.print(md.getRefinedLabels());

        // Write resulting model data to CSV:
        String filePath = wd + "/Data_TestJavaGS/Results/";
        WriteCSV.writeCSV(md.getXLearn(), filePath, "pgsTest_norm_xL.csv");
        WriteCSV.writeCSV(md.getXPred(), filePath, "pgsTest_norm_xP.csv");
        WriteCSV.writeCSV(md.getYLearn(), filePath, "pgsTest_norm_yL.csv");
        if (isHistorical != false) {
            WriteCSV.writeCSV(md.getYPred(), filePath, "pgsTest_norm_yP.csv");
        }
        WriteCSV.writeCSV(md.getPLearn(), filePath, "pgsTest_norm_pL.csv");
        WriteCSV.writeCSV(md.getPPred(), filePath, "pgsTest_norm_pP.csv");
        WriteCSV.writeCSV(md.getCatLearn(), filePath, "pgsTest_norm_cL.csv");
        WriteCSV.writeCSV(md.getCatPred(), filePath, "pgsTest_norm_cP.csv");
    }
}