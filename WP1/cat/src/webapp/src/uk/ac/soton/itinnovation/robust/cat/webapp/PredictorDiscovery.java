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
//      Created By :            Bassem Nasser
//      Created Date :          12-Jul-2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.webapp;

import java.net.URI;
import java.util.Collection;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.cat.datalayer.impl.DataLayerImpl;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.PredictorClientTest;
import uk.ac.soton.itinnovation.robust.cat.evaluationengine.predictorserviceclient.PredictorServiceClient;


public class PredictorDiscovery {

    private static final Logger log = LoggerFactory.getLogger(PredictorDiscovery.class);
    static String nameSpace = "http://impl.ps.robust.itinnovation.soton.ac.uk/";
    static String serviceName = "PredictorServiceImplService";
    static String portName = "PredictorServiceImplPort";
    static String psServiceURI;
    static IDataLayer datalayer;


    public static void main(String[] args) throws Exception {
        
         // get the set of predictors and save in database
        try {
            log.info("getting predictors from predictors.xml");
            PredictorDiscovery pd = new PredictorDiscovery();

            XMLConfiguration config = new XMLConfiguration("predictors.xml");
            // get the number of communities
            Object preds = config.getProperty("predictor.predurl");
             log.info("got predictors "+preds);
            if (preds instanceof Collection) {
                log.info("Number of predictors: " + ((Collection) preds).size());
                for (int i = 0; i < ((Collection) preds).size(); i++) {
                    String url = config.getString("predictor(" + i + ").predurl");
                }
            }else{
            log.info("nothing was loaded! ");
            }
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }

        psServiceURI="http://localhost:8080/userActivityPredictorService-1.0/service";
        PredictorDiscovery discovery = new PredictorDiscovery();
        PredictorServiceDescription psd = discovery.getPSD(psServiceURI, nameSpace, serviceName, portName);
        discovery.savePSD(psd);
    }

    public static String getPsServiceURI() {
        return psServiceURI;
    }

    public static void setPsServiceURI(String psServiceURI) {
        PredictorDiscovery.psServiceURI = psServiceURI;
    }

    public static IDataLayer getDatalayer() {
        return datalayer;
    }

    public static void setDatalayer(IDataLayer datalayer) {
        PredictorDiscovery.datalayer = datalayer;
    }
    
    private void savePSD(PredictorServiceDescription psd) {
        try {
            datalayer = new DataLayerImpl();
            datalayer.addPredictor(psd);
        } catch (Exception ex) {
            throw new RuntimeException("error while saving predictor service in db",ex);
        }
    }

    public PredictorServiceDescription getPSD(String serviceURI, String nameSpace, String serviceName, String portName) throws Exception
    {

        log.info("Starting web-service client");
        log.info("----------------------------");
        
        PredictorServiceClient predictorServiceClient = new PredictorServiceClient(serviceURI, nameSpace, serviceName, portName);

        log.info("Getting PredictorServiceDescription");
        PredictorServiceDescription predictorServiceDesc = predictorServiceClient.getPredictorServiceDescription();
        log.info("Service name: " + predictorServiceDesc.getName());
		
		predictorServiceDesc.setServiceURI(new URI(serviceURI));
		predictorServiceDesc.setServiceName(serviceName);
		predictorServiceDesc.setServicePortName(portName);
		predictorServiceDesc.setServiceTargetNamespace(nameSpace);
        return predictorServiceDesc;
    }
    
     public PredictorServiceDescription getPSD(String serviceURI) throws Exception
    {
             
        log.info("Starting web-service client");
        log.info("----------------------------");
        
        PredictorServiceClient predictorServiceClient = new PredictorServiceClient(serviceURI, nameSpace, serviceName, portName);

        log.info("Getting PredictorServiceDescription");
        PredictorServiceDescription predictorServiceDesc = predictorServiceClient.getPredictorServiceDescription();
        log.info("Service name: " + predictorServiceDesc.getName());
        return predictorServiceDesc;
    }
}
