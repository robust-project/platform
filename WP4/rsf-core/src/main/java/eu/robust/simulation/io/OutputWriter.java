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
 
 package eu.robust.simulation.io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class OutputWriter {
	
	PrintWriter outputStream;
	
	public OutputWriter(String name) {
		
		// construct output file name
		String defaultdir = "";
		String outputdir = "";
		Map<String, String> env = System.getenv();
		if (env.containsKey("rsf-output")) {
			outputdir = env.get("rsf-output")+"\\";
		} else outputdir = defaultdir;
		
		
		Date actDate = new Date(System.currentTimeMillis());
		//String timestamp = actDate.getYear()+"-"+actDate.getMonth()+"-"+actDate.getDay()+"T"+actDate.getHours()+":"+actDate.getMinutes()+":"+actDate.getSeconds();
		DateFormat writeFormat = new SimpleDateFormat( "yyyy-MM-dd-HHmmss");
		String timestamp = writeFormat.format(actDate);
		
	    try {

	    	outputStream = new PrintWriter(new FileWriter(outputdir+timestamp+name+".csv"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	    
	    
	public void write(String line) {
		if (outputStream != null) {
			outputStream.println(line);
		}
	}

	public void close() {
		if (outputStream != null) {
            outputStream.close();
        }
	}
	

}
