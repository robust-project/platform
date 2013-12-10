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

public class HealthRegressorResult {

    private double healthVal;
    private int healthIndicator;
    private String communityID;
    private Date date;

    public HealthRegressorResult(double healthVal, int healthIndicator, String communityID, Date date) {
        this.healthVal = healthVal;
        this.healthIndicator = healthIndicator;
        this.communityID = communityID;
        this.date = date;
    }

    public HealthRegressorResult() {
    }

    public double getHealthVal() {
        return healthVal;
    }

    public void setHealthVal(double healthVal) {
        this.healthVal = healthVal;
    }

    public int getHealthIndicator() {
        return healthIndicator;
    }

    public void setHealthIndicator(int healthIndicator) {
        this.healthIndicator = healthIndicator;
    }

    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "HealthRegressorResult{" +
                "healthVal=" + healthVal +
                ", healthIndicator=" + healthIndicator +
                ", communityID='" + communityID + '\'' +
                ", date=" + date +
                '}';
    }
}
