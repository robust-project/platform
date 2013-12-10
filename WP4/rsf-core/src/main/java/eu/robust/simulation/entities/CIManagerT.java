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
 
package eu.robust.simulation.entities;

import java.util.Collection;
import java.util.Random;

import org.apache.log4j.Logger;

import eu.robust.simulation.util.IndexedSet;

public class CIManagerT<Type extends ContentItem> {
	
	protected Logger logger = Logger.getLogger(CIManagerT.class);
	
	protected int idCount=0;
	protected int actualTick=0;
	
	protected IndexedSet<Type> items = new IndexedSet<Type>(3300000);
	protected Random r = new Random();
	
	// counts the extensions of content items, e.g. replies to threads
	private int extensionCount=0;
	
	public void incExtCount() {extensionCount++;}
	public int getExtCount() {return extensionCount;}
	
	public void setCurrentTick(int aTick) {
		actualTick = aTick;
	}	
	
	public void add(Type ci) {
		items.put(ci);
		if (ci.getID()==0) {
			ci.setInitValues(idCount, actualTick);
			idCount++;
		}
	}

	public boolean remove(Type ci) {
		return items.remove(ci);
	}

	public void add(Collection<Type> newContentItems) {
		for (Type c: newContentItems) {
			add(c);
		}
	}
	
	public ContentItem getRandom() {
		return items.getRandom(r);
	}
	
	public int getSize() {
		return items.getSize();
	}
	
	public IndexedSet<Type> getAllItems()
	{
		return items;
	}

}
