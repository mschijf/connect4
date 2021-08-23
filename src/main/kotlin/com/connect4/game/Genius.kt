package com.connect4.game

class Genius(board: Board) {
    //copy board in order to prevent it from changing the board hat is connected for other purpopses (for instance front end)
    val board = Board(board.toString())

    fun computeMove() : SearchResult {
        val result = alfabeta(5, -1000000, 1000000)
        return result
    }

    private fun alfabeta(depth:Int, alfa: Int, beta: Int): SearchResult {
        if (board.gameFinished()) {
            return SearchResult(null, endValue(depth))
        }
        if (depth <= 0) {
            return SearchResult(null, evaluate())
        }
        var bestValue = alfa
        var bestMove:Int? = null
        val moves=board.getMoves()
        for (move in moves) {
            board.doMove(move)
            val value = -alfabeta(depth-1, -beta, -bestValue).evaluationValue
            board.undoMove()
            if (value > bestValue) {
                bestValue = value
                bestMove = move
                if (bestValue > beta) {
                    break
                }
            }
        }
        return SearchResult(bestMove, bestValue)
    }

    private fun endValue(depth: Int): Int {
        return if (board.playerToMoveHasLost()) -(1000 + depth) else 0
    }

    private fun evaluate(): Int {
        var whiteValue = 0
        var blackValue = 0
        for (group in board.allGroups) {
            when (group.countOfColor(Color.White)) {
                1 -> whiteValue += 1
                2 -> whiteValue += 3
                3 -> whiteValue += 7
            }
            when (group.countOfColor(Color.Black)) {
                1 -> blackValue += 1
                2 -> blackValue += 3
                3 -> blackValue += 7
            }
        }
        return if (board.whoisToMove == Color.White)
            whiteValue - blackValue
        else
            blackValue - whiteValue
    }
}

class SearchResult (val column: Int?, val evaluationValue: Int)