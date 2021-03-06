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
 * <p>Java class for evaluationResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="evaluationResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="currentDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="forecastDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="jobDetails" type="{http://dataservice.ws.robust.swmind.pl/}jobDetails" minOccurs="0"/>
 *         &lt;element name="metaData" type="{http://dataservice.ws.robust.swmind.pl/}keyValuePair" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="resultItems" type="{http://dataservice.ws.robust.swmind.pl/}resultItem" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="resultUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="riskUUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "evaluationResult", propOrder = {
    "currentDate",
    "forecastDate",
    "jobDetails",
    "metaData",
    "resultItems",
    "resultUUID",
    "riskUUID"
})
public class EvaluationResult {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar currentDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar forecastDate;
    protected JobDetails jobDetails;
    @XmlElement(nillable = true)
    protected List<KeyValuePair> metaData;
    @XmlElement(nillable = true)
    protected List<ResultItem> resultItems;
    protected String resultUUID;
    protected String riskUUID;

    /**
     * Gets the value of the currentDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCurrentDate() {
        return currentDate;
    }

    /**
     * Sets the value of the currentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCurrentDate(XMLGregorianCalendar value) {
        this.currentDate = value;
    }

    /**
     * Gets the value of the forecastDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getForecastDate() {
        return forecastDate;
    }

    /**
     * Sets the value of the forecastDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setForecastDate(XMLGregorianCalendar value) {
        this.forecastDate = value;
    }

    /**
     * Gets the value of the jobDetails property.
     * 
     * @return
     *     possible object is
     *     {@link JobDetails }
     *     
     */
    public JobDetails getJobDetails() {
        return jobDetails;
    }

    /**
     * Sets the value of the jobDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link JobDetails }
     *     
     */
    public void setJobDetails(JobDetails value) {
        this.jobDetails = value;
    }

    /**
     * Gets the value of the metaData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the metaData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMetaData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KeyValuePair }
     * 
     * 
     */
    public List<KeyValuePair> getMetaData() {
        if (metaData == null) {
            metaData = new ArrayList<KeyValuePair>();
        }
        return this.metaData;
    }

    /**
     * Gets the value of the resultItems property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resultItems property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResultItems().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResultItem }
     * 
     * 
     */
    public List<ResultItem> getResultItems() {
        if (resultItems == null) {
            resultItems = new ArrayList<ResultItem>();
        }
        return this.resultItems;
    }

    /**
     * Gets the value of the resultUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultUUID() {
        return resultUUID;
    }

    /**
     * Sets the value of the resultUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultUUID(String value) {
        this.resultUUID = value;
    }

    /**
     * Gets the value of the riskUUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRiskUUID() {
        return riskUUID;
    }

    /**
     * Sets the value of the riskUUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiskUUID(String value) {
        this.riskUUID = value;
    }

}
