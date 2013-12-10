/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2013
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
//      Created By :            Ken Meacham
//      Created Date :          25 Mar 2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.welcome.subViewEvents;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catUIComponents.utility.UIChangeData;

public class CommunityCreateViewCD extends UIChangeData {

    private String name;
    private String id;
    private String uri;
    private Boolean isStream;
    private String streamName;

    public CommunityCreateViewCD(String name, String id, String uri, Boolean isStream, String streamName) {
        super();

        this.name = name;
        this.id = id;
        this.uri = uri;
        this.isStream = isStream;
        this.streamName = streamName;

        dataChanged = true;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public Boolean getIsStream() {
        return isStream;
    }

    public String getStreamName() {
        return streamName;
    }

    @Override
    public void reset() {
        super.reset();

        name = "";
        id = "";
        uri = "";
        isStream = false;
        streamName = "";
    }
}