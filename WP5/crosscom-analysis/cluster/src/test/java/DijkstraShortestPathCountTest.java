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

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import ie.deri.uimr.crosscomanalysis.jung.algorithms.shortestpath.DijkstraShortestPathCount;
import org.apache.commons.collections15.TransformerUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 05/02/2011
 * Time: 23:36
 * <p/>
 * <b>Acknowledgments:</b>This work was supported by Science Foundation Ireland (SFI) projects
 * Grant No. SFI/08/CE/I1380 (Lion-2) and Grant No. 08/SRC/I1407 (Clique: Graph & Network Analysis Cluster).
 */
public class DijkstraShortestPathCountTest {
    private static DijkstraShortestPathCount<Integer, Integer> distance;
    private static Graph<Integer, Integer> graph;

    @BeforeClass
    public static void createDistance() {
        // first create a graph
        graph = new UndirectedSparseGraph<Integer, Integer>();

        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(6);

        int edgeId = 1;
        graph.addEdge(edgeId++, 1, 2);
        graph.addEdge(edgeId++, 2, 3);
        graph.addEdge(edgeId++, 3, 1);

        graph.addEdge(edgeId++, 4, 5);
        graph.addEdge(edgeId++, 5, 6);
        graph.addEdge(edgeId++, 6, 4);

        graph.addEdge(edgeId, 2, 4);

        distance = new DijkstraShortestPathCount(graph, TransformerUtils.constantTransformer(1), true);
    }

    @Test
    public void testSubsequentCalls() {
        assertEquals(distance.getDistanceAsLong(1, 2), distance.getDistanceAsLong(2, 1));
        assertEquals(distance.getDistanceAsLong(1, 1), distance.getDistanceAsLong(1, 1));
        assertEquals(distance.getDistanceAsLong(1, 2), distance.getDistanceAsLong(1, 2));
    }

    @Test
    public void testIndividualDistances() {
        assertEquals(0d, distance.getDistance(1, 1));
        assertEquals(1d, distance.getDistance(1, 2));
        assertEquals(2d, distance.getDistance(1, 4));
        assertEquals(1d, distance.getDistance(2, 3));
    }

    @Test
    public void testConnectedComponent() {
        assertTrue(distance.isConnected());
    }

    @Test
    public void testDistances() {
        for (Integer s : graph.getVertices()) {
            assertEquals(distance.getDistance(s, s), distance.getDistance(s, s));
            assertEquals(0d, distance.getDistance(s, s));
            for (Integer t: graph.getVertices()) {
                assertTrue(distance.getDistanceAsDouble(s, t) >= 0);
            }
        }
    }

    @Test
    public void testPartialDependencies() {
        // partial dependencies include also source nodes!
        for (Integer s : graph.getVertices()) {
            assertEquals(5d, distance.getPartialDependency(s, s), Double.MIN_VALUE); // by definition
        }
        assertEquals(1d, distance.getPartialDependency(1, 3), Double.MIN_VALUE);
        assertEquals(4d, distance.getPartialDependency(1, 2), Double.MIN_VALUE);
    }

    @Test
    public void testPathCounts() {
        for (Integer s : graph.getVertices()) {
            for (Integer t: graph.getVertices()) {
                assertEquals(1, distance.getShortestPathCount(s, t));
            }
        }
    }

    @Test
    public void testDisconnectedGraph() {
        Graph<Integer, Integer> graph = new UndirectedSparseGraph<Integer, Integer>();

        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);
        graph.addVertex(6);

        int edgeId = 1;
        graph.addEdge(edgeId++, 1, 2);
        graph.addEdge(edgeId++, 2, 3);
        graph.addEdge(edgeId++, 3, 1);

        graph.addEdge(edgeId++, 4, 5);
        graph.addEdge(edgeId++, 5, 6);
        graph.addEdge(edgeId, 6, 4);

        DijkstraShortestPathCount dist = new DijkstraShortestPathCount(graph, TransformerUtils.constantTransformer(1), true);

        assertFalse(dist.isConnected());
    }
}