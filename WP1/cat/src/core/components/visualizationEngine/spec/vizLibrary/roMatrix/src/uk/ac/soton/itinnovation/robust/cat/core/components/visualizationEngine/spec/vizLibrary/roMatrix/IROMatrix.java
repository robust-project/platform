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
//      Created Date :          2011-12-09
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.vizLibrary.roMatrix;

import java.util.*;



/**
 * IROMatrix provides a basic risk matrix visualisation for risks/opportunities. Each
 * risk/opportunity has a probability and one+ impacts associated with it (to be rendered).
 * At render time, each risk's impacts are placed at the probability level associated with
 * the risk. It is also possible to high-light particular risks to the user.
 * 
 * @author Simon Crowle
 */
public interface IROMatrix
{
  /**
   * Clears all risk presentations from the matrix and clears all risk highlights
   * previously set
   */
  void resetMatrix();
  
  /**
   * Clears just the risks presentations from the matrix. Highlighted risk 
   * information is kept.
   */
  void clearMatrixData();
  
  /**
   * Creates a light-weight risk representation when supplied with the ID of that
   * risk. This entity will be included during the render process.
   * 
   * @param id - unique ID of the risk to be represented
   * @param index - (if not null) specifies the index of the risk in the list
   * @return   - a light-weight entity representing the risk
   */
  IROMRiskEntity addEntity( UUID id, Integer index );
  
  /**
   * Removes the current risk entity from the matrix.
   * 
   * @param id - unique ID of the risk to be represented
   */
  void removeEntity( UUID id );
  
  /**
   * Removes the current risk entity from the matrix.
   * 
   * @param entity - IROMRiskEntity representing the risk.
   */
  void removeEntity( IROMRiskEntity entity );
  
  /**
   * Provide a set of IDs that should be high-lighted during the render process.
   * 
   * @param ids - the set of IDs used in addEntity(..)
   */
  void highlightEntities( Set<UUID> ids );
  
  /**
   * It is sometimes useful to re-index entities being represented in the matrix.
   * user this method to do this.
   * 
   * @param indicies - a map of IDs representing each RiskEntity and its new index
   */
  void updateEntityIndicies( Map<UUID, Integer> indicies );
  
  /**
   * Renders and returns a visualised matrix.
   * 
   * @return - SVG based render
   */
  String getXMLResult();
  
  /**
   * To assist in scaling the visualisation render, you can specify (as a percentage)
   * the smallest 'unit size' of risk information presented within each matrix
   * cell. For example, given an overall matrix render size of 100x100 pixels,
   * specifying unit sizes of 10% and 12% will give a unit size of 10 x 12 pixels.
   * 
   * @param widthPercent  - unit width in percent
   * @param heightPercent - unit height in percent
   */
  void setAbsMinVizUnit( float widthPercent, float heightPercent );
}