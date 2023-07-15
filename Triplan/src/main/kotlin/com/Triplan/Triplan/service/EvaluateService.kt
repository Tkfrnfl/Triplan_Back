package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.evaluate.Evaluate
import com.Triplan.Triplan.domain.evaluate.dto.evluate.EvaluateRequestDto
import com.Triplan.Triplan.domain.evaluate.dto.evluate.EvaluateResponseDto
import com.Triplan.Triplan.repository.EvaluateRepository
import org.springframework.stereotype.Service


@Service
class EvaluateService( private var evaluateRepository: EvaluateRepository) {

    fun findByIds(evaluate: Evaluate):Evaluate{
        return  evaluateRepository.findEvaluateByUserIdAndPlanId(evaluate.userId,evaluate.planId).get()
    }

    fun requestEvaluateInfo(evaluateRequestDto: EvaluateRequestDto): EvaluateResponseDto {

        val evalCheck: Evaluate = evaluateRepository.findEvaluateByUserIdAndPlanId(evaluateRequestDto.userId, evaluateRequestDto.planId).get()

        return EvaluateResponseDto(evalCheck)
    }
}