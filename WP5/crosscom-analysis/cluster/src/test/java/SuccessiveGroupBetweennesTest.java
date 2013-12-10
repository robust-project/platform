/*
 * Copyright (c) 2010-2013 Digital Enterprise Research Institute, NUI Galway
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import org.apache.commons.collections15.TransformerUtils;
import ie.deri.uimr.crosscomanalysis.jung.algorithms.scoring.SuccessiveGroupBetweenness;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 06/02/2011
 * Time: 00:06
 * <b>Acknowledgments:</b>This work was supported by Science Foundation Ireland (SFI) projects
 * Grant No. SFI/08/CE/I1380 (Lion-2) and Grant No. 08/SRC/I1407 (Clique: Graph & Network Analysis Cluster).
 */
public class SuccessiveGroupBetweennesTest {
    private static SuccessiveGroupBetweenness<Integer, Integer> sgb;
    private static UndirectedGraph<Integer, Integer> graph;
    private static Set<Integer> bridge;
    private static Map<Integer, Double> weights;


    @BeforeClass
    public static void createDistance() {
        // first create a graph
        graph = new UndirectedSparseGraph<Integer, Integer>();
        weights = new HashMap<Integer, Double>();
        bridge = new HashSet<Integer>();

        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(6);

        int edgeId = 0;
        graph.addEdge(++edgeId, 1, 2);
        weights.put(edgeId, 1d);
        graph.addEdge(++edgeId, 2, 3);
        weights.put(edgeId, 1d);
        graph.addEdge(++edgeId, 3, 1);
        weights.put(edgeId, 1d);

        graph.addEdge(++edgeId, 4, 5);
        weights.put(edgeId, 1d);
        graph.addEdge(++edgeId, 5, 6);
        weights.put(edgeId, 1d);
        graph.addEdge(++edgeId, 6, 4);
        weights.put(edgeId, 1d);

        graph.addEdge(++edgeId, 2, 4);
        weights.put(edgeId, 1d);
        bridge.add(2);
        bridge.add(4);

        sgb = new SuccessiveGroupBetweenness<Integer, Integer>(graph, TransformerUtils.mapTransformer(weights), false);
    }

    @Test
    public void testGBEquality() {
        assertEquals(sgb.getVertexGroupScore(bridge), sgb.getVertexGroupScore(bridge));
    }

    @Test
    public void testGBBridge() {
        assertEquals(4d, sgb.getVertexGroupScore(bridge), Double.MIN_VALUE);
    }

    @Test
    public void testGBIndividualNodes() {
        for (Integer vertex : graph.getVertices()) {
            Set<Integer> ind = new HashSet<Integer>();
            ind.add(vertex);
            if (bridge.contains(vertex)) {
                assertEquals(6d, sgb.getVertexGroupScore(ind), Double.MIN_VALUE);
            } else {
                assertEquals(0d, sgb.getVertexGroupScore(ind), Double.MIN_VALUE);
            }
        }
    }

    @Test
    public void testNormalizedGB() {
        assertEquals(4 / 6d, sgb.getNormalizedGBC(bridge, null, true), Double.MIN_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDisconnectedGBC() {
        UndirectedGraph<Integer, Integer> g = new UndirectedSparseGraph<Integer, Integer>();
        Map<Integer, Double> w = new HashMap<Integer, Double>();
        g.addVertex(1);
        g.addVertex(2);
        g.addVertex(3);
        g.addVertex(4);
        g.addVertex(5);
        g.addVertex(6);

        int edgeId = 0;
        g.addEdge(++edgeId, 1, 2);
        w.put(edgeId, 1d);
        g.addEdge(++edgeId, 2, 3);
        w.put(edgeId, 1d);
        g.addEdge(++edgeId, 3, 1);
        w.put(edgeId, 1d);

        g.addEdge(++edgeId, 4, 5);
        w.put(edgeId, 1d);
        g.addEdge(++edgeId, 5, 6);
        w.put(edgeId, 1d);
        g.addEdge(++edgeId, 6, 4);
        w.put(edgeId, 1d);

        g.addEdge(++edgeId, 2, 4);
        w.put(edgeId, 1d);

        g.addVertex(7);

        Set<Integer> disconnectedGroup = new HashSet<Integer>();
        disconnectedGroup.add(7);
        disconnectedGroup.add(1);

        SuccessiveGroupBetweenness<Integer, Integer> gb = new SuccessiveGroupBetweenness<Integer, Integer>(g, TransformerUtils.mapTransformer(w), false);

        gb.getVertexGroupScore(disconnectedGroup);
    }

    @Test
    public void testWeights() {
        Map<Integer, Double> w = new HashMap<Integer, Double>();
        w.put(1, 0.1); // 1->2
        w.put(2, 0.1);
        w.put(3, 1d); // 3->1

        w.put(4, 0.1); // 4->5
        w.put(5, 0.1);
        w.put(6, 0.1);

        w.put(7, 0.1); // bridge

        SuccessiveGroupBetweenness<Integer, Integer> gb = new SuccessiveGroupBetweenness<Integer, Integer>(graph, TransformerUtils.mapTransformer(w), false);
        assertEquals(5d, gb.getVertexGroupScore(bridge), Double.MIN_VALUE);
    }
}