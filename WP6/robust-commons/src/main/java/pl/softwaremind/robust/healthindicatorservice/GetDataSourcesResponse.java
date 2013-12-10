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
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="datasources" type="{http://robust.softwaremind.pl/HealthIndicatorService}ListOfDataSources"/>
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
    "datasources"
})
@XmlRootElement(name = "getDataSourcesResponse")
public class GetDataSourcesResponse {

    @XmlElement(required = true)
    protected ListOfDataSources datasources;

    /**
     * Gets the value of the datasources property.
     * 
     * @return
     *     possible object is
     *     {@link ListOfDataSources }
     *     
     */
    public ListOfDataSources getDatasources() {
        return datasources;
    }

    /**
     * Sets the value of the datasources property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfDataSources }
     *     
     */
    public void setDatasources(ListOfDataSources value) {
        this.datasources = value;
    }

}
