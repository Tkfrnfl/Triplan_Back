package com.Triplan.Triplan.domain.plan

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import kotlinx.serialization.Serializable

@Entity
@Serializable
class DayPlan {

    @Id
    @GeneratedValue
    @Column(name = "dayPlan_id")
    var id: Long = 0L

    var startingPoint: String? = null

    var destination: String? = null

    @OneToMany
    var tripPlaces: MutableList<TripPlace> = mutableListOf()

}