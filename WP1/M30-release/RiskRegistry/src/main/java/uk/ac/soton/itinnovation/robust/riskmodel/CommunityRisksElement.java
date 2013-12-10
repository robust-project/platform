/** 
*Copyright 2013 Software Mind SA
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
package uk.ac.soton.itinnovation.robust.riskmodel;

import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

import java.util.Set;

/**
 * User: rafal
 * Date: 23.07.12
 */
public class CommunityRisksElement {

    private Community community = null;
    private Set<Risk> risks = null;

    public Community getCommunity() {
        return community;
    }

    public Set<Risk> getRisks() {
        return risks;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public void setRisks(Set<Risk> risks) {
        this.risks = risks;
    }
}
