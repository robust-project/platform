///////////////////////////////////////////////////////////////////////////
////
//// © University of Southampton IT Innovation Centre, 2011
////
//// Copyright in this software belongs to University of Southampton
//// IT Innovation Centre of Gamma House, Enterprise Road, 
//// Chilworth Science Park, Southampton, SO16 7NS, UK.
////
//// This software may not be used, sold, licensed, transferred, copied
//// or reproduced in whole or in part in any manner or form or in or
//// on any media by any person other than in accordance with the terms
//// of the Licence Agreement supplied with the software, or otherwise
//// without the prior written consent of the copyright owners.
////
//// This software is distributed WITHOUT ANY WARRANTY, without even the
//// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
//// PURPOSE, except where stated in the Licence Agreement supplied with
//// the software.
////
////      Created By :            bmn
////      Created Date :          18-Nov-2011
////      Created for Project :   ROBUST
////
///////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.datalayer.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.common.ps.*;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.Treatment;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.TreatmentTemplate;
import uk.ac.soton.itinnovation.robust.riskmodel.*;

public class PopulateDB {

    static final Logger logger = Logger.getLogger(PopulateDB.class);   
    private static String scriptPath=null;



    public static void getConfigs() {
        Properties prop = new Properties();

        try {
            prop.load(PopulateDB.class.getClassLoader().getResourceAsStream("data.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("Error with loading configuration file data.properties. " + ex.getMessage(), ex);
        }
        scriptPath= prop.getProperty("repoSQLSchema");

    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        logger.info("Starting populate DB script");
        boolean all=false;
        
        String usecase=null;
        
        if(args.length>1)
            throw new RuntimeException(" Recieved input parameters: "+args +" .Should be one input parameter only eith ibm or sap");
        
        if(args.length==0)
            all=true;
        else{ //length 1
         usecase=args[0];
        }
        
        System.out.println("************************************************************************************************");
        System.out.println("THIS WILL RUN THE SCRIPT AGAINST THE DATABASE. THIS WILL DROP AND RECREATE THE DB STRUCTURE");
        System.out.println("************************************************************************************************");

        
        getConfigs();
        DataLayerImpl datalayer = null;
        try {
            datalayer = new DataLayerImpl();
        } catch (Exception ex) {
            logger.debug("Error while initializing the datalayer. " + ex.getMessage());
            throw new RuntimeException("Error while initializint the datalayer. ", ex);
        }
        
        try{
         logger.debug("Creating DB using script at "+scriptPath);
            datalayer.runScript(scriptPath);
        }catch(Exception ex){
            logger.debug("**********************************************************************************");
            logger.debug("**********************************************************************************");
            logger.debug("Error while getting the mysql schema script at "+scriptPath+". Will continue assuming that the DB exist and is empty. " + ex.getMessage());
            logger.debug("**********************************************************************************");
        }
        
        PopulateIBM popIBM=new PopulateIBM();
        PopulateSAP popSAP=new PopulateSAP();
        
        try {
            if (all) {
                System.out.println("***************");
                System.out.println("IBM DATA");
                System.out.println("***************");
                popIBM.populateIBM(datalayer);

                System.out.println("***************");
                System.out.println("SAP DATA");
                System.out.println("****************");
                popSAP.populateSAP(datalayer);
            } else if (usecase.equalsIgnoreCase("ibm")) {
                System.out.println("***************");
                System.out.println("IBM DATA");
                System.out.println("***************");
                popIBM.populateIBM(datalayer);
            } else if (usecase.equalsIgnoreCase("sap")) {
                System.out.println("***************");
                System.out.println("SAP DATA");
                System.out.println("****************");
                popSAP.populateSAP(datalayer);
            }
            
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }


   
}
