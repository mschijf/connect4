package com.connect4.game

const val MAX_COL = 7
const val MAX_ROW = 6

const val SEPERATOR = '_'
const val WHITE_CHAR = 'o'
const val BLACK_CHAR = 'x'
val STONE_COLOR_CHAR_SET = setOf(WHITE_CHAR, BLACK_CHAR)

class Board() {

    private val fields = Array(MAX_COL) { col -> Array(MAX_ROW) { row -> Field(column=col, row=row) } }

    private val colHeights = Array(MAX_COL) { 0 }
    private var whoisToMove: Color = Color.White

    constructor(boardString: String) : this() {
        if (!isCorrectBoardString(boardString) ) {
            throw Exception("Wrong Connect4 BoardString format")
        }
        boardStringToBoardRepresentation(boardString)
        if (!isCorrectStoneColorBalance()) {
            throw Exception("Wrong balance of white and black stones")
        }
        determineWhoisToMove()
    }

    private fun isCorrectStoneColorBalance() : Boolean {
        val nWhiteStones = fields.sumOf { col -> col.filter {f -> f.stone == Color.White}.count() }
        val nBlackStones = fields.sumOf { col -> col.filter {f -> f.stone == Color.Black}.count() }
        return (nWhiteStones == nBlackStones || nWhiteStones == (nBlackStones + 1) )
    }

    private fun determineWhoisToMove() {
        whoisToMove = if (colHeights.sum() % 2 == 0) Color.White else Color.Black
    }

    private fun putStoneInColumn(column: Int, stoneColor: Color) {
        fields[column][colHeights[column]].stone = stoneColor
        ++colHeights[column]
    }

    private fun boardStringToBoardRepresentation(boardString:String) {
        for ((columnNumber, columnString) in boardString.split(SEPERATOR).withIndex()) {
            setStonesFromColumnString(columnNumber, columnString)
        }
    }

    private fun setStonesFromColumnString(column: Int, columnString: String) {
        if (columnString.isEmpty() || (columnString.length == 1 && columnString[0] == '0'))
            return

        for (ch in columnString) {
            putStoneInColumn(column, if (ch == WHITE_CHAR) Color.White else Color.Black)
        }
    }

    private fun isCorrectBoardString(boardString:String): Boolean {
        val parts = boardString.split(SEPERATOR)
        if (parts.size != MAX_COL)
            return false

        return parts.all {part -> isCorrectBoardPartString(part)}
    }

    private fun isCorrectBoardPartString(part: String) : Boolean {
        return if (part.isEmpty() || (part.length > MAX_ROW)) {
            false
        } else if (part.length == 1 && part[0] == '0') {
            true
        } else {
            part.none { ch -> (ch !in STONE_COLOR_CHAR_SET)}
        }
    }

    override fun toString(): String {
        var result = columnAsString(0)
        for(column in 1 until MAX_COL) {
            result += SEPERATOR + columnAsString(column)
        }
        return result
    }

    private fun columnAsString(column:Int) : String {
        if (colHeights[column] == 0)
            return "0"
        var result = ""
        for (row in 0 until colHeights[column]) {
            result += if (fields[column][row].stone == Color.White) WHITE_CHAR else BLACK_CHAR
        }
        return result
    }

    private fun swapPlayerToMove() {
        whoisToMove = OpponentColor(whoisToMove)
    }

    private fun isLegalMove(column: Int):Boolean {
        return (column in 0 until MAX_COL) && (colHeights[column] < MAX_ROW)
    }

    fun doMove(column: Int) {
        if (!isLegalMove(column))
            throw Exception("Illegal move")
        putStoneInColumn(column, whoisToMove)
        swapPlayerToMove()
    }

    fun getMoves(): List<Int> {
        return colHeights.withIndex().filter { (_,height) -> height < MAX_ROW }.map { (column, _) -> column}
    }

}


