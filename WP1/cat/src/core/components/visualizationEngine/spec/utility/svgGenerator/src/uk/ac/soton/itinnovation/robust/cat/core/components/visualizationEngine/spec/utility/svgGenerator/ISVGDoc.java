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
//      Created Date :          2011-09-22
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator;

import java.util.UUID;




/**
 * ISVGDoc is the top-level document object that contains SVG scripts, definitions
 * and graphical content.
 * 
 * @author Simon Crowle
 */
public interface ISVGDoc
{
  /**
   * Returns the ID of the document
   * 
   * @return - the ID of the document
   */
  UUID getID();
  
  /**
   * Clears all components of the document: scripts, definitions and content.
   */
  void clearAll();
  
  /**
   * Clears only the document contents (scripts & definitions remain)
   */
  void clearContent();
  
  /**
   * Returns a utility class to assist in document creation
   * 
   * @return 
   */
  ISVGDocUtility getDocUtility();
  
  /**
   * returns the SVG content for this document
   * 
   * @return - SVG document contents
   */
  String getXML();
  
  /**
   * Injects the current document contents into the XML wrapper wherever
   * the targetID string is found (as a node attribute: i.e., id="myID")
   * 
   * @param xmlWrapper - XML based wrapper for the current document's content
   * @param targetID   - the ID of the node into which the content is injected
   * @return           - returns the wrapper with injected content
   */
  String getXMLInWrapper( String xmlWrapper, String targetID );
  
  /**
   * Returns the root group of the document
   * 
   * @return - the root instance
   */
  ISVGElGroup getRootGroup();
  
  /**
   * Adds a javascript element to the document
   * 
   * @param funcDefintion - 
   */
  void addScriptFunction( String funcDefintion );
  
  /**
   * Adds a SVG definition to the document
   * 
   * @param def - the definition (see ISVGDocUtility for an example)
   */
  void addDefinition( ISVGDefinition def );
}