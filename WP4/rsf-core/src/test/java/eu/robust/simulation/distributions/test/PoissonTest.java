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
 
package eu.robust.simulation.distributions.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

public class PoissonTest {
	
	@Test
	public void testGetSample() {
		double lambda = 2;
		Random r = new Random();
		Map<Integer,Integer> m = new HashMap<Integer,Integer>();
		
		// draw 100 samples
		for (int i=0; i<100 ; i++) {
			int x = (int) eu.robust.simulation.distributions.Poisson.getSample(lambda, r);
			if (! m.containsKey(x)) {
				m.put(x, 0);
			} 
			m.put(x, m.get(x)+1);
		}
		
		int sum = 0;
		for(int i : m.keySet()) {
			sum=sum+m.get(i);
		}
		assertTrue(sum==100);
	}
}
