In order to drop and repopulate the risk repository, 

1- make sure that the username/pwd in data.properties are those of your local mysql server

run:

1- mvn dependency:copy-dependencies

2- populateDB-ibm.bat

(for specific scenario data only use the associated batch file ibm or sap)