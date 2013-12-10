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

import weka.core.Instance;
import weka.core.Instances;

import java.util.Date;
import java.util.TreeMap;

public class PredictionDataset {

    public Instances trainSplit;
    public Instances testSplit;
    public TreeMap<Date,Instance> testSplitArray;
    public Date testFromDate;
    public String communityID;


    public PredictionDataset(Instances trainSplit, Instances testSplit, TreeMap<Date, Instance> testSplitArray, Date testFromDate, String communityID) {
        this.trainSplit = trainSplit;
        this.testSplit = testSplit;
        this.testSplitArray = testSplitArray;
        this.testFromDate = testFromDate;
        this.communityID = communityID;
    }

    public PredictionDataset(String communityID,Date testFromDate) {
        this.testFromDate = testFromDate;
        this.communityID = communityID;
    }

    public PredictionDataset() {
    }


    public Instances getTrainSplit() {
        return trainSplit;
    }

    public void setTrainSplit(Instances trainSplit) {
        this.trainSplit = trainSplit;
    }

    public Instances getTestSplit() {
        return testSplit;
    }

    public void setTestSplit(Instances testSplit) {
        this.testSplit = testSplit;
    }

    public TreeMap<Date, Instance> getTestSplitArray() {
        return testSplitArray;
    }

    public void setTestSplitArray(TreeMap<Date, Instance> testSplitArray) {
        this.testSplitArray = testSplitArray;
    }

    public Date getTestFromDate() {
        return testFromDate;
    }

    public void setTestFromDate(Date testFromDate) {
        this.testFromDate = testFromDate;
    }

    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    @Override
    public String toString() {
        return "PredictionDataset{" +
                "trainSplit=" + trainSplit +
                ", testSplit=" + testSplit +
                ", testSplitArray=" + testSplitArray +
                ", testFromDate=" + testFromDate +
                ", communityID='" + communityID + '\'' +
                '}';
    }
}



