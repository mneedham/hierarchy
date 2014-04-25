

````
mvn clean assembly:single
cp target/hierarchy-1.0-jar-with-dependencies.jar /path/to/neo4j/plugins
````

conf/neo4j-server.properties
````
org.neo4j.server.thirdparty_jaxrs_classes=org.neo4j.hierarchy=/extensions
````

````
curl -v http://localhost:7474/extensions/hierarchy/814444
````
