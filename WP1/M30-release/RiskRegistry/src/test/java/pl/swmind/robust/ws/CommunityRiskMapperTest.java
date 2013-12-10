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
package pl.swmind.robust.ws;


import org.junit.BeforeClass;
import org.junit.Test;
import pl.swmind.robust.ws.dataservice.utils.CommunitiesRisksMapper;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.CommunityRisksElement;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

import java.util.*;

import static junit.framework.Assert.*;


public class CommunityRiskMapperTest {

    private static Map<Community, Set<Risk>> mapWithOneElement;
    private static Map<Community, Set<Risk>> mapWithThreeElements;
    private static Community community1;
    private static Community community2;
    private static Community community3;
    private static Risk risk1;
    private static Risk risk2;
    private static Risk risk3;

    private static final String COMMUNITY_NAME_1 = "COMMUNITY_NAME_1";
    private static final String COMMUNITY_NAME_2 = "COMMUNITY_NAME_2";
    private static final String COMMUNITY_NAME_3 = "COMMUNITY_NAME_3";

    private static final String RISK_ID_1 = "b2796ef0-d59d-11e1-9b23-0800200c9a66";
    private static final String RISK_ID_2 = "b54d0ed1-d641-11e1-9b23-0800200c9a66";
    private static final String RISK_ID_3 = "b54d0ed2-d641-11e1-9b23-0800200c9a66";



    @BeforeClass
    public static void setUp() {

        community1 = new Community();
        community1.setName(COMMUNITY_NAME_1);

        risk1 = new Risk();
        risk1.setId(RISK_ID_1);
        Set<Risk> set1 = new HashSet<Risk>();
        set1.add(risk1);

        mapWithOneElement = new HashMap<Community, Set<Risk>>();
        mapWithOneElement.put(community1, set1);


        mapWithThreeElements = new HashMap<Community, Set<Risk>>();
        mapWithThreeElements.put(community1, set1);


        community2 = new Community();
        community2.setName(COMMUNITY_NAME_2);

        risk2 = new Risk();
        risk2.setId(RISK_ID_2);
        Set<Risk> set2 = new HashSet<Risk>();
        set2.add(risk2);
        mapWithThreeElements.put(community2, set2);

        community3 = new Community();
        community3.setName(COMMUNITY_NAME_3);

        risk3 = new Risk();
        risk3.setId(RISK_ID_3);
        Set<Risk> riskSet3 = new HashSet<Risk>();
        riskSet3.add(risk3);
        riskSet3.add(risk2);
        riskSet3.add(risk1);
        mapWithThreeElements.put(community3, riskSet3);
    }

    @Test
    public void mapperWithOneElementTest() {

        List<CommunityRisksElement> listWithOneElement = CommunitiesRisksMapper.map(mapWithOneElement);
        assertNotNull(listWithOneElement);
        assertFalse(listWithOneElement.isEmpty());
        assertEquals(1, listWithOneElement.size());

        CommunityRisksElement communityRisksElement = listWithOneElement.get(0);
        assertNotNull(communityRisksElement);
        assertEquals(communityRisksElement.getCommunity(), community1);

        Set<Risk> risks = listWithOneElement.get(0).getRisks();
        assertEquals(communityRisksElement.getRisks(), risks);
    }

    @Test
    public void mapperWithThreeElementTest() {

        List<CommunityRisksElement> listWithThreeElements = CommunitiesRisksMapper.map(mapWithThreeElements);

        assertNotNull(listWithThreeElements);
        assertFalse(listWithThreeElements.isEmpty());
        assertEquals(3, listWithThreeElements.size());

        Map<Community, Set<Risk>> communityRisksElementMap = new HashMap<Community, Set<Risk>>();

        for (CommunityRisksElement communityRisksElement : listWithThreeElements){
            communityRisksElementMap.put(communityRisksElement.getCommunity(), communityRisksElement.getRisks());
        }

        assertTrue(communityRisksElementMap.containsKey(community3));
        assertTrue(communityRisksElementMap.get(community3).contains(risk3));
        assertTrue(communityRisksElementMap.get(community3).contains(risk2));
        assertTrue(communityRisksElementMap.get(community3).contains(risk1));

        assertTrue(communityRisksElementMap.containsKey(community2));
        assertFalse(communityRisksElementMap.get(community2).contains(risk1));
        assertTrue(communityRisksElementMap.get(community2).contains(risk2));
        assertFalse(communityRisksElementMap.get(community2).contains(risk3));

        assertTrue(communityRisksElementMap.containsKey(community1));
        assertTrue(communityRisksElementMap.get(community1).contains(risk1));
        assertFalse(communityRisksElementMap.get(community1).contains(risk3));
        assertFalse(communityRisksElementMap.get(community1).contains(risk2));

    }
}



