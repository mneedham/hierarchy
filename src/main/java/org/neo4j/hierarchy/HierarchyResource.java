package org.neo4j.hierarchy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.helpers.Pair;

@Path("/hierarchy")
public class HierarchyResource
{
    private GraphDatabaseService db;
    private final ObjectMapper objectMapper;

    public HierarchyResource(@Context GraphDatabaseService db) {

        this.db = db;
        objectMapper = new ObjectMapper();
    }

    @GET
    @Path("/{pkPerson}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadHierarchy(@PathParam( "pkPerson" ) String pkPerson) {
        Hierarchy hierarchy = new Hierarchy( db );
        final Set<Pair<String,Integer>> family = hierarchy.load( pkPerson );

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException
            {
                JsonGenerator jg = objectMapper.getJsonFactory().createJsonGenerator( os, JsonEncoding.UTF8 );
                jg.writeStartArray();

                for ( Pair<String, Integer> member : family )
                {
                    jg.writeStartObject();
                    jg.writeFieldName( "pkPerson" );
                    jg.writeRawValue( member.first() );
                    jg.writeFieldName( "role" );
                    jg.writeRawValue( member.other().toString() );
                    jg.writeEndObject();
                }
                jg.writeEndArray();

                jg.flush();
                jg.close();
            }
        };


        return Response.ok().entity( stream ).type( MediaType.APPLICATION_JSON ).build()    ;
    }


}
