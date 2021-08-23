package com.connect4.game

import java.util.*

const val MAX_COL = 7
const val MAX_ROW = 6

const val SEPERATOR = '_'
const val WHITE_CHAR = 'o'
const val BLACK_CHAR = 'x'
val STONE_COLOR_CHAR_SET = setOf(WHITE_CHAR, BLACK_CHAR)

class Board() {

    private val fields = Array(MAX_COL) { col -> Array(MAX_ROW) { row -> Field(column=col, row=row) } }
    private val colHeights = Array(MAX_COL) { 0 }
    val allGroups = mutableListOf<Group>()
    private val fieldsPlayedStack = Stack<Field>()
    var whoisToMove: Color = Color.White ; private set

    init {
        createHorizontalGroups()
        createVerticalGroups()
        createDiagonalSWNEGroups()
        createDiagonalNWSEGroups()
    }

    private fun createHorizontalGroups() {
        for (col in 0 until MAX_COL - (4-1)) {
            for (row in 0 until MAX_ROW) {
                createGroup(listOf(fields[col][row], fields[col+1][row], fields[col+2][row], fields[col+3][row]))
            }
        }
    }
    private fun createVerticalGroups() {
        for (col in 0 until MAX_COL) {
            for (row in 0 until MAX_ROW - (4-1)) {
                createGroup(listOf(fields[col][row], fields[col][row+1], fields[col][row+2], fields[col][row+3]))
            }
        }
    }
    private fun createDiagonalSWNEGroups() {
        for (col in 0 until MAX_COL - (4-1)) {
            for (row in 0 until MAX_ROW - (4-1)) {
                createGroup(listOf(fields[col][row], fields[col+1][row+1], fields[col+2][row+2], fields[col+3][row+3]))
            }
        }
    }
    private fun createDiagonalNWSEGroups() {
        for (col in 0 until MAX_COL - (4-1)) {
            for (row in MAX_ROW-1 downTo (4-1)) {
                createGroup(listOf(fields[col][row], fields[col+1][row-1], fields[col+2][row-2], fields[col+3][row-3]))
            }
        }
    }
    private fun createGroup(fieldList: List<Field>) { //f1: Field, f2: Field, f3: Field, f4: Field
        val group = Group(fieldList)
        allGroups.add(group)
        fieldList.forEach { f -> f.addGroup(group)}
    }

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
        val nWhiteStones = fields.sumOf { col -> col.count { f -> f.stone == Color.White } }
        val nBlackStones = fields.sumOf { col -> col.count { f -> f.stone == Color.Black } }
        return (nWhiteStones == nBlackStones || nWhiteStones == (nBlackStones + 1) )
    }

    private fun determineWhoisToMove() {
        whoisToMove = if (colHeights.sum() % 2 == 0) Color.White else Color.Black
    }

    private fun putStoneInColumn(column: Int, stoneColor: Color): Field {
        val field = fields[column][colHeights[column]]
        field.stone = stoneColor
        ++colHeights[column]
        return field
    }

    private fun removeStoneFromColumn(column: Int) {
        --colHeights[column]
        fields[column][colHeights[column]].stone = Color.None
    }

    private fun boardStringToBoardRepresentation(boardString:String) {
        for ((columnNumber, columnString) in boardString.split(SEPERATOR).withIndex()) {
            setStonesFromColumnString(columnNumber, columnString)
        }
    }

    private fun setStonesFromColumnString(column: Int, columnString: String) {
        if (columnString.isEmpty() )
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
        return if (part.length > MAX_ROW) {
            false
        } else if (part.isEmpty() ) {
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
        var result = ""
        for (row in 0 until colHeights[column]) {
            result += if (fields[column][row].stone == Color.White) WHITE_CHAR else BLACK_CHAR
        }
        return result
    }

    private fun swapPlayerToMove() {
        whoisToMove = opponentColor(whoisToMove)
    }

    private fun isLegalMove(column: Int):Boolean {
        return (column in 0 until MAX_COL) && (colHeights[column] < MAX_ROW)
    }

    private fun addFieldPlayed(field: Field) {
        fieldsPlayedStack.push(field)
    }

    private fun colorHasWon(color: Color): Boolean {
        return allGroups.any { grp -> grp.completeWithColor(color) }
    }

    private fun determineWinner() : Color? {
        return if (colorHasWon(Color.White))
            Color.White
        else if (colorHasWon(Color.Black))
            Color.Black
        else
            null
    }

    fun doMove(column: Int): Coordinate  {
        if (!isLegalMove(column))
            throw Exception("Illegal move")
        val fieldPlayed = putStoneInColumn(column, whoisToMove)
        swapPlayerToMove()

        addFieldPlayed(fieldPlayed)
        return Coordinate(fieldPlayed.column, fieldPlayed.row)
    }

    fun undoMove()  {
        if (fieldsPlayedStack.isEmpty())
            throw Exception("Illegal takeback action")
        val column = fieldsPlayedStack.pop().column
        removeStoneFromColumn(column)
        swapPlayerToMove()
    }

    fun lastFieldPlayed() : Coordinate? {
        return if(fieldsPlayedStack.isEmpty()) null else Coordinate(fieldsPlayedStack.peek().column, fieldsPlayedStack.peek().row)
    }

    fun getMoves(): List<Int> {
        if (colorHasWon(Color.White) || colorHasWon(Color.Black))
            return emptyList()
        return colHeights.withIndex().filter { (_,height) -> height < MAX_ROW }.map { (column, _) -> column}
    }

    fun playerToMoveHasLost(): Boolean {
        return colorHasWon(opponentColor(whoisToMove))
    }

    fun gameFinished(): Boolean {
        return getMoves().isEmpty()
    }

    fun getStoneColor(col: Int, row: Int) = fields[col][row].stone

    fun getWinningFields(): List<Coordinate> {
        val winner = determineWinner()
        if (winner != null) {
            return allGroups.first { grp -> grp.completeWithColor(winner) }.fields.map { f -> Coordinate(f.column, f.row) }
        }
        return emptyList()
    }

}




