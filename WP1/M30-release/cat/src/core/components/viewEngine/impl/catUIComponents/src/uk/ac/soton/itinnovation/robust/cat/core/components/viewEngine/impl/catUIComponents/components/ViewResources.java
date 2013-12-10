package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

/////////////////////////////////////////////////////////////////////////
//
// ï¿½ University of Southampton IT Innovation Centre, 2011
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
//      Created Date :          2011-10-26
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////



import com.vaadin.terminal.ThemeResource;
import java.io.Serializable;
import java.util.*;




public class ViewResources implements Serializable
{
  private HashMap<String, ThemeResource> resources;
  
  public static ViewResources CATAPPResInstance = null;
  
  public ViewResources()
  { resources = new HashMap<String, ThemeResource>(); }
  
  public void createResource( String ID, String resPath )
  {
    resources.remove( ID );
    resources.put( ID, new ThemeResource(resPath) );
  }
  
  public void removeResource( String ID )
  { resources.remove( ID ); }
  
  public ThemeResource getResource( String ID )
  { return resources.get( ID ); }
}
