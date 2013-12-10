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
 
package eu.robust.simulation.util;


import java.util.*;

public class IndexedSet<E> {
	private E[] indexmap;
	private HashMap<E, Integer> objectmap;
	private int size;
	

	public IndexedSet(int initialCapacity) {
		indexmap = (E[]) new Object[initialCapacity];
		size=0;
		objectmap = new HashMap<E, Integer>(initialCapacity);
	}
	
	public void put(E element) {
		indexmap[size]=element; 
		objectmap.put(element, size);
		size++;
	}
	
	
	public boolean remove(E element) {
		if (objectmap.containsKey(element)) {
			int key=objectmap.get(element);
			int highestListKey=size-1;
			if (key!=highestListKey) {
	
	
				E highestPost=indexmap[highestListKey];
				indexmap[key] = highestPost;
	
				objectmap.remove(highestPost);
				objectmap.put(highestPost,key);
			}
			objectmap.remove(element);
			size--;
			return true;
		} else {
			return false;
		}
	}
	
	
	public E get(int index){
		return indexmap[index];
	}
	
	public E getRandom(Random randomv) {
		return indexmap[randomv.nextInt(size)];
	}
	
	public int getSize() {
		return size;
	}
	
	public boolean contains(E element) {
		return objectmap.containsKey(element);
	}
	
	public String getStats() {
		return (size+","+objectmap.size());
	}
	
	public Set<E> getAll() {
		return objectmap.keySet();
	}

}
