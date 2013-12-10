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
//      Created By :            Simon Crowle
//      Created Date :          24-Oct-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.dashboard.indicators;

import uk.ac.soton.itinnovation.robust.riskmodel.*;

import java.util.*;




class RiskEvalResult
{
  private UUID        riskID;
  private String      riskTitle;
  private Date        currentDate;
  private Date        forecastDate;
  private ImpactLevel impactLevel;
  private double      probability;

  public RiskEvalResult( Risk        risk,
                         Date        currDate,
                         Date        foreDate,
                         ImpactLevel impLevel,
                         double      prob )
  {
    riskID       = risk.getId();
    riskTitle    = risk.getTitle();
    currentDate  = currDate;
    forecastDate = foreDate;
    impactLevel  = impLevel;
    probability  = prob;
  }

  public UUID getRiskID()
  { return riskID; }

  public String getRiskTitle()
  { return riskTitle; }

  public Date getCurrentDate()
  { return currentDate; }

  public Date getForecastDate()
  { return forecastDate; }

  public ImpactLevel getImpact()
  { return impactLevel; }

  public double getProbability()
  { return probability; }

}
