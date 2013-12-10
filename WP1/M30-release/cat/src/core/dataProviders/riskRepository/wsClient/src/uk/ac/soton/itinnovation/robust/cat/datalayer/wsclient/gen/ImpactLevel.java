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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for impactLevel.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="impactLevel">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NEG_VHIGH"/>
 *     &lt;enumeration value="NEG_HIGH"/>
 *     &lt;enumeration value="NEG_MEDIUM"/>
 *     &lt;enumeration value="NEG_LOW"/>
 *     &lt;enumeration value="NEG_VLOW"/>
 *     &lt;enumeration value="POS_VLOW"/>
 *     &lt;enumeration value="POS_LOW"/>
 *     &lt;enumeration value="POS_MEDIUM"/>
 *     &lt;enumeration value="POS_HIGH"/>
 *     &lt;enumeration value="POS_VHIGH"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "impactLevel")
@XmlEnum
public enum ImpactLevel {

    NEG_VHIGH,
    NEG_HIGH,
    NEG_MEDIUM,
    NEG_LOW,
    NEG_VLOW,
    POS_VLOW,
    POS_LOW,
    POS_MEDIUM,
    POS_HIGH,
    POS_VHIGH;

    public String value() {
        return name();
    }

    public static ImpactLevel fromValue(String v) {
        return valueOf(v);
    }

}
