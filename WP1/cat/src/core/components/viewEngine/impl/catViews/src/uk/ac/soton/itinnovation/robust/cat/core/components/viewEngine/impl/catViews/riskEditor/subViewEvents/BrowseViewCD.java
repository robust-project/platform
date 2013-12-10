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
//      Created By :            Simon Crowle
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
  // Change data variables are set to null initially, to distinguish when they are set for the first time.
  // In this case, the dataChanged value will not be set when the set methods are called.
  private String  roTitle = null;
  private Boolean roType = true;
  private String  roGroupName = null;
  private String  roOwnerName = null;
  private Scope   roScope = null;
  private Date    roDate = null;
  
  public BrowseViewCD()
  {
    super();
  }
  
  @Override
  public void reset()
  {
    roTitle = null;
    roType = true;
    roGroupName = null;
    roOwnerName = null;
    roScope = null;
    dataChanged = false;
  }

  @Override
  public boolean isDataChanged() {
      if (! dataChanged)
          return false;
      
      if ( (roTitle == null) || (roType == null) || (roGroupName == null) || (roOwnerName == null) || (roScope == null) ) {
          String vars = "";
          if (roTitle == null) vars += "roTitle";
          if (roType == null) vars += ( ("".equals(vars)) ? "" : ", " ) + "roType";
          if (roGroupName == null) vars += ( ("".equals(vars)) ? "" : ", " ) + "roGroupName";
          if (roOwnerName == null) vars += ( ("".equals(vars)) ? "" : ", " ) + "roOwnerName";
          if (roScope == null) vars += ( ("".equals(vars)) ? "" : ", " ) + "roScope";
              
          throw new RuntimeException("BrowseViewCD.isDataChanged() value is true, but following variables have not been set initially: " + vars);
      }
      
      return true;
  }
  
  public String getROTitle() {
      return roTitle;
  }
  
  public void setROTitle( String title ) {
      if (roTitle != null) {
          dataChanged = true;
      }
      roTitle = title;
  }
  
  public Boolean getROType() {
      return roType;
  }
  
  public void setROType( boolean type ) {
      if (roType != null) {
          dataChanged = true;
      }
      roType = type;
  }
  
  public String getROGroup() {
      return roGroupName;
  }
  
  public void setROGroup( String name ) {
      if (roGroupName != null) {
          dataChanged = true;
      }
      roGroupName = name;
  }
  
  public String getROOwner() {
      return roOwnerName;
  }
  
  public void setROOwner( String name ) {
      if (roOwnerName != null) {
          dataChanged = true;
      }
      roOwnerName = name;
  }
  
  public Scope getROScope() {
      return roScope;
  }
  
  public void setROScope( Scope scope ) {
      if (roScope != null) {
          dataChanged = true;
      }
      roScope = scope;
  }
  
  public Date getROExpiration() {
      return roDate;
  }
  
  public void setROExpiration( Date date ) {
      if (roDate != null) {
          dataChanged = true;
      }
      roDate = date;
  }
}