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
//      Created Date :          2011-29-12
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.vizLibrary.roMatrix;

import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.utility.svgGenerator.elements.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.vizLibrary.roMatrix.*;
import uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.impl.utility.svgGenerator.*;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.awt.geom.Point2D;
import java.util.AbstractMap.SimpleEntry;




public class ROMatrix implements IROMatrix
{
  private static String          roMatrixXMLTemplate;
  private static String          roMatrixFunctions;
  private static final float     cellWidth  = 100.0f * ( 1.0f / 6.0f  );
  private static final float     cellHeight = 100.0f * ( 1.0f / 5.0f );
  
  private static final String[] colourIndices = { "#EF0000", "#CF0000", "#AF0000", "#009F00", "#00AF00", "#00CF00",
                                                  "#E00000", "#C00000", "#A00000", "#007000", "#00A000", "#00C000",
                                                  "#D10000", "#B10000", "#910000", "#005100", "#009100", "#00B100",
                                                  "#C20000", "#A20000", "#820000", "#003200", "#007200", "#00A200",
                                                  "#B30000", "#930000", "#730000", "#002300", "#005300", "#009300" };
    
  private static HashMap<Integer, String> gradientURLs;
  private static HashMap<Integer, ISVGDefinition> gradientDefinitions;
  
  private HashMap<UUID, ROMRiskEntity> roIDEntityMap;
  private HashMap<Integer, MatCell>    matrixMap;
  private Set<UUID>                    roEntityHighlights;
  private ISVGDoc                      svgDoc;
  private Point2D.Float                minVizUnit;
  private float                        minVizUnitFontSize;
  
  public ROMatrix()
  {
    initResources();
    initSVGDoc();
    
    roIDEntityMap      = new HashMap<UUID, ROMRiskEntity>();
    matrixMap          = new HashMap<Integer, MatCell>();
    minVizUnit         = new Point2D.Float( 5.0f, 5.0f );
    minVizUnitFontSize = 20.0f;
    
    // Create matrix cells
    for ( int i = 0; i < 30; i++ )
    {
      MatCell newCell = new MatCell();
      newCell.setHighLightColour( "url(#" + gradientURLs.get(i) +")" );
      matrixMap.put( i, newCell );
    }
  }
    
  // IROMatrix -----------------------------------------------------------------
  @Override
  public void resetMatrix()
  {
    roEntityHighlights = null;
    clearMatrixData();
  }
  
  @Override
  public synchronized void clearMatrixData()
  { 
    roIDEntityMap.clear();
    
    for ( MatCell cell : matrixMap.values() )
      cell.clearEntities();
  }
  
  @Override
  public synchronized IROMRiskEntity addEntity( UUID id, Integer index )
  {
    ROMRiskEntity newEntity = new ROMRiskEntity( id );
    
    if (index != null)
        newEntity.setRiskIndex(index);
    
    roIDEntityMap.remove( newEntity.getRiskID() );
    roIDEntityMap.put( newEntity.getRiskID(), newEntity );
      
    return newEntity;
  }
  
  @Override
  public synchronized void removeEntity(UUID id) {
      roIDEntityMap.remove( id );
  }

  @Override
  public synchronized void removeEntity( IROMRiskEntity entity )
  {
    ROMRiskEntity delEntity = (ROMRiskEntity) entity;
    
    if ( delEntity != null ) roIDEntityMap.remove( delEntity.getRiskID() );
  }
  
  @Override
  public void highlightEntities( Set<UUID> ids )
  { roEntityHighlights = ids; }
  
  @Override
  public void updateEntityIndicies( Map<UUID, Integer> indicies )
  {
    if ( !roIDEntityMap.isEmpty() && !indicies.isEmpty() )
    {
      Set<Entry<UUID, Integer>> entries = indicies.entrySet();
      for ( Entry<UUID, Integer> entry : entries )
      {
        ROMRiskEntity re = roIDEntityMap.get( entry.getKey() );
        if ( re != null ) re.setRiskIndex( entry.getValue() );
      }
    }
  }
  
  @Override
  public synchronized String getXMLResult()
  {
    svgDoc.clearContent();
    
    renderMatrix( (ISVGElGroup) svgDoc.getRootGroup() );
    
    return svgDoc.getXMLInWrapper( roMatrixXMLTemplate, "RMData" );
  }
  
  @Override
  public void setAbsMinVizUnit( float widthPercent, float heightPercent )
  { 
    minVizUnit = new Point2D.Float( widthPercent, heightPercent );
    minVizUnitFontSize = heightPercent * 4.0f;
  }
  
  // Private methods -----------------------------------------------------------
  private void initResources()
  {
    if ( ROMatrix.roMatrixXMLTemplate == null )
      ROMatrix.roMatrixXMLTemplate = getMatrixResource( "roMatrixTemplate.xml" );
    
    if ( ROMatrix.roMatrixFunctions == null )
      ROMatrix.roMatrixFunctions = getMatrixResource( "roMatrixScripts.js" );
  }
  
  private void initSVGDoc()
  {
    SVGDocGenerator docGen = new SVGDocGenerator();
    
    svgDoc = docGen.createDoc( UUID.randomUUID() );
    svgDoc.addScriptFunction( ROMatrix.roMatrixFunctions );
    
    createHighlightGradients();
  }
  
  private void createHighlightGradients()
  {
    if ( gradientURLs == null )
    {
      // Create gradient URL map
      gradientURLs = new HashMap<Integer, String>();
      for ( int i = 0; i < 30; i++ )
        gradientURLs.put ( i, "cellHilite" + i );
    }
    
    if (gradientDefinitions == null)
    {
      // Create gradient definitions
      gradientDefinitions = new HashMap<Integer, ISVGDefinition>();
      
      List<Entry<String,String>> gradList = new ArrayList<Entry<String,String>>();
      SimpleEntry<String,String> bkgndStop = new SimpleEntry<String,String>( "#333333", "75%" );
      SimpleEntry<String,String> colourStop;
      
      ISVGDocUtility util = svgDoc.getDocUtility();
      
      for ( int i = 0; i < 30; i++ )
      {
        String url = gradientURLs.get( i );
        gradList.clear();

        colourStop = new SimpleEntry<String,String>( colourIndices[i], "0%" );
        gradList.add( colourStop );
        gradList.add( bkgndStop );

        ISVGDefinition grad = util.createGradient( url, gradList );
        ISVGElement el = (ISVGElement) grad;
        el.setAttribute( "x1", "50%" );
        el.setAttribute( "x2", "50%" );
        el.setAttribute( "y1", "0%" );
        el.setAttribute( "y2", "100%" );
        
        //svgDoc.addDefinition( grad );
        gradientDefinitions.put(i, grad);
      }
    }
    
    for ( int i = 0; i < 30; i++ )
    {
        ISVGDefinition grad = gradientDefinitions.get(i);
        svgDoc.addDefinition( grad );
    }
    
  }
  
  private String getMatrixResource( String resourceFile )
  {
    String resource = "";
    ClassLoader cl = getClass().getClassLoader();
    InputStream is = cl.getResourceAsStream( resourceFile );
    
    if ( is != null )
    {
      StringBuilder sb = new StringBuilder();
      BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
      String input;
      try
      {
        do
        {
          input = br.readLine();
          if ( input != null ) resource += input;
        }
        while ( input != null );
        is.close();
      }
      catch ( IOException ioe ) {}
    }
    
    return resource;
  }
  
  private void renderMatrix( ISVGElGroup parent )
  {
    if ( !roIDEntityMap.isEmpty() )
    {
      // Remove all known entities from cells
      for ( MatCell cell : matrixMap.values() )
        cell.clearEntities();
      
      // Re-populate the cells with new entities
      for ( ROMRiskEntity re : roIDEntityMap.values() )
      {
        IROMRiskEntity.eROMProb prob = re.getProbability();
        List<IROMRiskEntity.eROMImpact> impacts = re.getImpacts();
        for ( IROMRiskEntity.eROMImpact imp : impacts )
        {
          Integer index = getReMatIndex( prob, imp );
          MatCell cell = matrixMap.get( index );
          cell.addEntity( re );
        }
      }
      
      // Render each cell
      for ( Integer index : matrixMap.keySet() )
      {
        // Calculate cell position
        MatCell cell = matrixMap.get( index );
        int x = index % 6;
        int y = index / 6;
        
        cell.render( parent,
                     new Point2D.Float( x * cellWidth, y * cellHeight ) );
      }
    }
  }
  
  private Integer getReMatIndex( IROMRiskEntity.eROMProb prob,
                                 IROMRiskEntity.eROMImpact imp )
  {
    Integer index = new Integer(0);
    
    switch ( prob )
    {
      //   VHIGH  : 0
      case HIGH   : index += 6; break;
      case MEDIUM : index += 12; break;
      case LOW    : index += 18; break;
      case VLOW   : index += 24; break;
    }
    
    switch ( imp )
    {
      //   NEG_HIGH   : 0
      case NEG_MEDIUM : index += 1; break;
      case NEG_LOW    : index += 2; break;
      case POS_LOW    : index += 3; break;
      case POS_MEDIUM : index += 4; break;
      case POS_HIGH   : index += 5; break;
    }
    
    return index;
  }
  
  private boolean isEntitySetHighlighted( Set<UUID> entSet )
  {
    boolean highlight = false;
    
    if ( roEntityHighlights != null && entSet != null )
    {
      for ( UUID entityID : entSet )
      {
        if ( roEntityHighlights.contains(entityID) )
        {
          highlight = true;
          break;
        }
      }
    }
    
    return highlight;
  }

  // Private cell render class -------------------------------------------------
  private class MatCell
  {
    private HashMap<UUID, ROMRiskEntity> roEntities;
    private String highLightColour;
    
    public MatCell()
    {
      roEntities = new HashMap<UUID, ROMRiskEntity>();
      highLightColour = "#000000";
    }
    
    public void clearEntities()
    { roEntities.clear(); }
    
    public void addEntity( ROMRiskEntity re )
    { roEntities.put( re.getRiskID(), re ); }
    
    public void setHighLightColour( String col )
    { highLightColour = col; }
    
    public void render( ISVGElGroup parent,
                        Point2D.Float tl )
    {  
      if ( roEntities.size() > 0 )
      {
        // Highlight this cell if required
        if ( isEntitySetHighlighted( roEntities.keySet() ) )
          renderHighlight( parent, tl );
        
        renderRiskIndices( parent, tl );
        
        renderSummary( parent, tl );
        
      }
    }
    
    // Private methods ---------------------------------------------------------
    private void renderHighlight( ISVGElGroup parent, Point2D.Float tl )
    {
      ISVGRect rect = (ISVGRect) parent.addVector( ISVGElGroup.eShapeType.RECT );
      rect.setTL( tl );
      rect.setDimension( new Point2D.Float(cellWidth, cellHeight) );

      ISVGElement el = (ISVGElement) rect;
      el.setAttribute( "fill", highLightColour );
    }
    
    private void renderRiskIndices( ISVGElGroup parent, Point2D.Float tl )
    {
      if ( roEntities.size() > 0 )
      {
        // Get current entities and display some of them
        Point2D.Float insetTL = new Point2D.Float( tl.x + 1.0f, tl.y + 1.0f );
        int maxXIndices = (int)  (cellWidth - 2.0f) / (int) minVizUnit.x;
        int maxYIndices = (int) (cellHeight - 2.0f) / (int) minVizUnit.y;
        
        Collection<ROMRiskEntity> entities = roEntities.values();
        Iterator<ROMRiskEntity> reIt = entities.iterator();
        Point2D.Float reTL = new Point2D.Float();
        String fontSize = minVizUnitFontSize + "%";
        float reTLYOffset = minVizUnit.y / 2.0f;
        
        // Render as many indices as possible
        for ( int y = 0; y < maxYIndices; y++ )
          for ( int x = 0; x < maxXIndices; x++ )
          {
            if ( reIt.hasNext() )
            {
              ROMRiskEntity re = reIt.next();
              
              ISVGText reText = (ISVGText) parent.addVector( ISVGElGroup.eShapeType.TEXT );
              reText.setFontMetrics( "Arial", fontSize, "#DDDDDD" );
              reText.setValue( "RO" + re.getRiskIndex() );
              
              ISVGVector vec = (ISVGVector) reText;
              reTL.x = insetTL.x + ( x * minVizUnit.x );
              reTL.y = insetTL.y + reTLYOffset + ( y * minVizUnit.y );
              vec.setPosition( new Point2D.Float(reTL.x, reTL.y) );
              
              ISVGElement el = (ISVGElement) vec;
              el.setAttribute( "onclick", "onMatrixAlert(this)" );
            }
            else break;
          }
      }
    }
    
    private void renderSummary( ISVGElGroup parent, Point2D.Float tl )
    {
      // Total risk count rectangle backdrop
      Point2D.Float totBkDim = new Point2D.Float( cellWidth * 0.4f,
                                                  cellHeight * 0.3f );
      
      Point2D.Float totBkTL  = new Point2D.Float( tl.x + (cellWidth - totBkDim.x),
                                                  tl.y + (cellHeight - totBkDim.y) );
      
      ISVGRect totRect = (ISVGRect) parent.addVector( ISVGElGroup.eShapeType.RECT );
      totRect.setTL( totBkTL );
      totRect.setDimension( totBkDim );
      ISVGElement el = (ISVGElement) totRect;
      el.setAttribute( "fill" , "url(#roSumFillHoriz)" ); // See resource template for URL
      
      // Total risk count text
      int riskCount = roEntities.size();
      
      ISVGText txt = (ISVGText) parent.addVector( ISVGElGroup.eShapeType.TEXT );
      txt.setFontMetrics( "Arial", "30%", "#EEEEEE" );
      txt.setValue( Integer.toString(riskCount) );
      
      float xOffset = riskCount > 9 ? 0.2f : 0.4f;
      Point2D.Float txtPos = new Point2D.Float( totBkTL.x + ( totBkDim.x * xOffset ), 
                                                totBkTL.y + ( totBkDim.y * 0.7f) );
      
      ISVGVector vec = (ISVGVector) txt;
      vec.setPosition( txtPos );
    }
  }
}