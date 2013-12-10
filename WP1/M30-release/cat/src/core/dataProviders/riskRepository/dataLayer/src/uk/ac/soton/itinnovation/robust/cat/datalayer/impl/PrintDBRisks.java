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
//      Created Date :          02-Apr-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.datalayer.impl;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.Treatment;
import uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.TreatmentTemplate;
import uk.ac.soton.itinnovation.robust.cat.datalayer.common.IDataLayer;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

public class PrintDBRisks {



    public static void main(String[] args) {

        //BasicConfigurator.configure();
        IDataLayer datalayer = null;
        try {

            datalayer = new DataLayerImpl();
            printall(datalayer);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public static void printall(IDataLayer datalayer) {
        System.out.println("------------ print all risks ------------------");
        Set<Community> comms = datalayer.getCommunities();
        for (Community comm : comms) {
            Set<Risk> risks = datalayer.getRisks(comm.getUuid());
            int i = 1;
            for (Risk temp : risks) {

                System.out.println("\n****Risk " + i + "/" + risks.size());
                System.out.println(
                        "  UUID: " + temp.getId() + "\n"
                        + "  Title:" + temp.getTitle() + "\n");

                for(Event ev:temp.getSetEvent()){
                    PredictorServiceDescription pred = datalayer.getPredictor(ev);
                    System.out.println("  Predictor name: " + pred.getName() + ", at " + pred.getServiceURI());
                    System.out.println("  Event: " + ev.getTitle());
                    System.out.println(
                            "     Event conditions: " + ev.getEventConditions().size());
                    for (EventCondition evCond :ev.getEventConditions()) {
                        System.out.println("     - condition name: " + evCond.getName());
                        System.out.println("        condition type: " + evCond.getType());
                        System.out.println("        precondition value: " + evCond.getPreConditionValue().getValue());
                        System.out.println("        postcondition value: " + evCond.getPostConditionValue().getValue());
                    }
                }

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


                if (temp.getTreatment() != null) {
                    System.out.println("  Treatment templates: ");
                    
                    List<Map.Entry<Float,TreatmentTemplate>> templates = temp.getTreatment().getOrderedCopyOfTreatmentTemplates();
                    for ( Map.Entry<Float,TreatmentTemplate> tempEntry : templates )
                      System.out.println("     Template: " + tempEntry.getValue().getTitle() + ", uuid: " +  tempEntry.getValue().getID().toString() );
                
                }

                System.out.println(
                        "  Other:" + "\n"
                        + "    owner: " + temp.getOwner() + "\n"
                        + "    type: " + temp.getType() + "\n"
                        + "    expirydate: " + temp.getExpiryDate() + "\n"
                        + "    admin_review_freq: " + temp.getAdmin_review_freq() + "\n"
                        + "    cat_review_freq: " + temp.getCat_review_freq() + "\n"
                        + "    group: " + temp.getGroup() + "\n"
                        + "    state: " + temp.getState() + "\n"
                        + "    scope: " + temp.getScope() + "\n"
                        + "    cat_review_period: " + temp.getCat_review_period() + "\n"
                        + "    admin_review_period: " + temp.getAdmin_review_period());
                i++;
            }
        }

    }
}
