package com.Triplan.Triplan.repository

import com.Triplan.Triplan.domain.plan.Plan
import com.Triplan.Triplan.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PlanRepository: JpaRepository<Plan, Long> {

    fun findByUser(user: User): Optional<Plan>
}