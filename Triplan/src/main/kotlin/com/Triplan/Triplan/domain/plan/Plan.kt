package com.Triplan.Triplan.domain.plan

import com.Triplan.Triplan.domain.user.User
import com.Triplan.Triplan.serialize.LocalDateConverter
import jakarta.persistence.*
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Entity
@Serializable
class Plan {

    @Id
    @GeneratedValue
    @Column(name = "plan_id")
    var id: Long = 0L

    @ManyToOne(fetch = FetchType.LAZY)
    var user: User? = null

    @Convert(converter = LocalDateConverter::class)
    var startDate: LocalDate? = null

    @Convert(converter = LocalDateConverter::class)
    var endDate: LocalDate? = null

    var touristArea: String? = null

    var feedBack: Int = 0

    var feedBackNumber: Int = 0

    @OneToMany
    var dayPlans: MutableList<DayPlan> = mutableListOf()

}