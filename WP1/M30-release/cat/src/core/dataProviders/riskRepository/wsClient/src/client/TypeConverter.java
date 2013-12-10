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
//      Created Date :          28-Aug-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package client;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import java.lang.Exception;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.dozer.DozerBeanMapper;
import org.dozer.converters.XMLGregorianCalendarConverter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.*;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType;
import uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.*;
import uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Impact.ImpactMap;
import uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Impact.ImpactMap.Entry;
import uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.TreatmentWFs.TemplatesByOrder;
import uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.TreatmentWFs.TreatmentsTemplatesByID;
import uk.ac.soton.itinnovation.robust.riskmodel.*;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.Period;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;
import uk.ac.soton.itinnovation.robust.riskmodel.RiskState;
import uk.ac.soton.itinnovation.robust.riskmodel.Scope;

public class TypeConverter {

    private static DozerBeanMapper mapper;

    static {
        java.util.ArrayList myMappingFiles = new java.util.ArrayList();
        myMappingFiles.add("dozerBeanMapping.xml");
        mapper = new DozerBeanMapper();
        mapper.setMappingFiles(myMappingFiles);

    }

    public static uk.ac.soton.itinnovation.robust.riskmodel.Community getCommunity(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community srcObj) {
        uk.ac.soton.itinnovation.robust.riskmodel.Community destObject = mapper.map(srcObj, uk.ac.soton.itinnovation.robust.riskmodel.Community.class);
        return destObject;
    }

    public static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community getCommunity(uk.ac.soton.itinnovation.robust.riskmodel.Community srcObj) {

        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community destObject = mapper.map(srcObj, uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community.class);
        return destObject;
    }

    public static uk.ac.soton.itinnovation.robust.riskmodel.Objective getObjective(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.riskmodel.Objective destObject = mapper.map(srcObj, uk.ac.soton.itinnovation.robust.riskmodel.Objective.class);
        return destObject;
    }

        public static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective getObjective(uk.ac.soton.itinnovation.robust.riskmodel.Objective srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective destObject = mapper.map(srcObj, uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Objective.class);
        return destObject;
    }
        
    public static uk.ac.soton.itinnovation.robust.riskmodel.ImpactLevel getImpactLevel(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ImpactLevel srcObj) {
        if (srcObj == null) {
            return null;
        }
        //        uk.ac.soton.itinnovation.robust.riskmodel.ImpactLevel destObject = uk.ac.soton.itinnovation.robust.riskmodel.ImpactLevel.fromValue(obj.toString());
        uk.ac.soton.itinnovation.robust.riskmodel.ImpactLevel destObject = mapper.map(srcObj, uk.ac.soton.itinnovation.robust.riskmodel.ImpactLevel.class);
        return destObject;
    }

    public static uk.ac.soton.itinnovation.robust.riskmodel.Impact getImpact(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Impact srcObj) {
        if ((srcObj == null)||(srcObj.getImpactMap()==null)||(srcObj.getImpactMap().getEntry()==null)||(srcObj.getImpactMap().getEntry().isEmpty())) {
            return null;
        }

        uk.ac.soton.itinnovation.robust.riskmodel.Impact destObject = new uk.ac.soton.itinnovation.robust.riskmodel.Impact();
        ImpactMap srcImpMap = srcObj.getImpactMap();
        List<Entry> entries=srcImpMap.getEntry();
        
        for(int i=0;i<entries.size();i++){
           destObject.addImpactObj(getObjective(entries.get(i).getKey()), getImpactLevel(entries.get(i).getValue())); 
        }        
        
        return destObject;
    }

    public static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Impact getImpact(uk.ac.soton.itinnovation.robust.riskmodel.Impact srcObj) {
        if ((srcObj == null)|| srcObj.getImpactMap().isEmpty()) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Impact destObject = new uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Impact();//mapper.map(srcObj, uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Impact.class);
        
        Map<uk.ac.soton.itinnovation.robust.riskmodel.Objective, uk.ac.soton.itinnovation.robust.riskmodel.ImpactLevel> srcImpMap = srcObj.getImpactMap();
        ImpactMap impMap=new ImpactMap();
        
        
        for(uk.ac.soton.itinnovation.robust.riskmodel.Objective obj:srcImpMap.keySet()){
            Entry ent=new Entry();
            ent.setKey(getObjective(obj));
            ent.setValue(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ImpactLevel.fromValue(srcImpMap.get(obj).value()));
                    
            impMap.getEntry().add(ent);
        }
        
       destObject.setImpactMap(impMap);
        
        return destObject;
    }

    public static uk.ac.soton.itinnovation.robust.riskmodel.Risk getRisk(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk srcObj) {
        if (srcObj == null) {
            return null;
        }
//              UUID id;
//    String title;
//    String owner;
//    Boolean type;
//    Date expiryDate;
//    Impact impact;
//    Set<Event> setEvent;
//    TreatmentWFs treatment;
//    int admin_review_freq=1;
//    int cat_review_freq=1;
//    String group;
//    
//    Community community;
//    
//    boolean notification=false;
//    RiskState state=RiskState.INACTIVE;
//    Scope scope=Scope.COMMUNITY;
//    Period cat_review_period=Period.WEEK;
//    Period admin_review_period=Period.WEEK;
        uk.ac.soton.itinnovation.robust.riskmodel.Risk destObject = new Risk();//mapper.map(obj, uk.ac.soton.itinnovation.robust.riskmodel.Risk.class);
        destObject.setId(srcObj.getId());
        destObject.setTitle(srcObj.getTitle());
        destObject.setOwner(srcObj.getOwner());
        destObject.setType(srcObj.isType());

        destObject.setExpiryDate((srcObj.getExpiryDate() != null) ? srcObj.getExpiryDate().toGregorianCalendar().getTime() : null);
        destObject.setImpact(getImpact(srcObj.getImpact()));
        destObject.setSetEvent(getEvents(srcObj.getSetEvent()));

        destObject.setTreatment(getTreatment(srcObj.getTreatment())); 
        destObject.setAdmin_review_freq(srcObj.getAdminReviewFreq());
        destObject.setCat_review_freq(srcObj.getCatReviewFreq());
        destObject.setGroup(srcObj.getGroup());

        Community comm = null;
        if (srcObj.getCommunity() != null) {
            try {
                comm=new Community(srcObj.getCommunity().getName(), UUID.fromString(srcObj.getCommunity().getUuid()), srcObj.getCommunity().getCommunityID(), srcObj.getCommunity().getStreamName(), srcObj.getCommunity().getUri()==null?null:new URI(srcObj.getCommunity().getUri()), srcObj.getCommunity().isIsStream());
//                comm = new Community(srcObj.getCommunity().getName(), UUID.fromString(srcObj.getCommunity().getUuid()), new URI(srcObj.getCommunity().getUri()), srcObj.getCommunity().isIsStream());
            } catch (Exception ex) {
                throw new RuntimeException("error creating community object. " + ex, ex);
            }
        }

        destObject.setCommunity(comm);

        destObject.setNotification(srcObj.isNotification());

        destObject.setState(RiskState.fromString(srcObj.getState().value()));
        destObject.setScope(Scope.fromString(srcObj.getScope().value()));

        destObject.setCat_review_period(Period.fromString(srcObj.getCatReviewPeriod().value()));
        destObject.setAdmin_review_period(Period.fromString(srcObj.getAdminReviewPeriod().value()));

        destObject.setTreatProcIDS((java.util.ArrayList<String>)srcObj.getTreatProcIDS());
        
        return destObject;
    }

     public static java.util.List<uk.ac.soton.itinnovation.robust.riskmodel.Risk> getRiskList(java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk> srcObj){
         
         java.util.List<uk.ac.soton.itinnovation.robust.riskmodel.Risk> listRisks=new java.util.ArrayList<uk.ac.soton.itinnovation.robust.riskmodel.Risk>();
                 
         for(int i=0;i<srcObj.size();i++){
            listRisks.add(getRisk(srcObj.get(i)));
         }
         
         return listRisks;
     }
     
    public static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk getRisk(uk.ac.soton.itinnovation.robust.riskmodel.Risk srcObj) {
        if (srcObj == null) {
            return null;
        }
//              UUID id;
//    String title;
//    String owner;
//    Boolean type;
//    Date expiryDate;
//    Impact impact;
//    Set<Event> setEvent;
//    TreatmentWFs treatment;
//    int admin_review_freq=1;
//    int cat_review_freq=1;
//    String group;
//    
//    Community community;
//    
//    boolean notification=false;
//    RiskState state=RiskState.INACTIVE;
//    Scope scope=Scope.COMMUNITY;
//    Period cat_review_period=Period.WEEK;
//    Period admin_review_period=Period.WEEK;
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk destObject = new uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Risk();//mapper.map(obj, uk.ac.soton.itinnovation.robust.riskmodel.Risk.class);
        destObject.setId(srcObj.getId().toString());
        destObject.setTitle(srcObj.getTitle());
        destObject.setOwner(srcObj.getOwner());
        destObject.setType(srcObj.getType());
        if(srcObj.getExpiryDate()!=null){
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(srcObj.getExpiryDate());
        XMLGregorianCalendar date2 = null;
        try {
            date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(TypeConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

        destObject.setExpiryDate(date2);
        }
        destObject.setImpact(getImpact(srcObj.getImpact()));
        destObject.getSetEvent().addAll(getEvents(srcObj.getSetEvent()));

        destObject.setTreatment(getTreatment(srcObj.getTreatment())); //TO be sorted, service wsdl doesnt show the content
        destObject.setAdminReviewFreq(srcObj.getAdmin_review_freq());
        destObject.setCatReviewFreq(srcObj.getCat_review_freq());
        destObject.setGroup(srcObj.getGroup());

        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community comm = null;
        if (srcObj.getCommunity() != null) {
            try {
                comm = new uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Community();
                comm.setName(srcObj.getCommunity().getName());
                comm.setUuid(srcObj.getCommunity().getUuid().toString());
                if(srcObj.getCommunity().getUri()!=null)
                    comm.setUri(srcObj.getCommunity().getUri().toString());
                comm.setIsStream(srcObj.getCommunity().getIsStream());

            } catch (Exception ex) {
                throw new RuntimeException("error creating community object. " + ex, ex);
            }
        }

        destObject.setCommunity(comm);

        destObject.setNotification(srcObj.isNotification());

        destObject.setState(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.RiskState.fromValue(srcObj.getState().getName().toUpperCase()));
        destObject.setScope(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Scope.fromValue(srcObj.getScope().getName().toUpperCase()));

        destObject.setCatReviewPeriod(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Period.fromValue(srcObj.getCat_review_period().getPeriod().toUpperCase()));
        destObject.setAdminReviewPeriod(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Period.fromValue(srcObj.getAdmin_review_period().getPeriod().toUpperCase()));

        destObject.getTreatProcIDS().addAll(srcObj.getTreatProcIDS());
        
        return destObject;
    }

    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event getEvent(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event srcObj) {
        if (srcObj == null) {
            return null;
        }
        //            private UUID uuid;
//    private String title;
//    private String description;
//    private List<Parameter> configParams; // since each event might have different config options to the general service config options
//    private List<EventCondition> eventConditions; // an event 

        uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event destObject = new Event();
        destObject.setUuid(srcObj.getUuid());
        destObject.setTitle(srcObj.getTitle());
        destObject.setDescription(srcObj.getDescription());

        destObject.setConfigParams(getParameters(srcObj.getConfigParams()));

        destObject.setEventConditions(getEventConditions(srcObj.getEventConditions()));

        return destObject;
    }

    public static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event getEvent(uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event srcObj) {
        if (srcObj == null) {
            return null;
        }
        //            private UUID uuid;
//    private String title;
//    private String description;
//    private List<Parameter> configParams; // since each event might have different config options to the general service config options
//    private List<EventCondition> eventConditions; // an event 

        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event destObject = new uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event();
        destObject.setUuid(srcObj.getUuid().toString());
        destObject.setTitle(srcObj.getTitle());
        destObject.setDescription(srcObj.getDescription());

        destObject.getConfigParams().addAll(getParams(srcObj.getConfigParams()));

        destObject.getEventConditions().addAll(getEventConds(srcObj.getEventConditions()));

        return destObject;
    }

    private static Set<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event> getEvents(List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event> srcObj) {
        if (srcObj == null) {
            return null;
        }

        Set<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event> destObj = new HashSet<Event>();
        for (int i = 0; i < srcObj.size(); i++) {
            destObj.add(getEvent(srcObj.get(i)));
        }

        return destObj;
    }

    private static List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event> getEvents(Set<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event> srcObj) {
        if (srcObj == null) {
            return null;
        }
        java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event> destObj = new java.util.ArrayList<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event>();
        for (Event ev : srcObj) {
            uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event temp = getEvent(ev);
            destObj.add(temp);
        }

        return destObj;
    }
    
    private static java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event> getEventList(java.util.List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event> srcObj) {
        if (srcObj == null) {
            return null;
        }
        java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event> destObj = new java.util.ArrayList<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event>();
        for (Event ev : srcObj) {
            uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event temp = getEvent(ev);
            destObj.add(temp);
        }

        return destObj;
    }
    
        private static java.util.List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event> getEventLists(java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event> srcObj) {
        if (srcObj == null) {
            return null;
        }
        java.util.List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event> destObj = new java.util.ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event>();
        for (uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Event ev : srcObj) {
            uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event temp = getEvent(ev);
            destObj.add(temp);
        }

        return destObj;
    }

    private static uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter getParameter(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Parameter srcObj) {
        if (srcObj == null) {
            return null;
        }
//        private UUID uuid;
//    private String name;
//    private String description;
//    private String unit; // can be null
//    private ParameterValueType type;
//    
//    private ParameterValue value;
//    private ValuesAllowedType valuesAllowedType; // single/multiple/between - not used at the moment
//    private java.util.List<ValueConstraint> valueConstraints; 

        uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter destObject = new Parameter();
        destObject.setUUID(UUID.fromString(srcObj.getUUID()));
        destObject.setName(srcObj.getName());
        destObject.setDescription(srcObj.getDescription());
        destObject.setUnit(srcObj.getUnit());

        destObject.setType(ParameterValueType.fromValue(srcObj.getType().value()));
        ParameterValue val = new ParameterValue();
        if(srcObj.getValue()!=null)
            val.setValue(srcObj.getValue().getValue());
        
        if(srcObj.getValue()!=null && srcObj.getValue().getValueEvaluationType()!=null )
            val.setValueEvaluationType(EvaluationType.fromValue(srcObj.getValue().getValueEvaluationType().value()));
        destObject.setValue(val);
        destObject.setValuesAllowedType(ValuesAllowedType.fromValue(srcObj.getValuesAllowedType().value()));
        destObject.setValueConstraints(getValueConstraints(srcObj.getValueConstraints()));

        if (srcObj.getAllowedEvaluationTypes() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType> evaluationTypeList;
            evaluationTypeList = new java.util.ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType>();
            
            for (uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationType et : srcObj.getAllowedEvaluationTypes()) {
                evaluationTypeList.add(getEvaluationType(et));
            }
            
            destObject.setAllowedEvaluationTypes(evaluationTypeList);
        }

        return destObject;
    }

    private static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Parameter getParameter(uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter srcObj) {
        if (srcObj == null) {
            return null;
        }
//        private UUID uuid;
//    private String name;
//    private String description;
//    private String unit; // can be null
//    private ParameterValueType type;
//    
//    private ParameterValue value;
//    private ValuesAllowedType valuesAllowedType; // single/multiple/between - not used at the moment
//    private java.util.List<ValueConstraint> valueConstraints; 

        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Parameter destObject = new uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Parameter();
        destObject.setUUID(srcObj.getUUID().toString());
        destObject.setName(srcObj.getName());
        destObject.setDescription(srcObj.getDescription());
        destObject.setUnit(srcObj.getUnit());
        destObject.setType(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ParameterValueType.fromValue(srcObj.getType().value()));
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ParameterValue val = new uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ParameterValue();
        if(srcObj.getValue()!=null)
            val.setValue(srcObj.getValue().getValue());
        
        if(srcObj.getValue()!=null && srcObj.getValue().getValueEvaluationType()!=null)
            val.setValueEvaluationType(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationType.fromValue(srcObj.getValue().getValueEvaluationType().value()));
        destObject.setValue(val);
        destObject.setValuesAllowedType(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValuesAllowedType.fromValue(srcObj.getValuesAllowedType().value()));
        destObject.getValueConstraints().addAll(getValConstraints(srcObj.getValueConstraints()));

        if (srcObj.getAllowedEvaluationTypes() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationType> evaluationTypeList;
            evaluationTypeList = new java.util.ArrayList<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationType>();
            
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType et : srcObj.getAllowedEvaluationTypes()) {
                evaluationTypeList.add(getEvaluationType(et));
            }
            
            destObject.setAllowedEvaluationTypes(evaluationTypeList);
        }

        return destObject;
    }

    private static java.util.List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter> getParameters(java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Parameter> srcObj) {
        if (srcObj == null) {
            return null;
        }
        java.util.List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter> destObj = new java.util.ArrayList<Parameter>();
        for (int i = 0; i < srcObj.size(); i++) {
            destObj.add(getParameter(srcObj.get(i)));
        }

        return destObj;
    }

    private static java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Parameter> getParams(java.util.List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter> srcObj) {
        if (srcObj == null) {
            return null;
        }
        java.util.List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Parameter> destObj = new java.util.ArrayList<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Parameter>();
        for (int i = 0; i < srcObj.size(); i++) {
            destObj.add(getParameter(srcObj.get(i)));
        }

        return destObj;
    }

    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition getEventCondition(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EventCondition srcObj) {
        if (srcObj == null) {
            return null;
        }
        //         private UUID uuid;   
//    private ParameterValueType type; // assuming that the pre- and post-values are of the same type
//    private String name;
//    private String description;
//    private String unit; // can be null
//    private ParameterValue preConditionValue; // this can be null
//    private ParameterValue postConditionValue;
//    private ValuesAllowedType valuesAllowedType; // single/multiple/between - not used at the moment
//    private List<ValueConstraint> valueConstraints; // should define the bounds on the value, if any (can be NULL)


        uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition destObject = new EventCondition();
        destObject.setUUID(UUID.fromString(srcObj.getUUID()));
        destObject.setType(getParamValueType(srcObj.getType()));
        destObject.setName(srcObj.getName());
        destObject.setDescription(srcObj.getDescription());
        destObject.setUnit(srcObj.getUnit());
        destObject.setPreConditionValue(getParamValue(srcObj.getPreConditionValue()));
        destObject.setPostConditionValue(getParamValue(srcObj.getPostConditionValue()));
        destObject.setValuesAllowedType(getValueAllowedType(srcObj.getValuesAllowedType()));
        destObject.setValueConstraints(getValueConstraints(srcObj.getValueConstraints()));
        
        if (srcObj.getAllowedEvaluationTypes() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType> evaluationTypeList;
            evaluationTypeList = new java.util.ArrayList<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType>();
            
            for (uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationType et : srcObj.getAllowedEvaluationTypes()) {
                evaluationTypeList.add(getEvaluationType(et));
            }
            
            destObject.setAllowedEvaluationTypes(evaluationTypeList);
        }

        return destObject;
    }

    public static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EventCondition getEventCondition(uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition srcObj) {
        if (srcObj == null) {
            return null;
        }
        //         private UUID uuid;   
//    private ParameterValueType type; // assuming that the pre- and post-values are of the same type
//    private String name;
//    private String description;
//    private String unit; // can be null
//    private ParameterValue preConditionValue; // this can be null
//    private ParameterValue postConditionValue;
//    private ValuesAllowedType valuesAllowedType; // single/multiple/between - not used at the moment
//    private List<ValueConstraint> valueConstraints; // should define the bounds on the value, if any (can be NULL)


        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EventCondition destObject = new uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EventCondition();
        destObject.setUUID(srcObj.getUUID().toString());
        destObject.setType(getParamValueType(srcObj.getType()));
        destObject.setName(srcObj.getName());
        destObject.setDescription(srcObj.getDescription());
        destObject.setUnit(srcObj.getUnit());
        destObject.setPreConditionValue(getParamValue(srcObj.getPreConditionValue()));
        destObject.setPostConditionValue(getParamValue(srcObj.getPostConditionValue()));
        destObject.setValuesAllowedType(getValueAllowedType(srcObj.getValuesAllowedType()));
        destObject.getValueConstraints().addAll(getValConstraints(srcObj.getValueConstraints()));
        
        if (srcObj.getAllowedEvaluationTypes() != null)
        {
            List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationType> evaluationTypeList;
            evaluationTypeList = new java.util.ArrayList<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationType>();
            
            for (uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType et : srcObj.getAllowedEvaluationTypes()) {
                evaluationTypeList.add(getEvaluationType(et));
            }
            
            destObject.setAllowedEvaluationTypes(evaluationTypeList);
        }

        return destObject;
    }

    private static List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EventCondition> getEventConds(List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition> srcObj) {
        if (srcObj == null) {
            return null;
        }
        List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EventCondition> destObj = new java.util.ArrayList<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EventCondition>();
        for (int i = 0; i < srcObj.size(); i++) {
            destObj.add(getEventCondition(srcObj.get(i)));
        }

        return destObj;
    }

    private static List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition> getEventConditions(List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EventCondition> srcObj) {
        if (srcObj == null) {
            return null;
        }
        List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition> destObj = new java.util.ArrayList<EventCondition>();
        for (int i = 0; i < srcObj.size(); i++) {
            destObj.add(getEventCondition(srcObj.get(i)));
        }

        return destObj;
    }

    private static uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType getEvaluationType(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationType srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType destObject = EvaluationType.fromValue(srcObj.value());
        return destObject;
    }

    private static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationType getEvaluationType(uk.ac.soton.itinnovation.robust.cat.common.datastructures.EvaluationType srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationType destObject = uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationType.fromValue(srcObj.value());
        return destObject;
    }

    private static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType getParamValueType(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ParameterValueType srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType destObject = ParameterValueType.fromValue(srcObj.value());
        return destObject;
    }

    private static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ParameterValueType getParamValueType(uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ParameterValueType destObject = uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ParameterValueType.fromValue(srcObj.value());
        return destObject;
    }

    private static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue getParamValue(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ParameterValue srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue destObject = new ParameterValue(srcObj.getValue(), getEvaluationType(srcObj.getValueEvaluationType()));
        return destObject;
    }

    private static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ParameterValue getParamValue(uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValue srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ParameterValue destObject = new uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ParameterValue();
        destObject.setValue(srcObj.getValue());
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationType evaltype=srcObj.getValueEvaluationType()==null?null:uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EvaluationType.fromValue(srcObj.getValueEvaluationType().value());
        destObject.setValueEvaluationType(evaltype);

        return destObject;
    }

    private static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType getValueAllowedType(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValuesAllowedType srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType destObject = ValuesAllowedType.fromValue(srcObj.value());
        return destObject;
    }

    private static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValuesAllowedType getValueAllowedType(uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValuesAllowedType srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValuesAllowedType destObject = uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValuesAllowedType.fromValue(srcObj.value());
        return destObject;
    }

    private static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint getValueConstraint(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValueConstraint srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint destObject = new ValueConstraint(srcObj.getValue(), getValueConstraintType(srcObj.getConstraintType()));
        return destObject;
    }

    private static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValueConstraint getValueConstraint(uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValueConstraint destObject = new uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValueConstraint();
        destObject.setValue(srcObj.getValue());
        destObject.setConstraintType(getValueConstraintType(srcObj.getConstraintType()));

        return destObject;
    }

    private static List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint> getValueConstraints(List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValueConstraint> srcObjs) {
        if (srcObjs == null) {
            return null;
        }
        List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint> destObject = new java.util.ArrayList<ValueConstraint>();

        for (int i = 0; i < srcObjs.size(); i++) {
            destObject.add(getValueConstraint(srcObjs.get(i)));
        }

        return destObject;
    }

    private static List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValueConstraint> getValConstraints(List<uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraint> srcObjs) {
        if (srcObjs == null) {
            return null;
        }
        List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValueConstraint> destObject = new java.util.ArrayList<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValueConstraint>();

        for (ValueConstraint val : srcObjs) {
            destObject.add(getValueConstraint(val));
        }

        return destObject;
    }

    private static uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType getValueConstraintType(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValueConstraintType srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType destObject = ValueConstraintType.fromValue(srcObj.value());
        return destObject;
    }

    private static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValueConstraintType getValueConstraintType(uk.ac.soton.itinnovation.robust.cat.common.datastructures.ValueConstraintType srcObj) {
        if (srcObj == null) {
            return null;
        }
        uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValueConstraintType destObject = uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ValueConstraintType.fromValue(srcObj.value());
        return destObject;
    }
    
public static uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription getPredictor(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.PredictorServiceDescription srcObj){
       if (srcObj == null) {
            return null;
        }
       
//           private UUID uuid;
//    private String name;
//    private String version;
//    private String description;
//    private List<Parameter> configurationParams;
//    private List<Event> events;
//    private Parameter forecastPeriod; // OBS: should use unit of days!
//    
//    // the following is used by the WP1 framework only
//    private URI serviceURI;
//    private String serviceTargetNamespace;
//    private String serviceName;
//    private String servicePortName;
       uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription destObject=new uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription();
       destObject.setUuid(UUID.fromString(srcObj.getUuid()));
       destObject.setName(srcObj.getName());
       destObject.setVersion(srcObj.getVersion());        
       destObject.setDescription(srcObj.getDescription());
       
       destObject.setConfigurationParams(getParameters(srcObj.getConfigurationParams()));
       List<Event> listEvs= getEventLists(srcObj.getEvents());
       destObject.setEvents(listEvs);
       
       destObject.setForecastPeriod(getParameter(srcObj.getForecastPeriod()));
       try{
       destObject.setServiceURI(new URI(srcObj.getServiceURI()));
       }catch(Exception ex){
           throw new RuntimeException("error converting string to uri"+ex, ex);
       }
       
       destObject.setServiceTargetNamespace(srcObj.getServiceTargetNamespace());
       destObject.setServiceName(srcObj.getServiceName());
       destObject.setServicePortName(srcObj.getServicePortName());
       
       return destObject;
}


public static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.PredictorServiceDescription getPredictor(uk.ac.soton.itinnovation.robust.cat.common.ps.PredictorServiceDescription srcObj){
       if (srcObj == null) {
            return null;
        }
       
//           private UUID uuid;
//    private String name;
//    private String version;
//    private String description;
//    private List<Parameter> configurationParams;
//    private List<Event> events;
//    private Parameter forecastPeriod; // OBS: should use unit of days!
//    
//    // the following is used by the WP1 framework only
//    private URI serviceURI;
//    private String serviceTargetNamespace;
//    private String serviceName;
//    private String servicePortName;
       uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.PredictorServiceDescription destObject=new uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.PredictorServiceDescription();
       destObject.setUuid(srcObj.getUuid().toString());
       destObject.setName(srcObj.getName());
       destObject.setVersion(srcObj.getVersion());        
       destObject.setDescription(srcObj.getDescription());
        List<uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.Parameter> params = getParams(srcObj.getConfigurationParams());     
       if(params!=null)
               destObject.getConfigurationParams().addAll(params);
       destObject.getEvents().addAll(getEventList(srcObj.getEvents()));
       
       destObject.setForecastPeriod(getParameter(srcObj.getForecastPeriod()));
       try{
       destObject.setServiceURI(srcObj.getServiceURI().toString());
       }catch(Exception ex){
           throw new RuntimeException("error converting string to uri"+ex, ex);
       }
       
       destObject.setServiceTargetNamespace(srcObj.getServiceTargetNamespace());
       destObject.setServiceName(srcObj.getServiceName());
       destObject.setServicePortName(srcObj.getServicePortName());
       
       return destObject;
}

 public static EvaluationResult getEvaluationResult(uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult srcObj) {
//         private UUID resultUUID;
//    private UUID riskUUID;
//    private Date currentDate;
//    private Date forecastDate;
//    private JobDetails jobDetails;
//    private List<ResultItem> resultItems;
//    private String metaData;
//    private String currentObservation;
      if (srcObj == null) {
            return null;
      }
      
      EvaluationResult destObject=new EvaluationResult();
      
      if(srcObj.getResultUUID()!=null)
          destObject.setResultUUID(srcObj.getResultUUID().toString());
      if(srcObj.getRiskUUID()!=null)
          destObject.setRiskUUID(srcObj.getRiskUUID().toString());
      
  
      if(srcObj.getCurrentDate()!=null)
          destObject.setCurrentDate(dateToGregorian(srcObj.getCurrentDate()));
       
      if(srcObj.getForecastDate()!=null)
          destObject.setForecastDate(dateToGregorian(srcObj.getForecastDate()));
      
      destObject.setJobDetails(getJobDetails(srcObj.getJobDetails()));
      
      destObject.getResultItems().addAll(getResultItems(srcObj.getResultItems()));
      
      //destObject.setMetaData(srcObj.getMetaData()); // TODO: change meta-data in TypeConverter
      //destObject.setCurrentObservation(srcObj.getCurrentObservation()); // TODO: change current observation in TypeConverter
      
      return destObject;
               
    }

    private static JobDetails getJobDetails(uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails srcObj) {
//         private String jobRef;
//    private JobStatus jobStatus;
//    private Date jobRequestDate;
//    private Date evaluationStartDate;
//    private Date completionDate;
        JobDetails destObj=new JobDetails();
        destObj.setJobRef(srcObj.getJobRef());
        JobStatus st=new JobStatus();
        st.setMetaData(srcObj.getJobStatus().getMetaData());
        st.setStatus(JobStatusType.fromValue(srcObj.getJobStatus().getStatus().toString()));
        destObj.setJobStatus(st);
        
        if(srcObj.getRequestDate()!=null)
            destObj.setRequestDate(dateToGregorian(srcObj.getRequestDate()));
        
        if(srcObj.getStartDate()!=null)
            destObj.setStartDate(dateToGregorian(srcObj.getStartDate()));
        
        if(srcObj.getCompletionDate()!=null)
            destObj.setCompletionDate(dateToGregorian(srcObj.getCompletionDate()));
        
        return destObj;
    }
    
    private static uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails getJobDetails(JobDetails srcObj) {
//         private String jobRef;
//    private JobStatus jobStatus;
//    private Date jobRequestDate;
//    private Date evaluationStartDate;
//    private Date completionDate;
        uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails destObj=new uk.ac.soton.itinnovation.robust.cat.common.ps.JobDetails();
        destObj.setJobRef(srcObj.getJobRef());
        uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus st=new uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatus();
        st.setMetaData(srcObj.getJobStatus().getMetaData());
        st.setStatus(uk.ac.soton.itinnovation.robust.cat.common.ps.JobStatusType.fromValue(srcObj.getJobStatus().getStatus().toString()));
        destObj.setJobStatus(st);
        if(srcObj.getRequestDate()!=null)
            destObj.setRequestDate(srcObj.getRequestDate().toGregorianCalendar().getTime());
         if(srcObj.getStartDate()!=null)
             destObj.setStartDate(srcObj.getStartDate().toGregorianCalendar().getTime());
         if(srcObj.getCompletionDate()!=null)
        destObj.setCompletionDate(srcObj.getCompletionDate().toGregorianCalendar().getTime());
        
        return destObj;
    }
    
    public static XMLGregorianCalendar dateToGregorian(Date date){
        if(date==null)
            return null;
        
         GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        XMLGregorianCalendar outputDate=  null;
        try {
            outputDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            return outputDate;
        } catch (DatatypeConfigurationException ex) {
            throw new RuntimeException("error converting date to gregorian XML",ex);
        }
    }

    private static List<ResultItem> getResultItems(List<uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem> srcObj) {
        if(srcObj==null || srcObj.isEmpty())
            return null;
        
        List<ResultItem> destObj=new java.util.ArrayList<ResultItem>();
        for(int i=0;i<srcObj.size();i++){
            destObj.add(getResultItem(srcObj.get(i)));
        }
        return destObj;
    }

    private static List<uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem> getRltItems(List<ResultItem> srcObj) {
        if(srcObj==null || srcObj.isEmpty())
            return null;
        
        List<uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem> destObj=new java.util.ArrayList<uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem>();
        for(int i=0;i<srcObj.size();i++){
            destObj.add(getResultItem(srcObj.get(i)));
        }
        return destObj;
    }
       
    private static ResultItem getResultItem(uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem srcObj) {
//            private String name;
//    private Double probability;
        
        ResultItem destObj=new ResultItem();
        destObj.setName(srcObj.getName());
        destObj.setProbability(srcObj.getProbability());
        
        return destObj;
    }
    
    private static uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem  getResultItem( ResultItem srcObj) {
//            private String name;
//    private Double probability;
        
        uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem  destObj=new uk.ac.soton.itinnovation.robust.cat.common.ps.ResultItem ();
        destObj.setName(srcObj.getName());
        destObj.setProbability(srcObj.getProbability());
        
        return destObj;
    }

    public static uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult getEvalResult(EvaluationResult srcObj) {
        //         private UUID resultUUID;
//    private UUID riskUUID;
//    private Date currentDate;
//    private Date forecastDate;
//    private JobDetails jobDetails;
//    private List<ResultItem> resultItems;
//    private String metaData;
//    private String currentObservation;
      if (srcObj == null) {
            return null;
      }
      
      uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult destObject=new uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult();
      
      destObject.setResultUUID(UUID.fromString(srcObj.getResultUUID()));
      destObject.setRiskUUID(UUID.fromString(srcObj.getRiskUUID()));
      
       if(srcObj.getCurrentDate()!=null)
           destObject.setCurrentDate(srcObj.getCurrentDate().toGregorianCalendar().getTime());
        
        if(srcObj.getForecastDate()!=null)
            destObject.setForecastDate(srcObj.getForecastDate().toGregorianCalendar().getTime());
      
      destObject.setJobDetails(getJobDetails(srcObj.getJobDetails()));
      
      if(srcObj.getResultItems()!=null)
          destObject.setResultItems((getRltItems(srcObj.getResultItems())));
      
      //destObject.setMetaData(srcObj.getMetaData()); // TODO: change meta-data in TypeConverter
      //destObject.setCurrentObservation(srcObj.getCurrentObservation()); // TODO: change current observation in TypeConverter
      
      return destObject;
    }

    public static Set<uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult> getEvalResults(List<EvaluationResult> srcObj) {
        Set<uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult> destObj=new HashSet<uk.ac.soton.itinnovation.robust.cat.common.ps.EvaluationResult>();
          if ((srcObj == null)||(srcObj.isEmpty())) {
            return null;
            }
          
          for(int i=0;i<srcObj.size();i++){
              destObj.add(getEvalResult(srcObj.get(i)));
          }
              return destObj;
    }

    private static uk.ac.soton.itinnovation.robust.riskmodel.TreatmentWFs getTreatment(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.TreatmentWFs srcObj) {
         if ((srcObj == null)) {
            return null;
            }
         uk.ac.soton.itinnovation.robust.riskmodel.TreatmentWFs destObj=new uk.ac.soton.itinnovation.robust.riskmodel.TreatmentWFs();
         TreatmentsTemplatesByID tmplates=srcObj.getTreatmentsTemplatesByID();
        Iterator<TreatmentsTemplatesByID.Entry> it = tmplates.getEntry().iterator();
        
        while(it.hasNext()){
           uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.TreatmentTemplate tmpl=new uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.TreatmentTemplate();
            //  private String treatmentTitle;
            //  private String treatmentDescription;
            //  private UUID   activitiProcResourceID;
            TreatmentsTemplatesByID.Entry tmplateEntry = it.next();
           tmpl.setTreatmentTitle(tmplateEntry.getValue().getTreatmentTitle());
           tmpl.setTreatmentDescription(tmplateEntry.getValue().getTreatmentDescription());
           tmpl.setActivitiProcResourceID(UUID.fromString(tmplateEntry.getValue().getActivitiProcResourceID()));
           
           Float order=getKeyByValue(srcObj.getTemplatesByOrder().getEntry(),tmplateEntry.getValue().getActivitiProcResourceID());
                              
           destObj.addTreatmentTemplate(tmpl, order);
        }
       
         
        return destObj;
    }
    
        private static uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.TreatmentWFs getTreatment(uk.ac.soton.itinnovation.robust.riskmodel.TreatmentWFs srcObj) {
         if ((srcObj == null)) {
            return null;
            }
         uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.TreatmentWFs destObj=new uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.TreatmentWFs();
         
         TemplatesByOrder tmpByOrder=new TemplatesByOrder();
         TreatmentsTemplatesByID tmpById=new TreatmentsTemplatesByID();
         
        java.util.TreeMap<Float, UUID> srcTmByOrder = srcObj.getTemplatesByOrder();
        java.util.HashMap<UUID, uk.ac.soton.itinnovation.robust.cat.core.models.treatmentModel.TreatmentTemplate> srcTmpById = srcObj.getTreatmentsTemplatesByID();
         //loop
        
        for(Float order:srcTmByOrder.keySet()){
            
            TemplatesByOrder.Entry e=new TemplatesByOrder.Entry();
            e.setKey(order);
            e.setValue(srcTmByOrder.get(order).toString());
            tmpByOrder.getEntry().add(e);
         
        
        TreatmentsTemplatesByID.Entry et=new TreatmentsTemplatesByID.Entry();
        et.setKey(srcTmByOrder.get(order).toString());
        TreatmentTemplate tmtTemplate=new TreatmentTemplate();
        tmtTemplate.setTreatmentTitle(srcTmpById.get(srcTmByOrder.get(order)).getTitle());
        tmtTemplate.setTreatmentDescription(srcTmpById.get(srcTmByOrder.get(order)).getDescription());
        tmtTemplate.setActivitiProcResourceID(srcTmpById.get(srcTmByOrder.get(order)).getActivitiProcResourceID().toString());
        et.setValue(tmtTemplate);
        tmpById.getEntry().add(et);
         
        }
         //end loop
         destObj.setTreatmentsTemplatesByID(tmpById);
         destObj.setTemplatesByOrder(tmpByOrder);
               
         
        return destObj;
    }

    private static Float getKeyByValue(List<TemplatesByOrder.Entry> entryList, String activitiProcResourceID) {
        Iterator it=entryList.iterator();
        while(it.hasNext()){
            TemplatesByOrder.Entry ent=(TemplatesByOrder.Entry) it.next();
            if(ent.getValue().equalsIgnoreCase(activitiProcResourceID))
                return ent.getKey();
        }
        
        return null;
    }

   
            



}
