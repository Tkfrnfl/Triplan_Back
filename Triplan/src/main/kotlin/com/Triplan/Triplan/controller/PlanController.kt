package com.Triplan.Triplan.controller

import com.Triplan.Triplan.domain.plan.Plan
import com.Triplan.Triplan.domain.plan.dto.request.DayPlanRequestDto
import com.Triplan.Triplan.domain.plan.dto.request.PlanRequestDto
import com.Triplan.Triplan.service.PlanService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PlanController (val planService: PlanService){

    @MutationMapping
    fun requestPlanInformation(
        @RequestBody @Argument request: PlanRequestDto,
        @RequestBody @Argument requests: List<DayPlanRequestDto>
    ): Plan {
        return planService.route(request, requests)
    }
}