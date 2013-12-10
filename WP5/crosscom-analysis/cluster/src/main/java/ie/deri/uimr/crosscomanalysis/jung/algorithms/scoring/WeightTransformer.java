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

package ie.deri.uimr.crosscomanalysis.jung.algorithms.scoring;

import org.apache.commons.collections15.Transformer;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Author: vaclav.belak@deri.org
 * Date: 02-Nov-2010
 * Time: 20:19:37
 * ©2010 Digital Enterprise Research Institute, NUI Galway.
 */
public class WeightTransformer<V, Double> implements Transformer<V, Double> {
    private Double maxValue;
    private boolean normalize;
    private Map<V, Double> weights;

    public WeightTransformer(boolean normalize) {
        this.normalize = normalize;

    }

    public WeightTransformer() {
    }

    public Double transform(V v) {
        return null;
    }
}