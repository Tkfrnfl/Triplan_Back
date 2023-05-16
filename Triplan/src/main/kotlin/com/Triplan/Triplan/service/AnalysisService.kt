package com.Triplan.Triplan.service

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL
import kr.co.shineware.nlp.komoran.core.Komoran
import kr.co.shineware.nlp.komoran.model.KomoranResult
import kr.co.shineware.nlp.komoran.model.Token
import org.springframework.stereotype.Service


@Service
class AnalysisService {

    companion object tmp
    fun main(args: Array<String>) :List<Any>{
        val komoran = Komoran(DEFAULT_MODEL.LIGHT)
//        komoran.setFWDic("user_data/fwd.user")
//        komoran.setUserDic("user_data/dic.user")
        val input = "밀리언 달러 베이비랑 바람과 함께 사라지다랑 뭐가 더 재밌었어?"
        val analyzeResultList: KomoranResult = komoran.analyze(input)
        val tokenList: List<Token> = analyzeResultList.tokenList

        // 1. print each tokens by getTokenList()
        println("==========print 'getTokenList()'==========")
        for (token in tokenList) {
            System.out.println(token)
            println((((token.getMorph() + "/" + token.getPos()).toString() + "(" + token.getBeginIndex()).toString() + "," + token.getEndIndex()).toString() + ")")
            println()
        }

        // 2. print nouns
        println("==========print 'getNouns()'==========")
        println(analyzeResultList.nouns)
        println()

        // 3. print analyzed result as pos-tagged text
        println("==========print 'getPlainText()'==========")
        println(analyzeResultList.plainText)
        println()

        // 4. print analyzed result as list
        println("==========print 'getList()'==========")
        println(analyzeResultList.list)
        println()

        // 5. print morphes with selected pos
        println("==========print 'getMorphesByTags()'==========")
        println(analyzeResultList.getMorphesByTags("NP", "NNP", "JKB"))

        return analyzeResultList.nouns
    }
}