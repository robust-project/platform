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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for predictorServiceJobConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="predictorServiceJobConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="communityDetails" type="{http://ps.common.cat.robust.itinnovation.soton.ac.uk/}communityDetails" minOccurs="0"/>
 *         &lt;element name="configurationParams" type="{http://ps.common.cat.robust.itinnovation.soton.ac.uk/}parameter" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="evaluationEngineServiceURI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="events" type="{http://ps.common.cat.robust.itinnovation.soton.ac.uk/}event" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="forecastPeriod" type="{http://ps.common.cat.robust.itinnovation.soton.ac.uk/}parameter" minOccurs="0"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="streamDetails" type="{http://ps.common.cat.robust.itinnovation.soton.ac.uk/}streamDetails" minOccurs="0"/>
 *         &lt;element name="streaming" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "predictorServiceJobConfig", propOrder = {
    "communityDetails",
    "configurationParams",
    "evaluationEngineServiceURI",
    "events",
    "forecastPeriod",
    "startDate",
    "streamDetails",
    "streaming"
})
public class PredictorServiceJobConfig {

    protected CommunityDetails communityDetails;
    @XmlElement(nillable = true)
    protected List<Parameter> configurationParams;
    protected String evaluationEngineServiceURI;
    @XmlElement(nillable = true)
    protected List<Event> events;
    protected Parameter forecastPeriod;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startDate;
    protected StreamDetails streamDetails;
    protected boolean streaming;

    /**
     * Gets the value of the communityDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CommunityDetails }
     *     
     */
    public CommunityDetails getCommunityDetails() {
        return communityDetails;
    }

    /**
     * Sets the value of the communityDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommunityDetails }
     *     
     */
    public void setCommunityDetails(CommunityDetails value) {
        this.communityDetails = value;
    }

    /**
     * Gets the value of the configurationParams property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the configurationParams property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConfigurationParams().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Parameter }
     * 
     * 
     */
    public List<Parameter> getConfigurationParams() {
        if (configurationParams == null) {
            configurationParams = new ArrayList<Parameter>();
        }
        return this.configurationParams;
    }
    
    /**
     * Sets the value of the configurationParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link Parameter }
     *     
     */
    public void setConfigParams(List<Parameter> value) {
        this.configurationParams = value;
    }
    
    /**
     * Gets the value of the evaluationEngineServiceURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEvaluationEngineServiceURI() {
        return evaluationEngineServiceURI;
    }

    /**
     * Sets the value of the evaluationEngineServiceURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEvaluationEngineServiceURI(String value) {
        this.evaluationEngineServiceURI = value;
    }

    /**
     * Gets the value of the events property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the events property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEvents().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Event }
     * 
     * 
     */
    public List<Event> getEvents() {
        if (events == null) {
            events = new ArrayList<Event>();
        }
        return this.events;
    }
    
    /**
     * Sets the value of the events property.
     * 
     * @param value
     *     allowed object is
     *     {@link Event }
     *     
     */
    public void setEvents(List<Event> value) {
        this.events = value;
    }

    /**
     * Gets the value of the forecastPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link Parameter }
     *     
     */
    public Parameter getForecastPeriod() {
        return forecastPeriod;
    }

    /**
     * Sets the value of the forecastPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Parameter }
     *     
     */
    public void setForecastPeriod(Parameter value) {
        this.forecastPeriod = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the streamDetails property.
     * 
     * @return
     *     possible object is
     *     {@link StreamDetails }
     *     
     */
    public StreamDetails getStreamDetails() {
        return streamDetails;
    }

    /**
     * Sets the value of the streamDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link StreamDetails }
     *     
     */
    public void setStreamDetails(StreamDetails value) {
        this.streamDetails = value;
    }

    /**
     * Gets the value of the streaming property.
     * 
     */
    public boolean isStreaming() {
        return streaming;
    }

    /**
     * Sets the value of the streaming property.
     * 
     */
    public void setStreaming(boolean value) {
        this.streaming = value;
    }

}
