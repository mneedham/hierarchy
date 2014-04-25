package org.neo4j.hierarchy;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
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
                .onPort( 7520 )
                .withThirdPartyJaxRsPackage( "org.neo4j.hierarchy", "/extensions" )
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
        Client client = Client.create();

        ClientResponse response = client
                .resource( URI.create( "http://localhost:7520/extensions/hierarchy/814444" ) )
                .accept( MediaType.APPLICATION_JSON_TYPE )
                .get( ClientResponse.class );

        System.out.println(response);
        System.out.println(response.getEntity( String.class ));

    }
}
