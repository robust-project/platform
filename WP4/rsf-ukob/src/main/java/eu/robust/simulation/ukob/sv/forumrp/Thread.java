/*
 * Copyright 2012 WeST Institute - University of Koblenz-Landau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Created by       	: Felix Schwagereit 
 *
 * Creation Time    	: 07.12.2011
 *
 * Created for Project  : ROBUST
 */
 
package eu.robust.simulation.ukob.sv.forumrp;

import eu.robust.simulation.entities.Reply;


public class Thread extends eu.robust.simulation.entities.Thread {

	private boolean hasQualityReply = false; 
	private int timeToQualityReply = 0;

	
	
	public Thread(User Ccreator, int Cforum) {
		super(Ccreator, Cforum);
	}
	
	@Override
	public void addReply(Reply rep) {
		if (rep.getQuality()>0) {
			hasQualityReply=true;
			timeToQualityReply =  rep.creationtime - this.creationtime;
			//System.out.println("CreationTime: "+this.creationtime+" timespan " +timeToQualityReply);
		}
		super.addReply(rep);
	}
	
	public boolean hasQualityReply() {
		return hasQualityReply;
	}
	
	public int timeToQualityReply() {
		return timeToQualityReply;
	}

}
