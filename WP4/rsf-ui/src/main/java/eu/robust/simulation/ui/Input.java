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
 * Created by       	: Nicole Marienhagen
 *
 * Creation Time    	: 26.06.2012
 *
 * Created for Project  : ROBUST
 */ 

package eu.robust.simulation.ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Input {

	/**
	 * @param args
	 */

	public HashMap<String, String> inputFile(String filepath) {
		HashMap<String, String> finalResult = new HashMap<String, String>();
		String[] result = null;

		// Input of the data

		try {
			BufferedReader in = new BufferedReader(new FileReader(filepath));
			String row = null;
			while ((row = in.readLine()) != null) {
				// Splitting the row
				result = row.split("\\: |\\,");
				for (int x = 1; x < result.length; x = x + 2) {
					String[] help;
					String[] help2;

					// refining the keys and values of the HashMap (mostly
					// removal of quotation marks)
					if (result[x - 1].contains("\"")
							&& result[x].contains("\"")) {
						help = result[x - 1].split("\"");
						help2 = result[x].split("\"");
						finalResult.put(help[1], help2[1]);
					} else {
						if (result[x - 1].contains("\"")) {
							help = result[x - 1].split("\"");
							finalResult.put(help[1], result[x]);
						} else {
							if (result[x].contains("\"")) {
								help = result[x].split("\"");
								finalResult.put(result[x - 1], help[1]);
							} else {
								finalResult.put(result[x - 1], result[x]);
							}
						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return finalResult;

	}
}
