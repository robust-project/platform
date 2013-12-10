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
 * Creation Time    	: 27.09.2012
 *
 * Created for Project  : ROBUST
 */ 
 
package eu.robust.simulation.distributions;

import java.util.Random;

/**
 * @author Felix Schwagereit
 * We could also think about using the Apache Commons Math package: http://commons.apache.org/math/
 * After Michael, Schucany, Haas [Devroye1986]
 *
 */
public class Geometric {
	
	/**
	 * @param p
	 * @param r
	 * @return Random number
	 * Generates a random number according to a Geometric distribution with parameter p (probability of success in each trial).
	 */
	public static long getSample(double p, Random r) {
		long x = 0;
		double u = 0;
		do {
			u = r.nextDouble();
			x++;			
		} while (u>p);
		return x;	
	}

}
