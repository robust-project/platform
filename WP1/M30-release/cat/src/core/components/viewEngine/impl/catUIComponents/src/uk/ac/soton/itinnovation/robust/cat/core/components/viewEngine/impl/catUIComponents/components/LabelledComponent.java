/////////////////////////////////////////////////////////////////////////
//
// Â© University of Southampton IT Innovation Centre, 2011
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
//      Created Date :          2011-09-19
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UILayoutUtil;

import com.vaadin.ui.*;




public class LabelledComponent extends SimpleView
{
  private String    componentLabel;
  private Component component;
  
  public LabelledComponent( String    text,
                            String    labelWidth,
                            Component comp )
  {
    super();
    
    viewContents = new HorizontalLayout();
    componentLabel = text;
    component = comp;
    
    createComponent( labelWidth, "4px" );
  }
  
  public LabelledComponent( String    text,
                            String    labelWidth,
                            String    labelCompSpace,
                            Component comp )
  {
    componentLabel = text;
    component = comp;
    
    createComponent( labelWidth, labelCompSpace );
  }
  
  // Private methods -----------------------------------------------------------
  private void createComponent( String labelWidth, String labelCompSpace )
  {
    HorizontalLayout hl = (HorizontalLayout) viewContents;
    
    // Label
    Label label = new Label( componentLabel );
    label.setWidth( labelWidth );
    label.addStyleName( "small" );
    label.addStyleName( "catTextAlignRight" );
    hl.addComponent( label );
    hl.setComponentAlignment( label, Alignment.MIDDLE_LEFT );
    
    // Space
    hl.addComponent( UILayoutUtil.createSpace( labelCompSpace, null, true) );

    // Component
    hl.addComponent( component );
    hl.setExpandRatio( component, 1.0f );
    hl.setComponentAlignment( component, Alignment.MIDDLE_CENTER );
    
    // Tail space
    hl.addComponent( UILayoutUtil.createSpace( "2px", null, true) );
    
    hl.setComponentAlignment( label, Alignment.MIDDLE_RIGHT );
  }
}