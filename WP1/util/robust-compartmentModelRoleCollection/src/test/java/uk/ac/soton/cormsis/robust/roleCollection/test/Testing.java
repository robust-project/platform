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
//      Created Date :          2012-08-05
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.roleCollection.test;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import uk.ac.soton.cormsis.robust.compartmentmodel.impl.CompartmentModel;
import uk.ac.soton.cormsis.robust.compartmentmodel.spec.ICompartmentModel;
import uk.ac.soton.cormsis.robust.roleCollection.impl.RoleCollection;
import uk.ac.soton.cormsis.robust.roleCollection.spec.IRoleCollection;

/**
 *
 * The main class of the compartment model Used for testing
 */
public class Testing {

    /* Even though I am using a logger here, I am using quite a lot of
     * system print so that I can see things easily on the screen for things that
     * just need a little bit of verification
     */
    static Logger log = Logger.getLogger(Testing.class);

    public static void main(String[] args) throws Exception {
        String communityID = "http://forums.sdn.sap.com/uim/forum/264#id";
        String platformName = "SAP";
        // String behaviourServiceURI = "http://robust-demo.softwaremind.pl/robust-behaviour-analysis-service-ws-1.0-SNAPSHOT/robustBehaviourAnalysisService";
        String behaviourServiceURI = "http://robust-www.softwaremind.pl/robust-behaviour-analysis-service-ws-1.0-SNAPSHOT/robustBehaviourAnalysisService";

//        String communityID = "c76ebc42-5b21-41c9-99f4-7f05b926ff0c";
//        communityID = "7d0817fa-31ac-4dca-8017-984426d5b8b2";
//        communityID = "6e7f3d22-8946-4b18-840a-02e184661448";
//        communityID = "8757c3e7-3d37-409d-b8b8-7aa2dfb28226";
//        communityID = "c24348da-09e2-4a0c-a1f5-b233a705fca1";
//        communityID = "89af6521-0db4-452a-8ff9-5c6067bbaf02";
//        communityID = "97520ed0-b56e-4a3b-bfa6-6ce418f65f97";
//        communityID = "22c34a39-541a-4150-8552-056ccc06b4a3";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date initialDate = sdf.parse("2010-08-01");

        Calendar cDate = Calendar.getInstance();
        cDate.setTime(initialDate);
        IRoleCollection RC = new RoleCollection();
        RC.initialize(behaviourServiceURI, communityID, platformName);
//            RC.setPlatform("IBM");
        for (int i = 0; i < 1; i++) {
            try {
            Date date = cDate.getTime();

            ArrayList<double[]> m = new ArrayList<double[]>();

            try {
                m = RC.getMass(communityID, date);
                log.debug("We manage to get the mass in test class");
                log.debug("Number of roles = " + m.get(0).length);
            } catch (Exception e) {
                // log.error("Fail to get mass:", e);
            }
            ArrayList<double[][]> r = new ArrayList<double[][]>();
            try {
                r = RC.getRate(communityID, date);
            } catch (Throwable ex) {
                // log.error("Fail to get rate:" + ex);
            }
            int lastIndex = r.size();

//        log.info("Number of observed mass = " + m.size());
//        log.info("Number of observed rate = " + r.size());

//            System.out.println("Start printing the mass vector");	
//        for (int t = 0; t < m.size(); t++) {
//            System.out.println("Time = " + t);
//            double[][] rateTemp2D = r.get(t);
//            double[] massTemp1D = m.get(t);
//            for (int j = 0; j < massTemp1D.length; j++) {
//                System.out.printf("%9.0f", massTemp1D[j]);
//            }
//            System.out.println(" ");
//            for (int j = 0; j < massTemp1D.length; j++) {
//                for (int k = 0; k < rateTemp2D[0].length; k++) {
//                    System.out.printf("%9.5f", rateTemp2D[j][k]);
//                }
//                System.out.println(" ");
//            }
//            System.out.println(" ");
//        }
            
            r.remove(lastIndex - 1);
            log.debug("Number of observed mass = " + m.size());
            log.debug("Number of observed rate = " + r.size());

            // Test to see whether a valid date was entered
            double[] currentObservation;
            if (m.size() > 5) {
                // Running the compartment model

                int weekAhead = 1;
                                
                // System.out.println("Now, start testing the compartment model element");
                long startTime = System.currentTimeMillis();
                ICompartmentModel D = new CompartmentModel();
                int iteration = (int) 1E5;
                D.initialize(m, r);
                D.computeForecast(weekAhead, iteration);
                currentObservation = D.getCurrentObservation();
                long endTime = System.currentTimeMillis();
                System.out.println("\n Total time taken for the compartmental model "
                        + " to run = " + (endTime - startTime) + " for "
                        + " iteration = " + iteration + "\n");

                System.out.println("\n The optimized value = " + D.getOptimiseWeight(weekAhead));

                // Getting the forcast
                // CMoutput a = D.getForecastSummary();
                // double[][] mean = D.getForecastMean();

                // int numRow = mean.length;
                // int numCol = mean[0].length;

//            System.out.println("Size of output = " + numRow + " and " + numCol);
//
//            System.out.println("the mean from simulation is ");
//            for (int i = 0; i < numRow; i++) {
//                double dblTemp = 0;
//                for (int j = 0; j < numCol; j++) {
//                    dblTemp += mean[i][j];
//                    System.out.printf("%9.1f", mean[i][j]);
//                }
//                System.out.print("  and total = " + dblTemp);
//                System.out.println("");
//            }
//
//	    double[][] uci = D.getForecastUCI();
//            System.out.println("The upper bound");
//            for (int i = 0; i < numRow; i++) {
//                for (int j = 0; j < numCol; j++) {
//                    System.out.printf("%9.1f", uci[i][j]);
//                }
//                System.out.println("");
//            }
//
//	    double[][] lci = D.getForecastLCI();
//            System.out.println("The lower bound");
//            for (int i = 0; i < numRow; i++) {
//                for (int j = 0; j < numCol; j++) {
//                    System.out.printf("%9.1f", lci[i][j]);
//                }
//                System.out.println("");
//            }
                // GroupID, timeStep, threshold
//            for (int i = 0; i < numCol; i++) {
//		System.out.println("Probability of "+i+ " under 10 = " +D.getProbabilityUnderForecast(i,1,10));
//            }

                int[] groupid = {1, 2};
                double[] threshold = {currentObservation[1] * 1.2, currentObservation[2] * 0.8};
//            double[] threshold = {3,40};
//            boolean[] under = {true,true};
//            System.out.println("Probability of the combination of events of true/true= " +D.getProbabilityForecast(groupid,weekAhead,threshold,under));
//            under[1] = false;
//            System.out.println("Probability of the combination of events of true/false= " +D.getProbabilityForecast(groupid,weekAhead,threshold,under));
//            under[0] = false; under[1] = true;
//            System.out.println("Probability of the combination of events of false/true = " +D.getProbabilityForecast(groupid,weekAhead,threshold,under));
//            under[1] = false;
//            System.out.println("Probability of the combination of events of false/false = " +D.getProbabilityForecast(groupid,weekAhead,threshold,under));
//            
//            System.out.println("And now we start to test the or conditions");

                int[] groupid2 = {1, 2, 3, 7};
                double[] threshold2 = {currentObservation[1] * 1.2, currentObservation[2] * 0.8, currentObservation[3] * 0.8, currentObservation[7] * 0.8};
                boolean[] under2 = {false, true, true, true};
                boolean[] andCondition2 = {true, false, false, false};
                System.out.println("Probability with the OR condition = " + D.getProbabilityForecast(groupid2, weekAhead, threshold2, under2, andCondition2) + " at date " + date);
            }
//        try {
//            Map<String, Integer> b = RC.getRoleNameIndex();
//            Iterator<Entry<String, Integer>> it = b.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>) it.next();
//                System.out.println("Name of the role " + pairs.getKey() + " and the index " + pairs.getValue());
//            }
//        } catch (Throwable ex) {
//            log.error("Error getting the role name to index" + ex.getMessage(), ex);
//        }
//        log.info("the date = " + date);

            } catch (Exception e) {
            }
            cDate.add(Calendar.DATE, -7);
            
        }
    }
}
