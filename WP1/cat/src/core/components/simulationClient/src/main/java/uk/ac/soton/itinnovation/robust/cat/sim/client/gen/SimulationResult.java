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
//      Created Date :          2013-10-13
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.sim.client.gen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for simulationResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="simulationResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="jobDetails" type="{http://sim.common.cat.robust.itinnovation.soton.ac.uk/}jobDetails" minOccurs="0"/>
 *         &lt;element name="resultGroups" type="{http://sim.common.cat.robust.itinnovation.soton.ac.uk/}resultGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="simulationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "simulationResult", propOrder = {
    "jobDetails",
    "resultGroups",
    "simulationDate"
})
public class SimulationResult {

    protected JobDetails jobDetails;
    @XmlElement(nillable = true)
    protected List<ResultGroup> resultGroups;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar simulationDate;

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
     * Gets the value of the resultGroups property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resultGroups property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResultGroups().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResultGroup }
     * 
     * 
     */
    public List<ResultGroup> getResultGroups() {
        if (resultGroups == null) {
            resultGroups = new ArrayList<ResultGroup>();
        }
        return this.resultGroups;
    }
	
	/**
     * Sets the value of the resultGroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultGroup }
     *     
     */
    public void setResultGroups(List<ResultGroup> value) {
        this.resultGroups = value;
    }

    /**
     * Gets the value of the simulationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSimulationDate() {
        return simulationDate;
    }

    /**
     * Sets the value of the simulationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSimulationDate(XMLGregorianCalendar value) {
        this.simulationDate = value;
    }

}
