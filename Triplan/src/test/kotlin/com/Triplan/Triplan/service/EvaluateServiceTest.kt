package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.evaluate.Evaluate
import com.Triplan.Triplan.repository.EvaluateRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Service
import org.springframework.test.context.TestConstructor

// 특정 생성자만 wire해서 테스트하는방법?
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class EvaluateServiceTest  @Autowired constructor(private val evaluateRepository: EvaluateRepository){

 private  val evaluateService:EvaluateService= EvaluateService(evaluateRepository)

    @Test
    fun main1(){

        var eval1=Evaluate()
        eval1.planEvaluateId=1
        eval1.planId=1
        eval1.userId=1
      //  println(  evaluateRepository.findEvaluateByUserIdAndPlanId(eval1).get())


       println( evaluateService.findByIds(eval1).detail)
    }
}