package com.connect4.game

import java.util.*

const val MAX_COL = 7
const val MAX_ROW = 6
const val MAX_FIELDS = MAX_COL * MAX_ROW
const val CONNECT_NUMBER = 4

const val SEPERATOR = '_'
const val WHITE_CHAR = 'o'
const val BLACK_CHAR = 'x'
val STONE_COLOR_CHAR_SET = setOf(WHITE_CHAR, BLACK_CHAR)

fun toRow(fieldIndex: Int) = fieldIndex / MAX_COL
fun toColumn(fieldIndex: Int) = fieldIndex % MAX_COL
fun toCoordinate(fieldIndex: Int) = Coordinate(toColumn(fieldIndex), toRow(fieldIndex))
fun toFieldIndex(col: Int, row: Int) = row * MAX_COL + col
fun toFieldIndex(coordinate: Coordinate) = toFieldIndex(coordinate.col, coordinate.row)

class Board() {

    private val fields = Array(MAX_FIELDS) { field -> Field(field) }
    private val playableFieldIndexes = Array(MAX_COL) { i -> i }
    val allGroups = mutableListOf<Group>()
    private val fieldIndexesPlayedStack = Stack<Int>()
    var whoisToMove: Color = Color.White ; private set
    private var stoneCount = 0

    init {
        createHorizontalGroups()
        createVerticalGroups()
        createDiagonalSWNEGroups()
        createDiagonalNWSEGroups()
    }

    private fun createHorizontalGroups() {
        for (col in 0 until MAX_COL - (CONNECT_NUMBER-1)) {
            for (row in 0 until MAX_ROW) {
                createGroup(listOf(fields[toFieldIndex(col, row)], fields[toFieldIndex(col+1, row)], fields[toFieldIndex(col+2, row)], fields[toFieldIndex(col+3, row)]), GroupType.Horizontal)
            }
        }
    }
    private fun createVerticalGroups() {
        for (col in 0 until MAX_COL) {
            for (row in 0 until MAX_ROW - (CONNECT_NUMBER-1)) {
                createGroup(listOf(fields[toFieldIndex(col, row)], fields[toFieldIndex(col, row+1)], fields[toFieldIndex(col, row+2)], fields[toFieldIndex(col, row+3)]), GroupType.Vertical)
            }
        }
    }
    private fun createDiagonalSWNEGroups() {
        for (col in 0 until MAX_COL - (CONNECT_NUMBER-1)) {
            for (row in 0 until MAX_ROW - (CONNECT_NUMBER-1)) {
                createGroup(listOf(fields[toFieldIndex(col, row)], fields[toFieldIndex(col+1, row+1)], fields[toFieldIndex(col+2, row+2)], fields[toFieldIndex(col+3, row+3)]), GroupType.DiagonalSWNE)
            }
        }
    }
    private fun createDiagonalNWSEGroups() {
        for (col in 0 until MAX_COL - (CONNECT_NUMBER-1)) {
            for (row in MAX_ROW-1 downTo (CONNECT_NUMBER-1)) {
                createGroup(listOf(fields[toFieldIndex(col, row)], fields[toFieldIndex(col+1, row-1)], fields[toFieldIndex(col+2, row-2)], fields[toFieldIndex(col+3, row-3)]), GroupType.DiagonalNWSE)
            }
        }
    }

    private fun createGroup(fieldList: List<Field>, groupType: GroupType) {
        val group = Group(fieldList, groupType)
        allGroups.add(group)
        fieldList.forEach { f -> f.addGroup(group)}
    }

    //------------------------------------------------------------------------------------------------------------------

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

    private fun putStoneInColumn(column: Int, stoneColor: Color) {
        fillField(playableFieldIndexes[column], stoneColor)
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
        for (fieldIndex in column until playableFieldIndexes[column] step MAX_COL) {
            result += if (fields[fieldIndex].stone == Color.White) WHITE_CHAR else BLACK_CHAR
        }
        return result
    }

    private fun isCorrectStoneColorBalance() : Boolean {
        val nWhiteStones = fields.count { field -> field.stone == Color.White }
        val nBlackStones = fields.count { field -> field.stone == Color.Black }
        return (nWhiteStones == nBlackStones || nWhiteStones == (nBlackStones + 1) )
    }

    private fun determineWhoisToMove() {
        whoisToMove = if (fields.count { field -> field.stone == Color.White } == fields.count { field -> field.stone == Color.Black }) Color.White else Color.Black
    }

    //------------------------------------------------------------------------------------------------------------------

    private fun fillField(fieldIndex: Int, stoneColor: Color) {
        fields[fieldIndex].stone = stoneColor
        playableFieldIndexes[toColumn(fieldIndex)] += MAX_COL
        ++stoneCount
    }

    private fun clearField(fieldIndex: Int) {
        playableFieldIndexes[toColumn(fieldIndex)] -= MAX_COL
        fields[fieldIndex].stone = Color.None
        --stoneCount
    }

    //------------------------------------------------------------------------------------------------------------------

    private fun swapPlayerToMove() {
        whoisToMove = opponentColor(whoisToMove)
    }

    private fun addFieldIndexPlayed(fieldIndex: Int) {
        fieldIndexesPlayedStack.push(fieldIndex)
    }

    private fun colorHasWon(color: Color): Boolean {
        if (fieldIndexesPlayedStack.isEmpty())
            return allGroups.any { grp -> grp.completeWithColor(color) }
        val field = getField(fieldIndexesPlayedStack.peek())
        if (field.stone != color)
            return false
        return field.isPartOfCompleteGroupOfOneColor()
    }

    private fun determineWinner() : Color? {
        return if (colorHasWon(Color.White))
            Color.White
        else if (colorHasWon(Color.Black))
            Color.Black
        else
            null
    }

    //------------------------------------------------------------------------------------------------------------------

    fun isPlayableField(fieldIndex: Int): Boolean {
        return (fieldIndex in 0 until MAX_FIELDS) && (toRow(fieldIndex) == 0 || fields[fieldIndex - MAX_COL].stone != Color.None)
    }

    fun doMoveUnchecked(fieldIndex: Int) {
        fillField(fieldIndex, whoisToMove)
        swapPlayerToMove()
        addFieldIndexPlayed(fieldIndex)
    }

    fun doMove(fieldIndex: Int) {
        if (!isPlayableField(fieldIndex))
            throw Exception("Illegal move")
        doMoveUnchecked(fieldIndex)
    }

    fun doMoveByColumn(column: Int) {
        doMove(playableFieldIndexes[column])
    }

    fun doMoveByCoordinate(coordinate: Coordinate) {
        doMove(toFieldIndex(coordinate))
    }

    fun undoMove()  {
        if (fieldIndexesPlayedStack.isEmpty())
            throw Exception("Illegal takeback action")
        val fieldIndex = fieldIndexesPlayedStack.pop()
        clearField(fieldIndex)
        swapPlayerToMove()
    }

    fun getMoves(): List<Int> {
        if (colorHasWon(opponentColor(whoisToMove)))
            return emptyList()
        return playableFieldIndexes.filter { i -> i < MAX_FIELDS}
    }

    //------------------------------------------------------------------------------------------------------------------

    fun getField(fieldIndex: Int) : Field {
        return fields[fieldIndex]
    }

    //------------------------------------------------------------------------------------------------------------------

    fun playerToMoveHasLost(): Boolean {
        return colorHasWon(opponentColor(whoisToMove))
    }

    fun gameFinished(): Boolean {
        if (colorHasWon(Color.White) || colorHasWon(Color.Black))
            return true
        return stoneCount == MAX_FIELDS
    }

    //------------------------------------------------------------------------------------------------------------------

    fun lastFieldPlayed() : Coordinate? {
        return if(fieldIndexesPlayedStack.isEmpty()) null else toCoordinate(fieldIndexesPlayedStack.peek())
    }

    fun getStoneColor(col: Int, row: Int) = fields[toFieldIndex(col, row)].stone

    fun getWinningFields(): List<Coordinate> {
        val winner = determineWinner()
        if (winner != null) {
            return allGroups.first { grp -> grp.completeWithColor(winner) }.fields.map { f -> toCoordinate(f.fieldIndex) }
        }
        return emptyList()
    }

}




