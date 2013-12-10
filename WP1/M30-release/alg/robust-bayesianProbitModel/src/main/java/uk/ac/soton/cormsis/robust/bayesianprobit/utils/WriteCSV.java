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
package uk.ac.soton.cormsis.robust.bayesianprobit.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pah1g10
 */
public class WriteCSV {
    public static void writeCSV(Map<String, List<Number>> mapData, String filePath, String fileName) throws IOException {
        int numCovariate = mapData.values().iterator().next().size();
        String directoryPath = filePath;
        String fullPathName = directoryPath + fileName;
        FileWriter writer = new FileWriter(fullPathName);
        double[][] Y = new double[mapData.size()][numCovariate];
        int i = 0;
        for (Map.Entry<String, List<Number>> e : mapData.entrySet()) {
            writer.append(e.getKey());
            List<Number> y = e.getValue();
            double[] Yi = Y[i++];
            for (int j = 0; j < numCovariate; j++) {
                Yi[j] = y.get(j).doubleValue();
            }
            for (double d : Yi) {
                writer.append(",");
                writer.append(Double.toString(d));
            }
            writer.append("\n");
            writer.flush();
        }
        //close file
        writer.close();
    }
    
    public static void writeCSV(double[][] data, String filePath, String fileName) throws IOException {
        String directoryPath = filePath;
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

    public static void writeCSV(double[] data, String filePath, String fileName) throws IOException {
        String directoryPath = filePath;
        String fullPathName = directoryPath + fileName;
        FileWriter writer = new FileWriter(fullPathName);

        for (int i = 0; i < data.length; i++) {
            writer.append(Double.toString(data[i]));
            writer.append("\n");
            writer.flush();
        }
        //close file
        writer.close();
    }
    
    public static void writeCSV(int[] data, String filePath, String fileName) throws IOException {
        String directoryPath = filePath;
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
    
    public static void writeCSV(String[] data, String filePath, String fileName) throws IOException {
        String directoryPath = filePath;
        String fullPathName = directoryPath + fileName;
        FileWriter writer = new FileWriter(fullPathName);

        for (int i = 0; i < data.length; i++) {
            writer.append(data[i]);
            writer.append("\n");
            writer.flush();
        }
        //close file
        writer.close();
    }
}
