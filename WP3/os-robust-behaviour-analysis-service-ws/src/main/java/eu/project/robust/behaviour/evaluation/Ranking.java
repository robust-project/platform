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
package eu.project.robust.behaviour.evaluation;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.text.DecimalFormat;
import java.util.*;


public class Ranking {



    public static String computeNDCG(Instances dataset, Classifier l) {
        // compute idcg
        int[] ks = {1, 5, 10, 20, 50, 100};
        double avgNDCG = 0;
        String output = "";

        StringBuffer result = new StringBuffer();

        for (int k : ks) {
            // build treemap (sorts by actual volume)
            Map<String,Double> truthRank = new TreeMap<String,Double>();
            for (int i = 0; i < dataset.numInstances(); i++) {
                Instance inst = dataset.instance(i);
                truthRank.put(inst.toString(),inst.classValue());
            }
            truthRank = sortByValue(truthRank);
            double idcg = Ranking.getDCG(truthRank, truthRank, k);

            // build ranking for predictions
            Map<String,Double> regressRank = new TreeMap<String,Double>();
            for (int i = 0; i < dataset.numInstances(); i++) {
                Instance inst = dataset.instance(i);
                try {
                    Double pred = l.classifyInstance(inst);
                    regressRank.put(inst.toString(),pred);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            regressRank = sortByValue(regressRank);
            double dcg = Ranking.getDCG(regressRank, truthRank, k);

            // compute scores
//            System.out.println("DCG = " + dcg + ", IDCG = " + idcg);
            double ncdg = dcg / idcg;
            output += ncdg + ",";

            avgNDCG += ncdg;

            result.append(ncdg + ",");
        }
//        System.out.println(output.substring(0,output.length()-1));
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        avgNDCG /= 6.0;

        result.append(Double.valueOf(twoDForm.format(avgNDCG)));
        return  result.toString();
    }

    public static double getDCG(Map<String,Double> ranking, Map<String,Double> truthRanking, int k) {

        double sum = 0;

        int count =1;
        for (String inst : ranking.keySet()) {

            // work out the fav score
            int rankI = getPosition(inst, truthRanking);
            double fav = ranking.size() - rankI + 1;

            // work out the log denominator
            double denom = Math.log(1 + count);

            sum += fav / denom;

            count++;
            if(count > k)
                break;
        }

        sum /= (double)k;
        return sum;
    }


    public static int getPosition(String instance, Map<String,Double> truthRanking) {
        int  i = 1;
        for (String s : truthRanking.keySet()) {
            if(s.equals(instance)) {
                break;
            }
            i++;
        }

        return i;
    }


    public static Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {

            public int compare(Object o1, Object o2) {
                int order = ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
                order *= -1;
                return order;
            }
        });

        // logger.info(list);
        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
