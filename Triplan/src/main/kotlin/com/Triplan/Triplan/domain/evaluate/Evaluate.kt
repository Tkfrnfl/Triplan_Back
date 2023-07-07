package com.Triplan.Triplan.domain.evaluate

import jakarta.persistence.*


@Entity
@Table(name="plan_evaluate",

        )
class Evaluate {
    @Id
    var planEvaluateId: Long=0L


    var userId: Int = 0

    var planId:Int=0

    var rating:Int=0

    var detail:String=""


}
