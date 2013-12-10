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
//      Created Date :          21 Jun 2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.datasource.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import uk.ac.soton.itinnovation.robust.cat.datasource.spec.IDataSource;
import uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen.*;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.DataSource;

public class DataSourceImpl implements IDataSource {

    private static final QName SERVICE_NAME = new QName("http://ws.robust.swmind.pl/", "RobustDataServiceBoardsIEWSImplService");
    private URL wsdlURL;
    private RobustDataServiceBoardsIEWSImplService ss;
    private RobustDataServiceBoardsIEWS port;
    private DataSource dataSource;
    
    public DataSourceImpl() {
        wsdlURL = RobustDataServiceBoardsIEWSImplService.WSDL_LOCATION;
        createServiceAndPort();
    }
    
    /*
    public DataSourceImpl(URI endpoint) {
        try {
            wsdlURL = new URL(endpoint.toString() + "?wsdl");
            createServiceAndPort();
        } catch (MalformedURLException ex) {
            Logger.getLogger(DataSourceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    */

    public DataSourceImpl(DataSource ds) {
        try {
            dataSource = ds; // set my data source
            URI endpoint = ds.getEndpoint();
            wsdlURL = new URL(endpoint.toString() + "?wsdl");
            createServiceAndPort();
        } catch (MalformedURLException ex) {
            Logger.getLogger(DataSourceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createServiceAndPort() {
        if (wsdlURL != null) {
            ss = new RobustDataServiceBoardsIEWSImplService(wsdlURL, SERVICE_NAME);
            port = ss.getRobustDataServiceBoardsIEWSImplPort();
        }
    }

    @Override
    public String getPlatform() {
        System.out.println("Invoking getPlatform (dummy method)");
        return "BOARDSIE";
    }
    
    @Override
    public Set<Community> getCommunities() {
        String platform = getPlatform();
        System.out.println("platform: " + platform + "\n");
        
        System.out.println("Invoking getCommunities (getAllForums)...");
        List<ForumDto> forums = port.getAllForums();
        
        Set<Community> communities = new LinkedHashSet<Community>();
        
        for (ForumDto forum : forums) {
            String name = forum.getForumTitle();
            String communityID = Long.toString(forum.getForumId());
            
            /*
            URI uri = null;
            boolean isStreaming = false;
            String streamName = null;
            DataLocation dl=new DataSource(streamName, uri);//bmn: please check if ok
            */
            
            Community community = new Community(name,  communityID, dataSource, getPlatform());
            //TODO: add platform to community - something like this
            //community.setPlatform(platform);
            communities.add(community);
        }
        
        return communities;
    }

}
