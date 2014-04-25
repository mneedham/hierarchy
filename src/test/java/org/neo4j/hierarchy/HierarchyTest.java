package org.neo4j.hierarchy;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.neo4j.server.CommunityNeoServer;
import org.neo4j.server.helpers.CommunityServerBuilder;

public class HierarchyTest
{
    private static CommunityNeoServer server;

    @BeforeClass
    public static void startServer() throws IOException
    {
        server = CommunityServerBuilder
                .server()
                .persistent()
                .onPort( 7520 )
                .withThirdPartyJaxRsPackage( "org.neo4j.hierarchy", "/extensions" )
                .usingDatabaseDir( "/Users/markneedham/projects/support/globoforce/neo4j-testbed" )
                .build();

        server.start();
    }

    @AfterClass
    public static void shutdownServer() {
        server.stop();
    }

    @Test
    public void should() throws IOException
    {
        DefaultClientConfig defaultClientConfig = new DefaultClientConfig();
        defaultClientConfig.getClasses().add(JacksonJsonProvider.class);
        Client client = Client.create(defaultClientConfig);

        ClientResponse response = client
                .resource( URI.create( "http://localhost:7520/extensions/hierarchy/814444" ) )
                .accept( MediaType.APPLICATION_JSON_TYPE )
                .get( ClientResponse.class );

        System.out.println(response);
        JsonNode json = response.getEntity( JsonNode.class );

        System.out.println( json );

    }
}
