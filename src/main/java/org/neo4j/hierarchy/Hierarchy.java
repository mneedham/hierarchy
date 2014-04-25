package org.neo4j.hierarchy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.Pair;

public class Hierarchy
{
    private static final DynamicRelationshipType MANAGED_BY = DynamicRelationshipType.withName( "MANAGED_BY" );
    private static final String PK_PERSON = "pkPerson";
    private static final String END_DATE = "endDate";
    private final GraphDatabaseService db;

    public Hierarchy( GraphDatabaseService db ) {
        this.db = db;
        registerShutdownHook( db );
    }

    public static void main( String[] args )
    {
        String path = "/Users/markneedham/test-bench/databases/023/neo4j-enterprise-2.0.1/data/graph.db/";

        List<String> people = Arrays.asList( "814444" );

        Hierarchy hierarchy = new Hierarchy( new GraphDatabaseFactory().newEmbeddedDatabase( path ) );
        for ( String pkPerson : people )
        {
            hierarchy.load( pkPerson );
        }

    }

    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }

    public  void load( String pkPerson )
    {
        for ( int i = 0; i < 1; i++ )
        {
            Set<Pair<String, Integer>> result = new HashSet<>(  );

            long start = System.currentTimeMillis();
            try(Transaction tx = db.beginTx()) {
                Node person = db.index().forNodes( PK_PERSON ).get( PK_PERSON, pkPerson ).getSingle();

                Iterable<Relationship> childRelationships = person.getRelationships( MANAGED_BY, Direction.INCOMING );

                for ( Relationship childRelationship : childRelationships )
                {
                    Node child = childRelationship.getStartNode();

                    if( relationshipStillApplicable( childRelationship ) ) {
                        result.add(Pair.of( pkPersonFor( child ), 4 ));
                    }

                    for ( Relationship grandChildRelationship : child.getRelationships( MANAGED_BY, Direction.INCOMING ) )
                    {
                        Node grandChild = grandChildRelationship.getStartNode();

                        if( relationshipStillApplicable( grandChildRelationship ) ) {
                            result.add(Pair.of( pkPersonFor( grandChild ), 5 ));
                        }

                        for ( Relationship greatGrandChildRelationshipRelationship : grandChild.getRelationships( MANAGED_BY, Direction.INCOMING ) )
                        {
                            Node greatGrandChild = greatGrandChildRelationshipRelationship.getStartNode();

                            if( relationshipStillApplicable( greatGrandChildRelationshipRelationship ) ) {
                                result.add(Pair.of( pkPersonFor( greatGrandChild ), 6 ));
                            }
                        }
                    }
                }

                Iterable<Relationship> parentRelationships = person.getRelationships( MANAGED_BY, Direction.OUTGOING );

                for ( Relationship parentRelationship : parentRelationships )
                {
                    Node parent = parentRelationship.getEndNode();

                    if( relationshipStillApplicable( parentRelationship ) ) {
                        result.add(Pair.of( pkPersonFor( parent ), 4));

                        for ( Relationship otherChildRelationship : parent.getRelationships( MANAGED_BY, Direction.INCOMING ) )
                        {
                            Node otherChild = otherChildRelationship.getStartNode();

                            if( relationshipStillApplicable( otherChildRelationship ) && !otherChild.equals( person )) {
                                result.add(Pair.of( pkPersonFor( otherChild ), 7 ));
                            }

                            for ( Relationship otherGrandChildRelationship : otherChild.getRelationships( MANAGED_BY, Direction.INCOMING ) )
                            {
                                Node otherGrandChild = otherGrandChildRelationship.getStartNode();
                                if( relationshipStillApplicable( otherGrandChildRelationship ) && !otherGrandChild.equals( person )) {
                                    result.add(Pair.of( pkPersonFor( otherGrandChild ), 8 ));
                                }
                            }
                        }
                    }

                    for ( Relationship grandParentRelationship : parent.getRelationships( MANAGED_BY, Direction.OUTGOING ) )
                    {
                        Node grandParent = grandParentRelationship.getEndNode();

                        if( relationshipStillApplicable( grandParentRelationship ) ) {
                            result.add(Pair.of( pkPersonFor( grandParent ), 5));
                        }

                        if ( relationshipStillApplicable( grandParentRelationship ) && relationshipStillApplicable( parentRelationship ) )
                        {
                            for ( Relationship familyLevel1Relationship : grandParent.getRelationships( MANAGED_BY, Direction.INCOMING ) )
                            {
                                Node familyLevel1 = familyLevel1Relationship.getStartNode();

                                if(!familyLevel1.equals( parent )) {
                                    if( relationshipStillApplicable( familyLevel1Relationship ) && !managedBy( familyLevel1, parent ) )
                                    {
                                        result.add(Pair.of( pkPersonFor( familyLevel1 ), 9));
                                    }

                                    for ( Relationship familyLevel2Relationship : familyLevel1.getRelationships( MANAGED_BY, Direction.INCOMING ) )
                                    {
                                        Node familyLevel2 = familyLevel2Relationship.getStartNode();

                                        if( relationshipStillApplicable( familyLevel2Relationship ) && !managedBy( familyLevel2, parent ) )
                                        {
                                            result.add(Pair.of( pkPersonFor( familyLevel2 ), 10));
                                        }

                                        for ( Relationship familyLevel3Relationship : familyLevel2.getRelationships( MANAGED_BY, Direction.INCOMING ) )
                                        {
                                            Node familyLevel3 = familyLevel3Relationship.getStartNode();

                                            if( relationshipStillApplicable( familyLevel3Relationship ) && !managedBy( familyLevel3, parent ) )
                                            {
                                                result.add(Pair.of( pkPersonFor( familyLevel3 ), 11));
                                            }

                                            for ( Relationship familyLevel4Relationship : familyLevel3.getRelationships( MANAGED_BY, Direction.INCOMING ) )
                                            {
                                                Node familyLevel4 = familyLevel4Relationship.getStartNode();

                                                if( relationshipStillApplicable( familyLevel4Relationship ) && !managedBy( familyLevel4, parent ) )
                                                {
                                                    String pk = pkPersonFor( familyLevel4 );
//                                                        System.out.println("(" + pk + ")-[:MANAGED_BY]->(" + pkPersonFor( familyLevel3 ) + ")-[:MANAGED_BY]->(" + pkPersonFor( familyLevel2 ) + ")-[:MANAGED_BY]->(" + pkPersonFor( familyLevel1 ) + ")-[:MANAGED_BY]->(" + pkPersonFor(grandParent) + ")<-[:MANAGED_BY]-(" + pkPersonFor(parent) + ")<-[:MANAGED_BY]-(" + pkPersonFor( person ) + ")");
                                                    result.add(Pair.of( pk, 12));
                                                }
                                            }
                                        }
                                    }

                                }

                            }
                        }


                        for ( Relationship greatGrandParentRelationship : grandParent.getRelationships( MANAGED_BY, Direction.OUTGOING ) )
                        {
                            Node greatGrandParent = greatGrandParentRelationship.getEndNode();

                            if( relationshipStillApplicable( greatGrandParentRelationship ) ) {
                                result.add(Pair.of( pkPersonFor( greatGrandParent ), 6));

                                for ( Relationship secondCousin1Relationship : greatGrandParent.getRelationships(  MANAGED_BY, Direction.INCOMING ) )
                                {
                                    Node secondCousin1 = secondCousin1Relationship.getStartNode();
                                    if( relationshipStillApplicable( secondCousin1Relationship ) && !managedByAtLevel2(secondCousin1, grandParent))
                                    {
                                        result.add(Pair.of( pkPersonFor( secondCousin1 ), 13));
                                    }

                                    for ( Relationship secondCousin2Relationship : secondCousin1.getRelationships(  MANAGED_BY, Direction.INCOMING ) )
                                    {
                                        Node secondCousin2 = secondCousin2Relationship.getStartNode();
                                        if( relationshipStillApplicable( secondCousin2Relationship ) && !managedByAtLevel2(secondCousin2, grandParent))
                                        {
                                            result.add(Pair.of( pkPersonFor( secondCousin2 ), 13));
                                        }

                                        for ( Relationship secondCousin3Relationship : secondCousin2.getRelationships(  MANAGED_BY, Direction.INCOMING ) )
                                        {
                                            Node secondCousin3 = secondCousin3Relationship.getStartNode();
                                            if( relationshipStillApplicable( secondCousin3Relationship ) && !managedByAtLevel2(secondCousin3, grandParent))
                                            {
                                                result.add(Pair.of( pkPersonFor( secondCousin3 ), 13));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            long end = System.currentTimeMillis();

            System.out.println(pkPerson + ":" + i + " => " + (end - start));

            Map<Integer, Integer> counts = new HashMap<>();
            for ( Pair<String, Integer> row : result )
            {
//                    System.out.println(row);
                if(!counts.containsKey( row.other() )) {
                    counts.put( row.other(), 0 );
                }

                counts.put( row.other(), counts.get( row.other() ) + 1 );
            }

            int count = 0;
            for ( Map.Entry<Integer, Integer> row : counts.entrySet() )
            {
                count = count + row.getValue() ;
            }

            Set<String> values = new HashSet<>();
            for ( Pair<String, Integer> row : result )
            {
                values.add( row.first() );
            }

//                for ( String value : values )
//                {
//                    System.out.println(value);
//                }


            System.out.println(counts);
        }
    }

    private static String pkPersonFor( Node child )
    {
        return child.getProperty( PK_PERSON ).toString();
    }

    private static boolean relationshipStillApplicable( Relationship relationship )
    {
        return !relationship.hasProperty( END_DATE );
    }

    private static boolean managedByAtLevel2( Node secondCousin1, Node grandParent )
    {
        for ( Relationship relationship : secondCousin1.getRelationships( MANAGED_BY, Direction.OUTGOING ) )
        {
            for ( Relationship subRelationship : relationship.getEndNode().getRelationships( MANAGED_BY,
                    Direction.OUTGOING ) )
            {
                Node potentialGrandParent = subRelationship.getEndNode();
                if(potentialGrandParent.equals( grandParent )) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean managedBy( Node familyLevel1, Node parent )
    {
        for ( Relationship relationship : familyLevel1.getRelationships( MANAGED_BY, Direction.OUTGOING ) )
        {
            Node potentialParent = relationship.getEndNode();
            if(potentialParent.equals(parent)) {
                return true;
            }
        }
        return false;
    }
}
