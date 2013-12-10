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
//      Created Date :          27-Jul-2012
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.treatmentEngine.impl;

import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramGenerator;
import org.activiti.engine.repository.ProcessDefinition;

import java.io.*;
import java.util.*;




/**
 * This renderer is 'fat' because it producers PNG images (which are non-interactive)
 * To be replaced with an SVG based rendering from the visualisation engine in the future
 * @author Simon Crowle
 */
public class TreatmentProcessFatRenderer
{
	private ProcessDefinitionEntity procDefEntity;
	private List<String>            activeIDs;
	private InputStream             lastRenderStream;
  
  public TreatmentProcessFatRenderer()
  {		
		activeIDs = new ArrayList<String>();
  }
  
  public void setProcessDefinition( ProcessDefinition pd )
	{ procDefEntity = (ProcessDefinitionEntity) pd; }
  
  public void setActiveIDs( List<String> ids )
	{ if ( ids != null ) activeIDs = ids; }
  
  public void clearActiveIDs()
	{ activeIDs.clear(); }
  
  public InputStream getRenderedImageStream()
	{ return lastRenderStream; }
  
  public InputStream generateBPMNDiagram()
	{
    lastRenderStream = null;
		
		if ( procDefEntity != null )
      lastRenderStream = ProcessDiagramGenerator.generateDiagram( procDefEntity,
                                                                  "png",
                                                                  activeIDs );
	
		return lastRenderStream;
	}
}
