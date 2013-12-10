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

import weka.clusterers.EM;
import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class ClusterTuner {

    /*
     * Given a dataset, the cluster tuner runs three different tuning algorithms and returns an ordered mapping
      * of cluster numbers to silhouette coefficients.
     */
    public static HashMap<String,TreeMap<Integer,Double>> tuneClusters(Instances dataset) {
        HashMap<String,TreeMap<Integer,Double>> methodToClusters = new HashMap<String, TreeMap<Integer, Double>>();

        try {
            // remove the 2nd and 3rd columns from the dataset (as they are highly correlated)
            String clusterMethod = null;

            HashSet<String> clusterModels = new HashSet<String>();
            clusterModels.add("HierarchicalClustering");
            clusterModels.add("K-Means");
            clusterModels.add("EM");

            for (String clusterModel : clusterModels) {

                for (int n = 2; n < 30; n++) {

                    // cluster the dataset
                    if(clusterModel.equals("HierarchicalClustering")) {
                        HierarchicalClusterer k = new HierarchicalClusterer();
                        k.setNumClusters(n);
                        k.buildClusterer(dataset);

                        // assess the effectiveness of the clustering using internal measure
                        // derive cluster memberships
                        TreeMap<Integer, ArrayList<Instance>> clusters = new TreeMap<Integer,ArrayList<Instance>>();
                        for (int i = 0; i < dataset.numInstances(); i++) {
                            Instance inst = dataset.instance(i);
                            int clusterID = k.clusterInstance(inst);
                            if(clusters.containsKey(clusterID)) {
                                ArrayList<Instance> cluster = clusters.get(clusterID);
                                cluster.add(inst);
                                clusters.put(clusterID,cluster);
                            } else {
                                ArrayList<Instance> cluster = new ArrayList<Instance>();
                                cluster.add(inst);
                                clusters.put(clusterID,cluster);
                            }
                        }

                        // calculate the effectiveness of the clustering
                        double sil = EvalMeasures.calcSilhouette(clusters);

                        // add the setting to the map file
                        if(methodToClusters.containsKey(clusterModel)) {
                            TreeMap<Integer,Double> numberToSil = methodToClusters.get(clusterMethod);
                            numberToSil.put(n,sil);
                            methodToClusters.put(clusterMethod,numberToSil);
                        } else {
                            TreeMap<Integer,Double> numberToSil = new TreeMap<Integer, Double>();
                            numberToSil.put(n,sil);
                            methodToClusters.put(clusterMethod,numberToSil);
                        }
                    } else if(clusterModel.equals("K-Means")) {
                        SimpleKMeans k = new SimpleKMeans();
                        k.setNumClusters(n);
                        k.buildClusterer(dataset);

                        // assess the effectiveness of the clustering using internal measure
                        // derive cluster memberships
                        TreeMap<Integer, ArrayList<Instance>> clusters = new TreeMap<Integer,ArrayList<Instance>>();
                        for (int i = 0; i < dataset.numInstances(); i++) {
                            Instance inst = dataset.instance(i);
                            int clusterID = k.clusterInstance(inst);
                            if(clusters.containsKey(clusterID)) {
                                ArrayList<Instance> cluster = clusters.get(clusterID);
                                cluster.add(inst);
                                clusters.put(clusterID,cluster);
                            } else {
                                ArrayList<Instance> cluster = new ArrayList<Instance>();
                                cluster.add(inst);
                                clusters.put(clusterID,cluster);
                            }
                        }

                        // calculate the effectiveness of the clustering
                        double sil = EvalMeasures.calcSilhouette(clusters);

                        // add the setting to the map file
                        if(methodToClusters.containsKey(clusterModel)) {
                            TreeMap<Integer,Double> numberToSil = methodToClusters.get(clusterMethod);
                            numberToSil.put(n,sil);
                            methodToClusters.put(clusterMethod,numberToSil);
                        } else {
                            TreeMap<Integer,Double> numberToSil = new TreeMap<Integer, Double>();
                            numberToSil.put(n,sil);
                            methodToClusters.put(clusterMethod,numberToSil);
                        }
                    } else {

                        EM k = new EM();
                        k.setNumClusters(n);
                        k.buildClusterer(dataset);

                        // assess the effectiveness of the clustering using internal measure
                        // derive cluster memberships
                        TreeMap<Integer, ArrayList<Instance>> clusters = new TreeMap<Integer,ArrayList<Instance>>();
                        for (int i = 0; i < dataset.numInstances(); i++) {
                            Instance inst = dataset.instance(i);
                            int clusterID = k.clusterInstance(inst);
                            if(clusters.containsKey(clusterID)) {
                                ArrayList<Instance> cluster = clusters.get(clusterID);
                                cluster.add(inst);
                                clusters.put(clusterID,cluster);
                            } else {
                                ArrayList<Instance> cluster = new ArrayList<Instance>();
                                cluster.add(inst);
                                clusters.put(clusterID,cluster);
                            }
                        }

                        // calculate the effectiveness of the clustering
                        double sil = EvalMeasures.calcSilhouette(clusters);

                        // add the setting to the map file
                        if(methodToClusters.containsKey(clusterModel)) {
                            TreeMap<Integer,Double> numberToSil = methodToClusters.get(clusterMethod);
                            numberToSil.put(n,sil);
                            methodToClusters.put(clusterMethod,numberToSil);
                        } else {
                            TreeMap<Integer,Double> numberToSil = new TreeMap<Integer, Double>();
                            numberToSil.put(n,sil);
                            methodToClusters.put(clusterMethod,numberToSil);
                        }
                    }
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return methodToClusters;
    }
}
