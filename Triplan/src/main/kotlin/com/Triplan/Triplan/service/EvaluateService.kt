package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.evaluate.Evaluate
import com.Triplan.Triplan.domain.evaluate.dto.evluate.EvaluateMutationDto
import com.Triplan.Triplan.domain.evaluate.dto.evluate.EvaluateRequestDto
import com.Triplan.Triplan.domain.evaluate.dto.evluate.EvaluateResponseDto
import com.Triplan.Triplan.repository.EvaluateRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class EvaluateService( private var evaluateRepository: EvaluateRepository) {

    fun findByIds(evaluate: Evaluate):Evaluate{
        return  evaluateRepository.findEvaluateByUserIdAndPlanId(evaluate.userId,evaluate.planId).get()
    }

    fun requestEvaluateInfo(evaluateRequestDto: EvaluateRequestDto): EvaluateResponseDto {

        val evalCheck: Evaluate = evaluateRepository.findEvaluateByUserIdAndPlanId(evaluateRequestDto.userId, evaluateRequestDto.planId).get()

        return EvaluateResponseDto(evalCheck)
    }

    @Transactional
    fun evaluateMutation(evaluateMutationDto: EvaluateMutationDto):EvaluateResponseDto{
       var evaluate:Evaluate=evaluateRepository.findEvaluateByUserIdAndPlanId(evaluateMutationDto.userId,evaluateMutationDto.planId).get()
        evaluate.rating=evaluateMutationDto.rating
        evaluate.detail=evaluateMutationDto.detail
        evaluateRepository.save(evaluate)

        return EvaluateResponseDto(evaluate)
    }
}