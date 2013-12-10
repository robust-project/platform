set rootDir=./

set operatingPlatform=windows
set addLibPath=..\..\lib\
set libPath=.;..\;..\logs;%addLibPath%*

REM java -Xmx512m -cp "%libPath%" uk.ac.soton.itinnovation.prestoprime.iplatform.batch.ModelRunner %operatingPlatform% %libPath%
java -Xmx512m -cp "%libPath%" uk.ac.soton.itinnovation.prestoprime.iplatform.batch.ModelRunner %operatingPlatform% %libPath%  ./configuration.txt
 






