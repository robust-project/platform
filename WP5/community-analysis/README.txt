================================================================
Community Analysis Module
----------------------------------------------------------------
Hugo Hromic <hugo.hromic@deri.org>
Unit for Information Mining and Retrieval (UIMR)
Digital Enterprise Research Institute (DERI)
NUI Galway
================================================================

This module comprise 5 selectable user features to be computed for community
analysis inside a given time period:

- In-degree
- Out-degree
- Reciprocity
- Popularity
- Activity

-----
Usage
-----

Please see the 'config' directory, copy '*.sample' files to '*.conf' and edit
configuration options using suitable values for your environment.

For the caching system to work properly, it's advised to provide a suitable
underlying filesystem storage, as *lots* of files will be created there.

The main program is 'compute_feat.py', a Python script that takes care of
all the computation involved. For usage, please run the script with no
arguments and it will show the help message.

All computation is given to standard output (stdout), making it easy to
integrate with other programs that wish to make use of the feature values.

Also processing information is delivered to standard error (stderr) output.

--------------------
Webservice Component
--------------------

You can find a SOAP webservice client/server included, along with its
associated WSDL description file. You can launch the included
'webservice_server.py' script from the root directory, to get an instant
webservice for user features computation.

*** NOTE *** NOTE *** NOTE *** NOTE *** NOTE *** NOTE *** NOTE *** NOTE ***

The webservice server provided requires a small patch to be applied to
your local Python-ZSI module in order to fix two minor issues
(present at least on version 2.1). The required patch can be found in the
'ZSI-patch' directory, inside the 'webservice' directory.

The patch should be applied like this:
$ cd /usr/lib/pymodules/python2.7/ZSI   (depending on your distribution)
$ patch < /path/to/webservice/ZSI-patch/ServiceContainer.patch

If your distribution compiles Python byte-code, you should either delete
the old bytecode file or recreate it.

*** NOTE *** NOTE *** NOTE *** NOTE *** NOTE *** NOTE *** NOTE *** NOTE ***

Before launching the webservice for the first time, you need to adjust
the WSDL for the right service server address. After that, you need
to regenerate classes using the 'wsdl2py' tool included in the
python-zsi package, like this:

$ cd webservice   (if not already there)
$ wsdl2py --complexType RobustCommunityAnalysis.wsdl

Don't forget to add access keys in the appropiate config file.

------------
Requirements
------------

- python (>= 2.7.1)
- python-numpy (>= 1.5.1)
- python-mysqldb (>= 1.2.2)
- python-zsi (>= 2.1) [for the webservice component]

--------
Datasets
--------

This module can compute user features on the following datasets:

- Generic forums MySQL databases (use 'generic_schema.sql' template)
- TiddlyWiki MySQL database
- Boards.ie MySQL database
- SAP SCN MySQL database
