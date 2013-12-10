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
//      Created Date :          2012-10-25
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.spec;

import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

/**
 *
 * @author Vegard Engen
 */
public class PredictorConnectionDetails implements Serializable
{
    private UUID predictorUUID;
    private URI serviceURI; // not including ?wsdl
    private String serviceTargetNamespace;
    private String serviceName;
    private String servicePortName;

    public PredictorConnectionDetails(){}
    
    public PredictorConnectionDetails(PredictorConnectionDetails pcd)
    {
        if (pcd == null)
            return;
        
        this.predictorUUID = pcd.getPredictorUUID();
        this.serviceURI = pcd.getServiceURI();
        this.serviceTargetNamespace = pcd.getServiceTargetNamespace();
        this.serviceName = pcd.getServiceName();
        this.servicePortName = pcd.getServicePortName();
    }
    
    public PredictorConnectionDetails(UUID predictorUUID, URI serviceURI, String serviceTargetNamespace, String serviceName, String servicePortName)
    {
        this.predictorUUID = predictorUUID;
        this.serviceURI = serviceURI;
        this.serviceTargetNamespace = serviceTargetNamespace;
        this.serviceName = serviceName;
        this.servicePortName = servicePortName;
    }
    
    /**
     * @return the predictorUUID
     */
    public UUID getPredictorUUID()
    {
        return predictorUUID;
    }

    /**
     * @param predictorUUID the predictorUUID to set
     */
    public void setPredictorUUID(UUID predictorUUID)
    {
        this.predictorUUID = predictorUUID;
    }

    /**
     * @return the serviceURI
     */
    public URI getServiceURI()
    {
        return serviceURI;
    }

    /**
     * @param serviceURI the serviceURI to set
     */
    public void setServiceURI(URI serviceURI)
    {
        this.serviceURI = serviceURI;
    }

    /**
     * @return the serviceTargetNamespace
     */
    public String getServiceTargetNamespace()
    {
        return serviceTargetNamespace;
    }

    /**
     * @param serviceTargetNamespace the serviceTargetNamespace to set
     */
    public void setServiceTargetNamespace(String serviceTargetNamespace)
    {
        this.serviceTargetNamespace = serviceTargetNamespace;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName()
    {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    /**
     * @return the servicePortName
     */
    public String getServicePortName()
    {
        return servicePortName;
    }

    /**
     * @param servicePortName the servicePortName to set
     */
    public void setServicePortName(String servicePortName)
    {
        this.servicePortName = servicePortName;
    }
}
