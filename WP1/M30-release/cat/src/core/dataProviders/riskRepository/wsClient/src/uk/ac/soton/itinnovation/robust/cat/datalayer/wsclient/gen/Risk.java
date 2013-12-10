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
//      Created Date :          26-Sep-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for risk complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="risk">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="admin_review_freq" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="admin_review_period" type="{http://dataservice.ws.robust.swmind.pl/}period" minOccurs="0"/>
 *         &lt;element name="cat_review_freq" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cat_review_period" type="{http://dataservice.ws.robust.swmind.pl/}period" minOccurs="0"/>
 *         &lt;element name="community" type="{http://dataservice.ws.robust.swmind.pl/}community" minOccurs="0"/>
 *         &lt;element name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="group" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="impact" type="{http://dataservice.ws.robust.swmind.pl/}impact" minOccurs="0"/>
 *         &lt;element name="notification" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="owner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="scope" type="{http://dataservice.ws.robust.swmind.pl/}scope" minOccurs="0"/>
 *         &lt;element name="setEvent" type="{http://dataservice.ws.robust.swmind.pl/}event" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="state" type="{http://dataservice.ws.robust.swmind.pl/}riskState" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="treatProcIDS" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="treatment" type="{http://dataservice.ws.robust.swmind.pl/}treatmentWFs" minOccurs="0"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "risk", propOrder = {
    "adminReviewFreq",
    "adminReviewPeriod",
    "catReviewFreq",
    "catReviewPeriod",
    "community",
    "expiryDate",
    "group",
    "id",
    "impact",
    "notification",
    "owner",
    "scope",
    "setEvent",
    "state",
    "title",
    "treatProcIDS",
    "treatment",
    "type"
})
public class Risk {

    @XmlElement(name = "admin_review_freq")
    protected int adminReviewFreq;
    @XmlElement(name = "admin_review_period")
    protected Period adminReviewPeriod;
    @XmlElement(name = "cat_review_freq")
    protected int catReviewFreq;
    @XmlElement(name = "cat_review_period")
    protected Period catReviewPeriod;
    protected Community community;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expiryDate;
    protected String group;
    protected String id;
    protected Impact impact;
    protected boolean notification;
    protected String owner;
    protected Scope scope;
    @XmlElement(nillable = true)
    protected List<Event> setEvent;
    protected RiskState state;
    protected String title;
    @XmlElement(nillable = true)
    protected List<String> treatProcIDS;
    protected TreatmentWFs treatment;
    protected Boolean type;

    /**
     * Gets the value of the adminReviewFreq property.
     * 
     */
    public int getAdminReviewFreq() {
        return adminReviewFreq;
    }

    /**
     * Sets the value of the adminReviewFreq property.
     * 
     */
    public void setAdminReviewFreq(int value) {
        this.adminReviewFreq = value;
    }

    /**
     * Gets the value of the adminReviewPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link Period }
     *     
     */
    public Period getAdminReviewPeriod() {
        return adminReviewPeriod;
    }

    /**
     * Sets the value of the adminReviewPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Period }
     *     
     */
    public void setAdminReviewPeriod(Period value) {
        this.adminReviewPeriod = value;
    }

    /**
     * Gets the value of the catReviewFreq property.
     * 
     */
    public int getCatReviewFreq() {
        return catReviewFreq;
    }

    /**
     * Sets the value of the catReviewFreq property.
     * 
     */
    public void setCatReviewFreq(int value) {
        this.catReviewFreq = value;
    }

    /**
     * Gets the value of the catReviewPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link Period }
     *     
     */
    public Period getCatReviewPeriod() {
        return catReviewPeriod;
    }

    /**
     * Sets the value of the catReviewPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Period }
     *     
     */
    public void setCatReviewPeriod(Period value) {
        this.catReviewPeriod = value;
    }

    /**
     * Gets the value of the community property.
     * 
     * @return
     *     possible object is
     *     {@link Community }
     *     
     */
    public Community getCommunity() {
        return community;
    }

    /**
     * Sets the value of the community property.
     * 
     * @param value
     *     allowed object is
     *     {@link Community }
     *     
     */
    public void setCommunity(Community value) {
        this.community = value;
    }

    /**
     * Gets the value of the expiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the value of the expiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpiryDate(XMLGregorianCalendar value) {
        this.expiryDate = value;
    }

    /**
     * Gets the value of the group property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the value of the group property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroup(String value) {
        this.group = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the impact property.
     * 
     * @return
     *     possible object is
     *     {@link Impact }
     *     
     */
    public Impact getImpact() {
        return impact;
    }

    /**
     * Sets the value of the impact property.
     * 
     * @param value
     *     allowed object is
     *     {@link Impact }
     *     
     */
    public void setImpact(Impact value) {
        this.impact = value;
    }

    /**
     * Gets the value of the notification property.
     * 
     */
    public boolean isNotification() {
        return notification;
    }

    /**
     * Sets the value of the notification property.
     * 
     */
    public void setNotification(boolean value) {
        this.notification = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwner(String value) {
        this.owner = value;
    }

    /**
     * Gets the value of the scope property.
     * 
     * @return
     *     possible object is
     *     {@link Scope }
     *     
     */
    public Scope getScope() {
        return scope;
    }

    /**
     * Sets the value of the scope property.
     * 
     * @param value
     *     allowed object is
     *     {@link Scope }
     *     
     */
    public void setScope(Scope value) {
        this.scope = value;
    }

    /**
     * Gets the value of the setEvent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setEvent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSetEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Event }
     * 
     * 
     */
    public List<Event> getSetEvent() {
        if (setEvent == null) {
            setEvent = new ArrayList<Event>();
        }
        return this.setEvent;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link RiskState }
     *     
     */
    public RiskState getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link RiskState }
     *     
     */
    public void setState(RiskState value) {
        this.state = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the treatProcIDS property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the treatProcIDS property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTreatProcIDS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTreatProcIDS() {
        if (treatProcIDS == null) {
            treatProcIDS = new ArrayList<String>();
        }
        return this.treatProcIDS;
    }

    /**
     * Gets the value of the treatment property.
     * 
     * @return
     *     possible object is
     *     {@link TreatmentWFs }
     *     
     */
    public TreatmentWFs getTreatment() {
        return treatment;
    }

    /**
     * Sets the value of the treatment property.
     * 
     * @param value
     *     allowed object is
     *     {@link TreatmentWFs }
     *     
     */
    public void setTreatment(TreatmentWFs value) {
        this.treatment = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setType(Boolean value) {
        this.type = value;
    }

}
