/** 
*Copyright 2013 Software Mind SA
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/
package pl.softwaremind.robust.healthindicatorservice;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.4.3
 * 2011-12-23T11:05:35.300+01:00
 * Generated source version: 2.4.3
 * 
 */
@WebServiceClient(name = "HealthIndicatorService", 
                  wsdlLocation = "file:/home/rafal/workspace/robust/interfejs/trunk/src/main/resources/service.wsdl",
                  targetNamespace = "http://robust.softwaremind.pl/HealthIndicatorService") 
public class HealthIndicatorService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://robust.softwaremind.pl/HealthIndicatorService", "HealthIndicatorService");
    public final static QName HealthIndicatorServiceSoap = new QName("http://robust.softwaremind.pl/HealthIndicatorService", "HealthIndicatorServiceSoap");
    static {
        URL url = null;
        try {
            url = new URL("file:/home/rafal/workspace/robust/interfejs/trunk/src/main/resources/service.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(HealthIndicatorService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "file:/home/rafal/workspace/robust/interfejs/trunk/src/main/resources/service.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public HealthIndicatorService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public HealthIndicatorService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public HealthIndicatorService() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     *
     * @return
     *     returns HealthIndicatorServiceSoap
     */
    @WebEndpoint(name = "HealthIndicatorServiceSoap")
    public HealthIndicatorServiceSoap getHealthIndicatorServiceSoap() {
        return super.getPort(HealthIndicatorServiceSoap, HealthIndicatorServiceSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns HealthIndicatorServiceSoap
     */
    @WebEndpoint(name = "HealthIndicatorServiceSoap")
    public HealthIndicatorServiceSoap getHealthIndicatorServiceSoap(WebServiceFeature... features) {
        return super.getPort(HealthIndicatorServiceSoap, HealthIndicatorServiceSoap.class, features);
    }

}