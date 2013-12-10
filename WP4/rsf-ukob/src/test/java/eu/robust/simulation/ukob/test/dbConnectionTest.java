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
 
package eu.robust.simulation.ukob.test;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import eu.robust.simulation.dbconnection.*;



public class dbConnectionTest {

	@Test
	public void test() {
		Connectdb db = new Connectdb("jdbc:oracle:thin:@//robustdb:1521/orcl", "SAP_DATEN", "none");
		//Connectdb db = new Connectdb("jdbc:postgresql://localhost/postgres","postgres", "testdb");
		
		boolean success = true;
		
		// forum thread statistics
		System.out.println("gathering forum specific thread statistics");
		
		String[] colsTs = {"contributor", "forumuri", "posts"};
		String queryTs = "select contributor, forumuri, count(*) as posts from questions group by contributor, forumuri";
		List<Map<String,String>> userstatsTs = null;
		userstatsTs = db.getTable(colsTs, queryTs);
		success = (success && (userstatsTs != null));
		
		// forum reply statistics
		System.out.println("gathering forum specific reply statistics");
		String[] colsRs = {"contributor", "forumuri", "posts"};
		String queryRs = "select contributor, forumuri, count(*) as posts from replies group by contributor, forumuri";
		List<Map<String,String>> userstatsRs = null;
		userstatsRs = db.getTable(colsRs, queryRs);
		success = (success && (userstatsRs != null));
		
		System.out.println("gathering user statistics");
		
		String[] cols = {"contributor", "actduration", "questions", "replies", "points", "points0", "points1", "points2", "points3"};
		String query = "select * from userstats";
		List<Map<String,String>> userstats = db.getTable(cols, query);
		success = (success && (userstats != null));
		
		
		
		

		
		db.disconnectdb();
		
		if (! success) fail("No results");
	}

}
