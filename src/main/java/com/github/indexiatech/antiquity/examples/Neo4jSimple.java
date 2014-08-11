package com.github.indexiatech.antiquity.examples;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import com.indexiatech.antiquity.graph.ActiveVersionedGraph;
import com.indexiatech.antiquity.graph.Configuration;
import com.indexiatech.antiquity.graph.TransactionalVersionedGraph;
import com.indexiatech.antiquity.graph.identifierBehavior.LongGraphIdentifierBehavior;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

/**
 * Antiquity with Neo4j sample
 */
public class Neo4jSimple {
    public static void main(String[] args) {
        //Create Blueprints Neo4j2Graph instance
        //GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("target/database/location");
        GraphDatabaseService graph = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
        Neo4j2Graph neo4j2Graph = new Neo4j2Graph(graph);
        neo4j2Graph.autoStartTransaction(true);

        //Create Antiquity wrapper
        Configuration conf = new Configuration.ConfBuilder().build();
        TransactionalVersionedGraph<Neo4j2Graph, Long> vg = (TransactionalVersionedGraph) new ActiveVersionedGraph.ActiveVersionedTransactionalGraphBuilder<Neo4j2Graph, Long>(
                neo4j2Graph, new LongGraphIdentifierBehavior()).init(true).conf(conf).build();

        //Do something with vg (versioned graph) here
        Vertex v = vg.addVertex(null);
        vg.commit();
        v.setProperty("key", "foo");
        vg.commit();
        long verFoo = vg.getLatestGraphVersion();
        v.setProperty("key", "bar");
        vg.commit();
        long verBar = vg.getLatestGraphVersion();
        //Working with vg is just like working with the graph itself, it only contains the latest data.
        System.out.println(vg.getVertex(v.getId()).getProperty("key")); //prints bar

        //You can query how a vertex looked like in previous states by using the historic graph API
        Vertex item1InVer1 = vg.getHistoricGraph().getVertexForVersion(v.getId(), verFoo);
        System.out.println(item1InVer1.getProperty("key")); //prints foo
        Vertex item1InVer2 = vg.getHistoricGraph().getVertexForVersion(v.getId(), verBar);
        System.out.println(item1InVer2.getProperty("key")); //prints bar


        neo4j2Graph.shutdown();
        System.out.println("SHUTDOWN!");
    }
}
