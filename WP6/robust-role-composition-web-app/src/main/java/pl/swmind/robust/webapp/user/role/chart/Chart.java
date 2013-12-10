package pl.swmind.robust.webapp.user.role.chart;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import pl.swmind.robust.webapp.user.role.form.FormRequest;
import pl.swmind.robust.webapp.user.role.validation.Validations;
import pl.swmind.robust.webapp.ws.client.BehaviourAnalysisServiceClient;

import java.util.Random;

/**
 * TODO: Type description here! <br>
 * <p/>
 * Creation date: 17/10/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
public abstract class Chart extends VerticalLayout {
    protected Validations validations;
    protected Random random;
    protected pl.swmind.robust.webapp.user.role.form.FieldFactory fieldFactory;
    protected FormRequest formRequest;
    protected Form form = new Form();
    protected VerticalLayout formLayout;
    protected String platformId;
    protected String communityId;
    protected String styleNameLocal;
    protected @Autowired BehaviourAnalysisServiceClient behaviourAnalysisClient;

    protected void init(){
        this.random = new Random();
        this.formLayout = new VerticalLayout();
        formRequest = new FormRequest();

        setStyleName(styleNameLocal);
        setSizeFull();
        form.setWriteThrough(false);
        form.setInvalidCommitted(false);
        form.setStyleName(styleNameLocal);

        BeanItem<FormRequest> userPathRoleRequestBeanItem = new BeanItem<FormRequest>(formRequest);
        form.setItemDataSource(userPathRoleRequestBeanItem);
        formLayout.addComponent(form);
        formLayout.setStyleName(styleNameLocal);
        addComponent(formLayout);

        com.vaadin.ui.Button apply = getApplyButton();
        HorizontalLayout applyButtonLayout = new HorizontalLayout();
        applyButtonLayout.setStyleName(styleNameLocal);
        applyButtonLayout.addComponent(apply);
        applyButtonLayout.setComponentAlignment(apply, Alignment.TOP_RIGHT);
        form.getFooter().addComponent(applyButtonLayout);
    }

    public abstract void initChart();
    protected abstract void handleRequest();

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public void setStyleNameLocal(String styleNameLocal) {
        this.styleNameLocal = styleNameLocal;
    }

    protected Button getApplyButton(){
        return new com.vaadin.ui.Button(
            "Apply",
            new com.vaadin.ui.Button.ClickListener() {
                public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                    form.commit();

                    if(validations.validateRequest(formRequest)){
                        handleRequest();
                    }
                }
            }
        );
    }
}
