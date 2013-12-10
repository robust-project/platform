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
//      Created By :            Vegard Engen
//      Created Date :          2013-04-29
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.gen;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.4.3 2012-01-23T08:10:46.863Z
 * Generated source version: 2.4.3
 *
 */
@WebServiceClient(name = "PredictorServiceImpl",
wsdlLocation = "file:/D:/svn/robust/code/WP1/cat/trunk/src/core/components/evaluationEngine/predictorServiceClient/src/main/resources/PredictorService.wsdl",
targetNamespace = "http://cxf.generated.predictorserviceclient.evaluationengine.cat.robust.itinnovation.soton.ac.uk/")
public class PredictorServiceImpl extends Service
{
    private QName PredictorServiceImplPort = new QName("http://cxf.generated.predictorserviceclient.evaluationengine.cat.robust.itinnovation.soton.ac.uk/", "PredictorServiceImplPort");

    public PredictorServiceImpl(URL wsdlLocation, QName serviceName, QName portName)
    {
        super(wsdlLocation, serviceName);
        PredictorServiceImplPort = portName;
    }

    /**
     *
     * @return returns PredictorServiceImplPortType
     */
    @WebEndpoint(name = "PredictorServiceImplPort")
    public PredictorServiceImplPortType getPredictorServiceImplPort()
    {
        //return super.getPort(PredictorServiceImplPortType.class);
        return super.getPort(PredictorServiceImplPort, PredictorServiceImplPortType.class);
    }

    /**
     *
     * @param features A list of {@link javax.xml.ws.WebServiceFeature} to
     * configure on the proxy. Supported features not in the
     * <code>features</code> parameter will have their default values.
     * @return returns PredictorServiceImplPortType
     */
    @WebEndpoint(name = "PredictorServiceImplPort")
    public PredictorServiceImplPortType getPredictorServiceImplPort(WebServiceFeature... features)
    {
        return super.getPort(PredictorServiceImplPort, PredictorServiceImplPortType.class, features);
    }
}