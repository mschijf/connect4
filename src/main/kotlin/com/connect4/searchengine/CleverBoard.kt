package com.connect4.searchengine

import com.connect4.game.Board
import com.connect4.game.Color
import com.connect4.game.GroupType
import com.connect4.game.opponentColor

class CleverBoard(boardStatusString: String): Board(boardStatusString) {

    private val WIN_VALUE = 10_000
    private val DRAW_VALUE = 0
    private val DRAW_IS_WIN_VALUE = 8_000

    fun evaluateFromColorPerspective(color: Color): Int {
        var endVal: Int
        if (gameFinished()) {
            if (playerToMoveHasLost())
                endVal = -WIN_VALUE
            else
                endVal = if (color == Color.White) -DRAW_IS_WIN_VALUE else DRAW_IS_WIN_VALUE
        } else {
            endVal = evaluateFromPlayerToMovePerspective()
        }
        return if (color == whoisToMove) endVal else -endVal
    }


    fun endValue(depth: Int): Int {
        return if (playerToMoveHasLost()) -(WIN_VALUE + depth) else DRAW_VALUE
    }

    fun evaluateFromPlayerToMovePerspective(): Int {
        var whiteValue = 0
        var blackValue = 0
        for (group in allGroups) {
            if (group.groupType != GroupType.Vertical) {
                val whiteCount = group.countOfColor(Color.White)
                val blackCount = group.countOfColor(Color.Black)
                if (blackCount == 0) {
                    when (whiteCount) {
                        1 -> whiteValue += 10
                        2 -> {
                            whiteValue += 30
                            if (group.groupType == GroupType.Horizontal && group.getFirstEmptyField().isOdd)
                                whiteValue += 50
                        }
                        3 -> {
                            whiteValue += 70
                            if (group.getFirstEmptyField().isOdd)
                                whiteValue += 100
                        }
                    }
                }
                if (whiteCount == 0) {
                    when (blackCount) {
                        1 -> blackValue += 10
                        2 -> {
                            blackValue += 30
                            if (group.groupType == GroupType.Horizontal && group.getFirstEmptyField().isEven)
                                blackValue += 50
                        }
                        3 -> {
                            blackValue += 70
                            if (group.getFirstEmptyField().isEven)
                                blackValue += 100
                        }
                    }
                }
            }
        }
        return if (whoisToMove == Color.White)
            whiteValue - blackValue
        else
            blackValue - whiteValue
    }

    fun generateMoves() : List<Int> {
        var preventFromLosingMove = -1
        val goodMoves: MutableList<Int> = ArrayList()
        val colorOpponent = opponentColor(whoisToMove)
        for (move in getMoves()) {
            if (getField(move).isThread(whoisToMove)) {
                return listOf(move)
            } else if (getField(move).isThread(colorOpponent)) {
                preventFromLosingMove = move
            } else {
                goodMoves.add(move)
            }
        }
        return if (preventFromLosingMove >= 0) {
            listOf(preventFromLosingMove)
        } else {
            goodMoves
        }
    }
}