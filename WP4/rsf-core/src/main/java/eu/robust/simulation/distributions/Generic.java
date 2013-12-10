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
 
package eu.robust.simulation.distributions;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * @author Felix
 * Generic sampler for given frequency distribution
 *
 */
public class Generic {
	
	/**
	 * @param freuqencyTable For every occurrence i the according relative frequency p[i]
	 * @param r
	 * @return Random number
	 * Generates a random number according to a given relative frequency
	 */
	public static int getSample(double[] frequencyTable, Random r) {
		double prob = r.nextDouble();
		int i=0;
		double cumulative=frequencyTable[i];
		while (cumulative<prob) {
			i++;
			cumulative=cumulative+frequencyTable[i];
		} ; 
		return i;
	}
	
	/**
	 * @param freuqencyMap <a,p> For every occurrence a the according relative frequency p[a]
	 * @param r
	 * @return Random number
	 * Generates a random number according to a given relative frequency
	 */
	public static int getSample(Map<Integer,Double> frequencyMap, Random r) {
		double prob = r.nextDouble();
		Iterator<Integer> i = frequencyMap.keySet().iterator();
		int actState = i.next();
		double cumulative=frequencyMap.get(actState);
		while (cumulative<prob) {
			actState = i.next();
			cumulative=cumulative+frequencyMap.get(actState);
		} ; 
		return actState;
	}
	
	
	/**
	 * @param Collection col
	 * @param r
	 * @return Random number
	 * Draws a sample from the collection
	 */
//	public static Object getSampleFromSet(Collection<Object> col, Random r) {
//		
//		Object result = null;
//		int countToChoose = r.nextInt(col.size());
//		Iterator<Object> it = col.iterator();
//		for (int i = 0 ; i <= countToChoose; i++ ) {
//			result = it.next();
//		}
//		
//		return result;
//	}

}
