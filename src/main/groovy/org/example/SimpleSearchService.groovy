package org.example

import org.elasticsearch.client.Client
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits

class SimpleSearchService {

    private Client client

    SimpleSearchService(final Client client) {
        this.client = client
    }

    Collection<Map<String, Object>> findAll() {
        QueryBuilder query = QueryBuilders.matchAllQuery()
        fetch(query)
    }

    Collection<Map<String, Object>> findPublishedByTitle(final String token) {
        QueryBuilder query = QueryBuilders.boolQuery().
                must(QueryBuilders.rangeQuery("publishDate").lte("now")).
                must(QueryBuilders.matchQuery("title", token))
        fetch(query)
    }

    private Collection<Map<String, Object>> fetch(final QueryBuilder query) {
        SearchHits hs = client.prepareSearch("test-idx").
                setTypes("article").
                setQuery(query).
                execute().actionGet().
                getHits()
        resultSet(hs)
    }

    private Collection<Map<String, Object>> resultSet(final SearchHits searchHits) {
        Iterator<SearchHit> iter = searchHits.iterator()
        List<Map<String, Object>> records = []
        while (iter.hasNext()) {
            records << iter.next().source
        }
        records
    }
}
