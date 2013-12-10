/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2011
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
//      Created By :            Bassem Nasser
//      Created Date :          03-Nov-2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.riskmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum Scope {

    ALL_USERS("all_users"), USER("user"), GROUP("group"),COMMUNITY("community");
    private String scope;
    
    //if the scope is group, this can be used to specify the list of users involved
    //complex scenarios when group refers to community Role (lurker, expert...) can be considered later 
    private Set<String> users;

    private Scope() {
    }
 

    private Scope(String scope) {
        this.scope = scope;
      
    }

    public String getName() {
        return scope;
    }



    @Override
    public String toString() {
        return scope;
    }

    public static List<String> getAllowedTypes() {
        List<String> allowed = new ArrayList<String>();
        allowed.add(ALL_USERS.scope);
        allowed.add(USER.scope);
        allowed.add(GROUP.scope);
        allowed.add(COMMUNITY.scope);
        return allowed;
    }

    public static boolean isAllowed(String type) {
        for (Scope inst : values()) {
            if (inst.scope.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
    
    public void addUsers(String user){
        if(users==null){
            users=new HashSet<String>();
        }
        
        users.add(user);
    }
    
        public static Scope fromString(String scope) {
        if (scope != null) {
            for (Scope b : Scope.values()) {
                if (scope.equalsIgnoreCase(b.getName())) {
                    return b;
                }
            }

            throw new IllegalArgumentException("No enum const " + Scope.class + " for " + scope);

        }

        throw new RuntimeException("input parameter is null");

    }
}
