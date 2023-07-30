package com.Triplan.Triplan.domain.plan.dto.request

import kotlinx.datetime.LocalDate

class PlanRequestDto {

    var startDate: LocalDate? = null

    var endDate: LocalDate? = null

    var touristArea: String? = null
}