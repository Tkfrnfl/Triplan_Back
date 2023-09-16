package com.Triplan.Triplan.controller

import com.Triplan.Triplan.service.GptService
import com.Triplan.Triplan.service.PlanService
import org.json.simple.JSONObject
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class GptController (private  val gptService: GptService,private val planService: PlanService){

    @MutationMapping
    fun askGpt(@Argument question:String):Array<String>{
        val res=gptService.getQuestion(question,planService)
        return res.toArray(arrayOfNulls<String>(res.size))
    }
}