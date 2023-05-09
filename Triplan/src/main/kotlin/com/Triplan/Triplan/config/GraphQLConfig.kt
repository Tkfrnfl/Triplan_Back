package com.Triplan.Triplan.config

import graphql.scalars.ExtendedScalars
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import graphql.schema.idl.RuntimeWiring.Builder

@Configuration
class GraphQLConfig {

    @Bean
    fun runtimeWiringConfigurer(): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { wiringBuilder: Builder ->
            wiringBuilder.scalar(ExtendedScalars.Date)
        }
    }
}