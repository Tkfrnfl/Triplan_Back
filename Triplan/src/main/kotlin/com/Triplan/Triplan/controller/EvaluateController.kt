package com.Triplan.Triplan.controller

import com.Triplan.Triplan.domain.evaluate.Evaluate
import com.Triplan.Triplan.domain.evaluate.dto.evluate.EvaluateMutationDto
import com.Triplan.Triplan.domain.evaluate.dto.evluate.EvaluateRequestDto
import com.Triplan.Triplan.domain.evaluate.dto.evluate.EvaluateResponseDto
import com.Triplan.Triplan.service.AuthService
import com.Triplan.Triplan.service.EvaluateService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EvaluateController ( private val evaluateService: EvaluateService){


    @QueryMapping
    fun requestEvaluateInfo( @Argument request: EvaluateRequestDto):EvaluateResponseDto{
        return evaluateService.requestEvaluateInfo(request)
    }

    @MutationMapping
    fun evaluatePlan(@Argument request:EvaluateMutationDto):EvaluateResponseDto{
        return evaluateService.evaluateMutation(request)
    }
}