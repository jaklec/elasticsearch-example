package org.example

import org.elasticsearch.common.xcontent.XContentType

class SimpleSearchServiceSpec extends ElasticSearchSpec {

    String title = "I am a title"
    String body = "I am a body"
    String publishDate = "2015-01-14"

    def setup() {
        createIndex("test-idx")
        prepareDatabase()
        refreshIndex("test-idx")
    }

    def "should find all documents"() {
        when:
        Collection<Map<String, Object>> hits = new SimpleSearchService(client).findAll()

        then:
        hits.size() == 2
    }

    def "should fetch published articles by title"() {
        when:
        SimpleSearchService service = new SimpleSearchService(client)
        Collection<Map<String, Object>> records = service.findPublishedByTitle("title")

        then:
        records.size() == 1
        records[0].title == title
        records[0].body == body
        records[0].publishDate == publishDate
    }

    private void prepareDatabase() {
        Map<String, Object> data = [
                "title"      : title,
                "body"       : body,
                "publishDate": publishDate
        ]

        client.prepareIndex("test-idx", "article", "1").
                setSource(data, XContentType.JSON).
                execute().actionGet()

        Map<String, Object> data2 = [
                "title"      : "title",
                "body"       : "foo bar",
                "publishDate": "2025-01-01"
        ]

        client.prepareIndex("test-idx", "article", "2").
                setSource(data2, XContentType.JSON).
                execute().actionGet()
    }
}
