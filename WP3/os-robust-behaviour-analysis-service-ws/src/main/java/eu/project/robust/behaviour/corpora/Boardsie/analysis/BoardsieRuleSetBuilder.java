
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
package eu.project.robust.behaviour.corpora.Boardsie.analysis;

import eu.project.robust.behaviour.features.Feature;
import eu.project.robust.behaviour.features.Levels;
import eu.project.robust.behaviour.rules.Rule;
import eu.project.robust.behaviour.rules.RuleSetBuilder;

import java.util.HashMap;


public class BoardsieRuleSetBuilder extends RuleSetBuilder implements Levels {

    /*
     * Returns the set of rules and their features (including level mappings) for IBM Connections
     */
    public HashMap<String,Rule> getRuleSet(HashMap<String,Feature> features) {
        // Initiate the ruleset
        HashMap<String,Rule> ruleSet = new HashMap<String,Rule>();

        // 0 - Responding Engager
        HashMap<String,Integer> role0 = new HashMap<String,Integer>();
        role0.put("engagement",MID);
        role0.put("initiation",LOW);
        Rule role0Rule = new Rule("Lurker",role0,features);
        ruleSet.put(role0Rule.role,role0Rule);

        // 1 - Focussed Ignorer
        HashMap<String,Integer> role1 = new HashMap<String,Integer>();
        role1.put("engagement",LOW);
        role1.put("focus",LOW);
        Rule role1Rule = new Rule("Lurker",role1,features);
        ruleSet.put(role1Rule.role,role1Rule);

        // 2 - Participating Engager
        HashMap<String,Integer> role2 = new HashMap<String,Integer>();
        role2.put("engagement",MID);
        role2.put("initiation",MID);
        Rule role2Rule = new Rule("Contributor",role2,features);
        ruleSet.put(role2Rule.role,role2Rule);

        // 3 - Initiating Enthusiast
        HashMap<String,Integer> role3 = new HashMap<String,Integer>();
        role3.put("engagement",HIGH);
        role3.put("initiation",HIGH);
        Rule role3Rule = new Rule("Super User",role3,features);
        ruleSet.put(role3Rule.role,role3Rule);

        // 4 - Responding Enthusiast
        HashMap<String,Integer> role4 = new HashMap<String,Integer>();
        role4.put("engagement",HIGH);
        role4.put("initiation",LOW);
        Rule role4Rule = new Rule("Follower",role4,features);
        ruleSet.put(role4Rule.role,role4Rule);

        // 5 - Distributed Ignorer
        HashMap<String,Integer> role5 = new HashMap<String,Integer>();
        role5.put("engagement",LOW);
        role5.put("focus",HIGH);
        Rule role5Rule = new Rule("BroadCaster",role5,features);
        ruleSet.put(role5Rule.role,role5Rule);

        // 6 - Mixed Participating Ignorer
        HashMap<String,Integer> role6 = new HashMap<String,Integer>();
        role6.put("focus",MID);
        role6.put("engagement",LOW);
        role6.put("initiation",MID);
        Rule role6Rule = new Rule("Daily User",role6,features);
        ruleSet.put(role6Rule.role,role6Rule);

        // 8 - Initiating Engager
        HashMap<String,Integer> role8 = new HashMap<String,Integer>();
        role8.put("engagement",MID);
        role8.put("initiation",HIGH);
        Rule role8Rule = new Rule("Leader",role8,features);
        ruleSet.put(role8Rule.role,role8Rule);

        // 9 - Mixed Initiating Ignorer
        HashMap<String,Integer> role9 = new HashMap<String,Integer>();
        role9.put("engagement",LOW);
        role9.put("focus",MID);
        role9.put("initiation",HIGH);
        Rule role9Rule = new Rule("Celebrity",role9,features);
        ruleSet.put(role9Rule.role,role9Rule);

        // 10 - Participating Enthusiast
        HashMap<String,Integer> role10 = new HashMap<String,Integer>();
        role10.put("engagement",HIGH);
        role10.put("initiation",MID);
        Rule role10Rule = new Rule("Contributor",role10,features);
        ruleSet.put(role10Rule.role,role10Rule);

        return ruleSet;
    }

    /*
     * Returns the rules for the community roles
     */
    public HashMap<String,Integer> getRoleToIDMapping() {
         HashMap<String,Integer> roleToIDMapping = new HashMap<String, Integer>();

        // new mapping
        roleToIDMapping.put("Unmatched",-1);
        roleToIDMapping.put("Inactive",0);
        roleToIDMapping.put("Lurker",1);
        roleToIDMapping.put("Contributor",2);
        roleToIDMapping.put("Super User",3);
        roleToIDMapping.put("Follower",4);
        roleToIDMapping.put("BroadCaster",5);
        roleToIDMapping.put("Daily User",6);
        roleToIDMapping.put("Leader",7);
        roleToIDMapping.put("Celebrity",8);

        return roleToIDMapping;

    }

    public HashMap<Integer,String> getIDToRoleMapping() {

        HashMap<Integer,String> idToRoleMapping = new HashMap<Integer,String>();

        // new mapping
        idToRoleMapping.put(-1,"Unmatched");
        idToRoleMapping.put(0,"Inactive");
        idToRoleMapping.put(1,"Lurker");
        idToRoleMapping.put(2,"Contributor");
        idToRoleMapping.put(3,"Super User");
        idToRoleMapping.put(4,"Follower");
        idToRoleMapping.put(5,"BroadCaster");
        idToRoleMapping.put(6,"Daily User");
        idToRoleMapping.put(7,"Leader");
        idToRoleMapping.put(8,"Celebrity");

        // additional role ids for lifecycles include:
        // -2 = not joined yet
        // 10 = churned

        return idToRoleMapping;

    }
}
