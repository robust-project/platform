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
package eu.project.robust.behaviour.types;

import eu.project.robust.behaviour.features.HealthIndicatorFeatures;
import java.util.Date;

public class DeriveTSHealthIndicatorFeaturesResultEntry {

    private Date date;

    private HealthIndicatorFeatures healthIndicatorFeatures;


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public HealthIndicatorFeatures getHealthIndicatorFeatures() {
        return healthIndicatorFeatures;
    }

    public void setHealthIndicatorFeatures(HealthIndicatorFeatures healthIndicatorFeatures) {
        this.healthIndicatorFeatures = healthIndicatorFeatures;
    }
}
