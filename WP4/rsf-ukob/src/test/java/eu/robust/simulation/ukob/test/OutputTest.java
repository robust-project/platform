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
 * Creation Time    	: 16.01.2012
 *
 * Created for Project  : ROBUST
 */
 
package eu.robust.simulation.ukob.test;

import static org.junit.Assert.fail;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;

import eu.robust.simulation.dbconnection.*;
import eu.robust.simulation.io.OutputWriter;



public class OutputTest {

	@Test
	public void test() {

		
		
		
//		OutputWriter out = new OutputWriter("testoutput");
//		out.write("test2");
//		out.write("test3");
//		out.close();
		
		
		if (false) fail("No results");
	}
	
	
	@Test
	public void jsonWriteTest() {
		
		Map<String, String> avgReplyTimePerForum = new HashMap<String, String>();
		
		avgReplyTimePerForum.put("100","0.3335");
		avgReplyTimePerForum.put("101","0.5");
		
		
		Gson gson = new Gson();
		 
		String json = gson.toJson(avgReplyTimePerForum);
		 
		try {
			//write converted json data to a file named "file.json"
			FileWriter writer = new FileWriter("C:\\Users\\schwagereit\\Documents\\file.json");
			writer.write(json);
			writer.close();
	 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
