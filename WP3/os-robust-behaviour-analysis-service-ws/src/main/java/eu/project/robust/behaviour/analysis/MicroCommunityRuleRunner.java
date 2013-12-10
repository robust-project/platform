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
package eu.project.robust.behaviour.analysis;

import java.sql.Connection;
import java.util.Date;
import java.util.TreeMap;

public abstract class MicroCommunityRuleRunner {

    protected  Connection connection;

    protected MicroCommunityRuleRunner() throws Exception {
    }

    public abstract UserRoles deriveComposition(String communityID, Date date) throws Exception;

    public abstract TreeMap<Date,UserRoles> deriveTimeSeriesComposition(String communityID, Date startDate, Date endDate) throws Exception;

    public abstract void close() throws Exception;

}
