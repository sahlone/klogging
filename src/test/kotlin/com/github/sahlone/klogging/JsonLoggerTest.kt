package com.github.sahlone.klogging

import ch.qos.logback.classic.util.ContextInitializer
import com.fasterxml.jackson.databind.JsonNode
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.ShouldSpec
import java.util.Properties

//  For this to work we have included a property file in resources with our own ErrorProvider and appender
class JsonLoggerTest : ShouldSpec() {
    private val logger = createContextualLogger()

    init {
        val context = TracingContext("dummy_request_id")
        val additionalParameters = arrayOf(
            { "request_url" to "http://dummy_url" },
            { "transaction_id" to "dummy_transaction_id" }
        )

        val debugMessage = "this is a simple debug message"
        val errorMessage = "this is a simple error message"
        val throwable = RuntimeException("simple exception")

        val staticProperties = Properties()
        staticProperties.load(this.javaClass.getResourceAsStream("/static.properties"))

        "Logger should log messages with context" {

            fun checkInjected(field: String, logNode: JsonNode) =
                logNode.findPath(field).asText() shouldBe staticProperties[field]

            should("log messages with context nad static properties") {
                Appender.reset()
                logger.debug(context, *additionalParameters) { debugMessage }
                logger.debugWithCause(context, throwable, *additionalParameters) { debugMessage }

                logger.error(context, *additionalParameters) { errorMessage }
                logger.errorWithCause(
                    context,
                    throwable,
                    *additionalParameters,
                    { "customArg" to "customValue" }) { errorMessage }

                val jsonNodes = Appender.jsonNodes()
                jsonNodes[0].findPath("message").asText() shouldBe debugMessage
                jsonNodes[1].findPath("message").asText() shouldBe debugMessage
                jsonNodes[2].findPath("message").asText() shouldBe errorMessage
                jsonNodes[3].findPath("message").asText() shouldBe errorMessage

                jsonNodes.toList().forEach { jsonNode ->
                    jsonNode.findPath("correlation_id").asText() shouldBe "dummy_request_id"
                    jsonNode.findPath("request_url").asText() shouldBe "http://dummy_url"
                    jsonNode.findPath("transaction_id").asText() shouldBe "dummy_transaction_id"

                    staticProperties.forEach {
                        checkInjected(it.key.toString(), jsonNode)
                    }
                }
            }

            should("log messages with additional arguments") {
                Appender.reset()
                logger.errorWithCause(context, throwable, { "customArg" to "customValue" }) { errorMessage }
                Appender.jsonNodes()[0].findPath("customArg").asText() shouldBe "customValue"
                logger.errorWithCause(context, throwable) { errorMessage }
                Appender.jsonNodes()[1].findPath("customArg").asText() shouldBe ""
            }

            should("contain properties from throwable") {
                Appender.reset()
                logger.errorWithCause(context, throwable, { "customArg" to "customValue" }) { errorMessage }
                Appender.jsonNodes()[0].findPath("error-class").asText() shouldBe "java.lang.RuntimeException"
                Appender.jsonNodes()[0].findPath("error-message").asText() shouldBe "simple exception"
                Appender.jsonNodes()[0].findPath("error-stacktrace").asText() shouldNotBe ""
                Appender.jsonNodes()[0].findPath("error-stackhash").asText() shouldNotBe ""
            }
        }
    }
}
