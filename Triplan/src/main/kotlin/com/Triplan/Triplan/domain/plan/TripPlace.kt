package com.Triplan.Triplan.domain.plan

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class TripPlace {

    @Id
    @GeneratedValue
    @Column(name = "tripPlace_id")
    var id: Long = 0L

    var name: String? = null

    var location: String? = null

    var transitTime: LocalDateTime? = null

    var transports: String? = null

    var leadTime: LocalDateTime? = null

    var toll: Int = 0
}