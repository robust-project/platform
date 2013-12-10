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
//      Created Date :          2011-10-21
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import com.vaadin.ui.*;


public class VerticalLabel extends SimpleView
{
  private Label vertLabel;
  
  public VerticalLabel( String text, float pixLabelHeight )
  {
    super();
    
    vertLabel = new Label();
    viewContents.addComponent( vertLabel );
    
    vertLabel.setContentMode( Label.CONTENT_XHTML );
    
    Float labHeight = new Float( pixLabelHeight );
    
    vertLabel.setWidth( labHeight.toString() );    
    vertLabel.setValue( generateSVGLabel(text, labHeight) );
  }
  
  public void setHeight( String hVal )
  { vertLabel.setHeight( hVal ); }
  
  // Private methods -----------------------------------------------------------
  private String generateSVGLabel( String labelContents, Float labelHeight )
  {
    String svgContents = "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">";
   
    Float raisedHeight = labelHeight * 1.2f;
    svgContents += "<g transform=\"translate(" + raisedHeight +",0)\">";
    svgContents += "<g transform=\"rotate(90)\">";

    svgContents += "<text x=\"0\" y=\"" + labelHeight + "\" "
                 + "font-family=\"Verdana\" "
                 + "font-size=\"" + labelHeight + "\">"
                 + labelContents + "</text>";
    
    svgContents += "</g></g></g></svg>";
    
    return svgContents;
  }
}