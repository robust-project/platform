/*
 * Copyright 2013 ROBUST project consortium
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package eu.robust.wp2.hadoop.textmining;

import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.cf.taste.hadoop.similarity.item.ItemSimilarityJob;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.LoglikelihoodSimilarity;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.CooccurrenceCountSimilarity;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.CosineSimilarity;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.PearsonCorrelationSimilarity;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.TanimotoCoefficientSimilarity;

public class CooccurrenceJob extends AbstractJob {

  public static void main(String[] args) throws Exception {
    ToolRunner.run(new CooccurrenceJob(), args);
  }

  @Override
  public int run(String[] args) throws Exception {

    addInputOption();
    addOutputOption();
    addOption("similarity", null, "choose one of: {cooccurrences,cosine,pearson-correlation,jaccard,loglikelihoodratio}", true);
    addOption("requiredTermsPerDoc", null, "number of terms a doc must have to be included", String.valueOf(2));
    addOption("maxTermsPerDoc", null, "maximum number of terms to include per doc", String.valueOf(1000));
    addOption("numTopTerms", null, "number of top terms per term", String.valueOf(10));

    if (parseArguments(args) == null) {
      return -1;
    }

    HadoopUtil.delete(getConf(), getTempPath());

    String similarity = getOption("similarity");
    Class similarityClass = null;
    if ("cooccurrences".equals(similarity)) {
      similarityClass = CooccurrenceCountSimilarity.class;
    } else if ("cosine".equals(similarity)) {
      similarityClass = CosineSimilarity.class;
    } else if ("pearson-correlation".equals(similarity)) {
      similarityClass = PearsonCorrelationSimilarity.class;
    } else if ("jaccard".equals(similarity)) {
      similarityClass = TanimotoCoefficientSimilarity.class;
    } else if ("loglikelihoodratio".equals(similarity)) {
      similarityClass = LoglikelihoodSimilarity.class;
    }

    if (similarityClass == null) {
      System.err.println("Unknown similarity");
      return -1;
    }


    String[] opts = new String[] {
      "--input", getInputPath().toString(),
      "--output", getOutputPath().toString(),
      "--tempDir", getTempPath().toString(),
      "--similarityClassname", similarityClass.getName(),
      "--minPrefsPerUser", getOption("requiredTermsPerDoc"),
      "--maxPrefsPerUser", getOption("maxTermsPerDoc"),
      "--maxSimilaritiesPerItem", getOption("numTopTerms")
    };

    return ToolRunner.run(getConf(), new ItemSimilarityJob(), opts);
  }
}
