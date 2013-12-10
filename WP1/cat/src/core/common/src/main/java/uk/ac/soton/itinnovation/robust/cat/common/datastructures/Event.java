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
//      Created Date :          2012-01-11
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.log4j.Logger;

/**
 * PS: at the moment, there's no logic here to set if the event applies to, e.g.,
 * a single user, all users in a forum, etc.
 * @author Vegard Engen
 */
public class Event implements Serializable
{
    private UUID uuid;
    private String title;
    private String description;
    private List<Parameter> configParams; // since each event might have different config options to the general service config options
    private List<EventCondition> eventConditions; // an event can have multiple event conditions

    static Logger log = Logger.getLogger(Event.class);
//    private UUID instanceUUID;//this should go to the conditioninstance table in the database

    /**
     * Default constructor, which initialises the parameter lists (events and config).
     */
    public Event()
    {
        this.eventConditions = new ArrayList<EventCondition>();
        this.configParams = new ArrayList<Parameter>();
        this.uuid=UUID.randomUUID();
//        this.instanceUUID=UUID.randomUUID();//bmn
    }

//    public UUID getInstanceUUID() {//bmn
//        return instanceUUID;
//    }
//
//    public void setInstanceUUID(UUID instanceUUID) {//bmn
//        this.instanceUUID = instanceUUID;
//    }
    
    
    /**
     * Copy constructor, which makes a deep copy.
     * @param evt The object from which a copy is made.
     */
    public Event(Event evt)
    {
        this.eventConditions = new ArrayList<EventCondition>();
        this.configParams = new ArrayList<Parameter>();
        
        if (evt.getUuid() != null)
            this.uuid = UUID.fromString(evt.getUuid().toString());
        
//         if (evt.getInstanceUUID() != null) //bmn
//            this.instanceUUID = UUID.fromString(evt.getInstanceUUID().toString());
        
        this.title = evt.getTitle();
        this.description = evt.getDescription();
        
        if ((evt.getConfigParams() != null) || !evt.getConfigParams().isEmpty())
        {
            for (Parameter param : evt.getConfigParams())
            {
                this.configParams.add(new Parameter(param));
            }
        }
        
        if ((evt.getEventConditions() != null) || !evt.getEventConditions().isEmpty())
        {
            for (EventCondition evtCond : evt.getEventConditions())
            {
                this.eventConditions.add(new EventCondition(evtCond));
            }
        }
    }
    
    /**
     * Constructor to create an Event object with a specific UUID.
     * @param uuid 
     */
    public Event(UUID uuid)
    {
        this.eventConditions = new ArrayList<EventCondition>();
        this.configParams = new ArrayList<Parameter>();
        this.uuid=uuid;
    }
     
    /**
     * Constructor to set the title and description only.
     * @param title
     * @param description 
     */
    public Event(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }
    
    /**
     * Constructor to set the title and description only.
     * @param title
     * @param description 
     */
    public Event(UUID uuid, String title, String description) {
        this();
        this.uuid = uuid;
        this.title = title;
        this.description = description;
    }
    
    /**
     * Constructor to set the title, description and an event parameter.
     * @param title
     * @param description
     * @param evtCond 
     */

    public Event(String title, String description, EventCondition evtCond) {
        this(title, description);
        if (evtCond != null)
            this.eventConditions.add(evtCond);
    }
    
    /**
     * Constructor to set the title, description and event parameters.
     * @param title
     * @param description
     * @param evtCond 
     */
    public Event(String title, String description, List<EventCondition> evtCond) {
        this(title, description);
        if (evtCond != null)
            this.eventConditions.addAll(evtCond);
    }
    
    /**
     * Constructor to set the title, description and event parameters and a configuration parameter.
     * @param title
     * @param description
     * @param evtCond
     * @param configParam 
     */
    public Event(String title, String description, List<EventCondition> evtCond, Parameter configParam) {
        this(title, description, evtCond);
        if (configParam != null)
            this.configParams.add(configParam);
    }
    
       /**
     * Constructor to set the title, description and event parameters and a configuration parameter.
     * @param title
     * @param description
     * @param evtParams
     * @param configParam 
     */
    public Event(UUID uuid, String title, String description, List<EventCondition> evtParams, List<Parameter> configParams) {
       this.uuid=uuid;
       this.title=title;
       this.description=description;
       this.eventConditions = new ArrayList<EventCondition>();
        this.configParams = new ArrayList<Parameter>();
   
        if (evtParams != null){
            this.eventConditions.addAll(evtParams);
        }
        
        if (configParams != null){
            this.configParams.addAll(configParams);
        }
    }
    
    /**
     * Constructor to set the title, description and event parameters and configuration parameters.
     * @param title
     * @param description
     * @param evtCond
     * @param configParams 
     */
    public Event(String title, String description, List<EventCondition> evtCond, List<Parameter> configParams) {
        this(title, description, evtCond);

        if (configParams != null) {
            this.configParams.addAll(configParams);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * @return the eventParams
     */
    public List<EventCondition> getEventConditions() {
        return eventConditions;
    }
    
    public Parameter getConfigParamByID(UUID id)
    {
        if (id == null) {
            return null;
        }
        
        Parameter target = null;
        for (Parameter p: configParams)
        {
            if (p.getUUID().equals(id)) {
                target = p;
                break;
            }
        }
        
        return target;
    }
    
    public void removeConfigParam(Parameter param)
    {
        if (param != null) {
            configParams.remove(param);
        }
    }
    
    public EventCondition getEventConditionByID(UUID id)
    {
        if (id == null) {
            return null;
        }
        EventCondition target = null;
        log.info("Get event by UUID: " + id.toString());

        for (EventCondition ec : eventConditions) {
            if (ec.getUUID().equals(id)) {
                target = ec;
                break;
            }
        }

        return target;
    }
    
    public void removeEventCondition(EventCondition ec)
    {
      if ( ec != null )
        eventConditions.remove(ec);
    }

    /**
     * @param eventCond the event conditions to set
     */
    public void setEventConditions(List<EventCondition> eventCond) {
        this.eventConditions = eventCond;
    }
    
    /**
     * Set a single event condition.
     * If event conditions exist, the list is cleared. 
     * @param eventCond The event condition to set.
     */
    public void setEventCondition(EventCondition eventCond) {
        if ((eventCond == null) || !this.eventConditions.isEmpty())
            this.eventConditions.clear();

        this.eventConditions.add(eventCond);
    }
    
    /**
     * @param eventCond the event condition to add
     */
    public void addEventCondition(EventCondition eventCond) {
        if (this.eventConditions == null)
            this.eventConditions = new ArrayList<EventCondition>();
        this.eventConditions.add(eventCond);
    }
    
    /**
     * @param eventCond the event conditions to add
     */
    public void addEventConditions(List<EventCondition> eventCond) {
        if (this.eventConditions == null)
            this.eventConditions = new ArrayList<EventCondition>();
        
        this.eventConditions.addAll(eventCond);
    }
    
    /**
     * @return the configParams
     */
    public List<Parameter> getConfigParams() {
        return configParams;
    }

    /**
     * @param configParams the configParams to set
     */
    public void setConfigParams(List<Parameter> configParams) {
        this.configParams = configParams;
    }
    
    /**
     * Set a single configuration parameter.
     * If configuration parameters exist, the list is cleared. 
     * @param param The configuration parameter to set.
     */
    public void setConfigParam(Parameter param) {
        if ((configParams == null) || !configParams.isEmpty())
            configParams.clear();
        
        this.configParams.add(param);
    }
    
    /**
     * @param param the config param to add
     */
    public void addConfigParam(Parameter param) {
        if (this.configParams == null)
            this.configParams = new ArrayList<Parameter>();
        
        this.configParams.add(param);
    }
    
    /**
     * @param params the config params to add
     */
    public void addConfigParams(List<Parameter> params) {
        if (this.configParams == null)
            this.configParams = new ArrayList<Parameter>();
        
        this.configParams.addAll(params);
    }
    
    /**
     * Get the number of event parameters.
     * @return 
     */
    public int getNumEventParams()
    {
        if ((eventConditions == null) || eventConditions.isEmpty())
            return 0;
        else
            return eventConditions.size();
    }
    
    /**
     * Get the number of configuration parameters.
     * @return 
     */
    public int getNumConfigParams()
    {
        if ((configParams == null) || configParams.isEmpty())
            return 0;
        else
            return configParams.size();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    
   public void setUuid(String struuid) {
       if(struuid==null)
           throw new RuntimeException("input is null");
        this.uuid = UUID.fromString(struuid);
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        hash = 59 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 59 * hash + (this.configParams != null ? this.configParams.hashCode() : 0);
        hash = 59 * hash + (this.eventConditions != null ? this.eventConditions.hashCode() : 0);
        return hash;
    }

      @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Event other = (Event) obj;
        if (this.uuid != other.uuid && (this.uuid == null || !this.uuid.equals(other.uuid))) {
            return false;
        }
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }

        if (this.eventConditions != other.eventConditions) {
            boolean found;
            if (((this.eventConditions == null) || this.eventConditions.isEmpty()) && (other.eventConditions != null)) {
                return false;
              }

//              if ((this.eventConditions == null) && (other.eventConditions == null)) {
////                return true; //bmn: why return true here before checking the rest of the parameters?
//              }
//
//              if (this.eventConditions.isEmpty() && ((other.eventConditions == null))) {
////                return true;//bmn: why return true here before checking the rest of the parameters?
//              }
//            if(this.setEvent.size()!=other.setEvent.size())
//                return false;

              if ((this.eventConditions != null) && (other.eventConditions != null)) {
                  for (EventCondition ev : this.eventConditions) {
                      found = false;
                      for (EventCondition evother : other.eventConditions) {
                          if (ev.equals(evother)) {
                              found = true;
                          }
                      }

                      if (found == false) {
                          return false;
                      }
                  }
              }
          }

        if ((this.configParams==null || this.configParams.isEmpty()) && (other.configParams==null || other.configParams.isEmpty()))
            return true;//being the last thing tested here, we can return true.

//          if (this.configParams != other.configParams && (this.configParams == null || !this.configParams.equals(other.configParams))) {
//              return false;
//          }
          
             if (this.configParams !=null && other.configParams!=null){
                 for(Parameter param:configParams){
                     boolean paramfound=false;
                     
                     for(Parameter param2:other.configParams){
                         if (param.equals(param2)) {
                              paramfound = true;
                          }
                     }
                     
                     if(!paramfound)
                         return false;
                 }
             }


        return true;
    }

}
