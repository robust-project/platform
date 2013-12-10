package pl.swmind.robust.webapp.integration;

import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFController;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFModelRO;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.mvc.IUFView;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractEventManager;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFAbstractTypeException;
import uk.ac.soton.itinnovation.robust.cat.core.components.viewEngine.spec.uif.types.UFException;

import java.io.Serializable;
import java.util.UUID;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 17/10/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public class RoleCompositionController extends UFAbstractEventManager implements Serializable, IUFController {
    private RoleCompositionView roleCompositionView = new RoleCompositionView();

    public IUFModelRO getModel(UUID uuid) throws UFException {
        throw new UFAbstractTypeException("operation not supported");
    }

    public IUFView getView(IUFModelRO iufModelRO) throws UFException {
        return roleCompositionView;
    }

    public IUFView getView(String communityId) {
        return roleCompositionView;
    }

    public IUFView getView(String platformId, String communityId, String styleName){
        roleCompositionView.updateView(platformId, communityId, styleName);
        return roleCompositionView;
    }
}

