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
package eu.project.robust.behaviour.analysis.prediction;

public interface HealthIndicators {

    public final int CHURN_RATE = 0;
    public final int USER_COUNT = 1;
    public final int SEEDS_TO_NON_SEEDS_PROP = 2;
    public final int CC = 4;

    public final String[] INDICATOR_LABELS = {"churn_rate","user_count","seeds_to_non_seeds_prop","inter_clustering_coefficient"};
}
