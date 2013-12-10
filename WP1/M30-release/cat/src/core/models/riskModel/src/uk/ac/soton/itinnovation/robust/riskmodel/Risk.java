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
//      Created Date :          03-Nov-2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.riskmodel;

import java.util.*;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Event;

public class Risk {
    private UUID id;
    private String title;
    private String owner;
    private Boolean type;
    private Date expiryDate;
    private Impact impact;
    private Set<Event> setEvent;
    private TreatmentWFs treatment;
    private int admin_review_freq=1;
    private int cat_review_freq=1;
    private String group;
    
    private ArrayList<String> treatProcIDS;
    
    private Community community;
    
    private boolean notification=false;
    private RiskState state=RiskState.INACTIVE;
    private Scope scope=Scope.COMMUNITY;
    private Period cat_review_period=Period.WEEK;
    private Period admin_review_period=Period.WEEK;

    public Risk() {
      id = UUID.randomUUID();
      impact = new Impact();
      treatment = new TreatmentWFs();
    }

    public Risk(Community community, String title, String owner, Boolean type, String group){
        id = UUID.randomUUID();
        impact = new Impact();
        treatment = new TreatmentWFs();
        
        this.title = title;
        this.owner = owner;
        this.type = type;//true for risk, false for opportunity
        this.group=group;
        this.community=community;
    }

    public ArrayList<String> getTreatProcIDS() {
        return treatProcIDS;
    }

    public void setTreatProcIDS(ArrayList<String> treatProcIDS) {
        this.treatProcIDS = treatProcIDS;
    }
    
    public void addTreatProcID(String procId){
        if(this.treatProcIDS==null)
            this.treatProcIDS=new ArrayList<String>();
        
        this.treatProcIDS.add(procId);
  
    }

    public String getCommunityName() {
        return community.getName();
    }

    
    //allow impact, treatment, expirydate... to be filled in later
    public Risk(UUID id, String title, String owner, Boolean type, Date expiryDate, Impact impact, Set<Event> events, TreatmentWFs treatment, Scope scope, 
            int cat_review_freq, Period cat_review_period, int admin_review_freq, Period admin_review_period, boolean notification, RiskState state, String group) {
              
        if(id==null)
            throw new RuntimeException("Risk id is null!");
        if(title==null)
            throw new RuntimeException("Risk title is null!");
        if(owner==null)
            throw new RuntimeException("Risk owner is null!");
        if(type==null)
            throw new RuntimeException("Risk type is null!");
      
              
        
        this.id = id;
        this.title = title;
        this.owner = owner;
        this.type = type;//true for risk, false for opportunity
        this.expiryDate = expiryDate;
        this.impact = impact;
        this.setEvent = events;
        this.treatment = treatment;
        this.scope = scope;
        this.cat_review_freq = cat_review_freq;
        this.cat_review_period = cat_review_period;
        this.admin_review_freq = admin_review_freq;
        this.admin_review_period = admin_review_period;
        this.notification = notification;
        this.state = state;
        this.group=group;
    }

  //allow impact, treatment, expirydate... to be filled in later
    public Risk(Impact impact,
            Set<Event> events,
            TreatmentWFs treatment,
            Scope scope,
            int cat_review_freq,
            Period cat_review_period,
            int admin_review_freq,
            Period admin_review_period,
            boolean notification,
            RiskState state,
            String group) {
        
        if(title==null)
            throw new RuntimeException("Risk title is null!");
        if(owner==null)
            throw new RuntimeException("Risk owner is null!");
        if(type==null)
            throw new RuntimeException("Risk type is null!");
        
        
        
        id = UUID.randomUUID();
        
        this.impact = impact;
        this.setEvent = events;
        this.treatment = treatment;
        this.scope = scope;
        this.cat_review_freq = cat_review_freq;
        this.cat_review_period = cat_review_period;
        this.admin_review_freq = admin_review_freq;
        this.admin_review_period = admin_review_period;
        this.notification = notification;
        this.state = state;
        this.group=group;
    }
    
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
    
    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }
 
    public UUID getId() {
        return id;
    }
    
    public void setId( UUID id ) {
      this.id = id;
    }
           
    public void setId( String id ) {
      this.id = UUID.fromString( id );
    }

    public RiskState getState() {
        return state;
    }

    public void setState(RiskState state) {
        this.state = state;
    }

    
    public int getAdmin_review_freq() {
        return admin_review_freq;
    }

    public void setAdmin_review_freq(int admin_review_freq) {
        this.admin_review_freq = admin_review_freq;
    }

    public Period getAdmin_review_period() {
        return admin_review_period;
    }

    public void setAdmin_review_period(Period admin_review_period) {
        this.admin_review_period = admin_review_period;
    }

    public int getCat_review_freq() {
        return cat_review_freq;
    }

    public void setCat_review_freq(int cat_review_freq) {
        this.cat_review_freq = cat_review_freq;
    }

    public Period getCat_review_period() {
        return cat_review_period;
    }

    public void setCat_review_period(Period cat_review_period) {
        this.cat_review_period = cat_review_period;
    }

    public Set<Event> getSetEvent() {
        return setEvent;
    }

    public void setSetEvent(Set<Event> setEvent) {
        this.setEvent = setEvent;
    }
    
    public void addEvent(Event ev) {
       if(ev!=null){
        if(this.setEvent==null){
            setEvent=new HashSet<Event>();
        }
        setEvent.add(ev);
       }
    }


    public Impact getImpact() {
        return impact;
    }

    public void setImpact(Impact impact) {
        this.impact = impact;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public TreatmentWFs getTreatment() {
        return treatment;
    }

    public void setTreatment(TreatmentWFs treatment) {
        this.treatment = treatment;
    }

   

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Risk other = (Risk) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if ((this.owner == null) ? (other.owner != null) : !this.owner.equals(other.owner)) {
            return false;
        }
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        if ((this.impact != other.impact) && (this.impact == null || !this.impact.equals(other.impact))) {
            return false;
        }

         if ((this.community == null) ? (other.community != null) : !this.community.equals(other.community)) {
            return false;
        }
         
        if (this.treatProcIDS != other.treatProcIDS && (this.treatProcIDS == null || !this.treatProcIDS.equals(other.treatProcIDS))) {
            return false;
        }
            
        if (this.treatment != other.treatment && (this.treatment == null || !this.treatment.equals(other.treatment))) {
            return false;
        }else{
            System.out.println("treatments are equal "+(this.treatment==null?"null":this.treatment.getTreatmentsTemplatesByID())+ (other.treatment==null?"null":other.treatment.getTreatmentsTemplatesByID() ));
        }
        
        if ((this.group == null) ? (other.group != null) : !this.group.equals(other.group)) {
            return false;
        }

        if ((this.scope == null) ? (other.scope != null) : !this.scope.equals(other.scope)) {
            return false;
        }

        if (this.setEvent != other.setEvent ) {
            boolean found;           
            
            if((this.setEvent==null)&&(other.setEvent==null))
                    return true;
            
            if(this.setEvent.isEmpty()&&((other.setEvent==null)))
                    return true;
            
            if((this.setEvent==null)&&other.setEvent.isEmpty())
                    return true;
            
            if(this.setEvent.isEmpty()&&((other.setEvent.isEmpty())))
                    return true;
            
            if (((this.setEvent == null)||this.setEvent.isEmpty())&&((other.setEvent!=null)))
            return false;
//            if(this.setEvent.size()!=other.setEvent.size())
//                return false;
            
            for(Event ev:this.setEvent){
                found=false;
                for(Event evother:other.setEvent){
                    if(ev.equals(evother))
                        found=true;
                }
                
                if(found==false){
                    return false;
                }
            }
        }
                
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 67 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 67 * hash + (this.owner != null ? this.owner.hashCode() : 0);
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 67 * hash + (this.impact != null ? this.impact.hashCode() : 0);
        hash = 67 * hash + (this.setEvent != null ? this.setEvent.hashCode() : 0);
        hash = 67 * hash + (this.treatment != null ? this.treatment.hashCode() : 0);
        hash = 67 * hash + (this.group != null ? this.group.hashCode() : 0);
        hash = 67 * hash + (this.treatProcIDS != null ? this.treatProcIDS.hashCode() : 0);
        hash = 67 * hash + (this.community != null ? this.community.hashCode() : 0);
        return hash;
    }
    
    public boolean containEvent(Event ev){
        
        if(setEvent==null)
            return false;
        
        for(Event event:setEvent){
            if(ev.equals(event)){
                return true;
            }
        }
        
        return false;
        
    }
   

   
    
 

}
