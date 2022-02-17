package com.connect4.searchengine.dfs

import com.connect4.game.*
import com.connect4.searchengine.CleverBoard
import com.connect4.searchengine.IGenius
import com.connect4.searchengine.SearchResult
import java.time.Duration
import java.time.Instant

class Genius(): IGenius {

    data class InternalSearchResult (val moveSequence:String, val evaluationValue: Int)

    //copy board in order to prevent it from changing the board that is connected for other purposes (for instance front end)
    private var cleverBoard = CleverBoard(DEFAULT_BOARD)
    private var leafLevel = 0
    private var nodesVisited = 0

    private fun setBoard(board: Board) {
        cleverBoard = CleverBoard(board.toBoardStatusString())
        leafLevel = if (cleverBoard.whoisToMove == Color.White) 0 else 1
    }

    override fun computeMove(board: Board, level: Int): SearchResult {
        setBoard(board)
        return computeMove(level)
    }

    private fun computeMove(level:Int) : SearchResult {
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
        if (cleverBoard.gameFinished()) {
            return InternalSearchResult("", cleverBoard.endValue(depth))
        }
        if (depth <= 0) {
            return quiescence(ply, alfa, beta)
        }
        var bestValue = alfa
        var bestMove:Int? = null
        var bestMoveSequence = ""
        val moves=cleverBoard.generateMoves()
        val newDepth = if (moves.size == 1) depth else depth-1
        for (move in moves) {
            cleverBoard.doMove(move)
            val searchResult = alfabeta(newDepth, ply+1, -beta, -bestValue)
            val value = -searchResult.evaluationValue
            cleverBoard.undoMove()
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

    private fun quiescence(ply: Int, alfa: Int, beta: Int): InternalSearchResult {
        ++nodesVisited
        if (cleverBoard.gameFinished()) {
            return InternalSearchResult("", cleverBoard.endValue(-1))
        }
        var moves = cleverBoard.generateMoves()
        if (moves.size > 1) {
            if (ply % 2 == leafLevel)
                return InternalSearchResult("", cleverBoard.evaluateFromPlayerToMovePerspective())
            moves = listOf(moves[0])
        }
        var bestValue = alfa
        for (move in moves) {
            cleverBoard.doMove(move)
            val searchResult = quiescence( ply+1, -beta, -bestValue)
            val value = -searchResult.evaluationValue
            cleverBoard.undoMove()
            if (value > bestValue) {
                bestValue = value
                if (bestValue > beta) {
                    break
                }
            }
        }
        return InternalSearchResult("", bestValue)
    }
}
