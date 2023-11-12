package com.Triplan.Triplan.domain

import org.json.simple.JSONObject


data class AddressCoordinate(
    val name: String,
    val x: String,
    val y: String
) {

    fun toJson():JSONObject {
        return JSONObject().also {
            it["name"] = name
            it["x"] = x
            it["y"] = y
        }
//        return "{\"name\":\"$name\"," +
//                "\"x\":\"$x\"," +
//                "\"y\":\"$y\"}"
    }
}
