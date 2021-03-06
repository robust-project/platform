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
//      Created By :            bmn
//      Created Date :          03-Sep-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package client;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;
import uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.RODataServiceWS;
//http://localhost/robust-ro-dataservice-ws-1.2-SNAPSHOT/robustRODataService?wsdl
//http://robust-www.softwaremind.pl/robust-ro-dataservice-ws-1.2-SNAPSHOT/robustRODataService?wsdl
//http://robust.it-innovation.soton.ac.uk/robust-ro-dataservice-ws-1.2-SNAPSHOT/robustRODataService?wsdl
public class RODataServiceWSImplService extends Service {
    
    public final static String strUrl="http://robust-www.softwaremind.pl/robust-ro-dataservice-ws-1.2-SNAPSHOT/robustRODataService?wsdl";
    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://dataservice.ws.robust.swmind.pl/", "RODataServiceWSImplService");
    public final static QName RODataServiceWSImplPort = new QName("http://dataservice.ws.robust.swmind.pl/", "RODataServiceWSImplPort");
    static {
        URL url = null;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(RODataServiceWSImplService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://robust-www.softwaremind.pl/robust-ro-dataservice-ws-1.2-SNAPSHOT/robustRODataService?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public RODataServiceWSImplService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public RODataServiceWSImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public RODataServiceWSImplService() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     *
     * @return
     *     returns RODataServiceWS
     */
    @WebEndpoint(name = "RODataServiceWSImplPort")
    public RODataServiceWS getRODataServiceWSImplPort() {
        return super.getPort(RODataServiceWSImplPort, RODataServiceWS.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns RODataServiceWS
     */
    @WebEndpoint(name = "RODataServiceWSImplPort")
    public RODataServiceWS getRODataServiceWSImplPort(WebServiceFeature... features) {
        return super.getPort(RODataServiceWSImplPort, RODataServiceWS.class, features);
    }

}

