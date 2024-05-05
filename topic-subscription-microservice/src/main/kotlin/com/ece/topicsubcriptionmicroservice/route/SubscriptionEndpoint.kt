package com.ece.topicsubcriptionmicroservice.route

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Projections
import com.ece.topicsubcriptionmicroservice.models.JwtUtils
import com.ece.topicsubcriptionmicroservice.models.UserTopic
import mu.KLogging
import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel.INFO
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mongodb.MongoDbConstants
import org.apache.camel.model.rest.RestBindingMode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.text.Document


@Component
class SubscriptionEndpoint : RouteBuilder() {

    companion object : KLogging()

    @Value("\${netty.server.port}")
    private val serverPort: String? = null

    @Value("\${mongodb.username}")
    private val mongodbUserName: String? = null

    @Value("\${mongodb.password}")
    private val mongodbPassword: String? = null

    @Value("\${mongodb.host}")
    private val mongodbHost: String? = null

    @Value("\${mongodb.port}")
    private val mongodbPort: Int? = null

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    override fun configure() {

        var uri = "mongodb://$mongodbHost:$mongodbPort/?maxPoleSize=50&w=majority"

        //var uri = "mongodb://$mongodbUserName:$mongodbPassword@$mongodbHost:$mongodbPort/?maxPoleSize=50&w=majority"

        val serverApi = ServerApi.builder().version(ServerApiVersion.V1).build()
        val settings =
            MongoClientSettings.builder().applyConnectionString(ConnectionString(uri)).serverApi(serverApi).build()
        val mongodbClient = MongoClients.create(settings)

        context.registry.apply { bind("myDb", mongodbClient) }

        restConfiguration()
            .component("netty-http")
            .host("0.0.0.0")
            .port(serverPort)
            .bindingMode(RestBindingMode.auto)

        rest("/topics").get()
            .produces("application/json")
            .to("direct:gettopics")
            .post()
            .consumes("application/json")
            .produces("application/json")
            .to("direct:posttopic")

        rest("/subscribe")
            .get()
            .produces("application/json")
            .to("direct:getsubcribe")
            .post("?topic={topic}")
            .produces("application/json")
            .to("direct:postsubcribe")

        rest("/unsubscribe")
            .put("?topic={topic}")
            .produces("application/json")
            .to("direct:putunsubcribe")


        from("direct:gettopics")
            .log(INFO, log, "Received gettopics")
            .process {
                val authorization = it.`in`.getHeader("Authorization") as String
                log.info("Authorization : $authorization")
            }
            .to("mongodb:myDb?database=subscription&collection=topics&operation=findAll")

        from("direct:posttopic")
            .log(INFO, log, "Received posttopic")
            .process { }
            .to("mongodb:myDb?database=subscription&collection=topics&operation=insert")

        from("direct:getsubcribe")
            .log(INFO, log, "Received getsubcribe")
            .setHeader(MongoDbConstants.FIELDS_PROJECTION, constant(Projections.exclude("username", "_id")))
            .setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD, constant(Projections.elemMatch("topic")))
            .process {
                val authorization = it.`in`.getHeader("Authorization") as String
                val filter = eq("username", JwtUtils.getUsernameFromJwt(authorization))
                it.`in`.body = filter
            }
            .to("mongodb:myDb?database=subscription&collection=usertopics&operation=findAll")

        from("direct:postsubcribe")
            .log(INFO, log, "Received postsubcribe")
            .to("direct:putunsubcribe")
            .choice()
            .`when`(header("topic").isNotNull)
            .process {
                val current = LocalDateTime.now()
                val authorization = it.`in`.getHeader("Authorization") as String
                it.`in`.body = UserTopic(
                    it.`in`.getHeader("topic") as String,
                    JwtUtils.getUsernameFromJwt(authorization),
                    current.format(formatter)
                )
            }
            .to("mongodb:myDb?database=subscription&collection=usertopics&operation=insert")
            .otherwise()
            .to("direct:error")

        from("direct:putunsubcribe")
            .log(INFO, log, "Received putunsubcribe")
            .choice()
            .`when`(header("topic").isNotNull)
            .process {
                val authorization = it.`in`.getHeader("Authorization") as String
                val filter = Filters.and(
                    eq("topic", it.`in`.getHeader("topic") as String),
                    eq("username", JwtUtils.getUsernameFromJwt(authorization))
                )
                it.`in`.body = filter
            }
            .to("mongodb:myDb?database=subscription&collection=usertopics&operation=remove")
            .otherwise()
            .to("direct:error")

        from("direct:error")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setBody(constant("Bad request"))
    }
}
