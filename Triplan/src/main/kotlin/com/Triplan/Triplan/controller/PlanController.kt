package com.Triplan.Triplan.controller

import com.Triplan.Triplan.domain.plan.dto.request.DayPlanRequestDto
import com.Triplan.Triplan.domain.plan.dto.request.PlanRequestDto
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PlanController {

    @MutationMapping
    fun requestPlanInformation(
        @RequestBody @Argument request: PlanRequestDto,
        @RequestBody @Argument requests: List<DayPlanRequestDto>
    ): String {

        println("startDate : " + request.startDate)
        for (dayPlanRequestDto in requests) {
            for (tripPlace in dayPlanRequestDto.tripPlaces) {
                println(tripPlace)
            }
        }


        return "success"
    }
}