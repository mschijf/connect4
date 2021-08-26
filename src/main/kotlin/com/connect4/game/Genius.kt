package com.connect4.game

import java.time.Duration
import java.time.Instant

class Genius(board: Board) {
    //copy board in order to prevent it from changing the board hat is connected for other purpopses (for instance front end)
    private val board = Board(board.toString())
    private var nodesVisited = 0
    private val killer = Array(42)  {-1}

    fun computeMove(level:Int) : SearchResult {
        nodesVisited = 0
        val start = Instant.now()
        val result = alfabeta(level, 0, -1000000, 1000000)
        return SearchResult(result, nodesVisited, Duration.between(start, Instant.now()).toMillis())
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
        val moves=generateMoves(killer[ply])
        val newDepth = depth - 1  //if (moves.size == 1) depth else depth-1
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
                    killer[ply] = move
                    break
                }
            }
        }
        return InternalSearchResult(bestMove.toString() + bestMoveSequence, bestValue)
    }

    private fun endValue(depth: Int): Int {
        return if (board.playerToMoveHasLost()) -(1000 + depth) else 0
    }

    private fun evaluate(): Int {
        var whiteValue = 0
        var blackValue = 0
        for (group in board.allGroups) {
            if (group.groupType != GroupType.vertical) {
                val whiteCount = group.countOfColor(Color.White)
                val blackCount = group.countOfColor(Color.Black)
                if (blackCount == 0) {
                    when (whiteCount) {
                        1 -> whiteValue += 1
                        2 -> whiteValue += 3
                        3 -> whiteValue += 7
                    }
                }
                if (whiteCount == 0) {
                    when (blackCount) {
                        1 -> blackValue += 1
                        2 -> blackValue += 3
                        3 -> blackValue += 7
                    }
                }
            }
        }
        return if (board.whoisToMove == Color.White)
            whiteValue - blackValue
        else
            blackValue - whiteValue
    }

    //todo: killer moet met (colum, row) --> alleen column is niet goed.
    private fun generateMoves(killerMove: Int) : List<Int> {
        val moves=board.getMoves()
        for (move in moves) {
            if (board.getPlayableField(move).isThread(Color.White) || board.getPlayableField(move).isThread(Color.Black)) {
                return listOf(move)
            }
        }
        return moves.sortedBy {it != killerMove}
    }
}

data class InternalSearchResult (val moveSequence:String, val evaluationValue: Int)
class SearchResult (internalSearchResult: InternalSearchResult, val nodesVisited: Int, val durationMillis: Long){
    val moveSequence: List<Int> = internalSearchResult.moveSequence.map{ ch -> ch.code - '0'.code }.toList()
    val evaluationValue = internalSearchResult.evaluationValue
}
