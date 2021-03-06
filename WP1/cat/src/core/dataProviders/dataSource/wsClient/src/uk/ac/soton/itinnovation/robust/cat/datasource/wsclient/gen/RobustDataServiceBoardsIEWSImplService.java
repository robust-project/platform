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

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.6.1
 * 2013-07-02T16:15:29.128+01:00
 * Generated source version: 2.6.1
 * 
 */
@WebServiceClient(name = "RobustDataServiceBoardsIEWSImplService", 
                  wsdlLocation = "http://robust-demo.softwaremind.pl/robust-dataservice-boardsie-ws-1.0-SNAPSHOT/robustDataServiceBoardsIE?wsdl",
                  targetNamespace = "http://ws.robust.swmind.pl/") 
public class RobustDataServiceBoardsIEWSImplService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://ws.robust.swmind.pl/", "RobustDataServiceBoardsIEWSImplService");
    public final static QName RobustDataServiceBoardsIEWSImplPort = new QName("http://ws.robust.swmind.pl/", "RobustDataServiceBoardsIEWSImplPort");
    static {
        URL url = null;
        try {
            url = new URL("http://robust-demo.softwaremind.pl/robust-dataservice-boardsie-ws-1.0-SNAPSHOT/robustDataServiceBoardsIE?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(RobustDataServiceBoardsIEWSImplService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://robust-demo.softwaremind.pl/robust-dataservice-boardsie-ws-1.0-SNAPSHOT/robustDataServiceBoardsIE?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public RobustDataServiceBoardsIEWSImplService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public RobustDataServiceBoardsIEWSImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RobustDataServiceBoardsIEWSImplService() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     *
     * @return
     *     returns RobustDataServiceBoardsIEWS
     */
    @WebEndpoint(name = "RobustDataServiceBoardsIEWSImplPort")
    public RobustDataServiceBoardsIEWS getRobustDataServiceBoardsIEWSImplPort() {
        return super.getPort(RobustDataServiceBoardsIEWSImplPort, RobustDataServiceBoardsIEWS.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RobustDataServiceBoardsIEWS
     */
    @WebEndpoint(name = "RobustDataServiceBoardsIEWSImplPort")
    public RobustDataServiceBoardsIEWS getRobustDataServiceBoardsIEWSImplPort(WebServiceFeature... features) {
        return super.getPort(RobustDataServiceBoardsIEWSImplPort, RobustDataServiceBoardsIEWS.class, features);
    }

}
