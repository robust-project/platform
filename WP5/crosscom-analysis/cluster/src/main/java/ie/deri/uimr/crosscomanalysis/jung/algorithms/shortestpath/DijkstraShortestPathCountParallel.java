/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package ie.deri.uimr.crosscomanalysis.jung.algorithms.shortestpath;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.algorithms.util.MapBinaryHeap;
import edu.uci.ics.jung.graph.UndirectedGraph;
import org.apache.commons.collections15.Transformer;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 01-Nov-2010
 * Time: 21:49:24
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */
public class DijkstraShortestPathCountParallel<V, E> extends DijkstraDistance<V, E> {

    @SuppressWarnings("unchecked")
    public DijkstraShortestPathCountParallel(UndirectedGraph<V, E> g, Transformer<E, ? extends Number> nev, boolean cached,
                                             int threadsCount, long timeUnitsLimit, TimeUnit unit) throws InterruptedException, ComputationNotFinished {
        super(g, nev, cached);
        // compute all paths during the instantiation - should speed things little bit up, because for
        // group betweenness centrality, we need to compute all of them anyway
        ExecutorService es = Executors.newFixedThreadPool(threadsCount);
        for (V source : g.getVertices()) {
            es.execute(new ShortestPathFinder(source, g.getVertices(), Integer.MAX_VALUE, (SourcePathData) getSourceData(source)));
        }
        es.shutdown();
        if (!es.awaitTermination(timeUnitsLimit, unit)) {
            throw new ComputationNotFinished();
        }
    }

    private class ShortestPathFinder implements Runnable {

        private V source;
        private Collection<V> targets;
        private int numDests;
        private SourcePathData sd;

        /**
         * @param source   the vertex from which distances are to be measured
         * @param numDests the number of distances to measure
         * @param targets  the set of vertices to which distances are to be measured
         * @param sd       source path data container
         */
        private ShortestPathFinder(V source, Collection<V> targets, int numDests, SourcePathData sd) {
            this.source = source;
            this.targets = targets;
            this.numDests = numDests;
            this.sd = sd;
        }

        /**
         * Implements Dijkstra's single-source shortest-path algorithm for
         * weighted graphs.  Uses a <code>MapBinaryHeap</code> as the priority queue,
         * which gives this algorithm a time complexity of O(m lg n) (m = # of edges, n =
         * # of vertices).
         * This algorithm will terminate when any of the following have occurred (in order
         * of priority):
         * <ul>
         * <li> the distance to the specified target (if any) has been found
         * <li> no more vertices are reachable
         * <li> the specified # of distances have been found, or the maximum distance
         * desired has been exceeded
         * <li> all distances have been found
         * </ul>
         */
        @SuppressWarnings("unchecked")
        public void run() {

            Set<V> to_get = new HashSet<V>();
            if (targets != null) {
                to_get.addAll(targets);
                Set<V> existing_dists = sd.getDistances().keySet();
                for (V o : targets) {
                    if (existing_dists.contains(o))
                        to_get.remove(o);
                }
            }

            // if we've exceeded the max distance or max # of distances we're willing to calculate, or
            // if we already have all the distances we need,
            // terminate
            if (sd.reachedMax() ||
                    (targets != null && to_get.isEmpty()) ||
                    (sd.getDistances().size() >= numDests)) {
//                return sd.getDistances();
                return;
            }

            LinkedList<V> visitedVertices = new LinkedList<V>();

            while (!sd.getUnknownVertices().isEmpty() && (sd.getDistances().size() < numDests || !to_get.isEmpty())) {
                Map.Entry<V, Number> p = sd.getNextVertex();
                V v = p.getKey();
                visitedVertices.addFirst(v);

                // count shortest paths to the current node
                if (!v.equals(source)) {
                    sd.countPaths(v);
                }

                double v_dist = p.getValue().longValue();
                to_get.remove(v);
                if (v_dist > max_distance) {
                    // we're done; put this vertex back in so that we're not including
                    // a distance beyond what we specified
                    sd.restoreVertex(v, v_dist);
                    visitedVertices.removeFirst();
                    sd.setReachedMax(true);
                    break;
                }
                sd.setDistReached(v_dist);

                if (sd.getDistances().size() >= max_targets) {
                    sd.setReachedMax(true);
                    break;
                }

                for (E e : getEdgesToCheck(v)) {
                    for (V w : g.getIncidentVertices(e)) {
                        if (!sd.getDistances().containsKey(w)) {
                            double edge_weight = nev.transform(e).longValue();
                            if (edge_weight < 0)
                                throw new IllegalArgumentException("Edges weights must be non-negative");
                            double new_dist = v_dist + edge_weight;
                            if (!sd.getEstimatedDistances().containsKey(w)) {
                                sd.createRecord(w, e, new_dist);
                            } else {
                                double w_dist = sd.getEstimatedDistances().get(w).longValue();
                                if (new_dist < w_dist) { // update tentative distance & path for w
                                    sd.update(w, e, new_dist);
                                } else if (new_dist == w_dist) { // add tentative distance & path for w
                                    sd.add(w, e, new_dist);
                                }
                            }
                        }
                    }
                }
            }
            /*
            * Compute partial dependencies
            * Using Theorem 6 from U. Brandes (2001): 'Faster Algorithm for Betweenness Centrality'
            * Partial dependencies (delta dot) computation is modified so that it includes also source or end nodes as
            * it is described in Puzis et al. (2006).
            */
            // rows - source, columns - dependency (intermediary)
            Map<V, Double> partialDependencies = sd.partialDependencies;
            for (V w : visitedVertices) {
                if (w.equals(source)) continue; // predecessors of a source node are not defined
                Set<V> predecessors = getPredecessors(source, w);
                Double deltaW = partialDependencies.get(w);
                if (deltaW == null) {
                    deltaW = source.equals(w) ? 0d : 1d;
                    partialDependencies.put(w, source.equals(w) ? 0d : 1d);
                } else {
                    partialDependencies.put(w, deltaW + (source.equals(w) ? 0d : 1d));
                }
                for (V v : predecessors) {
                    long sigmaV = getShortestPathCount(source, v);
                    long sigmaW = getShortestPathCount(source, w);
                    double deltaV = 0;
                    if (partialDependencies.containsKey(v)) {
                        deltaV = partialDependencies.get(v);
                    }
                    if (partialDependencies.containsKey(w)) {
                        deltaW = partialDependencies.get(w);
                    }
                    partialDependencies.put(v,
                            deltaV + (((double) sigmaV) * deltaW / sigmaW));
                }
            }
        }
    }

    @Override
    protected SourceData getSourceData(V source) {
        SourceData sd = sourceMap.get(source);
        if (sd == null)
            sd = new SourcePathData(source);
        return sd;
    }

    @SuppressWarnings("unchecked")
    public long getShortestPathCount(V source, V target) {
        if (!(g.containsVertex(source) && g.containsVertex(target)))
            throw new IllegalArgumentException("Source or target are not part of graph " + g);
//        singleSourceShortestPath(source, g.getVertices(), Integer.MAX_VALUE); // run Dijkstra algorithm
        SourcePathData spd = (SourcePathData) sourceMap.get(source);
        return spd.getPathCount(target);
    }

    @SuppressWarnings("unchecked")
    public long getShortestPathCount(V source, V intermediary, V target) {
        if (!(g.containsVertex(source) && g.containsVertex(intermediary) && g.containsVertex(target))) {
            throw new IllegalArgumentException("Source, intermediary, or target are not part of graph " + g);
        }
        /*
        * For sigma source-intermediary-target we use Lemma 1 (Bellman criterion) in
        * 'Brandes, U. (2001): A faster algorithm for betweenneess centrality'
        */
        // intermediary has to be on the shortest path and if it equals source or target it's 0 (by definition)
        Long source2target = getDistanceAsLong(source, target);
        Long source2intermediary = getDistanceAsLong(source, intermediary);
        Long intermediary2target = getDistanceAsLong(intermediary, target);
        // if there's no shortest path, return 0
        if (source2target == null || source2intermediary == null || intermediary2target == null) {
            return 0;
        }
        if (source2target < source2intermediary + intermediary2target) {
            return 0;
        }
        SourcePathData spdSource = (SourcePathData) sourceMap.get(source);
        SourcePathData spdIntermediary = (SourcePathData) sourceMap.get(intermediary);
        long sigmaSI = spdSource.getPathCount(intermediary);
        long sigmaIT = spdIntermediary.getPathCount(target);
        return sigmaSI * sigmaIT;
    }

    @SuppressWarnings("unchecked")
    public Set<V> getPredecessors(V source, V target) {
        if (!g.containsVertex(target) || !g.containsVertex(source)) {
            throw new IllegalArgumentException("Vertices " + target + " or " + source + " are not part of graph " + g);
        }
//        singleSourceShortestPath(target, g.getVertices(), Integer.MAX_VALUE); // run Dijkstra algorithm
        SourcePathData spd = (SourcePathData) sourceMap.get(source);
        return spd.getPredecessors(target);
    }

    @SuppressWarnings("unchecked")
    public double getPartialDependency(V source, V intermediary) {
        if (!g.containsVertex(source) || !g.containsVertex(intermediary)) {
            throw new IllegalArgumentException("Vertices " + intermediary + " or " + source + " are not part of graph " + g);
        }
        SourcePathData spd = (SourcePathData) sourceMap.get(source);
        return spd.getPartialDependency(intermediary); // divide by two (undirected graph)
//        return spd.getPartialDependency(intermediary);
    }

    public Long getDistanceAsLong(V s, V t) {
        Number d = getDistance(s, t);
        if (d == null) {
            return null;
        } else {
            return d.longValue();
        }
    }

    public boolean isConnected() {
        for (V s : g.getVertices()) {
            if (getSPD(s).getAccessibleVerticesCount() < g.getVertexCount()) {
                return false;
            }
        }
        return true;
    }

    public int getAccessibleVerticesCount(V s) {
        return getSPD(s).getAccessibleVerticesCount();
    }

    @SuppressWarnings("unchecked")
    private SourcePathData getSPD(V s) {
        return (SourcePathData) sourceMap.get(s);

    }

    /**
     * For a given source vertex, holds the estimated and final distances,
     * tentative and final assignments of incoming edges on the shortest path from
     * the source vertex, and a priority queue (ordered by estimated distance)
     * of the vertices for which distances are unknown.
     * <p/>
     * Modified version from DijkstraShortestPath so as to store all incoming edges on the shortest
     * paths in order to allow partial dependency computation. This version also counts all shortest paths.
     *
     * @author Joshua O'Madadhain, Vaclav Belak
     */
    protected class SourcePathData extends SourceData {
        protected Map<V, Set<E>> tentativeIncomingEdges;
        protected LinkedHashMap<V, Set<E>> incomingEdges;
        protected Map<V, Long> shortestPathCount;
        protected Map<V, Double> partialDependencies = new HashMap<V, Double>();

        protected SourcePathData(V source) {
            super(source);
            incomingEdges = new LinkedHashMap<V, Set<E>>();
            tentativeIncomingEdges = new HashMap<V, Set<E>>();
//            visitedVertices = new LinkedList<V>();
            shortestPathCount = new HashMap<V, Long>();
            shortestPathCount.put(source, 1l);
        }

        @Override
        public void update(V dest, E tentative_edge, double new_dist) {
            super.update(dest, tentative_edge, new_dist);
            Set<E> edges = tentativeIncomingEdges.get(dest);
            // remove all edges - we find a shorter path
            edges.clear();
            edges.add(tentative_edge);
        }

        public void add(V dest, E tentative_edge, double new_dist) {
            super.update(dest, tentative_edge, new_dist);
            Set<E> edges = tentativeIncomingEdges.get(dest);
            edges.add(tentative_edge);
        }

        @Override
        public Map.Entry<V, Number> getNextVertex() {
            Map.Entry<V, Number> p = super.getNextVertex();
            V v = p.getKey();
            Set<E> incoming = tentativeIncomingEdges.remove(v);
            incomingEdges.put(v, incoming);

            return p;
        }

        public void countPaths(V currentNode) {
            Set<V> predecessors = getPredecessors(currentNode);
            long pathCount = 0;
            for (V predecessor : predecessors) {
                pathCount += shortestPathCount.get(predecessor);
            }
            shortestPathCount.put(currentNode, pathCount);
        }

        @Override
        public void restoreVertex(V v, double dist) {
            super.restoreVertex(v, dist);
            Set<E> incoming = incomingEdges.get(v);
            tentativeIncomingEdges.put(v, incoming);
        }

        @Override
        public void createRecord(V w, E e, double new_dist) {
            super.createRecord(w, e, new_dist);
            Set<E> edges = new HashSet<E>();
            edges.add(e);
            tentativeIncomingEdges.put(w, edges);
        }

        public boolean reachedMax() {
            return this.reached_max;
        }

        public void setReachedMax(boolean rm) {
            this.reached_max = rm;
        }

        public LinkedHashMap<V, Number> getDistances() {
            return distances;
        }

        public Map<V, Number> getEstimatedDistances() {
            return estimatedDistances;
        }

        public void setDistReached(double dr) {
            this.dist_reached = dr;
        }

        public MapBinaryHeap getUnknownVertices() {
            return this.unknownVertices;
        }

        public long getPathCount(V target) {
            if (shortestPathCount.containsKey(target)) {
                return shortestPathCount.get(target);
            } else {
                return 0;
            }
        }

        public Set<V> getPredecessors(V v) {
            Set<V> predecessors = new HashSet<V>();
            Set<E> incoming = incomingEdges.get(v);
            for (E edge : incoming) {
                Collection<V> incVertices = g.getIncidentVertices(edge);
                assert incVertices.size() == 2; // we assume pairs only
                for (V incV : incVertices) {
                    if (!incV.equals(v)) { // we are looking for v's predecessor
                        predecessors.add(incV);
                    }
                }
            }
            return predecessors;
        }

        public double getPartialDependency(V intermediary) {
            if (partialDependencies.containsKey(intermediary)) {
                return partialDependencies.get(intermediary);
            } else {
                return 0;
            }
        }

        public int getAccessibleVerticesCount() {
            return distances.size();
        }
    }

}
