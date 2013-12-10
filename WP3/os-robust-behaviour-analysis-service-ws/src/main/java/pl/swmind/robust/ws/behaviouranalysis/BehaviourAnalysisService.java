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
package pl.swmind.robust.ws.behaviouranalysis;

import eu.project.robust.behaviour.analysis.Composition;
import eu.project.robust.behaviour.analysis.UserRoles;
import eu.project.robust.behaviour.exception.RobustBehaviourServiceException;
import eu.project.robust.behaviour.types.*;

import javax.jws.WebService;
import java.util.Date;
import java.util.Set;
import java.sql.Timestamp;


@WebService
public interface BehaviourAnalysisService {


    // macro assessments
    public Composition deriveMacroComposition(String platformID, String communityID, Date date) throws RobustBehaviourServiceException;

    public Set<DeriveTSMacroCompositionsResultEntry> deriveTSMacroCompositions(String platformID, String communityID, Date startDate, Date endDate) throws RobustBehaviourServiceException;

    public int getRoleCount(String platformID, String communityID, int roleID, Date date) throws RobustBehaviourServiceException;

    public double getRoleProportion(String platformID, String communityID, int roleID, Date date) throws RobustBehaviourServiceException;


    // micro assessments
    public Set<GetRolePathResultEntry> getRolePath(String platformID, String communityID, String userid, Date endDate) throws RobustBehaviourServiceException;

    public Set<GetRolePathResultEntry> getTSRolePath(String platformID, String communityID, String userid, Date startDate, Date endDate) throws RobustBehaviourServiceException;

    public UserRoles deriveMicroComposition(String platformID, String communityID, Date date) throws RobustBehaviourServiceException;

    public Set<DeriveTSMicroCompositionsResultEntry> deriveTSMicroCompositions(String platformID, String communityID, Date startDate, Date endDate) throws RobustBehaviourServiceException;


    // UI utility functions
    public Set<String> getPlatformIDs();

    public Set<DeriveCommunityNameEntry> getCommunityIDs(String platformID) throws RobustBehaviourServiceException;

    public Set<String> getUserIDs(String platformID, String communityID, int numUsers) throws RobustBehaviourServiceException;

}
