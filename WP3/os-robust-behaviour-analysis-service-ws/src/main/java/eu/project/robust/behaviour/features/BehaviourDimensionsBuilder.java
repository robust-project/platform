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

import eu.project.robust.behaviour.analysis.UserRoles;

import java.util.Date;

public interface BehaviourDimensionsBuilder {

    /*
    * Writes the behaviour dimensions into the db for the platform.
    */
    public void buildGlobalFeatures(Date date) throws Exception;

    public void buildCommunityFeatures(String communityID, Date date) throws Exception;

    public UserRoles buildUserRoles(String communityID, Date date) throws Exception;


}
