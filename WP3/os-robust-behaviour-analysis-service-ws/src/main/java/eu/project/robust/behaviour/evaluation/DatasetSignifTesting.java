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
package eu.project.robust.behaviour.evaluation;

import weka.core.Instances;

import java.io.Serializable;

public class DatasetSignifTesting implements Serializable {

    public Instances train;
    public Instances[] valid;
    public Instances[] test;

    public DatasetSignifTesting(Instances train, Instances[] valid, Instances[] test) {
        this.train = train;
        this.valid = valid;
        this.test = test;
    }

    public String toString() {
        return new String("train size = " + this.train.numInstances() +
                "\nvalid size = " + this.valid.length +
                "\ntest size = " + this.test.length);
    }
}
