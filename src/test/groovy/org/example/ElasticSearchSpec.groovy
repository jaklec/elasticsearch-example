package org.example

import org.elasticsearch.client.Client
import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder
import spock.lang.Specification


abstract class ElasticSearchSpec extends Specification {

    protected Client client
    protected static final Node node

    static {
        NodeBuilder nodeBuilder = new NodeBuilder()
        nodeBuilder.settings().put("node.name", "test")
        nodeBuilder.settings().put("path.data", "target/es/data")
        nodeBuilder.settings().put("http.enabled", false)
        node = nodeBuilder.clusterName("test").data(true).local(true).node()
    }

    def setupSpec() {
        node.start()
    }

    def setup(){
        client = node.client()
    }

    def cleanup() {
        client.admin().indices().prepareDelete("_all").execute().actionGet()
        client.close()
    }

    def cleanupSpec() {
        node.stop()
        node.close()
    }

    def createIndex(final String idx) {
        client.admin().indices().
                prepareCreate(idx).
                execute().actionGet()
    }

    def refreshIndex(final String idx) {
        client.admin().indices().
                prepareRefresh(idx).
                execute().actionGet()
    }
}
