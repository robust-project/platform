/** 
*Copyright 2013 Software Mind SA
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/

package pl.softwaremind.robust.healthindicatorservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accessKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dataSourceId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="communityId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "accessKey",
    "dataSourceId",
    "communityId",
    "startDate",
    "endDate"
})
@XmlRootElement(name = "getHealthScoresRequest")
public class GetHealthScoresRequest {

    @XmlElement(required = true)
    protected String accessKey;
    protected int dataSourceId;
    protected int communityId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar startDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar endDate;

    /**
     * Gets the value of the accessKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * Sets the value of the accessKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessKey(String value) {
        this.accessKey = value;
    }

    /**
     * Gets the value of the dataSourceId property.
     * 
     */
    public int getDataSourceId() {
        return dataSourceId;
    }

    /**
     * Sets the value of the dataSourceId property.
     * 
     */
    public void setDataSourceId(int value) {
        this.dataSourceId = value;
    }

    /**
     * Gets the value of the communityId property.
     * 
     */
    public int getCommunityId() {
        return communityId;
    }

    /**
     * Sets the value of the communityId property.
     * 
     */
    public void setCommunityId(int value) {
        this.communityId = value;
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
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

}
