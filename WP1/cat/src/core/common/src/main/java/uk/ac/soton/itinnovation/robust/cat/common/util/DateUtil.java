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
//      Created By :            Vegard Engen
//      Created Date :          2011-06-23
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.FrequencyType;

/**
 * 
 * @author ve
 */
public class DateUtil
{
    /**
     * The date string should be in one of the following formats:
     *    YYYY-MM-DD
     *    YYYY/MM/DD
     *    DD-MM-YYYY
     *    DD/MM/YYYY
     * 
     * No American nonsense please ;-)
     * @param date
     * @throws Exception if the date string is not in a valid format
     * @return 
     */
    public static Long getEpoch (String date) throws Exception
    {
        String[] strArray = date.split("[/-]");
        Calendar calendar = Calendar.getInstance();
        
        if (!isDateStringValid(strArray, calendar)) {
            throw new RuntimeException("Date string not valid, please refer to the documentation");
		}
		
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
    
    /**
     * The date string should be in one of the following formats:
     *    YYYY-MM-DD
     *    YYYY/MM/DD
     *    DD-MM-YYYY
     *    DD/MM/YYYY
     * 
     * No American nonsense please ;-)
     * @param date
     * @throws Exception if the date string is not in a valid format
     * @return 
     */
    public static Date getDateObject (String date) throws Exception
    {
        return new Date (getEpoch(date));
    }
    
    /**
     * Returns a string in the format: YYYY-MM-DD
     * @param date A Date object specifying a date.
     * @return a string in the format: YYYY-MM-DD
     */
    public static String getDateString(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        
        return getDateString(cal);
    }
    
    /**
     * Returns a string in the format: YYYY-MM-DD
     * @param cal A Calendar object specifying a date.
     * @return a string in the format: YYYY-MM-DD
     */
    public static String getDateString(Calendar cal)
    {
        String DATE_FORMAT = "yyyy-MM-dd";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
        
        return sdf.format(cal.getTime());
    }
    
    /**
     * Get an XMLGregorianCalendar instance from a Date object.
     * @param date A Date object.
     * @return XMLGregorianCalendar instance.
     * @throws Exception 
     */
    public static XMLGregorianCalendar getXMLGregorianCalendar(Date date) throws Exception
    {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    }
    
    /**
     * Get a Date instance from an XMLGregorianCalendar object.
     * @param xmlCal XMLGregorianCalendar object.
     * @return A Date instance.
     * @throws Exception 
     */
    public static Date getDate(XMLGregorianCalendar xmlCal) throws Exception
    {
        return xmlCal.toGregorianCalendar().getTime();
    }
    
    /**
     * Validates a date String, which should be in one of the following formats:
     *    YYYY-MM-DD
     *    YYYY/MM/DD
     *    DD-MM-YYYY
     *    DD/MM/YYYY
     * 
     * No American nonsense please ;-)
     * @param date
     * @throws Exception if the date string is not in a valid format
     * @return 
     */
    public static boolean isDateStringValid (String date) throws Exception
    {
        String[] strArray = date.split("[/-]");
        Calendar calendar = Calendar.getInstance();
        return isDateStringValid(strArray, calendar);
    }
    
    private static boolean isDateStringValid (String[] strArray, Calendar calendar) throws Exception
    {
        if (strArray == null)
            return false;
        if (strArray.length != 3)
            return false;
        if (strArray[0].length() == 2) // first is day
        {
            if ((strArray[1].length() != 2) || (strArray[2].length() != 4))
                return false;
            calendar.set(Integer.parseInt(strArray[2]), Integer.parseInt(strArray[1])-1, Integer.parseInt(strArray[0]));
        }
        else if (strArray[0].length() == 4) // first is year
        {
            if ((strArray[1].length() != 2) || (strArray[2].length() != 2))
                return false;
            calendar.set(Integer.parseInt(strArray[0]), Integer.parseInt(strArray[1])-1, Integer.parseInt(strArray[2]));
        }
        else
            return false;
        
        return true;
    }
    /**
     * add one unit to the provided date
     */
    public static Date getToDate (Date from, FrequencyType freq)
    {
        Calendar calFrom = Calendar.getInstance();
        calFrom.setTimeInMillis(from.getTime());
        
        Calendar calTo = getCalendarToDate(calFrom, freq);
        
        return new Date(calTo.getTimeInMillis());
    }
    
     /**
     * add one unit to the provided date
     */
    public static Calendar getCalendarToDate (Calendar from, FrequencyType freq)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(from.getTimeInMillis());
        
        switch (freq)
        {
            case HOURLY:
            {
                cal.add(Calendar.HOUR_OF_DAY, 1);
                break;
            }
            case DAILY:
            {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                break;
            }
            case WEEKLY:
            {
                cal.add(Calendar.DAY_OF_MONTH, 7);
                break;
            }
            case BIWEEKLY:
            {
                cal.add(Calendar.DAY_OF_MONTH, 14);
                break;
            }
            case MONTHLY:
            {
                cal.add(Calendar.MONTH, 1);
                break;
            }
            case BIMONTHLY:
            {
                cal.add(Calendar.MONTH, 2);
                break;
            }
            case QUARTERLY:
            {
                cal.add(Calendar.MONTH, 3);
                break;
            }
            case SEMIANNUALLY:
            {
                cal.add(Calendar.MONTH, 6);
                break;
            }
            case ANNUALLY:
            {
                cal.add(Calendar.YEAR, 1);
                break;
            }
                
            default:
                break;
        }
        
        return cal;
    }
    
     /**
     * add frequency number units to the provided date
     */
    public static Date getToDate (Date from, FrequencyType frequencyType, int frequency)
    {
        Calendar calFrom = Calendar.getInstance();
        calFrom.setTimeInMillis(from.getTime());
        
        Calendar calTo = getCalendarToDate(calFrom, frequencyType, frequency);
        
        return new Date(calTo.getTimeInMillis());
    }
    
     /**
     * add frequency number units to the provided date
     */
    public static Calendar getCalendarToDate (Calendar from, FrequencyType frequencyType, int frequency)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(from.getTimeInMillis());
        
        switch (frequencyType)
        {
            case HOURLY:
            {
                cal.add(Calendar.HOUR_OF_DAY, (1*frequency));
                break;
            }
            case DAILY:
            {
                cal.add(Calendar.DAY_OF_MONTH, (1*frequency));
                break;
            }
            case WEEKLY:
            {
                cal.add(Calendar.DAY_OF_MONTH, (7*frequency));
                break;
            }
            case BIWEEKLY:
            {
                cal.add(Calendar.DAY_OF_MONTH, (14*frequency));
                break;
            }
            case MONTHLY:
            {
                cal.add(Calendar.MONTH, (1*frequency));
                break;
            }
            case BIMONTHLY:
            {
                cal.add(Calendar.MONTH, (2*frequency));
                break;
            }
            case QUARTERLY:
            {
                cal.add(Calendar.MONTH, (3*frequency));
                break;
            }
            case SEMIANNUALLY:
            {
                cal.add(Calendar.MONTH, (6*frequency));
                break;
            }
            case ANNUALLY:
            {
                cal.add(Calendar.YEAR, (1*frequency));
                break;
            }
                
            default:
                break;
        }
        
        return cal;
    }
    
     /**
     * subtract one frequency unit from the provided date
     */
    public static Date getFromDate (Date to, FrequencyType freq)
    {
        Calendar calTo = Calendar.getInstance();
        calTo.setTimeInMillis(to.getTime());
        
        Calendar calFrom = getCalendarFromDate(calTo, freq);
        
        return new Date(calFrom.getTimeInMillis());
    }
    
      /**
     * subtract one frequency unit from the provided date
     */
    public static Calendar getCalendarFromDate (Calendar to, FrequencyType freq)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(to.getTimeInMillis());
        
        switch (freq)
        {
            case HOURLY:
            {
                cal.add(Calendar.HOUR_OF_DAY, -1);
                break;
            }
            case DAILY:
            {
                cal.add(Calendar.DAY_OF_MONTH, -1);
                break;
            }
            case WEEKLY:
            {
                cal.add(Calendar.DAY_OF_MONTH, -7);
                break;
            }
            case BIWEEKLY:
            {
                cal.add(Calendar.DAY_OF_MONTH, -14);
                break;
            }
            case MONTHLY:
            {
                cal.add(Calendar.MONTH, -1);
                break;
            }
            case BIMONTHLY:
            {
                cal.add(Calendar.MONTH, -2);
                break;
            }
            case QUARTERLY:
            {
                cal.add(Calendar.MONTH, -3);
                break;
            }
            case SEMIANNUALLY:
            {
                cal.add(Calendar.MONTH, -6);
                break;
            }
            case ANNUALLY:
            {
                cal.add(Calendar.YEAR, -1);
                break;
            }
                
            default:
                break;
        }
        
        return cal;
    }
    
      /**
     * subtract num of frequency units from the provided date
     */
    public static Date getFromDate (Date to, FrequencyType frequencyType, int frequency)
    {
        Calendar calTo = Calendar.getInstance();
        calTo.setTimeInMillis(to.getTime());
        
        Calendar calFrom = getCalendarFromDate(calTo, frequencyType, frequency);
        
        return new Date(calFrom.getTimeInMillis());
    }
    
    public static Calendar getCalendarFromDate (Calendar to, FrequencyType frequencyType, int frequency)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(to.getTimeInMillis());
        
        switch (frequencyType)
        {
            case HOURLY:
            {
                cal.add(Calendar.HOUR_OF_DAY, -(1*frequency));
                break;
            }
            case DAILY:
            {
                cal.add(Calendar.DAY_OF_MONTH, -(1*frequency));
                break;
            }
            case WEEKLY:
            {
                cal.add(Calendar.DAY_OF_MONTH, -(7*frequency));
                break;
            }
            case BIWEEKLY:
            {
                cal.add(Calendar.DAY_OF_MONTH, -(14*frequency));
                break;
            }
            case MONTHLY:
            {
                cal.add(Calendar.MONTH, -(1*frequency));
                break;
            }
            case BIMONTHLY:
            {
                cal.add(Calendar.MONTH, -(2*frequency));
                break;
            }
            case QUARTERLY:
            {
                cal.add(Calendar.MONTH, -(3*frequency));
                break;
            }
            case SEMIANNUALLY:
            {
                cal.add(Calendar.MONTH, -(6*frequency));
                break;
            }
            case ANNUALLY:
            {
                cal.add(Calendar.YEAR, -(1*frequency));
                break;
            }
                
            default:
                break;
        }
        
        return cal;
    }
    
    public static int getNumDaysBetweenDates(Date d1, Date d2) throws Exception
    {
        return (int)( Math.abs(d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
	
//	public static boolean isFirstDateOlder(Date d1, Date d2) throws Exception
//    {
//		//logger.info("----> "+(d1.getTime() - d2.getTime()));
//
//    	if(d1.getTime() - d2.getTime() > 0)
//    	{
//    		return true;
//    	}
//    	else
//    	{
//    		return false;
//    	}
//    }
}
