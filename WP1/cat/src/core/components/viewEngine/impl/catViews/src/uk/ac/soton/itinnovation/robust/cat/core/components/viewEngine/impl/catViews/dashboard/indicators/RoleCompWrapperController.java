/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2012
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
//      Created By :            Simon Crowle
//      Created Date :          30-Oct-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.dashboard.indicators.IRoleCompWrapperController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import pl.swmind.robust.ws.behaviouranalysis.*;

public class RoleCompWrapperController extends UFAbstractEventManager
                                       implements Serializable,
                                                  IRoleCompWrapperController
{
  private RoleCompWrapperView roleCompView;
  private String              platformID;
  private String              communityID;
  private Date                startDate   = new Date();
  
  private HashMap<String, String> communityPlatforms; // map to get platform for a given community
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RoleCompWrapperController.class);
  
  private final BehaviourAnalysisService behaviourAnalysisService;
    
  public RoleCompWrapperController()
  {
    super();
    
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/role-composition-context.xml");
    behaviourAnalysisService = context.getBean(BehaviourAnalysisService.class);
    communityPlatforms = new HashMap<String, String>();
    
    try
    {
        // Get communities for each platform and create map of platform for each community
        if (behaviourAnalysisService != null) {
            List<String> platformIDs;
            //TODO: following currently only returns BOARDSIE, so mappings for IBM and SAP don't get defined!
            //platformIDs = behaviourAnalysisService.getPlatformIDs();
            
            platformIDs = new ArrayList<String>();
            platformIDs.add("BOARDSIE");
            platformIDs.add("IBM");
            platformIDs.add("SAP");

            if (platformIDs != null) {
                for (String platID : platformIDs) {
                    try {
                        List<DeriveCommunityNameEntry> communityIDs = behaviourAnalysisService.getCommunityIDs(platID);

                        for (DeriveCommunityNameEntry comm : communityIDs) {
                            communityPlatforms.put(comm.getCommunityID(), platID);
                            /* Test code to check which platform/community returns users
                            String users = "null";
                            Integer nUsers = 0;
                            try {
                                List<String> userIDs = behaviourAnalysisService.getUserIDs(platID, comm.getCommunityID(), 10);
                                if (userIDs != null) {
                                    nUsers = userIDs.size();
                                    users = nUsers.toString();
                                }
                            }
                            catch (Exception e) {
                                users = "error";
                            }
                            System.out.println(platID + ", " + comm.getCommunityID() + ", " + users);
                            */
                        }
                    } catch (RobustBehaviourServiceException_Exception ex) {
                        log.error("Could not get communities for platform " + platID + ": " + ex.getMessage(), ex);
                    }
                }
            }
        }
    }
    catch (Exception e)
    {
        log.error("Could not access behaviour analysis service: " + e.getMessage(), e);
    }
    
    createComponents();
  }
  
  public AbstractDashIndicatorView getIndicatorView()
  { return roleCompView; }
  
  // IRoleCompWrapperController ------------------------------------------------

    @Override
    public void setPlatformID(String id) {
        platformID = id;
    }
    
    public String getPlatformForCommunity(String commID)
    {
        String platID = communityPlatforms.get(commID);
        
        /*
        if (platID == null) {
            log.info("Could not get platform for community: " + commID);
            platID = platformID;
        }
        */
        
        return platID;
    }
    
    public BehaviourAnalysisService getBehaviourAnalysisService() {
        return behaviourAnalysisService;
    }

  @Override
  public void setCommunityID( String commID ) throws Exception
  { 
    communityID = commID;
    
    String platID = getPlatformForCommunity(commID);
    
    //if (platID == null) 
    //    throw new Exception("Could not determine platform for community " + commID);
    
    if (platID == null) {
        // behaviour service does not support this community
        log.warn("WARNING: behaviour analysis service does not support community: " + commID);
    }
    
    if (platformID == null) {
        platformID = platID;
    }
    else if ( (platID != null) && (! platID.equals(platformID)) ) {
        throw new Exception("Platform (" + platID + ") defined by behaviour service does not match global platform (" + platformID + ")");
    }
    
    roleCompView.updateView( platformID, communityID, startDate );
  }
  
  @Override
  public void setStartDate( Date date )
  { 
    startDate = date;
    String platID = getPlatformForCommunity(communityID);
    roleCompView.updateView( platID, communityID, startDate );
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponents()
  {
    roleCompView = new RoleCompWrapperView(this);
  }
}
