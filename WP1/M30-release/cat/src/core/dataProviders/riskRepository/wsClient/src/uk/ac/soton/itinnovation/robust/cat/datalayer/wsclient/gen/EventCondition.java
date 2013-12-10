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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for eventCondition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventCondition">
 *   &lt;complexContent>
 *     &lt;extension base="{http://dataservice.ws.robust.swmind.pl/}aParameter">
 *       &lt;sequence>
 *         &lt;element name="postConditionValue" type="{http://dataservice.ws.robust.swmind.pl/}parameterValue" minOccurs="0"/>
 *         &lt;element name="preConditionValue" type="{http://dataservice.ws.robust.swmind.pl/}parameterValue" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventCondition", propOrder = {
    "postConditionValue",
    "preConditionValue"
})
public class EventCondition
    extends AParameter
{

    protected ParameterValue postConditionValue;
    protected ParameterValue preConditionValue;

    /**
     * Gets the value of the postConditionValue property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValue }
     *     
     */
    public ParameterValue getPostConditionValue() {
        return postConditionValue;
    }

    /**
     * Sets the value of the postConditionValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValue }
     *     
     */
    public void setPostConditionValue(ParameterValue value) {
        this.postConditionValue = value;
    }

    /**
     * Gets the value of the preConditionValue property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValue }
     *     
     */
    public ParameterValue getPreConditionValue() {
        return preConditionValue;
    }

    /**
     * Sets the value of the preConditionValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValue }
     *     
     */
    public void setPreConditionValue(ParameterValue value) {
        this.preConditionValue = value;
    }

}
