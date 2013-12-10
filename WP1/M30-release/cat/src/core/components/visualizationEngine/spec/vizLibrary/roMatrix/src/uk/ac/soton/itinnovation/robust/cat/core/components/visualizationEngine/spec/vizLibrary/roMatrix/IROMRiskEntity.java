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
//      Created By :            sgc
//      Created Date :          18-Jan-2012
//      Created for Project :   robust
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.visualizationEngine.spec.vizLibrary.roMatrix;



/**
 * IROMRiskEntity is a light-weight, transient representation of a risk/opportunity
 * that is being presented in the graphical matrix. The IROMatrix creates these
 * to be configured by the client - it will then render each instance appropriately.
 * 
 * @author sgc
 */
public interface IROMRiskEntity
{
  /**
   * eROMProb - the current probability categories supported by the matrix
   */
  enum eROMProb   { VHIGH, HIGH, MEDIUM, LOW, VLOW };
  
  /**
   * eROMImpact - the current impact levels supported by the matrix
   */
  enum eROMImpact { NEG_HIGH, NEG_MEDIUM, NEG_LOW,
                    POS_LOW,  POS_MEDIUM, POS_HIGH };
  
  /**
   * Title of the risk entity (not currently rendered in the matrix)
   * @param title - risk title (keep short if possible)
   */
  void setTitle( String title );
  
  /**
   * High level description of the risk entity (not currently rendered in the matrix)
   * @param desc - simple description of the risk
   */
  void setDescription( String desc );
  
  /**
   * Numeric index of the risk - this is rendered in the matrix with an 'R' pre-fix
   * starting in the top-left hand corner
   * @param index - numerical value of the index
   */  
  void setRiskIndex( int index );
  
  /**
   * Sets the probability category of the risk (matrix positions accordingly)
   * @param prob - see eROMProb
   */
  void setProbCategory( eROMProb prob );
  
  /**
   * Associates an impact level with the risk (there may be more than one, but
   * only one at each impact level). Appropriate matrices are filled.
   * @param impact - see eROMImpact
   */
  void addImpactLevel( eROMImpact impact );
}
