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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for valueConstraintType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="valueConstraintType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DEFAULT"/>
 *     &lt;enumeration value="VALUESALLOWED"/>
 *     &lt;enumeration value="MIN"/>
 *     &lt;enumeration value="MAX"/>
 *     &lt;enumeration value="STEP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "valueConstraintType")
@XmlEnum
public enum ValueConstraintType {

    DEFAULT,
    VALUESALLOWED,
    MIN,
    MAX,
    STEP;

    public String value() {
        return name();
    }

    public static ValueConstraintType fromValue(String v) {
        return valueOf(v);
    }

}
