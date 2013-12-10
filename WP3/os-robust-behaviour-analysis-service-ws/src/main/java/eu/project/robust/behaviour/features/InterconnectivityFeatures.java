/**
 *Copyright 2013 Knowledge Media Institute
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
package eu.project.robust.behaviour.features;


public class InterconnectivityFeatures {
    public double clusteringCoefficient;
    public double mixingRate;
    public double degree;
    public int componentCount;
    public double graphEntropy;

    public double getGraphEntropy() {
        return graphEntropy;
    }

    public void setGraphEntropy(double graphEntropy) {
        this.graphEntropy = graphEntropy;
    }

    public double getClusteringCoefficient() {
        return clusteringCoefficient;
    }

    public void setClusteringCoefficient(double clusteringCoefficient) {
        this.clusteringCoefficient = clusteringCoefficient;
    }

    public double getMixingRate() {
        return mixingRate;
    }

    public void setMixingRate(double mixingRate) {
        this.mixingRate = mixingRate;
    }

    public double getDegree() {
        return degree;
    }

    public void setDegree(double degree) {
        this.degree = degree;
    }

    public int getComponentCount() {
        return componentCount;
    }

    public void setComponentCount(int componentCount) {
        this.componentCount = componentCount;
    }
}
