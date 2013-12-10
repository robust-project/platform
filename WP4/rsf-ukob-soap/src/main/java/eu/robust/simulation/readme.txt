put your java classes here

find in java classes and in xml files TODOs and if necessary please refactor the names.

to create/generate the necessary classes cxf-java2ws-plugin is added.
When the interface and its implementation is ready, please :
    1) execute 'mvn process-classes',
    2) copy generated classes from target/generated-sources into src/main/java
    3) comment the cxf-java2ws-plugin (if it is not commented, each time you invoke mvn install the ws classes are generated and can be found here and in src dir => compilatiom failure).
