/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton
// CORMSIS
// Centre of Operational Research, Management Science and Information Systems
// 2012
//
// Copyright in this software belongs to University of Southampton
// CORMSIS,
// University of Soutampton,
// Highfield Campus,
// Southampton,
// SO17 1BJ,
// UK
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
//      Created By :            Edwin Tye
//      Created Date :          2012-08-05
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.cormsis.robust.roleCollection.spec;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public interface IRoleCollection {
 
    /**
     * 
     * 
     * @param uri service uri
     * @param platformName use case platform SAP/IBM
     */
    public void initialize(String uri,
		       String platformName);
    
    /**
	 * 
	 * 
	 * @param uri service uri
	 * @param communityID the community id for the specific platform
	 * @param platformName SAP/IBM
	 */
    public void initialize(String uri,
			   String communityID,
			   String platformName);
    

    void setBehaviourServiceURI(String serviceURI);
    
    /**
     * Setting the stored community identifier
     * @param community The identifier for the community
     */
    public void setCommunity(String communityid);


    /**
     * Set the name of the platform that one wished to analyse
     * @param platformName SAP/IBM
     */
    public void setPlatform(String platformName);
    
    /**
     * Sets the date to find the information regarding the community
     * @param d any date
     */
    public void setDate(Date d);
    
    /**
     * 
     * @param day number of days in a time window
     */
    public void setTimeIncrement(int day);

    /**
     * 
     * @param c the identifier for the community 
     * @param d a date
     * @return a list of mass of dimension \left[ k \times 1 \right] where k is the number of groups
     */
    public ArrayList<double[]> getMass(String communityid,Date date) throws Exception;

    /**
     * 
     * @param c the identifier for the community
     * @param d a date
     * @return a list of rate of dimension \left[k \times k \right] where k is the number of groups
     */
    public ArrayList<double[][]> getRate(String communityid,Date d) throws Exception;

    /**
     * Get the total number of roles available
     * @return total number of roles
     */
    public int getNumRole() throws Throwable;
    
    /**
     * 
     * @return a list of names and the corresponding identifier given by role analysis
     */
    public Map<String,Integer> getRoleName() throws Throwable;

    /**
     * 
     * @return 
     */
    public Map<Integer,Integer> getRoleIndex() throws Throwable;
    
    // This one directly output the Names of the roles and the corresponding index in the vector/matrix
    /**
     *  
     * @return a list of names and the corresponding index
     */
    public Map<String,Integer> getRoleNameIndex() throws Throwable;
    
    /**
     * Returns all the Users that have ever posted in the forum
     * @return a list of all the Users
     */
    public ArrayList<String> getListUserID() throws Throwable;
    
    /**
     * Computes the roles. (Maybe I should make it such that it doesn't require another external call
     */
    public void compute() throws Throwable;
}
