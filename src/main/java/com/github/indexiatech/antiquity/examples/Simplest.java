package com.github.indexiatech.antiquity.examples;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.indexiatech.antiquity.graph.ActiveVersionedGraph;
import com.indexiatech.antiquity.graph.Configuration;
import com.indexiatech.antiquity.graph.identifierBehavior.LongGraphIdentifierBehavior;

/**
 * Simplest usage of Antiquity
 */
public class Simplest {
    public static void main(String[] args) {
        TinkerGraph graph = new TinkerGraph();
        Configuration conf = new Configuration.ConfBuilder().build();
        ActiveVersionedGraph<TinkerGraph, Long> vg = new ActiveVersionedGraph.ActiveVersionedNonTransactionalGraphBuilder<TinkerGraph, Long>(
                graph, new LongGraphIdentifierBehavior()).init(true).conf(conf).build();

        //Do something with vg (versioned graph) here
        Vertex v = vg.addVertex("item1");
        v.setProperty("key", "foo");
        long verFoo = vg.getLatestGraphVersion();
        v.setProperty("key", "bar");
        long verBar = vg.getLatestGraphVersion();
        //Working with vg is just like working with the graph itself, it only contains the latest data.
        System.out.println(vg.getVertex("item1").getProperty("key")); //prints bar

        //You can query how a vertex looked like in previous states by using the historic graph API
        Vertex item1InVer1 = vg.getHistoricGraph().getVertexForVersion(v.getId(), verFoo);
        System.out.println(item1InVer1.getProperty("key")); //prints foo
        Vertex item1InVer2 = vg.getHistoricGraph().getVertexForVersion(v.getId(), verBar);
        System.out.println(item1InVer2.getProperty("key")); //prints bar
    }
}
