package com.Triplan.Triplan.service

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class AnalysisServiceTest {

    private val analysisService:AnalysisService= AnalysisService()
    @Test
    fun main() {
        val str = "abcd"
        val array: Array<String> = str.toCharArray().map { it.toString() }.toTypedArray()
//
//        com.Triplan.Triplan.main(array)
        val tmp= analysisService.main(array)

        println(tmp)
    }
}