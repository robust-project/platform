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
//      Created Date :          2012-08-07
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.client;

import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vegard Engen
 */
public class WS2LocalClassConverter
{
    private static final Logger log = LoggerFactory.getLogger(WS2LocalClassConverter.class);
    
    public static uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.spec.Status getStatus(uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.Status statusWS)
    {
        if (statusWS == null)
        {
            log.debug("The WS Status object is NULL, which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.spec.Status status = new uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.spec.Status();
        status.setSuccessful(statusWS.isSuccessful());
        status.setMetaData(statusWS.getMetaData());
        
        return status;
    }
    
    public static uk.ac.soton.itinnovation.robust.cat.common.datastructures.StreamDetails getStreamDetails(uk.ac.soton.itinnovation.robust.cat.evaluationengine.ws.gen.StreamDetails streamDetailsWS)
    {
        if (streamDetailsWS == null)
        {
            log.debug("The WS StreamDetails object is NULL, which is what's being returned...");
            return null;
        }
        
        uk.ac.soton.itinnovation.robust.cat.common.datastructures.StreamDetails streamDetails = new uk.ac.soton.itinnovation.robust.cat.common.datastructures.StreamDetails();
        streamDetails.setStreamName(streamDetailsWS.getStreamName());
        try{
            streamDetails.setStreamURI(new URI(streamDetailsWS.getStreamURI()));
        } catch (URISyntaxException ex) {
            log.error("Caught a URI syntax exception when trying to create a URI object from the the stream URI string of the WS object", ex);
        } catch (NullPointerException npe) {
            log.error("Caught a null pointer exception when trying to create a URI object from the the stream URI string of the WS object", npe);
        }
        
        return streamDetails;
    }
}
