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
//      Created Date :          2011-09-12
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator;

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.*;

import java.util.*;
import java.util.regex.*;




public class SVGDoc implements ISVGDoc
{
  private static String defaultXMLHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
  private static String defaultDocType = "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n";
  private static String defaultSVGNS = "version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" ";
  
  private static SVGDocUtility docUtility;
  
  private UUID                          docID;
  private SVGElGroup                    rootGroup;
  private ArrayList<String>             scriptFunctions;
  private HashMap<UUID, ISVGDefinition> docDefinitions;
  
  // ISVGDOC -------------------------------------------------------------------
  @Override
  public UUID getID()
  { return docID; }
  
  @Override
  public void clearAll()
  { 
    rootGroup.clearAllElements();
    scriptFunctions.clear();
    docDefinitions.clear();
  }
  
  @Override
  public void clearContent()
  {
    rootGroup.clearAllElements();
  }
  
  @Override
  public ISVGDocUtility getDocUtility()
  {
    if ( docUtility == null ) docUtility = new SVGDocUtility();
    return docUtility;
  }
  
  @Override
  public String getXML()
  {
    // Header
    String xmlResult = getHeader();
    
    // Body
    xmlResult += getDocFullBody();
    
    // Footer
    xmlResult += "</svg>\n";
    
    return xmlResult;
  }
  
  @Override
  public String getXMLInWrapper( String xmlWrapper, String targetID )
  {
    // Find id="<targetID value>"
    Pattern pattern  = Pattern.compile( "(id)([\\s]*(=)[\\s]*)(\"" + targetID + "\")" );
    Matcher matcher  = pattern.matcher( xmlWrapper );
    String resultDoc = new String(); 
    
    // Inject post-header material
    String injection = getDocFullBody();
    
    int endOfTag = 0;
    int lastEndOfTag = 0;
    
    // In a validated document, this should only match ONE tag
    while ( matcher.find() )
    {
      endOfTag = matcher.end();
      endOfTag = xmlWrapper.indexOf( ">", endOfTag ) +1;
      
      // Found the end of the tag with the target ID, so make with the injection
      if ( endOfTag != -1 )
      {
        resultDoc = xmlWrapper.substring( lastEndOfTag, endOfTag );
        
        // Embedded XML content
        resultDoc += injection;
        
        lastEndOfTag = endOfTag;
      }
    }
    
    // Append remaining part of the document
    resultDoc += xmlWrapper.substring( endOfTag, xmlWrapper.length() );
    
    return resultDoc;    
  }
  
  @Override
  public ISVGElGroup getRootGroup()
  {
    return rootGroup;
  }
  
  @Override
  public void addScriptFunction( String funcDefinition )
  { if ( funcDefinition != null ) scriptFunctions.add( funcDefinition ); }
  
  @Override
  public void addDefinition( ISVGDefinition def )
  {
    if ( def != null )
    {
      ISVGElement el = (ISVGElement) def;
      UUID defID = el.getID();
      
      docDefinitions.remove( defID );
      docDefinitions.put( defID, def );
    }
  }
  
  // Protected methods ---------------------------------------------------------
  /**
   * SVGDocGenerator must create this document (as we may need to link in specific
   * event handling later on which the doc writer will know how to do
   * 
   * @param ID 
   */
  protected SVGDoc( UUID id )
  {
    docID           = id;
    rootGroup       = new SVGElGroup( null, id );
    scriptFunctions = new ArrayList<String>();
    docDefinitions  = new HashMap<UUID, ISVGDefinition>();
  }
  
  // Private methods -----------------------------------------------------------
  private String getHeader()
  {
    String xml = SVGDoc.defaultXMLHeader;
    xml += SVGDoc.defaultDocType;
    xml += "<svg " + defaultSVGNS + ">\n";

    return xml;
  }
  
  private String getDocFullBody()
  {
    String body = getScriptBody();
    
    body += getDefinitionsBody();
    
    body += rootGroup.getXML();
    
    return body;
  }
  
  private String getScriptBody()
  {
    // Header
    String script = "<script type=\"text/javascript\"><![CDATA[\n";
        
    // Functions
    Iterator<String> scriptIt = scriptFunctions.iterator();
    while ( scriptIt.hasNext() )
      script += scriptIt.next() + "\n";    
    
    // Footer
    script += "]]></script>\n";
    
    return script;
  }
  
  private String getDefinitionsBody()
  {
    String body = "";
    
    Collection<ISVGDefinition> defs = docDefinitions.values();
    
    if ( !defs.isEmpty() )
    {
      body = "<defs>\n";
      
      Iterator<ISVGDefinition> defIt = defs.iterator();
      while ( defIt.hasNext() )
      {
        ISVGElement el = (ISVGElement) defIt.next();
        body += el.getXML();
      }
      
      body += "</defs>\n";
    }
    
    return body;
  }
}