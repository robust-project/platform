/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre and CORMSIS, 2012
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
//      Created By :            Vegard Engen, Edwin Tye
//      Created Date :          2013-04-09
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.ps.cm;

/**
 * A simple enum for the different platform names.
 * 
 * @author ve
 */
public enum PlatformType
{
    IBM, SAP, BOARDSIE;
    
    public String value() {
        return name();
    }

    public static PlatformType fromValue(String v) {
        return valueOf(v.toUpperCase());
    }
}
