================================================================
Community User Features Browser
----------------------------------------------------------------
Hugo Hromic <hugo.hromic@deri.org>
Unit for Information Mining and Retrieval (UIMR)
Digital Enterprise Research Institute (DERI)
NUI Galway
================================================================

This module allows to explore user community features using a customizable
and interactive visual charting web application.

--------------
Database Setup
--------------

For this module to work, a database with pre-computed feature statistics
values for different datasets and communities has to be built.

In this database, one table per dataset must be created, where the table name
must match the dataset name. This table can be created in MySQL with the
following query:

CREATE TABLE `dataset1` (
  `community_id` int(11) NOT NULL,
  `feature` varchar(255) NOT NULL,
  `from` date NOT NULL,
  `to` date NOT NULL,
  `min` double NOT NULL,
  `max` double NOT NULL,
  `mean` double NOT NULL,
  `median` double NOT NULL,
  `stddev` double NOT NULL,
  `var` double NOT NULL,
  `sum` double NOT NULL,
  `used_size` int(11) NOT NULL,
  `data_size` int(11) NOT NULL,
  PRIMARY KEY (`community_id`,`feature`,`from`,`to`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8

To populate these tables, the "community-analysis" module can be used, by
using the "stats" and "sum" aggregations, together with the "tabulate"
consolidation for feeding the required fields in the tables. "tabulate" allows
a very easy parsing of the standard output of "compute_feature.py".

-----
Usage
-----

Once you have the required tables populated, please see the 'rest/config'
directory, copy the 'database.php.sample' file to 'database.php' and edit
the configuration options using suitable values for your environment.

If you want fancy community names for the IDs, you can create a text file
inside the 'data' folder, named 'communities_DATASETNAME.txt'. This file is
a TAB-separated map, one per line, of a community ID and a name. For example:

-1      (all communities)
1       1: Arts
3       3: Rec
7       7: After Hours
8       8: Sports

Three of these files are provided with this module suitables for the
"boardsie", "sapscn" and "tiddlywiki" datasets.

To use the User Features Browser, simply point your browser to the root
folder where the 'index.xhtml' file resides. Your browser must support
JavaScript to run properly (Google Chrome or Mozilla Firefox recommended).

------------
Requirements
------------

- php5 (>= 5.3.6)
- php5-mysql (>= 5.3.6)
