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
package eu.project.robust.behaviour.rules;

import eu.project.robust.behaviour.features.Feature;

import java.util.HashMap;

public abstract class RuleSetBuilder {

    public abstract HashMap<String,Rule> getRuleSet(HashMap<String,Feature> features);

    public abstract HashMap<String,Integer> getRoleToIDMapping();

    public abstract HashMap<Integer,String> getIDToRoleMapping();
}
