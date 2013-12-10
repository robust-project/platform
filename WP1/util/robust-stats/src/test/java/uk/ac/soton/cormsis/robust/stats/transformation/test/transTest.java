/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton CORMSIS, 2012
//
// Copyright in this software belongs to University of Southampton
// IT Innovation Centre of Gamma House, Enterprise Road, 
// Chilworth Science Park, Southampton, SO16 7NS, UK.
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
//      Created By :            Edwin
//      Created Date :          2012-01-18
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.stats.transformation.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import uk.ac.soton.cormsis.robust.stats.transformation.impl.BasisFunction;
import uk.ac.soton.cormsis.robust.stats.transformation.impl.Standardize;
import uk.ac.soton.cormsis.robust.stats.transformation.spec.IBasisFunction;
import uk.ac.soton.cormsis.robust.stats.transformation.spec.IStandardize;
import uk.ac.soton.cormsis.robust.stats.utils.display;

/**
 *
 * @author Edwin
 */
public class transTest {
 
    
        public static void main(String[] args) throws Exception {

        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

        // The data used in the original paper
        // Please define your own data path
        File file = new File("/home/et4g08/data/diabetes.csv");

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

        int numCol = data.get(0).size();
        int numRow = data.size();

        double[][] x = new double[numRow][numCol - 1];
        double[] y = new double[numRow];

        for (int i = 0; i < numRow; i++) {
            ArrayList<String> listTemp = data.get(i);
            for (int j = 0; j < numCol - 1; j++) {
                x[i][j] = Double.valueOf(listTemp.get(j));
            }
            y[i] = Double.valueOf(listTemp.get(numCol - 1));
        }

	// End of getting the data
        
	IBasisFunction bf = new BasisFunction(x);
	double[][] pureQuadX = bf.getPurePolynomial(x,2);
	System.out.println("The quadratic of X");
	display.print(pureQuadX);

	double[][] interX = bf.getInteraction();
	System.out.println("The interaction of X");
	display.print(interX);	
	System.out.println("The number of columns after interaction = " + interX[0].length);

	double[][] quadX = bf.getQuadratic();
	System.out.println("The quadratic of X");
	display.print(quadX);	
	System.out.println("");
	System.out.println("The number of columns after quadratic = " + quadX[0].length);

	IStandardize std = new Standardize(x);
        
        }
}
