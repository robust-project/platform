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
package uk.ac.soton.cormsis.robust.bayesianprobit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.ac.soton.cormsis.robust.bayesianprobit.bpgs.BayesianProbitwGS;
import uk.ac.soton.cormsis.robust.bayesianprobit.utils.ActiveFilter;
import uk.ac.soton.cormsis.robust.stats.utils.GibbsMatrixOperation;
import uk.ac.soton.cormsis.robust.stats.utils.display;

/**
 *
 * @author pah1g10
 */
public class ElementTest {

    public static void main(String[] args) {

//        String[] strID = {"A", "B", "C", "D"};
//        double[] dblP = {5,69.5,69.5,100};
//        
//        int[] rank = PreProcessData.indexRanking(strID, dblP, false);
//        System.out.println("Rank vector: "); display.print(rank);
//        List<Integer> index = BayesianProbitwGS.grep(69.5,dblP); int numOcc = index.size();
//        int[] intIndex = new int[numOcc];
//        for (int i=0; i<numOcc; i++) {
//            intIndex[i] = index.get(i).intValue();
//        }
//        String[] strOcc = new String[numOcc];
//        for (int i=0; i<numOcc; i++) {
//            strOcc[i] = strID[index.get(i).intValue()];
//        }        
//        System.out.println("Index found: "); display.print(strOcc);

//        double[][] mat = BayesianProbitwGS.fill(Double.POSITIVE_INFINITY, 4, 3);
//        System.out.println("Filled matrix: "); display.print(mat);

//        double[] dblY = {1,2,0,2};
//        double[][] yMat = BayesianProbitwGS.createBinClsMatrix(dblY, 3);
//        System.out.println("Create binary y matrix: "); display.print(yMat);

        String[] strID = {"A", "B", "C"};
        double[][] dblP = {{5, 69.5, 69.5, 100},
            {0, 0, 0, 0},
            {10, 0, 0, 0}};
        Map<String, List<Number>> mapData = new HashMap<String, List<Number>>();
        for (int i = 0; i < dblP.length; i++) {
            List<Number> lstData = new ArrayList<Number>();
            for (int j = 0; j < dblP[0].length; j++) {
                lstData.add(dblP[i][j]);
            }
            mapData.put(strID[i], lstData);
        }
        System.out.println("original data: "); display.print(mapData);
        
         double activeThreshold = 6;
         int numLeadFeature = dblP[0].length;
         
         Map<String, List<Number>> mapRefData = ActiveFilter.activeSum(mapData, activeThreshold, numLeadFeature);
         System.out.println("refined data: "); display.print(mapRefData);
    }
    
}
