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
 *   limitations under the License.
 */

package eu.robust.wp2.hadoop.graph.conversion;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

public class SymmetricText extends AbstractJob {
    
  @Override
  public int run(String[] args) throws Exception {

    addInputOption();
    addOutputOption();

    Map<String,String> parsedArgs = parseArguments(args);
    if (parsedArgs == null) {
      return -1;
    }

    Job convert = prepareJob(getInputPath(), getOutputPath(), TextInputFormat.class, EdgesMapper.class,
        IntWritable.class, IntWritable.class, LinesReducer.class, Text.class, NullWritable.class,
        TextOutputFormat.class);

    convert.waitForCompletion(true);

    return 0;
  }

  static class EdgesMapper extends Mapper<LongWritable,Text,IntWritable,IntWritable> {
    
    private static Pattern SEP = Pattern.compile("[\t ]");
    
    @Override
    protected void map(LongWritable key, Text value, Context ctx) throws IOException, InterruptedException {
      String[] parts = SEP.split(value.toString());
      int v1 = Integer.parseInt(parts[0]);
      int v2 = Integer.parseInt(parts[1]);
      ctx.write(new IntWritable(v1), new IntWritable(v2));
      ctx.write(new IntWritable(v2), new IntWritable(v1));
    }                                                                                                           
  }   
  
  static class LinesReducer extends Reducer<IntWritable,IntWritable,Text,NullWritable> {
    @Override
    protected void reduce(IntWritable vertex, Iterable<IntWritable> neighbors, Context ctx) 
        throws IOException, InterruptedException {
      StringBuilder buffer = new StringBuilder();
      buffer.append(vertex.get());
      for (IntWritable neighbor : neighbors) {
        buffer.append(" ").append(neighbor.get());
      }
      ctx.write(new Text(buffer.toString()), NullWritable.get());
    }
  }

  public static void main(String[] args) throws Exception {
    ToolRunner.run(new SymmetricText(), args);
  }
}
                                           
