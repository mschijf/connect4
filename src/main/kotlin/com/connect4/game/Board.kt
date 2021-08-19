package com.connect4.game

const val MAX_COL = 7
const val MAX_ROW = 6
const val MAX_FIELDS = MAX_COL * MAX_ROW
const val SEPERATOR = "_"

class Board() {

    private val fields = Array<StoneColor?>(MAX_FIELDS) { null }
    private val colHeights = Array(MAX_COL) { 0 }
    private var whiteToMove: Boolean = true

    constructor(boardString: String) : this() {
        if (!isCorrectBoardString(boardString) ) {
            throw Exception("Wrong Connect4 BoardString format")
        }
        boardStringToBoardRepresentation(boardString)
        if (!isCorrectStoneColorBalance()) {
            throw Exception("Wrong balance of white and black stones")
        }
        determineWhiteToMove()
    }

    private fun isCorrectStoneColorBalance() : Boolean {
        val nWhiteStones = fields.filter{f -> f == StoneColor.White}.count()
        val nBlackStones = fields.filter{f -> f == StoneColor.Black}.count()
        return (nWhiteStones == nBlackStones || nWhiteStones == (nBlackStones + 1) )
    }

    private fun determineWhiteToMove() {
        whiteToMove = colHeights.sum() % 2 == 0
    }

    private fun putStoneInColumn(column: Int, stoneColor: StoneColor) {
        fields[toField(column, colHeights[column])] = stoneColor
        ++colHeights[column]
    }

    private fun toField(column: Int, row: Int): Int = column * MAX_ROW + row

    private fun boardStringToBoardRepresentation(boardString:String) {
        for ((columnNumber, columnString) in boardString.split(SEPERATOR).withIndex()) {
            setStonesFromColumnString(columnNumber, columnString)
        }
    }

    private fun setStonesFromColumnString(column: Int, columnString: String) {
        if (columnString.isEmpty() || columnString.length == 1 && columnString[0] == '0')
            return

        for (ch in columnString) {
            putStoneInColumn(column, charToStoneColor(ch))
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
            part.none { ch -> (ch !in StoneColorCharacterSet)}
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
            result += fields[toField(column, row)]?.character
        }
        return result
    }

    private fun isLegalMove(column: Int):Boolean {
        return (column in 0 until MAX_COL) && (colHeights[column] < MAX_ROW)
    }

    fun doMove(column: Int) {
        if (!isLegalMove(column))
            throw Exception("Illegal move")
        putStoneInColumn(column, if (whiteToMove) StoneColor.White else StoneColor.Black)
        whiteToMove = !whiteToMove
    }

    fun getMoves(): List<Int> {
        return colHeights.withIndex().filter { (_,height) -> height < MAX_ROW }.map { (column, _) -> column}
    }

}


