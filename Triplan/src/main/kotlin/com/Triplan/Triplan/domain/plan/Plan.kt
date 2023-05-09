package com.Triplan.Triplan.domain.plan

import com.Triplan.Triplan.domain.user.User
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Plan {

    @Id
    @GeneratedValue
    @Column(name = "plan_id")
    var id: Long = 0L

    @ManyToOne(fetch = FetchType.LAZY)
    var user: User? = null

    var startDate: LocalDate? = null

    var endDate: LocalDate? = null

    var feedBack: Int = 0

    var feedBackNumber: Int = 0

    fun create(user: User, startDate: LocalDate, endDate: LocalDate): Plan {
        this.user = user
        this.startDate = startDate
        this.endDate = endDate

        return this
    }
}