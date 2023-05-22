package com.Triplan.Triplan.service

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL
import kr.co.shineware.nlp.komoran.core.Komoran
import kr.co.shineware.nlp.komoran.model.KomoranResult
import kr.co.shineware.nlp.komoran.model.Token
import org.springframework.stereotype.Service


@Service
class AnalysisService {

    companion object tmp
    fun analysisSentence(args: ArrayList<String>) :List<String>{
        val komoran = Komoran(DEFAULT_MODEL.LIGHT)
//        komoran.setFWDic("user_data/fwd.user")
//        komoran.setUserDic("user_data/dic.user")
        var input :String
        var analyzeResultList: KomoranResult
        var tokenList: List<Token>

        var analyzedList=ArrayList<String>()

        for(i in 0 until  args.count()){
            input=args[i]
            analyzeResultList=komoran.analyze(input)
            analyzedList.add(analyzeResultList.nouns.toString())
            tokenList=analyzeResultList.tokenList
//
        }

//        // 1. print each tokens by getTokenList()
//        println("==========print 'getTokenList()'==========")
//        for (token in tokenList) {
//            System.out.println(token)
//            println((((token.getMorph() + "/" + token.getPos()).toString() + "(" + token.getBeginIndex()).toString() + "," + token.getEndIndex()).toString() + ")")
//            println()
//        }

        // 2. print nouns
//        println("==========print 'getNouns()'==========")
//        println(analyzeResultList.nouns)
//        println()

//        // 3. print analyzed result as pos-tagged text
//        println("==========print 'getPlainText()'==========")
//        println(analyzeResultList.plainText)
//        println()
//
//        // 4. print analyzed result as list
//        println("==========print 'getList()'==========")
//        println(analyzeResultList.list)
//        println()
//
//        // 5. print morphes with selected pos
//        println("==========print 'getMorphesByTags()'==========")
       // println(analyzeResultList.getMorphesByTags("NN"))

        return analyzedList.toList()
    }
}