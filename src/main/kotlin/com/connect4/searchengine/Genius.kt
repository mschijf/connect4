package com.connect4.searchengine

import com.connect4.game.*
import java.time.Duration
import java.time.Instant

class Genius(board: Board) {

    data class InternalSearchResult (val moveSequence:String, val evaluationValue: Int)

    //copy board in order to prevent it from changing the board that is connected for other purposes (for instance front end)
    private val board = Board(board.toString())
    private var nodesVisited = 0
    private val leafLevel = if (board.whoisToMove == Color.White) 0 else 1

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
            return quiescence(ply, alfa, beta)
        }
        var bestValue = alfa
        var bestMove:Int? = null
        var bestMoveSequence = ""
        val moves=generateMoves()
        val newDepth = if (moves.size == 1) depth else depth-1
        for (move in moves) {
            board.doMove(move)
            val searchResult = alfabeta(newDepth, ply+1, -beta, -bestValue)
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

    private fun quiescence(ply: Int, alfa: Int, beta: Int): InternalSearchResult {
        ++nodesVisited
        if (board.gameFinished()) {
            return InternalSearchResult("", endValue(-1))
        }
        var moves = generateMoves()
        if (moves.size > 1) {
            if (ply % 2 == leafLevel)
                return InternalSearchResult("", evaluate())
            moves = listOf(moves[0])
        }
        var bestValue = alfa
        var bestMove:Int? = null
        var bestMoveSequence = ""
        for (move in moves) {
            board.doMove(move)
            val searchResult = quiescence( ply+1, -beta, -bestValue)
            val value = -searchResult.evaluationValue
            board.undoMove()
            if (value > bestValue) {
                bestValue = value
                if (bestValue > beta) {
                    break
                }
            }
        }
        return InternalSearchResult("", bestValue)
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
        var preventFromLosingMove = -1
        val goodMoves: MutableList<Int> = ArrayList()
        val colorOpponent = opponentColor(board.whoisToMove)
        for (move in board.getMoves()) {
            if (board.getField(move).isThread(board.whoisToMove)) {
                return listOf(move)
            } else if (board.getField(move).isThread(colorOpponent)) {
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
