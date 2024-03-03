package com.librarydevloperjo.kcs

class KCS {

    companion object{

    private const val UNICODE_HangulStart = 44032
    private const val UNICODE_HangulLast = 55203


    private val CHO = listOf("ㄱ","ㄲ","ㄴ","ㄷ","ㄸ","ㄹ","ㅁ","ㅂ","ㅃ", "ㅅ","ㅆ","ㅇ","ㅈ","ㅉ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ")
    private val JOONG = listOf("ㅏ","ㅐ","ㅑ","ㅒ","ㅓ","ㅔ","ㅕ","ㅖ","ㅗ","ㅘ", "ㅙ","ㅚ","ㅛ","ㅜ","ㅝ","ㅞ","ㅟ","ㅠ","ㅡ","ㅢ","ㅣ")
    private val JONG = listOf("","ㄱ","ㄲ","ㄳ","ㄴ","ㄵ","ㄶ","ㄷ","ㄹ","ㄺ","ㄻ","ㄼ", "ㄽ","ㄾ","ㄿ","ㅀ","ㅁ","ㅂ","ㅄ","ㅅ","ㅆ","ㅇ","ㅈ","ㅊ","ㅋ","ㅌ","ㅍ","ㅎ")

    /*
    Korean unicode formular = (cho * 21 + joong) * 28 + jong + 0xAC00
    */

        fun match(query:String, toCompare:String):Boolean{
            var matched=false
            val letterPositionList = mutableListOf<Int>() // 글자 위치
            if( toCompare.length >= query.length && !(isOnlyCho(query).isNullOrEmpty()) ){ // 초성이 섞인 경우
                if(isOnlyCho(query).size != query.length){ // 초성, 글 혼용 검색
                    /**
                     * 검색어, 단어 둘다 초성만 해서 필터링
                     * 글자 위치얻어서 그 위치부분만 글자로 체크
                     * 초성끼리 비교 후 해당 위치부터 검색 단어 크기만큼 잘라서
                     * 글자 위치들 마다 같은지 비교하기
                     */
                    val queryChars = query.toCharArray()
                    val compareChars = toCompare.toCharArray()
                    for(i in query.indices){
                        val cho = getCho(query[i].toString())
                        // 검색어 한글(자음, 모음 조합) 부분 위치 저장
                        if(isHangeul(query[i].toString().single()) || !CHO.contains(query[i].toString())) {
                            letterPositionList.add(i)
                        }
                        queryChars[i]= cho.single()
                    }
                    for(i in toCompare.indices){
                        val cho = getCho(toCompare[i].toString())
                        compareChars[i]= cho.single()
                    }
                    val queryCho = String(queryChars)
                    val compareCho = String(compareChars)
                    // 검색어, 단어 서로 초성 검색
                    if(!compareCho.contains(queryCho,true)) return false //초성 끼리 비교 다를경우 false 반환
                    else { // 초성끼리 같은경우
                        val startIndex = compareCho.indexOf(queryCho)  // 검색단어 시작위치
                        val substringCompare = toCompare.substring(startIndex, startIndex + query.length) // 초성일치부분만 자르기 (성능 고려)
                        for(i in letterPositionList.indices){
                            // 글자 포함 안될경우 바로 false 반환
                            if(!substringCompare.contains(query[letterPositionList[i]])) return false
                            else matched = true
                        }
                    }

                } else { // 초성만
                    val compareChars = toCompare.toCharArray()
                    for(i in toCompare.indices){ // 기존 단어 초성 변경
                        val cho = getCho(toCompare[i].toString())
                        compareChars[i]= cho.single()
                    }
                    val compareReplaced = String(compareChars) //초성으로 이루어진 리스트
                    if(compareReplaced.contains(query,true)){
                        matched= true
                    }
                }
            }else if(toCompare.contains(query,true)){ // 단어로만 이루어지는 경우
                matched = true
            }
            return matched
        }

    fun isOnlyCho(word:String):ArrayList<Int>{
        val choindexList = ArrayList<Int>()

        for(cho_ in CHO){
            if(word.contains(cho_)){
                var same_index = word.indexOf(cho_)
                while(same_index>=0){
                    choindexList.add(same_index)
                    same_index = word.indexOf(cho_,same_index+1)
                }
            }
        }
        return choindexList
    }

    fun getCho(word:String):String{
        var word_cho = word

        if(isHangeul(word.single())){
            val charuni = word[0]
            val cho_uniIndex = ((charuni.code-0xAC00) / 28 /21).toChar().code
            word_cho = CHO[cho_uniIndex]
        }

        return word_cho
    }

    fun getJoong(word:String):String{
        var word_joong = word

        if(isHangeul(word.single())){
            val charuni = word[0]
            val joong_uniIndex = ((charuni.code-0xAC00) / 28 %21).toChar().code
            word_joong = JOONG[joong_uniIndex]
        }
        return word_joong
    }

    fun getJong(word:String):String{
        var word_jong = word

        if(isHangeul(word.single())){
            val charuni = word[0]
            val jong_uniIndex = ((charuni.code-0xAC00) %28).toChar().code
            word_jong = JONG[jong_uniIndex]
        }
        return word_jong
    }

    fun isHangeul(word: Char): Boolean {
        return UNICODE_HangulStart <= word.code && word.code <= UNICODE_HangulLast
    }

    }
}