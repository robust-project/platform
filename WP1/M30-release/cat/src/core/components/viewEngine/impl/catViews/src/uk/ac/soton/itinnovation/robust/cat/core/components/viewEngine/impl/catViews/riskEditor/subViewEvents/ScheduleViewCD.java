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
//      Created By :            sgc
//      Created Date :          11 Nov 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UIChangeData;

import uk.ac.soton.itinnovation.robust.riskmodel.Period;




public class ScheduleViewCD extends UIChangeData
{
  private int catFreq;
  private int humanFreq;
  private Period catPeriod;
  private Period humanPeriod;
  private boolean notifyUser;
  
  public ScheduleViewCD()
  { super(); }
  
  @Override
  public void reset()
  {
    catFreq = 0;
    humanFreq = 0;
    catPeriod = Period.DAY;
    humanPeriod = Period.DAY;
    notifyUser = false;
    dataChanged = false;
  }
  
  public int  getCatReviewFrequency() { return catFreq; }
  public void setCatReviewFrequency( int freq ) { catFreq = freq; dataChanged = true; }
  
  public Period getCatReviewPeriod() { return catPeriod; }
  public void   setCatReviewPeriod( Period per ) { catPeriod = per; dataChanged = true; }
  
  public int  getHumanReviewFrequency() { return humanFreq; }
  public void setHumanReviewFrequency( int freq ) { humanFreq = freq; dataChanged = true; }
  
  public Period getHumanReviewPeriod() { return humanPeriod; }
  public void   setHumanReviewPeriod( Period per ) { humanPeriod = per; dataChanged = true; }
  
  public boolean getNotifyUser() { return notifyUser; }
  public void    setNotifyUser( boolean notify ) { notifyUser = notify; dataChanged = true; }
}