package com.github.sahlone.klogging

import com.fasterxml.jackson.databind.MappingJsonFactory
import net.logstash.logback.decorate.JsonFactoryDecorator

class LoggerDecorator : JsonFactoryDecorator {
    override fun decorate(factory: MappingJsonFactory) = factory.apply {
        codec = ObjectMapperFactory.createDefaultObjectMapper()
    }
}
