
In order to populate the DB with sample data
-----------------------------------------------

- for LINUX (e.g. SMIND Robust server): 
check out folder svnSoftwareMind\WP1\code\cat\riskRepository and read instructions in the readme.txt.


- for Windows: check instructions at
check out folder ITInnSVN\robust\code\WP1\cat\trunk\src\core\repositoryand and read instructions in the readme.txt.





Instructions for configuring the datalayer within the MS12-catDemo. 
----------------------------------------------------------------------------------

1- build and Deploy the robust-cat-apps-demos-MS12-catDemo-1.2.war 



2- You will need to configure the datalayer with the username/pwd to access the database. this is currently done using a �data.properties� file within 
$TOMCAT/ webapps\ \robust-cat-apps-demos-MS62-catDemo-1.2\WEB-INF\classes.

You can edit the file and fill in your DB details. Currently it contains:

dburl = localhost:3306
dbname = robustwp1
username = root
password = password
repoSQLSchema=path to sql script (currently available at SVN/\WP1\code\cat\trunk\src\core\repository/robust.sql)

2.1 having this configured, the datalayer will be able to connect to the database (may need restarting tomcat). I suggest keeping the dbname =robustwp1 as this may conflict with the script above. We can change the names later once we are in a better circumstances.

2.2 The data.properties file also contains the urls of the predictor services. You may want to change to localhost according to your deployment.

3- Via your browser go the MS12-catDemo and test that there are no errors.
