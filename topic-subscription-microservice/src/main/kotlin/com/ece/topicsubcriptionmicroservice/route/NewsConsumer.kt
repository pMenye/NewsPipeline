package com.ece.topicsubcriptionmicroservice.route

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.client.MongoClients
import mu.KLogging
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.dataformat.JsonLibrary
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class NewsConsumer : RouteBuilder() {

    companion object : KLogging()

    @Value("\${mongodb.news.host}")
    private val mongodbHost: String? = null

    @Value("\${kafka.host}")
    private val kafkaHost: String? = null

    @Value("\${kafka.topics}")
    private val topics: String? = null

    @Value("\${mongodb.port}")
    private val mongodbPort: Int? = null

    override fun configure() {

        var uri = "mongodb://$mongodbHost:$mongodbPort/?maxPoleSize=50&w=majority"

        val serverApi = ServerApi.builder().version(ServerApiVersion.V1).build()
        val settings =
            MongoClientSettings.builder().applyConnectionString(ConnectionString(uri)).serverApi(serverApi).build()
        val mongodbClient = MongoClients.create(settings)

        context.registry.apply { bind("myDbNews", mongodbClient) }
        val topicsList = topics?.split(":")

        topicsList?.stream()?.forEach {
            from("kafka:$it?brokers=$kafkaHost:9092")
                .unmarshal().json(JsonLibrary.Jackson)
                .to("mongodb:myDbNews?database=news&collection=$it&operation=insert")
        }
    }
}