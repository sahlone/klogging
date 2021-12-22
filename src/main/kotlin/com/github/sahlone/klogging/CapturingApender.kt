package com.github.sahlone.klogging

import ch.qos.logback.core.OutputStreamAppender
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.io.output.TeeOutputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream

object Appender {

    private val logStream = ByteArrayOutputStream()
    private val mapper = ObjectMapper()
    val teeStream: OutputStream = TeeOutputStream(logStream, System.out)

    fun reset() = logStream.reset()

    fun logLines(): Array<String> = logStream.toString().split("\n").dropLast(1).toTypedArray()

    fun jsonNodes(): Array<JsonNode> = logLines().map { mapper.readTree(it) }.toTypedArray()

    fun jsonNode(): JsonNode = mapper.readTree(logStream.toString())
}

class CapturingAppender<T> : OutputStreamAppender<T>() {

    init {
        name = "CapturingAppender"
        outputStream = Appender.teeStream
    }
}
