package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.evaluate.Evaluate
import com.Triplan.Triplan.repository.EvaluateRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Service


@Service
class EvaluateService( private var evaluateRepository: EvaluateRepository) {

    fun findByIds(evaluate: Evaluate):Evaluate{
        return  evaluateRepository.findEvaluateByUserIdAndPlanId(evaluate.userId,evaluate.planId).get()
    }
}