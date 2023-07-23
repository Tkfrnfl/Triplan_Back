package com.Triplan.Triplan.repository

import com.Triplan.Triplan.domain.evaluate.Evaluate
import com.Triplan.Triplan.domain.plan.Plan
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EvaluateRepository :JpaRepository<Evaluate,Long>{

    fun findEvaluateByUserIdAndPlanId(userId:Int,planId:Int): Optional<Evaluate>
    fun findEvaluatesByUserId(userId: Int):Array<Evaluate>
}