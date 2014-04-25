
First run this:
````
mvn clean assembly:single
cp target/hierarchy-1.0-jar-with-dependencies.jar /path/to/neo4j/plugins
````

Then update your config file like so:

conf/neo4j-server.properties
````
org.neo4j.server.thirdparty_jaxrs_classes=org.neo4j.hierarchy=/extensions
````

Restart the server and then you'll be able to hit the following end point. Change to whatever pkPerson you like.

````
curl -v http://localhost:7474/extensions/hierarchy/814444
````

The main code is in Hierarchy.
