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
package eu.project.robust.behaviour.features;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

import java.util.HashMap;

public class Binner {
    public static HashMap<String, Feature> getFeatures(Instances dataset) {
        HashMap<String,Feature> features = new HashMap<String,Feature>();
        try {

            // work out the bins for the instances in the dataset
            for (int i = 0; i < dataset.numAttributes(); i++) {
                int[] atts = new int[1];
                atts[0] = i;

                Discretize d = new Discretize();
                d.setAttributeIndicesArray(atts);
                d.setBins(3);
                d.setUseEqualFrequency(true);
                d.setInputFormat(dataset);

                double[] cutPoints = d.getCutPoints(i);

                try {
                    if(cutPoints.length == 2) {
                        Feature feature = new Feature(dataset.attribute(i).name(),cutPoints[0],cutPoints[1]);
                        features.put(dataset.attribute(i).name(),feature);
                    } else if (cutPoints.length == 1) {
                        Feature feature = new Feature(dataset.attribute(i).name(),0,cutPoints[0]);
                        features.put(dataset.attribute(i).name(),feature);
                    }

                } catch(Exception e) {
                    Feature feature = new Feature(dataset.attribute(i).name(),0,0);
                    features.put(dataset.attribute(i).name(),feature);
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return features;
    }
}
