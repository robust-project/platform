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

/**
 * User: mcr266
 * Date: 08/08/2012
 * Time: 10:47
 * Comment: Adapted the interface to only include 4 health indicators
 */
public abstract class HealthIndicatorBuilder {

    protected Connection connection;

    protected HealthIndicatorBuilder(Connection connection) {
        this.connection = connection;
    }

    public HealthIndicatorBuilder() throws Exception{
    }

    public abstract void writeHealthIndicators(String communityID, Date date) throws Exception;

    public abstract double getChurnRate(String communityID, Date date);

    public abstract int getUserCount(String communityID, Date date);

    public abstract double getSeedToNonSeedProportion(String communityID, Date date);

    public abstract double getClusteringCoefficient(String communityID, Date data);

    public abstract void close() throws Exception;

}
