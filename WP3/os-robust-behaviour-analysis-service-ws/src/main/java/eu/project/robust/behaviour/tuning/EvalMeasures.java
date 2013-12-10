/**
 *Copyright 2013 Knowledge Media Institute
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
package eu.project.robust.behaviour.tuning;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import weka.core.Instance;

import java.util.ArrayList;
import java.util.TreeMap;

public class EvalMeasures {
    public static double calcSilhouette(TreeMap<Integer, ArrayList<Instance>> clusters) {

        // convert each instance into a numeric representation : i.e. vector form
        TreeMap<Integer, ArrayList<DoubleMatrix1D>> vClusters = new TreeMap<Integer, ArrayList<DoubleMatrix1D>>();
        for (Integer clusterID : clusters.keySet()) {
            ArrayList<Instance> cluster = clusters.get(clusterID);
            ArrayList<DoubleMatrix1D> vCluster = new ArrayList<DoubleMatrix1D>();
            for (Instance instance : cluster) {
                DoubleMatrix1D v = new DenseDoubleMatrix1D(instance.numAttributes());
                for (int i = 0; i < instance.numAttributes(); i++) {
                    double attVal = instance.value(i);
                    v.set(i,attVal);
                }
                vCluster.add(v);
            }
            vClusters.put(clusterID,vCluster);
        }


        // work out the silhouette coefficient for the clustering
        double silTally = 0;
        double sil = 0;
        for (Integer clusterID : vClusters.keySet()) {

            // for each object i work out its average distance to all over items in the same cluster
            for (DoubleMatrix1D v : vClusters.get(clusterID)) {
                double a = 0;
                ArrayList<DoubleMatrix1D> vCluster = vClusters.get(clusterID);
                for (DoubleMatrix1D vA : vCluster) {
                    if(!v.equals(vA)) {
                        a += DistanceMeasures.calcEuclidean(v, vA);
                    }
                }
                if(vCluster.size() > 1) {
                    a /= (double) (vCluster.size() - 1);
                } else {
                    a = 1;
                }

                // for each object i work out the average distance to all over iterms in different clusters, take the minimum distance
                TreeMap<Integer,Double> bDistances = new TreeMap<Integer,Double>();
                for (Integer clusterIDA : vClusters.keySet()) {
                    if(!clusterID.equals(clusterIDA)) {
                        double b = 0;
                        for (DoubleMatrix1D vA : vClusters.get(clusterIDA)) {
                            b += DistanceMeasures.calcEuclidean(v, vA);
                        }

                        b /= (double) vClusters.get(clusterIDA).size();
                        bDistances.put(clusterIDA,b);
                    }
                }

                // work out the min distance to other clusters: b
                double bFinal = 10000000000000.0;
                for (Double b : bDistances.values()) {
                    if(b < bFinal)
                        bFinal = b;
                }

                // derive the silhouette coefficient for i
                double denom = bFinal;
                if(a > bFinal)
                    denom = a;

                double s = (bFinal - a) / denom;

//                System.out.println(s);

                silTally++;
                sil += s;
            }
        }
        sil /= silTally;

        return sil;

    }
}
