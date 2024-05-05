package com.ece.topicsubcriptionmicroservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.ClassPathXmlApplicationContext

@SpringBootApplication
class TopicSubcriptionMicroserviceApplication

fun main(args: Array<String>) {
	runApplication<TopicSubcriptionMicroserviceApplication>(*args)
}
