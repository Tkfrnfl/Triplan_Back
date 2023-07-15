package com.Triplan.Triplan.domain.evaluate.dto.evluate

import com.Triplan.Triplan.domain.evaluate.Evaluate

class EvaluateResponseDto(evaluate: Evaluate) {
    var planEvaluateId: Long?=evaluate.planEvaluateId


    var userId: Int? = evaluate.userId

    var planId:Int=evaluate.planId

    var rating:Int=evaluate.rating

    var detail:String=evaluate.detail

}