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

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import pl.swmind.robust.ws.behaviouranalysis.Composition;
import pl.swmind.robust.ws.behaviouranalysis.Composition.IdToRoleMapping;
import pl.swmind.robust.ws.behaviouranalysis.UserRoles;
import pl.swmind.robust.ws.behaviouranalysis.UserRoles.UserToRoleMap;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.soton.itinnovation.robust.behaviourAnalysisClient.BehaviourAnalysisClient;

/**
 *
 * @author Edwin Tye
 */
public class FindRole {

    static Logger log = Logger.getLogger(FindRole.class);
    String serviceURI;// = "http://robust-www.softwaremind.pl/ROBUST-D3.2-WS/robustBehaviourAnalysisService";
    String serviceNameSpace;// = "http://behaviouranalysis.ws.robust.swmind.pl/";
    String serviceName;// = "BehaviourAnalysisServiceImplService";
    String servicePort;// = "BehaviourAnalysisServiceImplPort";
    BehaviourAnalysisClient client;// = new BehaviourAnalysisClient(this.serviceURI, this.serviceNameSpace, this.serviceName, this.servicePort);
    // some common config for the test functions
    String communityID = "http://forums.sdn.sap.com/uim/forum/200#id";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String platformName = null;
    
    /*
     * Constructors
     */
    protected FindRole() {
        initialize();
    }

    protected FindRole(String serviceURI,
		       String platformName,
		       String communityID) {
	this.serviceURI = serviceURI;
	this.platformName = platformName;
	this.communityID = communityID;
	try {
	    this.client = new BehaviourAnalysisClient(this.serviceURI);
	} catch(Exception e) {
	    log.error("Error initializing the behaviour analysis client",e);
	}
    }

    /**
     * 
     * 
     * @param serviceURI uri of the behavior analysis service
     * @param platformName SAP/IBM
     */
    protected FindRole(String serviceURI,
		       String platformName) {
	this.serviceURI = serviceURI;
	this.platformName = platformName;
	try {
	    this.client = new BehaviourAnalysisClient(this.serviceURI);
	    // this.client.setPlatform(this.platformName);
	} catch(Exception e) {
	    log.error("Error initializing the behaviour analysis client",e);
	}
    }

    /**
     * Setting the community id
     * 
     * @param community
     */
    protected void setCommunity(String community) {
        this.communityID = community;
    }

    protected String getCommunity() {
        return this.communityID;
    }

    protected HashMap<String, Integer> getRoles(Date dateInput) throws Exception {

        HashMap<String, Integer> mapUserRole = new HashMap<String, Integer>();
        try {
            //log.info("Getting micro role composition at the current date");

	    // Composition composition = client.deriveMacroComposition(platformName, communityID, date);
            // UserRoles userRoles = client.deriveMicroComposition(this.communityID, dateInput);
	    UserRoles userRoles = client.deriveMicroComposition(platformName,this.communityID, dateInput);
            UserToRoleMap userToRoleMap = userRoles.getUserToRoleMap();

            // Getting the user in the first date and putting it in a map for quick access later
            for (pl.swmind.robust.ws.behaviouranalysis.UserRoles.UserToRoleMap.Entry e : userToRoleMap.getEntry()) {
                mapUserRole.put(e.getKey(), e.getValue());
            }
        } catch (Exception e) {
            throw e;
        }
        return mapUserRole;
    }
 
    protected ArrayList<String> getUserID(Date dateRandom) throws Exception {
        ArrayList<String> listUserID = new ArrayList<String>();
        
        try {
            UserRoles userRoles = client.deriveMicroComposition(platformName,this.communityID, dateRandom);
            UserToRoleMap userToRoleMap = userRoles.getUserToRoleMap();

            for (pl.swmind.robust.ws.behaviouranalysis.UserRoles.UserToRoleMap.Entry e : userToRoleMap.getEntry()) {
                listUserID.add(e.getKey());
            }
        } catch (Exception e) {
            throw e;
        }
        return listUserID;
    }

    protected HashMap<Integer, Integer> getRoleToIndex(Date dateRandom) throws Exception {
        //System.out.println("The date that was processed is = " +dateRandom);
        //System.out.println("The communityid = " + this.communityID);
        HashMap<Integer, Integer> mapRoleIDToMatrix = new HashMap<Integer, Integer>();
        try {
            log.debug("The community entered = "+this.communityID);
            log.debug("Date entered = " +dateRandom);
            log.debug("Platform entered = " +this.platformName);
            Composition composition = client.deriveMacroComposition(platformName,this.communityID, dateRandom);
            IdToRoleMapping idToRoleMapping = composition.getIdToRoleMapping();

            int count = 0;
            for (pl.swmind.robust.ws.behaviouranalysis.Composition.IdToRoleMapping.Entry e : idToRoleMapping.getEntry()) {
                // Giving the correct index for the given numerical role values
                mapRoleIDToMatrix.put(e.getKey(), count);
                count++;
            }
        } catch (Exception e) {
            // log.info("Role to index : " +ex.getMessage(), ex);
            // throw ex;
	    throw e;
        }
        return mapRoleIDToMatrix;
    }

    public HashMap<String, Integer> getRoleName(Date dateRandom) throws Exception {
        HashMap<String, Integer> mapRoleID = new HashMap<String, Integer>();
        try {
            Composition composition = client.deriveMacroComposition(platformName,this.communityID, dateRandom);
            IdToRoleMapping idToRoleMapping = composition.getIdToRoleMapping();

            for (pl.swmind.robust.ws.behaviouranalysis.Composition.IdToRoleMapping.Entry e : idToRoleMapping.getEntry()) {
                // This just gets the name, which is repeating the original output of idToRoleMapping
                mapRoleID.put(e.getValue(), e.getKey());
            }
        } catch (Exception e) {
            // throw ex;
	    throw e;
        }
        return mapRoleID;
    }

    private void initialize() {
	// The old code
        // this.serviceURI = "http://robust-www.softwaremind.pl/ROBUST-D3.2-WS/robustBehaviourAnalysisService";
        // this.serviceNameSpace = "http://behaviouranalysis.ws.robust.swmind.pl/";
        // this.serviceName = "BehaviourAnalysisServiceImplService";
        // this.servicePort = "BehaviourAnalysisServiceImplPort";
        //this.client = new BehaviourAnalysisClient(this.serviceURI, this.serviceNameSpace, this.serviceName, this.servicePort);
        // this.communityID = "http://forums.sdn.sap.com/uim/forum/200#id";
        // some common config for the test functions
        this.serviceURI = "http://robust-www.softwaremind.pl/robust-behaviour-analysis-service-ws-1.0-SNAPSHOT/robustBehaviourAnalysisService";
	// this.serviceURI = "http://robust-www.softwaremind.pl/robust-behaviour-analysis-service-ws-1.0-SNAPSHOT/robustBehaviourAnalysisService";
	try {
	    this.client = new BehaviourAnalysisClient(this.serviceURI);
	    // this.client.setPlatform("SAP");
	} catch(Exception e) {
	    log.error("Error initializing the behaviour analysis client",e);
	}

    }

}
