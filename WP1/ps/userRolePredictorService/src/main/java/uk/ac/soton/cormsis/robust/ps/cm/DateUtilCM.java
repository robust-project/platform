/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton CORMSIS, 2012
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
//      Created By :            Edwin
//      Created Date :          2012-01-18
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.cormsis.robust.ps.cm;
import java.util.Calendar;
import java.util.Date;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;
import uk.ac.soton.itinnovation.robust.cat.common.util.DateUtil;

/**
 *
 * @author Ed
 */
public class DateUtilCM extends DateUtil {
    
    public static Date getToDate (Date from, FrequencyType freq, int step)
    {
        Calendar calFrom = Calendar.getInstance();
        calFrom.setTimeInMillis(from.getTime());
        
        Calendar calTo = getCalendarToDate(calFrom, freq, step);
        
        return new Date(calTo.getTimeInMillis());
    }
    
    public static Calendar getCalendarToDate(Calendar from, FrequencyType freq, int step) 
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(from.getTimeInMillis());
 
        switch (freq)
        {
            case WEEKLY:
            {
                cal.add(Calendar.DAY_OF_MONTH, 7*step);
                break;
            }
            case BIWEEKLY:
            {
                cal.add(Calendar.DAY_OF_MONTH, 14*step);
                break;
            }
            case MONTHLY:
            {
                cal.add(Calendar.MONTH, 1*step);
                break;
            }
            case BIMONTHLY:
            {
                cal.add(Calendar.MONTH, 2*step);
                break;
            }
            case QUARTERLY:
            {
                cal.add(Calendar.MONTH, 3*step);
                break;
            }
            case SEMIANNUALLY:
            {
                cal.add(Calendar.MONTH, 6*step);
                break;
            }
            case ANNUALLY:
            {
                cal.add(Calendar.YEAR, 1*step);
                break;
            }
                
            default:
                break;
        }
        
        return cal;
    }

}
