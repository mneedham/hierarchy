package org.neo4j.hierarchy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.neo4j.graphdb.GraphDatabaseService;

@Path("/hierarchy")
public class HierarchyResource
{
    private GraphDatabaseService db;

    public HierarchyResource(@Context GraphDatabaseService db) {

        this.db = db;
    }

    @GET
    @Path("/{pkPerson}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadHierarchy(@PathParam( "pkPerson" ) String pkPerson) {
        Hierarchy hierarchy = new Hierarchy( db );

        return Response.ok().entity( "hello " + pkPerson ).build()    ;
    }
}
