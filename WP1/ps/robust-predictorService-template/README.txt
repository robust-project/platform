Predictor Service Template README
=================================

Please note that this template is suitable for predictor services for
streaming data as well as non-streaming data. The management code for this template
project is based on events and makes a distinction between jobs running on streaming data.

General project set-up
----------------------

To create and configure a new Predictor Service based on this template project,
you should change the following to "personalise" your service (at a minimum):

  1: pom.xml file, which is in the same directory as this file
     - groupId
	 - artifactId
	 - name
	 - finalName in the build clause
  2: refactor source package name
  3: application-context.xml file, which is in ./src/main/resources
     - update bean class according to your package name
	 - jaxws endpoint address if you wish - it can probably stay the same
  4: change name of your service in ./src/main/webapp/WEB-INF

The next step is to change the actual service implementation. In the Template project
there is a simple job management component, which you can use to get started.

OBS: there is no persistence of the job management component provided with this
     template project, so if the service crashes, all the jobs saved in memory will
     be lost, which is not a good thing. You may want to persist the job data in
	 a database or serialise the data to disk.


PredictorServiceImpl
--------------------

The main class to consider in the first instance is 'PredictorServiceImpl' which
implements the IPredictorService interface with all its required methods. There is 
one methods to consider here:

  generatePredictorServiceDescription()

This method is important as the service should give a description of the
events and configurations possible. A PredictorServiceDescription object
is created, which is then returned in a service call getPredictorServiceDescription()

The other methods in the PredictorServiceImpl just passes things on to the
PredictorJobManager class for creating evaluation jobs, etc.


PredictorJobManager
-------------------

The PredictorJobManager class manages two types of jobs; streaming and non-streaming
jobs. Each of which have a separate queue and the number allowed jobs to run in
parallel can be controlled via config file parameters. The config file is in:

  ./src/main/resources/service.properties

This config file is read automatically when the service is started up, via the
following method in the manager class.

  getConfigs()
  
By default, there are three parameters with the following values:

  maxRunningJobs = 1
  maxRunningStreamingJobs = 2

When a new evaluation job is created (typically by the evaluation engine), it
is put in a queue. If the size of the respective queue (streaming or non-streaming), 
is not greater than the maximum number of running jobs, it will be started. An
evaluation job is started by instantiating an Evaluator object that will run as a
thread, allowing the predictor service to remain responsive to any other requests.
The PredictorJobManager subscribes to events from the Evaluator thread, so it does
not have to poll for status. It can receive two types of events:

  1: new evaluation result
  2: error

The PredictorJobManager will communicate the new result or error to the evaluation
engine and will update its job management collections accordingly. That is, cleaning
up finished or erroronous jobs and starting new jobs that may be queued up.

An ExampleEvaluator implementation has been provided with this template project, which
is discussed further below. First, there are a few places in the PredictorJobManager
class where you'll need to make some changes to your service!

  1: isJobConfigValid(..) [call from createNewJob(..) in PredictorServiceImpl]
  2: startJobFromQueue()
  3: isEvaluationResultValid(..)

So, first you should do some validation of the job configuration you've received.
Check that you get an event you know about and that required configuration parameters
have been set with valid values.

Second, you need in the startJobFromQueue() method, you need to change the ExampleEvaluator
for your own implementation class. See the next section for more details on this.

Finally, you should validate that the EvaluationResult is OK. But this is perhaps not
necessary since you're the one controlling the code that creates the EvaluationResult
in the first place! So consider it optional :)

  
ExampleEvaluator
----------------

This is the class that more tightly integrates with any analysis component that
should be wrapped up into the service. Although only one class here, there may
be more in your case that gets integrated.

So, best suggestion is to make a new implementation class, for which you can use
this class as a template. As long as you implement the IEvaluator interface,
your implementation class will be compatible with the PredictorJobManager.

Your implementation class needs to:

  1: start a job based on a PredictorServiceJobConfig object, which should have
     been validated already in the PredictorJobManager (see above).
  2: return the status of the job
  3: notify of an evaluation result when the job's finished (the PredictorJobManager
     will add itself as a listener)
  4: cancel a running job

In #1 you'll need to extract the configuration from the PredictorServiceJobConfig
object and pass this on to your component to start the evaluation process.

#2 should be fairly obvious when looking at the ExampleEvaluator. There probably
are these states you'd end up using: EVALUATING, RESULT_AVAILABLE, FINISHED or ERROR.

The EvaluationResult created in #3 should include at a minimum the information
shown in the ExampleEvaluator class. You can add some meta data too, but all other
parameters of the EvaluationResult class will be set elsewhere.

Last, it may be necessary to cancel a job whilst it is running. Although it may
not be possible to stop your algorithm mid-processing, you should change the
state and do any clean-up needed and know that there's no point in making an
EvaluationResult. In this example class, the boolean variable sustaining the run-
loop is changed, so it will finish before getting to the stage of getting a
result (although just based on some random numbers here).

