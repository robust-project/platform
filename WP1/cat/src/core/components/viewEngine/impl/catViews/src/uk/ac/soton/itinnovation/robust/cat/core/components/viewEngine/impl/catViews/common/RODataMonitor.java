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
//      Created Date :          21 Mar 2013
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.impl.catViews.common;

import java.util.List;
import java.util.UUID;
import org.apache.log4j.Logger;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.catViews.IRODataChangedListener;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.riskmodel.Community;
import uk.ac.soton.itinnovation.robust.riskmodel.DataSource;
import uk.ac.soton.itinnovation.robust.riskmodel.Objective;
import uk.ac.soton.itinnovation.robust.riskmodel.Risk;

/**
 *
 * @author kem
 */
public class RODataMonitor extends UFAbstractEventManager implements IRODataChangedListener {
    
    private static RODataMonitor roDataMonitor = null;
    private static Logger log = Logger.getLogger(RODataMonitor.class);
    
    public static RODataMonitor getInstance() {
        log.debug("RODataMonitor getInstance() issued");
        if (roDataMonitor == null) {
            log.debug("Creating RODataMonitor instance");
            roDataMonitor = new RODataMonitor();
        }

        return roDataMonitor;
    }

    @Override
    public void onRiskAdded(Risk risk) {
        log.debug("RODataMonitor onRiskAdded()");
        List<IRODataChangedListener> listeners = getListenersByType();
        for (IRODataChangedListener listener : listeners) {
            listener.onRiskAdded(risk);
        }
    }

    @Override
    public void onRiskUpdated(Risk risk) {
        log.debug("RODataMonitor onRiskUpdated()");
        List<IRODataChangedListener> listeners = getListenersByType();
        for (IRODataChangedListener listener : listeners) {
            listener.onRiskUpdated(risk);
        }
    }

    @Override
    public void onRiskDeleted(Risk risk) {
        log.debug("RODataMonitor onRiskDeleted()");
        List<IRODataChangedListener> listeners = getListenersByType();
        for (IRODataChangedListener listener : listeners) {
            listener.onRiskDeleted(risk);
        }
    }

    @Override
    public void onDataSourceAdded(DataSource dataSource) {
        log.debug("RODataMonitor onDataSourceAdded()");
        List<IRODataChangedListener> listeners = getListenersByType();
        for (IRODataChangedListener listener : listeners) {
            listener.onDataSourceAdded(dataSource);
        }
    }

    @Override
    public void onDataSourceUpdated(UUID dataSourceID) {
        log.debug("RODataMonitor onDataSourceUpdated()");
        List<IRODataChangedListener> listeners = getListenersByType();
        for (IRODataChangedListener listener : listeners) {
            listener.onDataSourceUpdated(dataSourceID);
        }
    }

    @Override
    public void onDataSourceDeleted(UUID dataSourceID) {
        log.debug("RODataMonitor onDataSourceDeleted()");
        List<IRODataChangedListener> listeners = getListenersByType();
        for (IRODataChangedListener listener : listeners) {
            listener.onDataSourceDeleted(dataSourceID);
        }
    }

    @Override
    public void onCommunityAdded(UUID communityID) {
        log.debug("RODataMonitor onCommunityAdded()");
        List<IRODataChangedListener> listeners = getListenersByType();
        for (IRODataChangedListener listener : listeners) {
            listener.onCommunityAdded(communityID);
        }
    }

    @Override
    public void onCommunityUpdated(UUID communityID) {
        log.debug("RODataMonitor onCommunityUpdated()");
        List<IRODataChangedListener> listeners = getListenersByType();
        for (IRODataChangedListener listener : listeners) {
            listener.onCommunityUpdated(communityID);
        }
    }

    @Override
    public void onCommunityDeleted(UUID communityID) {
        log.debug("RODataMonitor onCommunityDeleted()");
        List<IRODataChangedListener> listeners = getListenersByType();
        for (IRODataChangedListener listener : listeners) {
            listener.onCommunityDeleted(communityID);
        }
    }

    @Override
    public void onObjectiveAdded(UUID communityID, UUID objectiveID) {
        log.debug("RODataMonitor onObjectiveAdded()");
        List<IRODataChangedListener> listeners = getListenersByType();
        for (IRODataChangedListener listener : listeners) {
            listener.onObjectiveAdded(communityID, objectiveID);
        }
    }

    @Override
    public void onObjectiveUpdated(UUID communityID, UUID objectiveID) {
        log.debug("RODataMonitor onObjectiveUpdated()");
        List<IRODataChangedListener> listeners = getListenersByType();
        for (IRODataChangedListener listener : listeners) {
            listener.onObjectiveUpdated(communityID, objectiveID);
        }
    }

    @Override
    public void onObjectiveDeleted(UUID communityID, UUID objectiveID) {
        log.debug("RODataMonitor onObjectiveDeleted()");
        List<IRODataChangedListener> listeners = getListenersByType();
        for (IRODataChangedListener listener : listeners) {
            listener.onObjectiveDeleted(communityID, objectiveID);
        }
    }

}
