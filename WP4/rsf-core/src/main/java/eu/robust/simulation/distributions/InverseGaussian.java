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

import java.util.Random;

/**
 * @author Felix
 * We could also think about using the Apache Commons Math package: http://commons.apache.org/math/
 *
 */
public class InverseGaussian {
	

	
	/**
	 * @param lambda
	 * @param mu
	 * @return Random number
	 * Generates a random number according to an inverse-Gaussian distribution
	 */
	
	public double getSample(double mu, double lambda) {
	       Random rand = new Random();
	       double v = rand.nextGaussian();   // sample from a normal distribution with a mean of 0 and 1 standard deviation
	       double y = v*v;
	       double x = mu + (mu*mu*y)/(2*lambda) - (mu/(2*lambda)) * Math.sqrt(4*mu*lambda*y + mu*mu*y*y);
	       double test = rand.nextDouble();  // sample from a uniform distribution between 0 and 1
	       if (test <= (mu)/(mu + x))
	              return x;
	       else
	              return (mu*mu)/x;
	}

}
