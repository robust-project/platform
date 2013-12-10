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
//      Created By :            Ken Meachem
//      Created Date :          2013-7-13
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.6.1
 * 2013-07-02T16:15:29.081+01:00
 * Generated source version: 2.6.1
 * 
 */
public final class RobustDataServiceBoardsIEWS_RobustDataServiceBoardsIEWSImplPort_Client {

    private static final QName SERVICE_NAME = new QName("http://ws.robust.swmind.pl/", "RobustDataServiceBoardsIEWSImplService");

    private RobustDataServiceBoardsIEWS_RobustDataServiceBoardsIEWSImplPort_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = RobustDataServiceBoardsIEWSImplService.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        RobustDataServiceBoardsIEWSImplService ss = new RobustDataServiceBoardsIEWSImplService(wsdlURL, SERVICE_NAME);
        RobustDataServiceBoardsIEWS port = ss.getRobustDataServiceBoardsIEWSImplPort();  
        
        {
        System.out.println("Invoking getAllUserAccounts...");
        java.util.List<uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen.UserDto> _getAllUserAccounts__return = port.getAllUserAccounts();
        System.out.println("getAllUserAccounts.result=" + _getAllUserAccounts__return);


        }
        {
        System.out.println("Invoking getPostsByForumUri...");
        java.lang.String _getPostsByForumUri_arg0 = "";
        java.util.List<uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen.PostDto> _getPostsByForumUri__return = port.getPostsByForumUri(_getPostsByForumUri_arg0);
        System.out.println("getPostsByForumUri.result=" + _getPostsByForumUri__return);


        }
        {
        System.out.println("Invoking getUserAccount...");
        java.lang.String _getUserAccount_arg0 = "";
        uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen.UserDto _getUserAccount__return = port.getUserAccount(_getUserAccount_arg0);
        System.out.println("getUserAccount.result=" + _getUserAccount__return);


        }
        {
        System.out.println("Invoking getAllPosts...");
        java.util.List<uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen.PostDto> _getAllPosts__return = port.getAllPosts();
        System.out.println("getAllPosts.result=" + _getAllPosts__return);


        }
        {
        System.out.println("Invoking getPosts...");
        java.util.List<java.lang.String> _getPosts_arg0 = null;
        java.util.List<uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen.PostDto> _getPosts__return = port.getPosts(_getPosts_arg0);
        System.out.println("getPosts.result=" + _getPosts__return);


        }
        {
        System.out.println("Invoking getPostsByUserName...");
        java.lang.String _getPostsByUserName_arg0 = "";
        java.util.List<uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen.PostDto> _getPostsByUserName__return = port.getPostsByUserName(_getPostsByUserName_arg0);
        System.out.println("getPostsByUserName.result=" + _getPostsByUserName__return);


        }
        {
        System.out.println("Invoking getPostsByUserUri...");
        java.lang.String _getPostsByUserUri_arg0 = "";
        java.util.List<uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen.PostDto> _getPostsByUserUri__return = port.getPostsByUserUri(_getPostsByUserUri_arg0);
        System.out.println("getPostsByUserUri.result=" + _getPostsByUserUri__return);


        }
        {
        System.out.println("Invoking getAllForums...");
        java.util.List<uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen.ForumDto> _getAllForums__return = port.getAllForums();
        System.out.println("getAllForums.result=" + _getAllForums__return);


        }

        System.exit(0);
    }

}
