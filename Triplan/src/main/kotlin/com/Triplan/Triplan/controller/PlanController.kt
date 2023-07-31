package com.Triplan.Triplan.controller

import com.Triplan.Triplan.domain.plan.dto.request.DayPlanRequestDto
import com.Triplan.Triplan.domain.plan.dto.request.PlanRequestDto
import com.Triplan.Triplan.service.PlanService
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PlanController (val planService: PlanService){

    @MutationMapping
    fun requestPlanInformation(
        @RequestBody @Argument request: PlanRequestDto,
        @RequestBody @Argument requests: List<DayPlanRequestDto>
    ): JsonElement {

        val result = planService.route(request, requests)
        return Json.encodeToJsonElement(result)
    }
}