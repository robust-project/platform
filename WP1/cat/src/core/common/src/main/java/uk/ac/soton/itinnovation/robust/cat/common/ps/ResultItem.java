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
//      Created By :            Vegard Engen
//      Created Date :          2012-01-11
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.ps;

import java.io.Serializable;

/**
 *
 * @author Vegard Engen
 */
public class ResultItem implements Serializable
{
    private String name;
    private Double probability;
    private String currentObservation;
    
    public ResultItem(){}
    
    public ResultItem(String name, Double prob)
    {
        this.name = name;
        this.probability = prob;
    }
    
    public ResultItem(String name, Double prob, String obs)
    {
        this.name = name;
        this.probability = prob;
        this.currentObservation = obs;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the probability
     */
    public Double getProbability() {
        return probability;
    }

    /**
     * @param probability the probability to set
     */
    public void setProbability(Double probability) {
        this.probability = probability;
    }

    /**
     * @return the currentObservation
     */
    public String getCurrentObservation()
    {
        return currentObservation;
    }

    /**
     * @param currentObservation the currentObservation to set
     */
    public void setCurrentObservation(String currentObservation)
    {
        this.currentObservation = currentObservation;
    }
    
}
