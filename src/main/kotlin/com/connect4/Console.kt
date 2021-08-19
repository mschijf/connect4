package com.connect4

fun main() {
    checkBoardString("xox_0_oxo_0_o_x_o")
}

fun checkBoardString(boardString:String) {
    println(boardString)
    val parts = boardString.split("_")
    if (parts.size != 7)
        throw Exception("Wrong number of columns exception");
    var nWhite = 0
    var nBlack = 0
    for(part in parts) {
        val stoneCount = checkBoardStringPart(part)
        nWhite += stoneCount.whiteStones
        nBlack += stoneCount.blackStones
    }
    if (nWhite < nBlack)
        throw Exception("Black has more stones than white exception");
    if (nWhite - nBlack >  1)
        throw Exception("White has at least one stone too much exception");
}

private fun checkBoardStringPart(part: String) : StoneCount {
    if (part.length == 0) {
        throw Exception("No chars at all in column exception")
    }

    if (part.length == 1 && part[0] == '0') {
        return StoneCount(whiteStones = 0, blackStones = 0)
    }

    var nBlack = 0
    var nWhite = 0
    for (ch in part) {
        when (ch) {
            'x' -> nBlack++
            'o' -> nWhite++
            else -> {
                throw Exception("Wrong char in column exception")
            }
        }
    }
    if (nBlack + nWhite <= 6) {
        return StoneCount(whiteStones = nWhite, blackStones = nBlack)
    } else {
        throw Exception("Too many stones in column exception")
    }
}

private data class StoneCount (val whiteStones: Int, val blackStones: Int)