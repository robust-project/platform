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
//      Created By :            Ken Meacham
//      Created Date :          24 Jun 2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.subViewEvents;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UIChangeData;

import uk.ac.soton.itinnovation.robust.riskmodel.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class SelectedCommunitiesCD extends UIChangeData {

    private HashMap<String, Community> addedCommunities = new HashMap<String, Community>();
    private HashMap<String, Community> removedCommunities = new HashMap<String, Community>();

    public SelectedCommunitiesCD() {
        super();
    }

    @Override
    public void reset() {
        addedCommunities = new HashMap<String, Community>();
        removedCommunities = new HashMap<String, Community>();
    }

    private String getCommKey(Community comm) {
        return comm.getName() + comm.getCommunityID();
    }

    public void addCommunity(Community comm) {
        String commKey = getCommKey(comm);
        if (removedCommunities.containsKey(commKey)) {
            removedCommunities.remove(commKey);
        } else {
            addedCommunities.put(commKey, comm);
        }
    }

    public void removeCommunity(Community comm) {
        String commKey = getCommKey(comm);
        if (addedCommunities.containsKey(commKey)) {
            addedCommunities.remove(commKey);
        } else {
            removedCommunities.put(commKey, comm);
        }
    }

    @Override
    public boolean isDataChanged() {
        return ((addedCommunities.size() > 0) || (removedCommunities.size() > 0));
    }

    public Set<Community> getAddedCommunities() {
        Set<Community> communities = new HashSet<Community>();
        Set<String> keys = addedCommunities.keySet();
        for (String key : keys) {
            communities.add(addedCommunities.get(key));
        }
        return communities;
    }

    public Set<Community> getRemovedCommunities() {
        Set<Community> communities = new HashSet<Community>();
        Set<String> keys = removedCommunities.keySet();
        for (String key : keys) {
            communities.add(removedCommunities.get(key));
        }
        return communities;
    }
    
}