package com.Triplan.Triplan.domain.plan.dto.request

class DayPlanRequestDto(
    var startingPoint: String? = null,
    var tripPlaces: MutableList<String> = mutableListOf(),
    var destination: String? = null
)