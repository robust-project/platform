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

import java.util.Date;
import java.util.TreeMap;

public abstract class HealthClassifier {

    /*
     * Classifies the forum for that time step and whether there has been a change in the health indicators
     */
    public abstract HealthClassifierResult classifyForumOnDate(String communityID, int healthIndicator, Date date) throws Exception;

    /*
     * Classifies the forum for the allocated time steps, training the models on the previous time steps
     */
    public abstract TreeMap<Date,HealthClassifierResult> classifyForumInWindow(String communityId, int healthIndicator, Date startDate, Date endDate) throws Exception;

}
