Stream player

It is used to test stream processing scenario:
 * gets data from some data source
 * sends this data to queue

Data source and queue publisher are injected so source and destination can be dynamically changed at runtime.

Stream player requires some broker to be run before start (in tests such broker is created on localhost, but it is meant to connect to the real queue).
Gets data from injected DAO and sends it to queue with publish-subscribe model.
Data is got within given time window and its amount is restricted with OPTIONAL 'number of messages' argument.
Speed up argument is for speeding data send to the queue.

*********************************************************
********               prerequisite              ********
*********************************************************

SAP dataset should be modified to get proper message
creation date. In order to modify the dataset
configure and run InitDatabase.groovy script first
(resources/init/InitDatabase.groovy).

1. SAP db dump imported
 * https://greenhouse.lotus.com/wikis/home?lang=pl#!/wiki/W6a94c3aab157_4a8d_9bd7_07dc8bf4bf2f/page/SCN%20Data%20Description

2. Groovy installed
 * http://groovy.codehaus.org/Installing+Groovy

3. Tables Configuration
Please set:
 * messagesStreamTable - the name of the messages table
 * messageContentStreamTable - the name of the table;
 * messagePointsStreamTable (only for gibbs profile)
 * threadStreamTable (only for gibbs profile)

4. DB configuration
Please set resources/streamplayer.properties
  * stream.db.url
  * stream.db.name
  * stream.db.pass


5. Running the script
In console:
    cd {project_home_dir}/src/main/resources/init/
    groovy InitDatabase.groovy

This may take about 30 minutes...
*********************************************************


To run default args type (default args can be set in pom.xml file):
mvn clean install exec:java -Psap
    for the sap profile

mvn clean install exec:java -Pgibbs
    for the gibbs profile

Usage: speedUp, from date, to date, date format, topic name, broker url, number of messages (OPTIONAL)

Example arguments:
with number of messages:
    100.0 "Mon May 24 15:18:00 CEST 2010" "Sun May 30 15:18:00 CEST 2010" "EEE MMM d HH:mm:ss z yyyy" streamTopic tcp://localhost:61616 1000

without number of messages:
        100.0 "Mon May 24 15:18:00 CEST 2010" "Sun May 30 15:18:00 CEST 2010" "EEE MMM d HH:mm:ss z yyyy" streamTopic tcp://localhost:61616

without number of messages and simpler data format :
        100.0 "May 24 15:18:00 CEST 2010" "May 30 15:18:00 CEST 2010" "MMM d HH:mm:ss z yyyy" streamTopic tcp://localhost:61616