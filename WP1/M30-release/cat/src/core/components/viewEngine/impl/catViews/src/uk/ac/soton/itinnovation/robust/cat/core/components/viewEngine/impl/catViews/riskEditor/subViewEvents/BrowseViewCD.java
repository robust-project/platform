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
//      Created Date :          14 Nov 2011
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.riskEditor.subViewEvents;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UIChangeData;

import uk.ac.soton.itinnovation.robust.riskmodel.*;

import java.util.Date;


public class BrowseViewCD extends UIChangeData
{
  private String  roTitle;
  private boolean roType = true;
  private String  roGroupName;
  private String  roOwnerName;
  private Scope   roScope;
  private Date    roDate;
  
  public BrowseViewCD()
  {
    super();
  }
  
  @Override
  public void reset()
  {
    roTitle = "";
    roType = true;
    roGroupName = "";
    roOwnerName = "";
    roScope = Scope.ALL_USERS;
    dataChanged = false;
  }
  
  public String getROTitle() { return roTitle; }
  public void setROTitle( String title ) { roTitle = title; dataChanged = true; }
  
  public boolean getROType() { return roType; }
  public void setROType( boolean type ) { roType = type; dataChanged = true; }
  
  public String getROGroup() { return roGroupName; }
  public void setROGroup( String name ) { roGroupName = name; dataChanged = true; }
  
  public String getROOwner() { return roOwnerName; }
  public void setROOwner( String name ) { roOwnerName = name; dataChanged = true; }
  
  public Scope getROScope() { return roScope; }
  public void setROScope( Scope scope ) { roScope = scope; dataChanged = true; }
  
  public Date getROExpiration() { return roDate; }
  public void setROExpiration( Date date ) { roDate = date; dataChanged = true; }
}