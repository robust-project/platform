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
//      Created By :            Vegard Engen
//      Created Date :          2013-05-01
//      Created for Project :   
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.datalayer.impl;

import java.util.Map;
import java.util.Set;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

/**
*
* @author Vegard Engen
*/
public class TestRisks
{

    static final Logger logger = Logger.getLogger(TestRisks.class);
    static IDataLayer datalayer = null;

    public static void main(String[] args)
    {

        Map<Community, Set<Risk>> allRisks = null;

        BasicConfigurator.configure();


        try {
            datalayer = new DataLayerImpl();

            allRisks = datalayer.getAllActiveRisks();

            if (allRisks != null) {
                for (Community com : allRisks.keySet()) {

                    printAllRisks(allRisks.get(com));
                }
            }

            Set<PredictorServiceDescription> preds = datalayer.getPredictors();
            printPreds(preds);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

      public static void printPreds(Set<PredictorServiceDescription> preds)
    {
    
        for(PredictorServiceDescription pred:preds){
             System.out.println("\n****Predictor: " +pred.getName());
             if(pred.getForecastPeriod()!=null){
                 System.out.println("Forecast period param " +pred.getForecastPeriod().getName());
                 System.out.println("**Forecast period max " +pred.getForecastPeriod().getMax());
                 System.out.println("**Forecast period min " +pred.getForecastPeriod().getMin());
                 System.out.println("**Forecast period default " +pred.getForecastPeriod().getDefaultValue());
                 System.out.println("**Forecast period value " +pred.getForecastPeriod().getValue());
             }
             
             if(pred.getConfigurationParams()!=null){
             for(Parameter param:pred.getConfigurationParams()){
                  System.out.println("param: "+param.getName());
                  System.out.println("param max: "+param.getMax());
                  System.out.println("param min: "+param.getMin());
                  System.out.println("param default: "+param.getDefaultValue());
                  System.out.println("param value: "+param.getValue());
             }
             }
             
             if (pred.getEvents() != null) {
                System.out.println("  Events: ");
                for (Event ev : pred.getEvents()) {
                    System.out.println("  Event title: " + ev.getTitle());
                    System.out.println("     Event conditions: ");
                    for (EventCondition evCond : ev.getEventConditions()) {
                        System.out.println("     - condition name: " + evCond.getName());
                        System.out.println("        condition type: " + evCond.getType());
                        
                    }
                }
            }
        }
    }
      
      
    public static void printAllRisks(Set<Risk> risks)
    {
        System.out.println("------------ print all risks ------------------");
        
        for (Risk temp : risks) {

            System.out.println("\n****Risk: ");
            System.out.println(
                    "  uuid: " + temp.getId() + "\n"
                    + "  title:" + temp.getTitle() + "\n"
                    + "  owner: " + temp.getOwner() + "\n"
                    + "  type: " + temp.getType() + "\n"
                    + "  expirydate: " + temp.getExpiryDate() + "\n"
                    + "  admin_review_freq: " + temp.getAdmin_review_freq() + "\n"
                    + "  cat_review_freq: " + temp.getCat_review_freq() + "\n"
                    + "  group: " + temp.getGroup() + "\n"
                    + "  state: " + temp.getState() + "\n"
                    + "  scope: " + temp.getScope() + "\n"
                    + "  cat_review_period: " + temp.getCat_review_period() + "\n"
                    + "  admin_review_period: " + temp.getAdmin_review_period());

            if (temp.getImpact() != null && temp.getImpact().getImpactMap() != null) {
                System.out.println("  Impact: ");
                for (Objective obj : temp.getImpact().getImpactMap().keySet()) {
                    System.out.println(
                            "     obj id: " + obj.getId()
                            + "     title:" + obj.getTitle()
                            + "     impactlevel: " + temp.getImpact().getImpactMap().get(obj).name() /*
                             * + " impactPolarity: " +
                             * temp.getImpact().getImpactMap().get(obj).isIsPositive()
                             */);
                }
            }
            
 

            if (temp.getSetEvent() != null) {
                System.out.println("  Events: ");
                for (Event ev : temp.getSetEvent()) {
                    System.out.println("  Event title: " + ev.getTitle());
                    System.out.println("     Event conditions: ");
                    if (ev.getConfigParams() != null) {
                        for (Parameter param : ev.getConfigParams()) {
                            System.out.println("Event param: " + param.getName());
                            System.out.println("  param max: " + param.getMax());
                            System.out.println("  param min: " + param.getMin());
                            System.out.println("  param default: " + param.getDefaultValue());
                            System.out.println("  param value: " + param.getValue());
                            System.out.println("  param value eval type: " + param.getValue().getValueEvaluationType());
                        }
                    }
                    for (EventCondition evCond : ev.getEventConditions()) {
                        System.out.println("     - condition name: " + evCond.getName());
                        System.out.println("        condition type: " + evCond.getType());
                        System.out.println("        precondition value: " + evCond.getPreConditionValue().getValue());
                        System.out.println("        postcondition value: " + evCond.getPostConditionValue().getValue());
                    }
                }
            }

        }
    }
}
