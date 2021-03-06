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

package eu.robust.wp2.hadoop.graph.preprocessing;

import java.io.IOException;
import java.util.regex.Pattern;

import eu.robust.wp2.hadoop.graph.model.UndirectedEdge;
import eu.robust.wp2.hadoop.graph.model.Vertex;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.Pair;

/**
 * Simplifies a graph. That is: remove loops, aggregate edges to {@link eu.robust.wp2.hadoop.graph.model.UndirectedEdge }. The input file
 * format is a {@link TextInputFormat}
 * 
 * This job accepts two input arguments
 * 
 * <pre>
 *  input
 *  output
 * </pre>
 * 
 * The output is a {@link SequenceFile} containing a {@link eu.robust.wp2.hadoop.graph.model.UndirectedEdge} as key
 * and a {@link NullWritable} as value.
 */
public class SimplifyGraphJob extends AbstractJob {

  public static void main(String[] args) throws Exception {
    ToolRunner.run(new SimplifyGraphJob(), args);
  }

  @Override
  public int run(String[] args) throws Exception {
    addInputOption();
    addOutputOption();

    if (parseArguments(args) == null) {
      return -1;
    }

    Job simplify = prepareJob(getInputPath(), getOutputPath(), TextInputFormat.class, SimplifyGraphMapper.class,
        UndirectedEdge.class, NullWritable.class, SimplifyGraphReducer.class, UndirectedEdge.class, NullWritable.class,
        SequenceFileOutputFormat.class);
    simplify.waitForCompletion(true);

    return 0;
  }

  /** Bins edges by an ordered membership set. Scatters edges with at least two vertices in the membership set.*/
  public static class SimplifyGraphMapper extends Mapper<Object, Text, UndirectedEdge, NullWritable> {

    private static final Pattern SEPARATOR = Pattern.compile(",");

    @Override
    public void map(Object key, Text line, Context ctx) throws IOException, InterruptedException {
      try {
        String[] tokens = SEPARATOR.split(line.toString());
        Pair<Vertex,Vertex> vertices = new Pair<Vertex,Vertex>(new Vertex(Long.parseLong(tokens[0])),
            new Vertex(Long.parseLong(tokens[1])));
        Vertex one = vertices.getFirst();
        Vertex two = vertices.getSecond();
        // remove loops and un-direct edges
        if (!one.equals(two)) {
          ctx.write(new UndirectedEdge(one, two), NullWritable.get());
        }
      } catch (NumberFormatException e) {
        //ignore unparseable lines
      }
    }
  }

  public static class SimplifyGraphReducer extends Reducer<UndirectedEdge, NullWritable, UndirectedEdge, NullWritable> {
    @Override
    protected void reduce(UndirectedEdge edge, Iterable<NullWritable> values, Context ctx)
        throws IOException, InterruptedException {
      ctx.write(edge, NullWritable.get());
    }
  }
}
