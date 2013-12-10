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

package ie.deri.uimr.crosscomanalysis.jung.algorithms.layout;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;

import java.awt.geom.Point2D;
import java.util.LinkedHashMap;

/**
 * Aggregate layout with deterministic iteration over its sublayouts.
 *
 * @param <V>
 * @param <E>
 * @author vaclav.belak@deri.org
 */
public class AggregateLayout<V, E> extends edu.uci.ics.jung.algorithms.layout.AggregateLayout<V, E> {

    public AggregateLayout(Graph<V, E> g) {
        super(new StaticLayout<V, E>(g));
        super.layouts = new LinkedHashMap<Layout<V, E>, Point2D>();
    }
}