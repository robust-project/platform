/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2013
//
// Copyright in this software belongs to University of Southampton
// IT Innovation Centre of Gamma House, Enterprise Road, 
// Chilworth Science Park, Southampton, SO16 7NS, UK.
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
//      Created By :            Vegard Engen
//      Created Date :          2013-10-10
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.sim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.KeyValuePair;

/**
 * A class to bundle a group of results (key value pairs) that logically fit together.
 * @author Vegard Engen
 */
public class ResultGroup implements Serializable
{
	private String name; // name of the result group
    private List<KeyValuePair> results; // using KeyValuePair object instead of Map<String, String> due to WS limitations
	
	/**
	 * Default constructor that initialises the results list.
	 */
	public ResultGroup()
	{
		results = new ArrayList<KeyValuePair>();
	}
	
	/**
	 * Constructor to set the name of the result group.
	 * @param name Name of the group.
	 */
	public ResultGroup(String name)
	{
		this();
		this.name = name;
	}
	
	/**
	 * Constructor to set the required parameters of the result group.
	 * @param name Name of the group.
	 * @param res Results in the form of a list of key-value pairs.
	 */
	public ResultGroup(String name, List<KeyValuePair> res)
	{
		this.name = name;
		this.results = res;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the results
	 */
	public List<KeyValuePair> getResults()
	{
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(List<KeyValuePair> results)
	{
		this.results = results;
	}
	
	/**
     * @param results the results to add
     */
    public void addResult(String key, String value) {
        if (results == null) {
            this.results = new ArrayList<KeyValuePair> ();
        }
        this.results.add(new KeyValuePair(key, value));
    }
    
    /**
     * @param results the results to add
     */
    public void addResult(List<KeyValuePair> results) {
        if (this.results == null) {
            this.results = new ArrayList<KeyValuePair>();
        }
        if (results != null) {
            this.results.addAll(results);
        }
    }
    
    /**
     * @param results the results to add
     */
    public void addResult(Map<String, String> results) {
        if (this.results == null) {
            this.results = new ArrayList<KeyValuePair>();
        }
        if (results != null)
        {
            for (String key : results.keySet()) {
                this.results.add(new KeyValuePair(key, results.get(key)));
            }
        }
    }
	
}
