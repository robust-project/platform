/////////////////////////////////////////////////////////////////////////
//
// Â© University of Southampton IT Innovation Centre, 2012
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
//      Created By :            Vegard Engen, Edwin Tye
//      Created Date :          2013-04-09
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.ps.cm;

import java.util.UUID;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType;
import uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription;

/**
 *
 * @author Vegard Engen
 */
public class PSDGenerator
{

    /**
     * Create the SAP PredictorServiceDescription object, which defines the
     * offerings of the service to any clients.
     */
    public static PredictorServiceDescription getSAPdesc()
    {

        PredictorServiceDescription desc = new PredictorServiceDescription(UUID.randomUUID(), "CM Predictor Service", "1.0", "CM Predictor service)");

        // We first start to define the forecast period
        Parameter forecastPeriod = new Parameter(ParameterValueType.INT, "Forecast period", "The available forecast period options", "weeks");
        forecastPeriod.addValueConstraint("1", ValueConstraintType.DEFAULT);
        forecastPeriod.addValueConstraint("1", ValueConstraintType.MIN);
        forecastPeriod.addValueConstraint("52", ValueConstraintType.MAX);
        forecastPeriod.addValueConstraint("1", ValueConstraintType.STEP);
        desc.setForecastPeriod(forecastPeriod);

        // The the general parameter that is suitable for all of the events

        // Lets set which group we want to look at for both the SAP and IBM use case.  Two different configuration because the possible roles
        // are different in the two community

        Parameter roleParamSAP = new Parameter(ParameterValueType.STRING, "Roles for the use case", "Insert the name of the role", "");
        roleParamSAP.addValueConstraint("Inactive", ValueConstraintType.DEFAULT);
        roleParamSAP.addValueConstraint("Inactive", ValueConstraintType.VALUESALLOWED);                      // 0 
        roleParamSAP.addValueConstraint("Focussed_Expert_Participant", ValueConstraintType.VALUESALLOWED);   // 1
        roleParamSAP.addValueConstraint("Focussed_Novice", ValueConstraintType.VALUESALLOWED);               // 2
        roleParamSAP.addValueConstraint("Mixed_Novice", ValueConstraintType.VALUESALLOWED);                  // 3
        roleParamSAP.addValueConstraint("Distributed_Expert", ValueConstraintType.VALUESALLOWED);            // 4
        roleParamSAP.addValueConstraint("Focussed_Expert_Initator", ValueConstraintType.VALUESALLOWED);      // 5
        roleParamSAP.addValueConstraint("Distributed_Novice", ValueConstraintType.VALUESALLOWED);            // 6
        roleParamSAP.addValueConstraint("Focussed_Knowledgeable_Member", ValueConstraintType.VALUESALLOWED); // 7
        roleParamSAP.addValueConstraint("Mixed_Expert", ValueConstraintType.VALUESALLOWED);                  // 8
        roleParamSAP.addValueConstraint("Focussed_Knowledgeable_Sink", ValueConstraintType.VALUESALLOWED);   // 9
        roleParamSAP.addValueConstraint("Unmatched", ValueConstraintType.VALUESALLOWED);                     // -1

        desc.addConfigurationParam(roleParamSAP);

        // events (s)

        //////// Now we start to define the events where each event will have their own parameter that needs to be specified

        // First event
        /* The event that a particular role has dropped below a certain threshold 
         * Finding the probability that it is below a value i.e. P(X \le x) 
         */
        //UUID eventUUID = UUID.fromString("7b8bca1f-0a8a-4321-a885-ecec28ccbfef");
        UUID eventUUID1 = UUID.randomUUID();
        Event evt1 = new Event("Decrease in percentage of user in a role", "");
        evt1.setUuid(eventUUID1);
        EventCondition evtCond1 = new EventCondition(ParameterValueType.FLOAT, "Decrease in percentage of number of users in a role", "Percentage change in total number of user", "");
        evtCond1.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond1.addValueConstraint("-20", ValueConstraintType.DEFAULT);
        evtCond1.addValueConstraint("-100", ValueConstraintType.MIN);
        evtCond1.addValueConstraint("0", ValueConstraintType.MAX);
        // Basiclaly, we are replicating here because it feels more natural to 
        // use pre/post condition instead of any other way given
        // ParameterValue b1 = new ParameterValue("-0.2");
        evtCond1.setPostConditionValue(new ParameterValue("-20"));
        evt1.setEventCondition(evtCond1);
        Parameter roleParamSAPcopy1 = new Parameter(roleParamSAP);
        roleParamSAPcopy1.setUUID(UUID.randomUUID());
        evt1.addConfigParam(roleParamSAPcopy1);
        // Parameter roleParamSAPcopy1 = new Parameter(roleParamSAP);
        // roleParamSAPcopy1.setUUID(UUID.randomUUID());
        // evt1.addConfigParam(roleParamSAPcopy1);
        desc.addEvent(evt1);

        // Second event
        /* Inverse of the first, which means we want to know the probability that 
         * it we have increased by x\% or more, i.e. P(X \ge x)
         */
        UUID eventUUID2 = UUID.randomUUID();
        Event evt2 = new Event("Increase in percentage of user in a role", "");
        evt2.setUuid(eventUUID2);
        EventCondition evtCond2 = new EventCondition(ParameterValueType.FLOAT, "Increase in percentage of number of users in a role", "Percentage change in total number of users", "");
        evtCond2.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond2.addValueConstraint("20", ValueConstraintType.DEFAULT);
        evtCond2.addValueConstraint("0", ValueConstraintType.MIN);
        evtCond2.addValueConstraint("200", ValueConstraintType.MAX);
        // ParameterValue b2 = new ParameterValue("0.2");
        evtCond2.setPostConditionValue(new ParameterValue("20"));
        evt2.setEventCondition(evtCond2);
        Parameter roleParamSAPcopy2 = new Parameter(roleParamSAP);
        roleParamSAPcopy2.setUUID(UUID.randomUUID());
        evt2.addConfigParam(roleParamSAPcopy2);
        // Parameter roleParamSAPcopy2 = new Parameter(roleParamSAP);
        // roleParamSAPcopy2.setUUID(UUID.randomUUID());
        // evt2.addConfigParam(roleParamSAPcopy2);
        desc.addEvent(evt2);

        // The third and fourth event.  Same as the second event but instead of using a threshold, we want to use an absolute number
        UUID eventUUID3 = UUID.randomUUID();
        Event evt3 = new Event("Number of user below a value", "");
        evt3.setUuid(eventUUID3);
        EventCondition evtCond3 = new EventCondition(ParameterValueType.FLOAT, "Number of Users below the specified value", "Number of user below the specified value", "");
        evtCond3.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond3.addValueConstraint("10", ValueConstraintType.DEFAULT);
        evtCond3.addValueConstraint("0", ValueConstraintType.MIN);
        evtCond3.addValueConstraint("10000", ValueConstraintType.MAX);
        // ParameterValue b3 = new ParameterValue("10");
        evtCond3.setPostConditionValue(new ParameterValue("10"));
        evt3.setEventCondition(evtCond3);
        Parameter roleParamSAPcopy3 = new Parameter(roleParamSAP);
        roleParamSAPcopy3.setUUID(UUID.randomUUID());
        evt3.addConfigParam(roleParamSAPcopy3);
        // Parameter roleParamSAPcopy3 = new Parameter(roleParamSAP);
        // roleParamSAPcopy3.setUUID(UUID.randomUUID());
        // evt3.addConfigParam(roleParamSAPcopy3);
        desc.addEvent(evt3);

        UUID eventUUID4 = UUID.randomUUID();
        Event evt4 = new Event("Number of user above a value", "");
        evt4.setUuid(eventUUID4);
        EventCondition evtCond4 = new EventCondition(ParameterValueType.FLOAT, "Number of Users above the specified value", "Number of user above the specified value", "");
        evtCond4.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond4.addValueConstraint("10", ValueConstraintType.DEFAULT);
        evtCond4.addValueConstraint("0", ValueConstraintType.MIN);
        evtCond4.addValueConstraint("10000", ValueConstraintType.MAX);
        // ParameterValue b4 = new ParameterValue("10");
        evtCond4.setPostConditionValue(new ParameterValue("10"));
        evt4.setEventCondition(evtCond4);
        Parameter roleParamSAPcopy4 = new Parameter(roleParamSAP);
        roleParamSAPcopy4.setUUID(UUID.randomUUID());
        evt4.addConfigParam(roleParamSAPcopy4);
        // Parameter roleParamSAPcopy4 = new Parameter(roleParamSAP);
        // roleParamSAPcopy4.setUUID(UUID.randomUUID());
        // evt4.addConfigParam(roleParamSAPcopy4);
        desc.addEvent(evt4);

        return desc;
    }

    /**
     * Create the IBM PredictorServiceDescription object, which defines the
     * offerings of the service to any clients.
     */
    public static PredictorServiceDescription getIBMdesc()
    {

        PredictorServiceDescription desc = new PredictorServiceDescription(UUID.randomUUID(), "CM Predictor Service", "1.0", "CM Predictor service)");

        // We first start to define the forecast period
        Parameter forecastPeriod = new Parameter(ParameterValueType.INT, "Forecast period", "The available forecast period options", "weeks");
        forecastPeriod.addValueConstraint("1", ValueConstraintType.DEFAULT);
        forecastPeriod.addValueConstraint("1", ValueConstraintType.MIN);
        forecastPeriod.addValueConstraint("52", ValueConstraintType.MAX);
        forecastPeriod.addValueConstraint("1", ValueConstraintType.STEP);
        desc.setForecastPeriod(forecastPeriod);

        // The the general parameter that is suitable for all of the events

        Parameter roleParamIBM = new Parameter(ParameterValueType.STRING, "Roles for the use case", "Insert the name of the role", "");
        roleParamIBM.addValueConstraint("Lurker", ValueConstraintType.DEFAULT);
        roleParamIBM.addValueConstraint("Inactive", ValueConstraintType.VALUESALLOWED);                      // 0 
        roleParamIBM.addValueConstraint("Lurker", ValueConstraintType.VALUESALLOWED);                        // 1
        roleParamIBM.addValueConstraint("Contributor", ValueConstraintType.VALUESALLOWED);                   // 2
        roleParamIBM.addValueConstraint("Super User", ValueConstraintType.VALUESALLOWED);                    // 3
        roleParamIBM.addValueConstraint("Follower", ValueConstraintType.VALUESALLOWED);                      // 4
        roleParamIBM.addValueConstraint("BroadCaster", ValueConstraintType.VALUESALLOWED);                   // 5
        roleParamIBM.addValueConstraint("Daily User", ValueConstraintType.VALUESALLOWED);                    // 6
        roleParamIBM.addValueConstraint("Leader", ValueConstraintType.VALUESALLOWED);                        // 7
        roleParamIBM.addValueConstraint("Celebrity", ValueConstraintType.VALUESALLOWED);                     // 8
        roleParamIBM.addValueConstraint("Unmatched", ValueConstraintType.VALUESALLOWED);                     // -1

        // events (s)

        //////// Now we start to define the events where each event will have their own parameter that needs to be specified

        // First event
        /* The event that a particular role has dropped below a certain threshold 
         * Finding the probability that it is below a value i.e. P(X \le x) 
         */
        //UUID eventUUID = UUID.fromString("7b8bca1f-0a8a-4321-a885-ecec28ccbfef");
        UUID eventUUID1 = UUID.randomUUID();
        Event evt1 = new Event("Decrease in percentage of user in a role", "");
        evt1.setUuid(eventUUID1);
        EventCondition evtCond1 = new EventCondition(ParameterValueType.FLOAT, "Decrease in percentage of number of users in a role", "Percentage change in total number of user", "");
        evtCond1.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond1.addValueConstraint("-20", ValueConstraintType.DEFAULT);
        evtCond1.addValueConstraint("-100", ValueConstraintType.MIN);
        evtCond1.addValueConstraint("0", ValueConstraintType.MAX);
        // Basiclaly, we are replicating here because it feels more natural to 
        // use pre/post condition instead of any other way given
        // ParameterValue b1 = new ParameterValue("-0.2");
        evtCond1.setPostConditionValue(new ParameterValue("-20"));
        evt1.setEventCondition(evtCond1);
        Parameter roleParamIBMcopy1 = new Parameter(roleParamIBM);
        roleParamIBMcopy1.setUUID(UUID.randomUUID());
        evt1.addConfigParam(roleParamIBMcopy1);
        desc.addEvent(evt1);

        // Second event
        /* Inverse of the first, which means we want to know the probability that 
         * it we have increased by x\% or more, i.e. P(X \ge x)
         */
        UUID eventUUID2 = UUID.randomUUID();
        Event evt2 = new Event("Increase in percentage of user in a role", "");
        evt2.setUuid(eventUUID2);
        EventCondition evtCond2 = new EventCondition(ParameterValueType.FLOAT, "Increase in percentage of number of users in a role", "Percentage change in total number of users", "");
        evtCond2.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond2.addValueConstraint("20", ValueConstraintType.DEFAULT);
        evtCond2.addValueConstraint("0", ValueConstraintType.MIN);
        evtCond2.addValueConstraint("200", ValueConstraintType.MAX);
        // ParameterValue b2 = new ParameterValue("0.2");
        evtCond2.setPostConditionValue(new ParameterValue("20"));
        evt2.setEventCondition(evtCond2);
        Parameter roleParamIBMcopy2 = new Parameter(roleParamIBM);
        roleParamIBMcopy2.setUUID(UUID.randomUUID());
        evt2.addConfigParam(roleParamIBMcopy2);
        desc.addEvent(evt2);

        // The third and fourth event.  Same as the second event but instead of using a threshold, we want to use an absolute number
        UUID eventUUID3 = UUID.randomUUID();
        Event evt3 = new Event("Number of user below a value", "");
        evt3.setUuid(eventUUID3);
        EventCondition evtCond3 = new EventCondition(ParameterValueType.FLOAT, "Number of Users below the specified value", "Number of user below the specified value", "");
        evtCond3.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond3.addValueConstraint("10", ValueConstraintType.DEFAULT);
        evtCond3.addValueConstraint("0", ValueConstraintType.MIN);
        evtCond3.addValueConstraint("10000", ValueConstraintType.MAX);
        // ParameterValue b3 = new ParameterValue("10");
        evtCond3.setPostConditionValue(new ParameterValue("10"));
        evt3.setEventCondition(evtCond3);
        Parameter roleParamIBMcopy3 = new Parameter(roleParamIBM);
        roleParamIBMcopy3.setUUID(UUID.randomUUID());
        evt3.addConfigParam(roleParamIBMcopy3);
        desc.addEvent(evt3);

        UUID eventUUID4 = UUID.randomUUID();
        Event evt4 = new Event("Number of user above a value", "");
        evt4.setUuid(eventUUID4);
        EventCondition evtCond4 = new EventCondition(ParameterValueType.FLOAT, "Number of Users above the specified value", "Number of user above the specified value", "");
        evtCond4.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond4.addValueConstraint("10", ValueConstraintType.DEFAULT);
        evtCond4.addValueConstraint("0", ValueConstraintType.MIN);
        evtCond4.addValueConstraint("10000", ValueConstraintType.MAX);
        // ParameterValue b4 = new ParameterValue("10");
        evtCond4.setPostConditionValue(new ParameterValue("10"));
        evt4.setEventCondition(evtCond4);
        Parameter roleParamIBMcopy4 = new Parameter(roleParamIBM);
        roleParamIBMcopy4.setUUID(UUID.randomUUID());
        evt4.addConfigParam(roleParamIBMcopy4);
        desc.addEvent(evt4);

        return desc;
    }

    /**
     * Create the Boards.ie PredictorServiceDescription object, which defines
     * the offerings of the service to any clients.
     */
    public static PredictorServiceDescription getBOARDSIEdesc()
    {
        UUID psdUUID = UUID.fromString("68afb02e-7e89-4e5e-96c2-473c85c4ea8e");
        
        UUID event1UUID = UUID.fromString("ccce03cf-2df9-4dbd-bb39-ba5e341b5878");
        UUID event2UUID = UUID.fromString("23b37482-2a33-4144-be4f-7ddf3d7c9081");
        UUID event3UUID = UUID.fromString("5d8e9d73-c751-481c-a805-55b380e22324");
        UUID event4UUID = UUID.fromString("a3fd457a-ee52-4117-91b0-a9d7ef3443e1");
        
        UUID event1_roleParamBOARDSIEUUID = UUID.fromString("966f1759-57d7-4be5-92ef-e28782e1f58e");
        UUID event2_roleParamBOARDSIEUUID = UUID.fromString("dfd2e0ca-f747-49e2-8e13-57b94fc439fe");
        UUID event3_roleParamBOARDSIEUUID = UUID.fromString("9f6b87fb-88b7-4566-9e6a-b602b62ac692");
        UUID event4_roleParamBOARDSIEUUID = UUID.fromString("1589e427-a935-4d01-b7eb-2fc6e93f868e");
        
        PredictorServiceDescription desc = new PredictorServiceDescription(psdUUID, "User Role (CM) Predictor Service", "1.3", "A Predictor service using the Compartment Model to make predictions about roles of users in the community");

        // We first start to define the forecast period
        Parameter forecastPeriod = new Parameter(ParameterValueType.INT, "Forecast period", "The available forecast period options", "weeks");
        forecastPeriod.addValueConstraint("1", ValueConstraintType.DEFAULT);
        forecastPeriod.addValueConstraint("1", ValueConstraintType.MIN);
        forecastPeriod.addValueConstraint("52", ValueConstraintType.MAX);
        forecastPeriod.addValueConstraint("1", ValueConstraintType.STEP);
        desc.setForecastPeriod(forecastPeriod);

        // The the general parameter that is suitable for all of the events
        Parameter roleParamBOARDSIE = new Parameter(ParameterValueType.STRING, "Roles for the use case", "Insert the name of the role", "");
        roleParamBOARDSIE.addValueConstraint("Lurker", ValueConstraintType.DEFAULT);
        roleParamBOARDSIE.addValueConstraint("Inactive", ValueConstraintType.VALUESALLOWED);                      // 0 
        roleParamBOARDSIE.addValueConstraint("Lurker", ValueConstraintType.VALUESALLOWED);                        // 1
        roleParamBOARDSIE.addValueConstraint("Contributor", ValueConstraintType.VALUESALLOWED);                   // 2
        roleParamBOARDSIE.addValueConstraint("Super User", ValueConstraintType.VALUESALLOWED);                    // 3
        roleParamBOARDSIE.addValueConstraint("Follower", ValueConstraintType.VALUESALLOWED);                      // 4
        roleParamBOARDSIE.addValueConstraint("BroadCaster", ValueConstraintType.VALUESALLOWED);                   // 5
        roleParamBOARDSIE.addValueConstraint("Daily User", ValueConstraintType.VALUESALLOWED);                    // 6
        roleParamBOARDSIE.addValueConstraint("Leader", ValueConstraintType.VALUESALLOWED);                        // 7
        roleParamBOARDSIE.addValueConstraint("Celebrity", ValueConstraintType.VALUESALLOWED);                     // 8
        roleParamBOARDSIE.addValueConstraint("Unmatched", ValueConstraintType.VALUESALLOWED);                     // -1

        // events (s)

        //////// Now we start to define the events where each event will have their own parameter that needs to be specified

        // First event
        /* The event that a particular role has dropped below a certain threshold 
         * Finding the probability that it is below a value i.e. P(X \le x) 
         */
        
        Event evt1 = new Event("Decrease in percentage of user in a role", "");
        evt1.setUuid(event1UUID);
        EventCondition evtCond1 = new EventCondition(ParameterValueType.FLOAT, "Decrease in percentage of number of users in a role", "Percentage change in total number of user", "");
        evtCond1.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond1.addValueConstraint("-20", ValueConstraintType.DEFAULT);
        evtCond1.addValueConstraint("-100", ValueConstraintType.MIN);
        evtCond1.addValueConstraint("0", ValueConstraintType.MAX);
        evt1.setEventCondition(evtCond1);
        Parameter roleParamBOARDSIEcopy1 = new Parameter(roleParamBOARDSIE);
        roleParamBOARDSIEcopy1.setUUID(event1_roleParamBOARDSIEUUID);
        evt1.addConfigParam(roleParamBOARDSIEcopy1);
        desc.addEvent(evt1);

        // Second event
        /* Inverse of the first, which means we want to know the probability that 
         * it we have increased by x\% or more, i.e. P(X \ge x)
         */
        
        Event evt2 = new Event("Increase in percentage of user in a role", "");
        evt2.setUuid(event2UUID);
        EventCondition evtCond2 = new EventCondition(ParameterValueType.FLOAT, "Increase in percentage of number of users in a role", "Percentage change in total number of users", "");
        evtCond2.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond2.addValueConstraint("20", ValueConstraintType.DEFAULT);
        evtCond2.addValueConstraint("0", ValueConstraintType.MIN);
        evtCond2.addValueConstraint("200", ValueConstraintType.MAX);
        evt2.setEventCondition(evtCond2);
        Parameter roleParamBOARDSIEcopy2 = new Parameter(roleParamBOARDSIE);
        roleParamBOARDSIEcopy2.setUUID(event2_roleParamBOARDSIEUUID);
        evt2.addConfigParam(roleParamBOARDSIEcopy2);
        desc.addEvent(evt2);

        // The third and fourth event.  Same as the second event but instead of using a threshold, we want to use an absolute number
        
        Event evt3 = new Event("Number of user below a value", "");
        evt3.setUuid(event3UUID);
        EventCondition evtCond3 = new EventCondition(ParameterValueType.FLOAT, "Number of Users below the specified value", "Number of user below the specified value", "");
        evtCond3.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond3.addValueConstraint("10", ValueConstraintType.DEFAULT);
        evtCond3.addValueConstraint("0", ValueConstraintType.MIN);
        evtCond3.addValueConstraint("10000", ValueConstraintType.MAX);
        evt3.setEventCondition(evtCond3);
        Parameter roleParamBOARDSIEcopy3 = new Parameter(roleParamBOARDSIE);
        roleParamBOARDSIEcopy3.setUUID(event3_roleParamBOARDSIEUUID);
        evt3.addConfigParam(roleParamBOARDSIEcopy3);
        desc.addEvent(evt3);

        
        Event evt4 = new Event("Number of user above a value", "");
        evt4.setUuid(event4UUID);
        EventCondition evtCond4 = new EventCondition(ParameterValueType.FLOAT, "Number of Users above the specified value", "Number of user above the specified value", "");
        evtCond4.setValuesAllowedType(ValuesAllowedType.SINGLE);
        evtCond4.addValueConstraint("10", ValueConstraintType.DEFAULT);
        evtCond4.addValueConstraint("0", ValueConstraintType.MIN);
        evtCond4.addValueConstraint("10000", ValueConstraintType.MAX);
        evt4.setEventCondition(evtCond4);
        Parameter roleParamBOARDSIEcopy4 = new Parameter(roleParamBOARDSIE);
        roleParamBOARDSIEcopy4.setUUID(event4_roleParamBOARDSIEUUID);
        evt4.addConfigParam(roleParamBOARDSIEcopy4);
        desc.addEvent(evt4);

        return desc;
    }
}
