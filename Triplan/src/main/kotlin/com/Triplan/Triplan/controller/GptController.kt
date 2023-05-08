package com.Triplan.Triplan.controller

import com.Triplan.Triplan.service.GptService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class GptController (private  val gptService: GptService){

    @MutationMapping
    fun askGpt(@Argument question:String){
        return gptService.getQuestion(question)
    }
}