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

import eu.robust.simulation.util.IndexedSet;

public class UserManager<E extends User> {
	
	private int idCount=0;
	private int actualTick=0;
	
	private IndexedSet<E> users;
	
	public void setActualTick(int aTick) {
		actualTick = aTick;
	}
	
	public UserManager(int maxUsers) {
		super();
		users = new IndexedSet<E>(maxUsers+1);
	}
	
	public void add(E user) {
		users.put(user);
		if (user.getID()==0) {
			user.setInitValues(idCount, actualTick);
			idCount++;
		}
	}
	
	public void add(Collection<E> newUsers) {
		for (E u: newUsers) {
			add(u);
		}
	}
	
	public int size() {
		return users.getSize();
	}
	
	public Collection<E> getUsers() {
		return users.getAll();
	}


}
