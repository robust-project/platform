/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.giraph.lib;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import junit.framework.TestCase;
import org.apache.giraph.graph.BasicVertex;
import org.apache.giraph.graph.BspUtils;
import org.apache.giraph.graph.Edge;
import org.apache.giraph.graph.GiraphJob;
import org.apache.giraph.graph.GraphState;
import org.apache.giraph.graph.EdgeListVertex;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestTextDoubleDoubleAdjacencyListVertexInputFormat extends TestCase {

  private RecordReader<LongWritable, Text> rr;
  private Configuration conf;
  private TaskAttemptContext tac;
  private GraphState<Text, DoubleWritable, DoubleWritable, BooleanWritable> graphState;

  public void setUp() throws IOException, InterruptedException {
    rr = mock(RecordReader.class);
    when(rr.nextKeyValue()).thenReturn(true).thenReturn(false);
    conf = new Configuration();
    conf.setClass(GiraphJob.VERTEX_CLASS, DummyVertex.class, BasicVertex.class);
    conf.setClass(GiraphJob.VERTEX_INDEX_CLASS, Text.class, Writable.class);
    conf.setClass(GiraphJob.VERTEX_VALUE_CLASS, DoubleWritable.class, Writable.class);
    graphState = mock(GraphState.class);
    tac = mock(TaskAttemptContext.class);
    when(tac.getConfiguration()).thenReturn(conf);
  }

  public void testIndexMustHaveValue() throws IOException, InterruptedException {
    String input = "hi";

    when(rr.getCurrentValue()).thenReturn(new Text(input));
    TextDoubleDoubleAdjacencyListVertexInputFormat.VertexReader<BooleanWritable> vr =
        new TextDoubleDoubleAdjacencyListVertexInputFormat.VertexReader<BooleanWritable>(rr);

    vr.initialize(null, tac);

    try {
      vr.nextVertex();
      vr.getCurrentVertex();
      fail("Should have thrown an IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      assertTrue(iae.getMessage().startsWith("Line did not split correctly: "));
    }
  }

  public void testEdgesMustHaveValues() throws IOException, InterruptedException {
    String input = "index\t55.66\tindex2";

    when(rr.getCurrentValue()).thenReturn(new Text(input));
    TextDoubleDoubleAdjacencyListVertexInputFormat.VertexReader<BooleanWritable> vr =
        new TextDoubleDoubleAdjacencyListVertexInputFormat.VertexReader<BooleanWritable>(rr);
    vr.initialize(null, tac);
    try {
      vr.nextVertex();
      vr.getCurrentVertex();
      fail("Should have thrown an IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      assertTrue(iae.getMessage().startsWith("Line did not split correctly: "));
    }
  }

  public static void setGraphState(BasicVertex vertex, GraphState graphState) throws Exception {
    Class<? extends BasicVertex> c = BasicVertex.class;
    Method m = c.getDeclaredMethod("setGraphState", GraphState.class);
    m.setAccessible(true);
    m.invoke(vertex, graphState);
  }

  public static <I extends WritableComparable, V extends Writable,
      E extends Writable, M extends Writable> void assertValidVertex(Configuration conf,
      GraphState<I, V, E, M> graphState, BasicVertex<I, V, E, M> actual,
      I expectedId, V expectedValue, Edge<I, E>... edges)
      throws Exception {
    BasicVertex<I, V, E, M> expected = BspUtils.createVertex(conf);
    setGraphState(expected, graphState);

    // FIXME! maybe can't work if not instantiated properly
    Map<I, E> edgeMap = Maps.newHashMap();
    for(Edge<I, E> edge : edges) {
      edgeMap.put(edge.getDestVertexId(), edge.getEdgeValue());
    }
    expected.initialize(expectedId, expectedValue, edgeMap, null);
    assertValid(expected, actual);
  }

  public static
  <I extends WritableComparable, V extends Writable, E extends Writable, M extends Writable> void
  assertValid(BasicVertex<I, V, E, M> expected, BasicVertex<I, V, E, M> actual) {
    assertEquals(expected.getVertexId(), actual.getVertexId());
    assertEquals(expected.getVertexValue(), actual.getVertexValue());
    assertEquals(expected.getNumEdges(), actual.getNumEdges());
    List<Edge<I, E>> expectedEdges = Lists.newArrayList();
    List<Edge<I, E>> actualEdges = Lists.newArrayList();
    for(I actualDestId : actual) {
      actualEdges.add(new Edge<I, E>(actualDestId, actual.getEdgeValue(actualDestId)));
    }
    for(I expectedDestId : expected) {
      expectedEdges.add(new Edge<I, E>(expectedDestId, expected.getEdgeValue(expectedDestId)));
    }
    Collections.sort(expectedEdges);
    Collections.sort(actualEdges);
    for(int i = 0; i < expectedEdges.size(); i++) {
      assertEquals(expectedEdges.get(i), actualEdges.get(i));
    }
  }

  public void testHappyPath() throws Exception {
    String input = "Hi\t0\tCiao\t1.123\tBomdia\t2.234\tOla\t3.345";

    when(rr.getCurrentValue()).thenReturn(new Text(input));
    TextDoubleDoubleAdjacencyListVertexInputFormat.VertexReader<BooleanWritable> vr =
        new TextDoubleDoubleAdjacencyListVertexInputFormat.VertexReader<BooleanWritable>(rr);

    vr.initialize(null, tac);
    assertTrue("Should have been able to add a vertex", vr.nextVertex());
    BasicVertex<Text, DoubleWritable, DoubleWritable, BooleanWritable> vertex =
        vr.getCurrentVertex();
    setGraphState(vertex, graphState);
    assertValidVertex(conf, graphState, vertex, new Text("Hi"), new DoubleWritable(0),
        new Edge<Text, DoubleWritable>(new Text("Ciao"), new DoubleWritable(1.123d)),
        new Edge<Text, DoubleWritable>(new Text("Bomdia"), new DoubleWritable(2.234d)),
        new Edge<Text, DoubleWritable>(new Text("Ola"), new DoubleWritable(3.345d)));
    assertEquals(vertex.getNumOutEdges(), 3);
  }

  public void testLineSanitizer() throws Exception {
    String input = "Bye\t0.01\tCiao\t1.001\tTchau\t2.0001\tAdios\t3.00001";

    AdjacencyListVertexReader.LineSanitizer toUpper =
        new AdjacencyListVertexReader.LineSanitizer() {
      @Override
      public String sanitize(String s) {
        return s.toUpperCase();
      }
    };

    when(rr.getCurrentValue()).thenReturn(new Text(input));
    TextDoubleDoubleAdjacencyListVertexInputFormat.VertexReader<BooleanWritable> vr =
        new TextDoubleDoubleAdjacencyListVertexInputFormat.VertexReader<BooleanWritable>(rr, toUpper);

    vr.initialize(null, tac);
    assertTrue("Should have been able to read vertex", vr.nextVertex());
    BasicVertex<Text, DoubleWritable, DoubleWritable, BooleanWritable> vertex =
        vr.getCurrentVertex();
    setGraphState(vertex, graphState);
    assertValidVertex(conf, graphState, vertex,
        new Text("BYE"), new DoubleWritable(0.01d),
        new Edge<Text, DoubleWritable>(new Text("CIAO"), new DoubleWritable(1.001d)),
        new Edge<Text, DoubleWritable>(new Text("TCHAU"), new DoubleWritable(2.0001d)),
        new Edge<Text, DoubleWritable>(new Text("ADIOS"), new DoubleWritable(3.00001d)));

    assertEquals(vertex.getNumOutEdges(), 3);
  }

  public void testDifferentSeparators() throws Exception {
    String input = "alpha:42:beta:99";

    when(rr.getCurrentValue()).thenReturn(new Text(input));
    conf.set(AdjacencyListVertexReader.LINE_TOKENIZE_VALUE, ":");
    TextDoubleDoubleAdjacencyListVertexInputFormat.VertexReader<BooleanWritable> vr =
        new TextDoubleDoubleAdjacencyListVertexInputFormat.VertexReader<BooleanWritable>(rr);

    vr.initialize(null, tac);
    assertTrue("Should have been able to read vertex", vr.nextVertex());
    BasicVertex<Text, DoubleWritable, DoubleWritable, BooleanWritable> vertex =
        vr.getCurrentVertex();
    setGraphState(vertex, graphState);
    assertValidVertex(conf, graphState, vertex, new Text("alpha"), new DoubleWritable(42d),
        new Edge<Text, DoubleWritable>(new Text("beta"), new DoubleWritable(99d)));
    assertEquals(vertex.getNumOutEdges(), 1);
  }

  public static class DummyVertex
      extends EdgeListVertex<Text, DoubleWritable,
      DoubleWritable, BooleanWritable> {
    @Override
    public void compute(Iterator<BooleanWritable> msgIterator) throws IOException {
      // ignore
    }
  }
}
