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

import java.sql.Connection;
import java.util.Date;
import java.util.TreeMap;


public abstract class HealthIndicatorRetriever {

    protected Connection connection;


    protected HealthIndicatorRetriever(Connection connection) {
        this.connection = connection;
    }

    public HealthIndicatorRetriever() throws Exception {
    }

    public abstract HealthIndicatorFeatures getHealthIndicators(String communityID, Date date) throws Exception;

    public abstract TreeMap<Date,HealthIndicatorFeatures> getTimeSeriesHealthIndicators(String communityID, Date startDate, Date endDate) throws Exception;

    public abstract double getHealthIndicator(String communityID, int healthIndicator, Date date) throws Exception;

    public abstract double getChurnRate(String communityID, Date date) throws Exception;

    public abstract int getUserCount(String communityID, Date date) throws Exception;

    public abstract double getSeedToNonSeedProportion(String communityID, Date date) throws Exception;

    public abstract double getClusteringCoefficient(String communityID, Date date) throws Exception;

    /*
     * Retrieves a time-series of the given health indicator for a specified community and window
     */
    public abstract TreeMap<Date,Double> getTimeSeriesHealthIndicator(String communityID, int healthIndicator, Date startDate, Date endDate) throws Exception;

    public abstract void close() throws Exception;
}
