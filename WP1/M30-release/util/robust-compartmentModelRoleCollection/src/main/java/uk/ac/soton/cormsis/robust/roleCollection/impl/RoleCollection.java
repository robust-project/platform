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

package uk.ac.soton.cormsis.robust.roleCollection.impl;

import java.util.Date;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;

import uk.ac.soton.cormsis.robust.roleCollection.spec.IRoleCollection;

public class RoleCollection implements IRoleCollection {

    static Logger log = Logger.getLogger(RoleCollection.class);

    // private FindRole FR = new FindRole();
    private FindRole FR;
    //private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private String serviceURI;
    // private String serviceNameSpace;
    // private String serviceName;
    // private String servicePort;
    private String communityID;
    
    private String platformName;

    private Date dateCurrent = null;
    private int timeIncrement;
    private int totalRole;

    private ArrayList<double[]> listMass = new ArrayList<double[]>();
    private ArrayList<double[][]> listRate = new ArrayList<double[][]>();
    private ArrayList<String> listUser = new ArrayList<String>();
    private Set<String> setUser= new HashSet<String>();

    private Map<Integer,Integer> mapRoleIDToMatrix;
    private Map<String,Integer> mapRoleID;
    private Map<String,Integer> mapRoleNameToIndex;

    private boolean hasOutput = false;
    private boolean hasInitialized = false;

    /*
     * Constructors
     */

    public RoleCollection() {
	//initialization();
        this.timeIncrement = 7;
    };

    @Override
	public void initialize(String communityID,
			       String platformName) {
	this.communityID = communityID;
	this.platformName = platformName;
        setFR();
        this.hasInitialized = true;
    }

    @Override 
	public void initialize(String uri,
				String communityID,
				String platformName) {
	
	this.serviceURI = uri;
	this.communityID = communityID;
	this.platformName = platformName;
        setFR();
        this.hasInitialized = true;
    }

    @Override
    public void setBehaviourServiceURI(String serviceURI)
    {
        this.serviceURI = serviceURI;
        setFR();
    }
    
    @Override
    public void setCommunity(String community) {
	this.communityID = community;
        setFR();
    }

    @Override
    public void setPlatform(String platform) {
	this.platformName = platform;
        setFR();
    }

    // This is currently a private method because it appears that the service doesn't give output
    // unless it is on specific dates and on a weekly increment
    @Override
    public void setTimeIncrement(int i) {
     	this.timeIncrement = i;
     }
    
    @Override
    public void setDate(Date d) {
        this.dateCurrent = d;
    }

    public ArrayList<double[]> getMass() throws Throwable{
	if (this.hasOutput) {
	    return this.listMass;
	} else {
            try {
		compute();
            } catch(Throwable ex) {
		log.error("Fail to get the mass",ex);
                throw ex;
            }
            return this.listMass;
	}
    }

    public ArrayList<double[][]> getRate() throws Throwable{
	if (hasOutput) {
	    return listRate;
	} else {
	    compute();
	    return listRate;
	}
    }

    //public ArrayList<RealMatrix> getMass(String c,Date d) {
    @Override
    public ArrayList<double[]> getMass(String c, Date d) throws Exception {
        if (this.dateCurrent == null) {
            this.dateCurrent = d;
        }
        if (this.dateCurrent.equals(d)) {
            if (this.hasOutput) {
                return this.listMass;
            } else {
                FR.setCommunity(c);
                this.communityID = c;
                this.dateCurrent = d;
                try {
                    log.debug("Computing the mass");
                    compute();
                    log.debug("We got the mass");
                } catch (Throwable ex) {
                    log.error("Fail to get mass:" + ex);
                }
                return this.listMass;
            }
        } else {
            this.communityID = c;
            this.dateCurrent = d;
                         try {
                    log.debug("Computing the mass");
                    compute();
                    log.debug("We got the mass");
                } catch (Throwable ex) {
                    log.error("Fail to get mass:" + ex);
                }
                return this.listMass;
        }
    }

    //public ArrayList<RealMatrix> getRate(String c,Date d) {
    @Override
    public ArrayList<double[][]> getRate(String c,Date d) throws Exception {
        if (this.dateCurrent == null) {
            this.dateCurrent = d;
        }
        if (this.dateCurrent.equals(d)) {
            if (this.hasOutput) {
                return this.listRate;
            } else {
                FR.setCommunity(c);
                this.communityID = c;
                this.dateCurrent = d;
                try {
                    log.debug("Computing the mass");
                    compute();
                    log.debug("We got the mass");
                } catch (Throwable ex) {
                    log.error("Fail to get mass:" + ex);
                }
                return this.listRate;
            }
        } else {
            this.communityID = c;
            this.dateCurrent = d;
                try {
                    log.debug("Computing the mass");
                    compute();
                    log.debug("We got the mass");
                } catch (Throwable ex) {
                    log.error("Fail to get mass:" + ex);
                }
                return this.listRate;
        }
    }

    // Getting the total number of roles
    @Override
    public int getNumRole() throws Throwable{
	if(this.hasOutput) {
	    return this.totalRole;
	} else {
            try {
	    getForumInfo();
            } catch (Throwable ex) {
                ex.getCause();
            }
            return this.totalRole;
	}
    }
    
    @Override
    public Map<String,Integer> getRoleName() throws Throwable{
	if(this.hasOutput) {
	    return this.mapRoleID;
	} else { 
            try {
	    getForumInfo();
            } catch (Throwable ex) {
                ex.getCause();
            }
            return this.mapRoleID;
	}
    }

    @Override
    public Map<Integer,Integer> getRoleIndex() throws Throwable{
	if(this.hasOutput) {
	    return this.mapRoleIDToMatrix;
	} else {
            try {
	    getForumInfo();
            } catch(Throwable ex) {
                throw ex;
            }
	    return this.mapRoleIDToMatrix;
	}
    }
    
    // This one directly output the Names of the roles and the corresponding index in the vector/matrix
    // The main requirement when using the CMEvaluator class in the predictor service because this 
    // will map directly to the index given the platform name
    @Override
    public Map<String,Integer> getRoleNameIndex() throws Exception {
	if(this.hasOutput) {
	    return this.mapRoleNameToIndex;
	} else {
            try {
	    getForumInfo();
            } catch (Exception e) {
                // throw new Exception("Fail to get the name of the roles");
                log.error("Fail to get the name of the roles",e);
            }
            return this.mapRoleNameToIndex;
	}
    }

    @Override
    public ArrayList<String> getListUserID() throws Exception {
        ArrayList<String> userid = new ArrayList<String>();
        try {
	userid = FR.getUserID(dateCurrent);
        } catch(Throwable ex) {
	    throw new RuntimeException("Fail to obtain a list of users",ex);
        }
        return userid;
    }

    private void getForumInfo() throws Exception {
	try{
	    log.debug("Trying to get the forum information");
	    this.mapRoleIDToMatrix = FR.getRoleToIndex(dateCurrent);
	    log.debug("We manage to get the map of role");
	    this.mapRoleID = FR.getRoleName(dateCurrent);
	    log.debug("We also mange to get the role name");
	    this.totalRole = this.mapRoleID.size();
	    this.listUser = FR.getUserID(dateCurrent);
	    for (String u : listUser) {
		setUser.add(u);
	    }
	    log.debug("obtaining the list of user is successful");
	    //System.out.println("the number of entry in mapRoleID = " +mapRoleID.size());
            this.mapRoleNameToIndex = new HashMap<String, Integer>();
            
            for (Entry<String,Integer> e:this.mapRoleID.entrySet()) {
                Integer roleIndex = Integer.parseInt(e.getValue().toString());
                this.mapRoleNameToIndex.put(e.getKey(), this.mapRoleIDToMatrix.get(roleIndex));
            }
        } catch (Exception e) {
            throw e;
	}
     }

    @Override
    public void compute() throws Exception {
	if (this.hasInitialized) {
	    // Getting the name of the Roles and the correct mapping to the index to a matrix
            try {
		getForumInfo();
            } catch (Exception e) {
                throw e;
            }
	    // The proper code to loop through all the avaiable time and putting them into the matrix
	    // Collecting two set of map, one for time at t, the other at time t+1 and make the comparison to obtain the rate
            try {
		log.debug("We are looping through time");
		loopTime(this.dateCurrent);
                this.hasOutput = true;
            } catch(Exception e) {
                log.error("Error in looping through time" ,e);
            }
	} else {
	    // throw new IllegalArgumentException("It has not been properly initialized");
            log.error("It has not been properly initialized");
	}
    }
	
    private void loopTime(Date date) throws Exception {
        listMass.clear();
        listRate.clear();
        
        int indexOfInactive = this.mapRoleIDToMatrix.get(this.mapRoleID.get("Inactive"));
	//String strDateCurrent = sdf.format(date);

	collectUserID();
	log.debug("Number of user = " +setUser.size());

	Calendar cPrevious = Calendar.getInstance();
	Calendar cCurrent = Calendar.getInstance();
	cPrevious.setTime(date);
	cCurrent.setTime(date);
	this.timeIncrement = 7;
        cPrevious.add(Calendar.DATE, -1 * timeIncrement);

	HashMap<String,Integer> mapUserRolePrevious = new HashMap<String,Integer>();
	HashMap<String,Integer> mapUserRoleCurrent = new HashMap<String,Integer>();

        Date dPrevious = cPrevious.getTime();
        Date dCurrent = cCurrent.getTime();
        int totalActive = 100;
	boolean first = true;
	// log.info("Beginning to loop over time");
	int iteration = 0;

	while (totalActive != 0 && iteration < 30) {
	    totalActive = 0;
            try {
		if (first == true) {
		    mapUserRolePrevious= FR.getRoles(dPrevious);
		    mapUserRoleCurrent = FR.getRoles(dCurrent);
                    first = false;
		}
		mapUserRolePrevious= FR.getRoles(dPrevious);
            } catch (Exception e) {
                // throw e;
            }
	    // log.info("Got past the first iteration");

	    // Defining the size of the matrix according to the number of roles
	    double[] dblMassPrevious = new double[this.totalRole];
	    double[] dblMassCurrent = new double[this.totalRole];
	    double[][] dblFlow = new double[this.totalRole][this.totalRole];
	    double[][] dblRate = new double[this.totalRole][this.totalRole];

	    // Start counting the user to generate the respecitive mass and rates
	    // for (String strUserID : this.listUser) {
	    for (String strUserID : this.setUser) {
		// String strUserID = this.listUser.get(i);
		// This is the part to put user in inactive
                int intPrevious = indexOfInactive;
                int intCurrent = indexOfInactive;
                try {
                    intPrevious = mapRoleIDToMatrix.get(mapUserRolePrevious.get(strUserID));
                } catch (Exception e) {
		    log.debug(e.getMessage());
                }
		// We wish to continue even if the previous one throws an error, which is why
		// this is in a separate block
		// Assuming that any User that had problem retrieving information from is 
		// actually inactive.
                try {
                    intCurrent = mapRoleIDToMatrix.get(mapUserRoleCurrent.get(strUserID));
                } catch (Exception e) {
		    log.debug(e.getMessage());
                }
                    dblMassCurrent[intCurrent]++;
                    dblMassPrevious[intPrevious]++;
                    dblFlow[intPrevious][intCurrent]++;
//                } catch (Exception e) {
//                }
	    } // ~ for (String strUserID : this.setUser)

	    for (int i = 0; i < this.totalRole; i++) {
		double[] dblRatei = dblRate[i];
		double[] dblFlowi = dblFlow[i];
		if (dblMassPrevious[i] == 0) {
		    for (int j = 0; j < this.totalRole; j++) {
			dblRatei[j] = 0;
		    }
		} else {
		    double dblTransitionedOut = 0;
		    for (int j = 0; j < this.totalRole; j++) {
			if (i != j) {
			    dblRatei[j] = dblFlowi[j] / dblMassPrevious[i];
			    dblTransitionedOut += dblRatei[j];
			}
		    }
		    dblRatei[i] = -1 * dblTransitionedOut;
		}
	    }

	    listMass.add(dblMassCurrent);
	    listRate.add(dblRate);

	    // Now, it changes the dates
	    cPrevious.add(Calendar.DATE, -1 * timeIncrement);
	    cCurrent.add(Calendar.DATE, -1 * timeIncrement);
	    dPrevious = cPrevious.getTime();
	    dCurrent = cCurrent.getTime();
	    for (int i = 0; i < this.totalRole; i++) {
		totalActive += dblMassCurrent[i];
	    }

	    mapUserRoleCurrent = mapUserRolePrevious;
	    totalActive -= (int)dblMassCurrent[this.mapRoleIDToMatrix.get(this.mapRoleID.get("Inactive"))];
	    iteration++;
            log.debug("total active = " +totalActive+ " and iteration = " +iteration);
            // log.info(dCurrent);
	} // while  (totalActive != 0 && iteration < 30)
        log.debug("Finish looping through time");
    }

    private void collectUserID() {
	Date date = this.dateCurrent;
	Calendar cCurrent = Calendar.getInstance();
	cCurrent.setTime(date);
	this.timeIncrement = 7;
        Date dCurrent = cCurrent.getTime();
	// log.info("Beginning to loop over time");
	int iteration = 0;
	while (iteration < 30) {
	    try {
		ArrayList<String> listCurrentUser = FR.getUserID(dCurrent);
		for (String u : listCurrentUser) {
		    setUser.add(u);
		}
	    } catch (Exception e) {
	    }

	    // Now, it changes the dates
	    cCurrent.add(Calendar.DATE, -1 * timeIncrement);
	    dCurrent = cCurrent.getTime();
	    iteration++;
	} // while loop
    }
    
    private void setFR() {
        FR = new FindRole(this.serviceURI, this.platformName, this.communityID);
        //FR.setCommunity(communityID);
    }

}
