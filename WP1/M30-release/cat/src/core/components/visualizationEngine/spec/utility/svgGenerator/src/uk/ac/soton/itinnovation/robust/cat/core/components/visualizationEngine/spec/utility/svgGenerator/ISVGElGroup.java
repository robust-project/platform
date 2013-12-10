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
//      Created Date :          2011-09-22
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator;

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.elements.ISVGVector;

import java.util.UUID;

/**
 * ISVGElGroup is an aggregating class that contains other SVG objects. Use this
 * class to generate a number of different shape types and additional sub-groups.
 * 
 * @author sgc
 */
public interface ISVGElGroup
{
  /**
   * eShapeType enumerates the currently shape types directly supported by th
   * API. 
   */
  enum eShapeType { LINE, RECT, POLYGON, CIRCLE, TEXT };
  
  /**
   * Clears all elements from the group
   */
  void clearAllElements();
  
  /**
   * Adds a shape of type eShapeType to the group
   * 
   * @param type - the eShapeType required
   * @return     - the instance of the shape that has been added to the group
   */
  ISVGVector addVector( eShapeType type );
  
  /**
   * Adds a shape of type eShapeType to the group
   * 
   * @param type    - the eShapeType required
   * @param visible - the shape's visibility
   * @return        - the instance of the shape that has been added to the group
   */
  ISVGVector addVector ( eShapeType type, boolean visible );
  
  /**
   * Adds a sub-group with a specific ID
   * 
   * @param id - ID used to identify the sub-group
   * @return   - the sub-group instance added to the group
   */
  ISVGElGroup addSubGroup( UUID id );
  
  /**
   * Adds a sub-group with a specific ID
   * 
   * @param id      - ID used to identify the sub-group
   * @param visible - the visibility of the group's elements and all sub-groups
   * @return        - the sub-group instance added to the group
   */
  ISVGElGroup addSubGroup( UUID id, boolean visible );
  
  /**
   * Returns the element associated with the ID
   * 
   * @param id - the ID identifying the element
   * @return   - the element (or sub-group) found, or null
   */
  ISVGElement getElement( UUID id );
  
  /**
   * Removes the specified vector from the group
   * 
   * @param el - the element (vector or sub-group) to be removed
   */
  void removeElement( ISVGElement el );
}