package com.Triplan.Triplan.domain.plan

import com.Triplan.Triplan.serialize.LocalDateTimeConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime

@Entity
@Serializable
class TripPlace {

    @Id
    @GeneratedValue
    @Column(name = "tripPlace_id")
    var id: Long = 0L

    var name: String? = null

    var location: String? = null

    @Convert(converter = LocalDateTimeConverter::class)
    var transitTime: LocalDateTime = LocalDateTime.parse(
        java.time.LocalDateTime.now().toString()
    )

    var transports: String? = null

    @Convert(converter = LocalDateTimeConverter::class)
    var leadTime: LocalDateTime? = null

    var toll: Int = 0
}