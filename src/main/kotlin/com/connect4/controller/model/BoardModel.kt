package com.connect4.controller.model

import com.connect4.game.*

class BoardModel(board: Board, val searchResult: SearchResult?=null)  {
    val numberOfRows = MAX_ROW
    val numberOfColumns = MAX_COL
    val fields = Array(MAX_ROW) { row -> Array(MAX_COL) { col -> FieldModel(col, row, board.getStoneColor(col, row), fieldPlayable(board, col, row)) } }
    val colorToMove = board.whoisToMove
    val colorHasWon =
        if (board.gameFinished())
            if (board.playerToMoveHasLost())
                opponentColor(board.whoisToMove)
            else
                Color.None
        else
            Color.None
    val gameFinished = board.gameFinished()
    val winningFields =  board.getWinningFields().map { f -> Coordinate(f.col, f.row) }.toList()
    val takeBackPossible = board.lastFieldPlayed() != null
    val lastFieldPlayed = board.lastFieldPlayed()

    private fun fieldPlayable(board: Board, col: Int, row: Int): Boolean {
        if (board.getStoneColor(col, row) == Color.None)
            return (row == 0 || board.getStoneColor(col, row-1) != Color.None)
        return false
    }

}

data class FieldModel(val col: Int, val row: Int, val color: Color, val playable: Boolean)

