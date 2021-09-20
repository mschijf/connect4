package com.connect4.searchengine

import com.connect4.game.*
import java.time.Duration
import java.time.Instant

class Genius(board: Board) {

    data class InternalSearchResult (val moveSequence:String, val evaluationValue: Int)

    //copy board in order to prevent it from changing the board that is connected for other purposes (for instance front end)
    private val board = Board(board.toString())
    private var nodesVisited = 0

    fun computeMove(level:Int) : SearchResult {
        nodesVisited = 0
        val start = Instant.now()
        val result = alfabeta(level, 0, -1000000, 1000000)
        val timePassed = Duration.between(start, Instant.now()).toMillis()
        val moveList = result.moveSequence
            .split('-')
            .filter{s -> s.isNotEmpty()}
            .map{ s -> toCoordinate(s.toInt()) }
            .toList()
        return SearchResult(moveList, result.evaluationValue, nodesVisited, timePassed)
    }

    private fun alfabeta(depth:Int, ply: Int, alfa: Int, beta: Int): InternalSearchResult {
        ++nodesVisited
        if (board.gameFinished()) {
            return InternalSearchResult("", endValue(depth))
        }
        if (depth <= 0) {
            return InternalSearchResult("", evaluate())
        }
        var bestValue = alfa
        var bestMove:Int? = null
        var bestMoveSequence = ""
        val moves=generateMoves()
        for (move in moves) {
            board.doMove(move)
            val searchResult = alfabeta(depth-1, ply+1, -beta, -bestValue)
            val value = -searchResult.evaluationValue
            board.undoMove()
            if (value > bestValue) {
                bestValue = value
                bestMove = move
                bestMoveSequence = searchResult.moveSequence
                if (bestValue > beta) {
                    break
                }
            }
        }
        return InternalSearchResult(bestMove.toString() + "-" + bestMoveSequence, bestValue)
    }

    private fun endValue(depth: Int): Int {
        return if (board.playerToMoveHasLost()) -(1000 + depth) else 0
    }

    private fun evaluate(): Int {
        var whiteValue = 0
        var blackValue = 0
        for (group in board.allGroups) {
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
        return if (board.whoisToMove == Color.White)
            whiteValue - blackValue
        else
            blackValue - whiteValue
    }

    private fun generateMoves() : List<Int> {
        val colorOpponent = opponentColor(board.whoisToMove)
        val moves=board.getMoves()
        var preventFromLosingMove = -1
        for (move in moves) {
            if (board.getField(move).isThread(board.whoisToMove)) {
                return listOf(move)
            }
            if (board.getField(move).isThread(colorOpponent)) {
                preventFromLosingMove = move
            }
        }
        if (preventFromLosingMove >= 0) {
            return listOf(preventFromLosingMove)
        }
        return moves
    }
}
