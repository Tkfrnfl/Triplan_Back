package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.plan.Plan
import com.Triplan.Triplan.domain.user.User
import com.Triplan.Triplan.repository.PlanRepository
import org.springframework.stereotype.Service

@Service
class PlanService(
    private var planRepository: PlanRepository
) {

    fun findByUser(user: User): Plan {

        return planRepository.findByUser(user).get()
    }
}