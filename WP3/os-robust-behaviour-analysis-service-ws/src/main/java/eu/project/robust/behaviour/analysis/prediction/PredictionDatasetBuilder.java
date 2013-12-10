/**
 *Copyright 2013 Knowledge Media Institute
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
package eu.project.robust.behaviour.analysis.prediction;

import java.sql.Connection;
import java.util.Date;

public abstract class PredictionDatasetBuilder {

    protected Connection connection;


    public PredictionDatasetBuilder() {
    }

    /*
    * Builds a prediction dataset for predicting the health change of the community from the previous timestep
    */
    public abstract PredictionDataset buildClassificationDatasetOnDate(String communityID, int healthIndicator, Date date) throws Exception;


    /*
     * Builds a prediction dataset for predicting the health change of the community within a given time window
     */
    public abstract PredictionDataset buildClassificationDatasetInWindow(String communityID, int healthIndicator, Date startDate, Date endDate) throws Exception;


    /*
     * Builds a prediction dataset for predicting the health indicator of the community at the next time step
     */
    public abstract PredictionDataset buildRegressionDatasetOnDate(String communityID, int healthIndicator, Date date) throws Exception;

    /*
     * Builds a prediction dataset for predicting the health indicator of the community within a given time window
     */
    public abstract PredictionDataset buildRegressionDatasetInWindow(String communityID, int healthIndicator, Date startDate, Date endDate) throws Exception;


    public abstract void close() throws Exception;

}
