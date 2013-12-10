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
package eu.project.robust.behaviour.rules;

import eu.project.robust.behaviour.features.Feature;
import eu.project.robust.behaviour.features.Levels;
import java.util.HashMap;

public class Rule implements Levels {
    public HashMap<String,Feature> features;
    public HashMap<String,Integer> featureLevels;
    public String role;


    public Rule(String role, HashMap<String,Integer> featureLevels, HashMap<String,Feature> features) {
        this.role = role;
        this.featureLevels = featureLevels;
        this.features = features;
    }

    // Runs the rule using the input feature values of the user
    public boolean runRule(HashMap<String,Double> featureValues) {
        boolean pass = true;

        // go through each of the features in the levels and match the feature values to those levels
        for (String feature : featureLevels.keySet()) {

            int level = featureLevels.get(feature);
            double value = featureValues.get(feature);
            Feature featureObj = features.get(feature);
//            System.out.println(feature + " : " + level + " -> " + value);

            if(level == LOW) {
                if(!featureObj.isLow(value)) {
//                    System.out.println("checked low");
                    pass = false;
                }

            // mid
            } else if(level == MID) {
                if(!featureObj.isMid(value)) {
//                    System.out.println("checked mid");
                    pass = false;
                }

            // high
            } else {
                if(!featureObj.isHigh(value)) {
//                    System.out.println("checked high");
                    pass = false;
                }
            }
        }
        return pass;
    }

    public String toString() {
        String output = role + " : ";
        for (String feature : featureLevels.keySet()) {
            output += feature + "=" + featureLevels.get(feature) + ", ";
        }
        return output;
    }
}
