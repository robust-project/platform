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

import weka.core.Instance;
import weka.core.Instances;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public abstract class DatasetBuilder {
    protected Connection connection;


    protected DatasetBuilder(Connection connection) {
        this.connection = connection;
    }

    public DatasetBuilder() {
    }

    public abstract Instances buildCommunityDataset(String communityID, Date date) throws Exception;

    public abstract HashMap<String,Instance> buildUserInstances(String communityID, Date date) throws Exception;

    public abstract HashSet<String> getCommunityUsers(String communityID) throws Exception;

    public abstract void close() throws Exception;
}
