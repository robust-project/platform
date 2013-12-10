/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2013
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
//      Created By :            Ken Meacham
//      Created Date :          26 Oct 2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.components;

import uk.ac.soton.itinnovation.robust.cat.common.datastructures.AParameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.Parameter;
import uk.ac.soton.itinnovation.robust.cat.common.datastructures.ParameterValueType;

public class UIParameter extends Parameter {
    
    // flag to indicate if parameter should be displayed in the UI
    private boolean visible = true; 
    
    public UIParameter()
    {
        super();
    }
    
    public UIParameter(ParameterValueType type, String name, String desc, String unit) {
        super(type, name, desc, unit);
    }

    /**
     * Copy constructor.
     * @eventCond The UIParameter object you want to make a copy of.
     */
    public UIParameter(AParameter param)
    {
        super(param);
        
        if (param instanceof UIParameter)
        {
            this.visible = ((UIParameter)param).isVisible();
        }
    }
    
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
}
