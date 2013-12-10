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
//      Created Date :          20-Jan-2012
//      Created for Project :   robust
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import org.vaadin.svg.SvgComponent;




public class SVGView extends SimpleView
{
  private SvgComponent svgComponent;
  
  public SVGView()
  {
    super();
    
    initComponents();    
  }
  
  public void setContent( String content )
  { 
    if ( content != null )
    {
      svgComponent.setSvg( content );
      svgComponent.requestRepaint();
    }
  }
  
  // Private methods -----------------------------------------------------------
  private void initComponents()
  {
    svgComponent = new SvgComponent();
    svgComponent.setWidth( "100%" );
    svgComponent.setHeight( "100%" );
    viewContents.addComponent( svgComponent );
  }
}
