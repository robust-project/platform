This code is part of the final release of the ROBUST WP1 code. It is provided under the LGPL v2.1 licence and the copyright statement, both are included in this folder.


COMMUNITY ANALYSIS TOOL README
==============================

This README contains information about:
 * How to build and install
 * General information about the project structure


TO BUILD AND INSTALL
--------------------

To build the Community Analysis Tool (CAT), you need to install and configure Maven 3.x.

The general installation instructions are not repeated here, but the one thing to sort 
out if you already have Maven installed, is to add the ROBUST Nexus repository to your
settings.xml file.

The settings.xml file is typically in your Maven .m2 directory, e.g.,
 - C:\Users\<username>\.m2	[in Windows 7]
 - /home/<username>/.m2		[in Linux]

You may not have a settings.xml file. There is typically one in the conf directory of your
Maven installation, so you can copy that and edit. Or you can create one like the
example below. The example below only contains the essential information that
you need to add to your settings.xml file to be able to build the robust-parent-pom,
and, thus, the CAT project (sets up ROBUST Nexus Mirror).
 
<settings xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
  http://maven.apache.org/xsd/settings-1.0.0.xsd">
  
  <servers>
    <server>
      <id>Robust</id>
      <username>username</username>
      <password>password</password>
    </server>
  </servers>

</settings>

Once you have added this settings.xml file to your ~/.m2 directory, you can build
the cat project, which will get the required dependencies from the Nexus repository.


Generate dependencies and licences list
---------------------------------------

	mvn project-info-reports:dependencies


Package up the project
----------------------

Currently packaging up the project only consists of copying WAR files to a bin folder at
the top level. The packaging is instigated by a profile, so if you want to do this with
any maven phase from 'package' level and higher, just add -P package, e.g.:

	mvn install -P package


CAT PROJECT STRUCTURE
---------------------

The CAT project consists of two main branches of the src tree:

src
 `-- core
 |  `-- common
 |  `-- components
 |  `-- dataProviders
 |  `-- models
 |  `-- repository
 |  `-- probEstimator
 `-- webapp

The webapp directory contains a web application demo based on the core components.
Each of the modules within the core project have multiple projects within them. 
A list and description of these are not maintained in this file, however.