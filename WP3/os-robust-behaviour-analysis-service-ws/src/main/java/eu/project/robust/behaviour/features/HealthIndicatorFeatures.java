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

public class HealthIndicatorFeatures {

    double churnRate;
    int userCount;
    double seedToNonSeedsProp;
    double avgCC;

    public HealthIndicatorFeatures(double churnRate, int userCount, double seedToNonSeedsProp, double avgCC) {
        this.churnRate = churnRate;
        this.userCount = userCount;
        this.seedToNonSeedsProp = seedToNonSeedsProp;
        this.avgCC = avgCC;
    }

    public HealthIndicatorFeatures() {
    }

    public double getChurnRate() {
        return churnRate;
    }

    public void setChurnRate(double churnRate) {
        this.churnRate = churnRate;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public double getSeedToNonSeedsProp() {
        return seedToNonSeedsProp;
    }

    public void setSeedToNonSeedsProp(double seedToNonSeedsProp) {
        this.seedToNonSeedsProp = seedToNonSeedsProp;
    }

    public double getAvgCC() {
        return avgCC;
    }

    public void setAvgCC(double avgCC) {
        this.avgCC = avgCC;
    }

    @Override
    public String toString() {
        return "" +
                + churnRate +
                "," + userCount +
                "," + seedToNonSeedsProp +
                "," + avgCC;
    }
}
