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
//      Created Date :          28-Aug-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package client;

import org.dozer.MappingException;

public class EnumConverter implements org.dozer.CustomConverter {

    public Object convert(Object destination, Object source, Class destClass, Class sourceClass) {
        if (source == null) {
            return null;
        }
        Object dest = null;
        if(destClass.equals(uk.ac.soton.itinnovation.robust.riskmodel.ImpactLevel.class)){
            return uk.ac.soton.itinnovation.robust.riskmodel.ImpactLevel.fromString(source.toString());
        }
      
        if(destClass.equals(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ImpactLevel.class)){
            return uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.ImpactLevel.fromValue(source.toString());
        }
        
//        if(destClass.equals(uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition.class)&&sourceClass.equals(uk.ac.soton.itinnovation.robust.cat.datalayer.wsclient.gen.EventCondition.class)){
//            dest=new uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition();
//            ((uk.ac.soton.itinnovation.robust.cat.common.datastructures.EventCondition)dest).setU
//            
//            return dest;
//        }
        
        throw new MappingException("Enum Converter used incorrectly. Arguments passed in were:"+ destination + " and " + source);
    }
       
    
}
