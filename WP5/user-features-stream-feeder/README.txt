====================================
User Features Stream Feeding Service
====================================

1. Introduction

This module implements a simple feeding service that subscribes
to an ActiveMQ broker and Topic, to listen for incoming events
to feed into a MySQL database. The database schema must follow
the standard schema of the User Features Framework.

2. Starting it up

To start the service, first you need to configure relevant
parameters in the "streamfeeder.properties" file. You can use
the default included file as a template.

After the service is configured, simply launch it by invoking
Java supplied with the JAR file. For example:

$ java -jar user-features-stream-feeder-1.0-SNAPSHOT.jar

The service will start, connect and subscribe to the defined
ActiveMQ broker and Topic and then begin committing incoming
events into the configured database in regular intervals.

The commit interval is initially set for 30 seconds, but can
be modified using the Control Servlet. See below.

3. Control Servlet

The feeding service includes an embedded Control Servlet to allow
some basic control operations remotely. The base request syntax is:

http://address:port/control?action=ACTION&parm1=VAL1&parm2=VAL2&...

The "address:port" part is what you specified in the configuration
file, "action" is the desired action and "parmX=VALX" are parameters
that some actions may need.

Currently, the Control Servlet include the following actions:

     ACTION: "reset"
 PARAMETERS: (none)
DESCRIPTION: Wipes all data in the database.

     ACTION: "set_commit_interval"
 PARAMETERS: "interval_millis" (floating point number)
DESCRIPTION: Sets a new commit interval in milliseconds. The minimum
             allowed value is 10000.0 (10 seconds). Please note that
             setting a new interval does not trigger a commit, but
             will be effective after the next one.

     ACTION: "commit"
 PARAMETERS: (none)
DESCRIPTION: Triggers a commit of all received events in the buffer
             to the database.

Example control requests:

$ curl http://address:port/control?action=reset
$ curl http://address:port/control?action=set_commit_interval
                                   &interval_millis=15.000
$ curl http://address:port/control?action=commit

The Control Servlet will respond with simple plain text strings about
what happened with your request.
