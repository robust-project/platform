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

package eu.robust.wp2.hadoop.textmining.polecat;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.commandline.DefaultOptionCreator;
import org.apache.mahout.vectorizer.collocations.llr.CollocDriver;

import java.io.IOException;
import java.util.UUID;

public class Collocations extends AbstractJob {

  public static void main(String[] args) throws Exception {
    ToolRunner.run(new Collocations(), args);
  }

  @Override
  public int run(String[] args) throws Exception {

    addInputOption();
    addOutputOption();
    addOption(DefaultOptionCreator.overwriteOption().create());
    addOption(DefaultOptionCreator.numReducersOption().create());

    if (parseArguments(args) == null) {
      return -1;
    }

    HadoopUtil.delete(getConf(), getTempPath());

    Job toSequenceFiles = prepareJob(getInputPath(), getOutputPath("seq"), TextInputFormat.class,
        ToSequenceFilesMapper.class, Text.class, Text.class, SequenceFileOutputFormat.class);

    toSequenceFiles.waitForCompletion(true);

    String[] opts = new String[] {
      "--input", getOutputPath("seq").toString(),
      "--output", getOutputPath("colloc").toString(),
      "--maxRed", getOption("maxRed"),
      "--tempDir", getTempPath().toString(),
      "--maxNGramSize", String.valueOf(3),
      "--minLLR", String.valueOf(50),
      "--minSupport", String.valueOf(2),
      "--preprocess"
    };

    ToolRunner.run(getConf(), new CollocDriver(), opts);

    Job readableOutput = prepareJob(getOutputPath("colloc/ngrams"), getOutputPath("readable"),
        SequenceFileInputFormat.class, IdentityMapper.class, Text.class, DoubleWritable.class, TextOutputFormat.class);

    readableOutput.waitForCompletion(true);

    return 0;
  }

  static class ToSequenceFilesMapper extends Mapper<LongWritable, Text, Text, Text> {
    @Override
    protected void map(LongWritable key, Text line, Context ctx) throws IOException, InterruptedException {
      ctx.write(new Text(UUID.randomUUID().toString()), line);
    }
  }

  static class IdentityMapper extends Mapper<Text,DoubleWritable,Text,DoubleWritable> {}



}
