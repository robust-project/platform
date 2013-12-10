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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for aParameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="aParameter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="allowedEvaluationTypes" type="{http://sim.common.cat.robust.itinnovation.soton.ac.uk/}evaluationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="type" type="{http://sim.common.cat.robust.itinnovation.soton.ac.uk/}parameterValueType" minOccurs="0"/>
 *         &lt;element name="UUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="valueConstraints" type="{http://sim.common.cat.robust.itinnovation.soton.ac.uk/}valueConstraint" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="valuesAllowedType" type="{http://sim.common.cat.robust.itinnovation.soton.ac.uk/}valuesAllowedType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "aParameter", propOrder = {
    "allowedEvaluationTypes",
    "description",
    "name",
    "type",
    "uuid",
    "unit",
    "valueConstraints",
    "valuesAllowedType"
})
@XmlSeeAlso({
    Parameter.class
})
public abstract class AParameter {

    @XmlElement(nillable = true)
    protected List<EvaluationType> allowedEvaluationTypes;
    protected String description;
    protected String name;
    protected ParameterValueType type;
    @XmlElement(name = "UUID")
    protected String uuid;
    protected String unit;
    @XmlElement(nillable = true)
    protected List<ValueConstraint> valueConstraints;
    protected ValuesAllowedType valuesAllowedType;

    /**
     * Gets the value of the allowedEvaluationTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allowedEvaluationTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllowedEvaluationTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EvaluationType }
     * 
     * 
     */
    public List<EvaluationType> getAllowedEvaluationTypes() {
        if (allowedEvaluationTypes == null) {
            allowedEvaluationTypes = new ArrayList<EvaluationType>();
        }
        return this.allowedEvaluationTypes;
    }
	
	/**
     * Sets the value of the allowedEvaluationTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link EvaluationType }
     *     
     */
    public void setAllowedEvaluationTypes(List<EvaluationType> value) {
        this.allowedEvaluationTypes = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setType(ParameterValueType value) {
        this.type = value;
    }

    /**
     * Gets the value of the uuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Sets the value of the uuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUUID(String value) {
        this.uuid = value;
    }

    /**
     * Gets the value of the unit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the value of the unit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnit(String value) {
        this.unit = value;
    }

    /**
     * Gets the value of the valueConstraints property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the valueConstraints property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValueConstraints().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValueConstraint }
     * 
     * 
     */
    public List<ValueConstraint> getValueConstraints() {
        if (valueConstraints == null) {
            valueConstraints = new ArrayList<ValueConstraint>();
        }
        return this.valueConstraints;
    }
	
	/**
     * Sets the value of the valueConstraints property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueConstraint }
     *     
     */
    public void setValueConstraints(List<ValueConstraint> value) {
        this.valueConstraints = value;
    }

    /**
     * Gets the value of the valuesAllowedType property.
     * 
     * @return
     *     possible object is
     *     {@link ValuesAllowedType }
     *     
     */
    public ValuesAllowedType getValuesAllowedType() {
        return valuesAllowedType;
    }

    /**
     * Sets the value of the valuesAllowedType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValuesAllowedType }
     *     
     */
    public void setValuesAllowedType(ValuesAllowedType value) {
        this.valuesAllowedType = value;
    }

}
