package pl.swmind.robust.webapp.user.role.chart.impl;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.vaadin.vaadinvisualizations.PieChart;
import pl.swmind.robust.webapp.user.role.chart.Chart;
import pl.swmind.robust.webapp.user.role.form.FieldFactory;
import pl.swmind.robust.webapp.user.role.validation.impl.CoverageValidations;
import pl.swmind.robust.ws.behaviouranalysis.Composition;
import pl.swmind.robust.ws.behaviouranalysis.HealthIndicatorFeatures;
import pl.swmind.robust.ws.behaviouranalysis.RobustBehaviourServiceException_Exception;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;
import static pl.swmind.robust.webapp.user.role.form.FormFields.DATE;

/**
 * Role coverage pie chart. Presents pie chart with legend and health indicators at requested date. <br>
 * <p/>
 * Creation date: 18/09/12<br>
 *
 * @author dalo (<a href="http://www.softwaremind.pl">SoftwareMind</a>)
 */
@Component
@Scope("prototype")
public class PieCoverageChart extends Chart {
    private static final org.apache.log4j.Logger logger = Logger.getLogger(PieCoverageChart.class);
    private static final int CHART_WIDTH = 1000;
    private static final int CHART_HEIGHT = CHART_WIDTH/2;
    private @Value("${pieChart.BackgroundColor}") String BACKGROUND_COLOUR;
    private static final String BACKGROUND_COLOR_OPTION = "backgroundColor";
    private PieChart pieChart;
    private List<String> chartLabels = new LinkedList<String>();
    private Label userCountLabel;
    private Label avgCCLabel;
    private Label churnRateLabel;
    private Label seedsPropLabel;
    private static final String caption = "Choose date for role coverage";
    private static final String description = "Role coverage";

    public void initChart(){
        init();
        this.validations = new CoverageValidations(this,false);
        try {
            this.fieldFactory = new FieldFactory();
        } catch (Exception e) {
            getWindow().showNotification("[Error] Problem with Form creation");
            logger.error(e.getMessage());
        }
        form.setFormFieldFactory(fieldFactory);

        setStyleName(styleNameLocal);
        setDescription(description);

        pieChart = new PieChart();
        pieChart.setStyleName(styleNameLocal);
        pieChart.setSizeFull();
        pieChart.setOption("width", CHART_WIDTH);
        pieChart.setOption("height", CHART_HEIGHT);
        pieChart.setOption("is3D", true);
        pieChart.setOption(BACKGROUND_COLOR_OPTION, BACKGROUND_COLOUR);

        form.setVisibleItemProperties(
            Arrays.asList(new String[]{DATE.getFieldName()})
        );

        VerticalLayout healthIndicators = new VerticalLayout();

        churnRateLabel = new Label();
        healthIndicators.addComponent(churnRateLabel);

        seedsPropLabel = new Label();
        healthIndicators.addComponent(seedsPropLabel);

        userCountLabel = new Label();
        healthIndicators.addComponent(userCountLabel);

        avgCCLabel = new Label();
        healthIndicators.addComponent(avgCCLabel);

        HorizontalLayout healthIndsWithChart = new HorizontalLayout();
        healthIndsWithChart.addComponent(healthIndicators);
        healthIndsWithChart.setComponentAlignment(healthIndicators,Alignment.MIDDLE_LEFT);
        healthIndsWithChart.setStyleName(styleNameLocal);

        VerticalLayout pieLayout = new VerticalLayout();
        pieLayout.addComponent(pieChart);
        pieLayout.setStyleName(styleNameLocal);
        healthIndsWithChart.addComponent(pieLayout);

        formLayout.addComponent(healthIndsWithChart);
    }

    @Override
    protected void handleRequest() {
        logger.info("Filling chart for " + formRequest.getUserName() + " at: " + formRequest.getDate() );

        Composition composition = null;
        try {
            composition = behaviourAnalysisClient.getComposition(platformId, communityId, formRequest.getDate());
        } catch (RobustBehaviourServiceException_Exception e) {
            getWindow().showNotification("Community composition was not computed");
            logger.error(e.getMessage());
            return;
        }
        List<Composition.IdToRoleMapping.Entry> idToRoleMapping = composition.getIdToRoleMapping().getEntry();
        List<Composition.RoleCoverage.Entry> coverage = composition.getRoleCoverage().getEntry();

        logger.info("Got " + coverage.size() + " roles and " + idToRoleMapping.size() + " mappings");

        for(String label: chartLabels){
            pieChart.remove(label);
        }

        chartLabels = new LinkedList<String>();

        for(Composition.RoleCoverage.Entry entry: coverage){
            String label = getLabelFrom(idToRoleMapping,entry.getKey());
            logger.info(format("Adding %s with id %d and coverage %f to chart", label, entry.getKey(),entry.getValue()));
            pieChart.add(label, entry.getValue());
            chartLabels.add(label);
        }

        logger.info("Chart filled.");

        HealthIndicatorFeatures hiFeatures = null;

        try {
            hiFeatures = behaviourAnalysisClient.getHealthIndicators(platformId, communityId, formRequest.getDate());
        } catch (RobustBehaviourServiceException_Exception e) {
            //getWindow().showNotification("Health indicators were not computed");
            logger.error(e.getMessage());
            return;
        }

        logger.info(format("Got health indicators: (%f, %f, %f, %d)",
            hiFeatures.getAvgCC(), hiFeatures.getChurnRate(), hiFeatures.getSeedToNonSeedsProp(), hiFeatures.getUserCount()));

        churnRateLabel.setValue(format("Loyalty: %.2f",hiFeatures.getChurnRate()));
        seedsPropLabel.setValue(format("Activity: %.2f",hiFeatures.getSeedToNonSeedsProp()));
        userCountLabel.setValue(format("Participation: %d",hiFeatures.getUserCount()));
        avgCCLabel.setValue(format("Social Capital: %.2f",hiFeatures.getAvgCC()));
    }

    private String getLabelFrom(List<Composition.IdToRoleMapping.Entry> idToRoleMapping, int key){
        for(Composition.IdToRoleMapping.Entry entry: idToRoleMapping){
            if(entry.getKey().equals(key)){
                return entry.getValue();
            }
        }
        return "";
    }
}
