package com.connect4.controller.model

import com.connect4.game.*

class BoardModel(board: Board)  {
    val numberOfRows = MAX_ROW
    val numberOfColumns = MAX_COL
    val fields = Array(MAX_ROW) { row -> Array(MAX_COL) { col -> FieldModel(col, row, board.getStoneColor(col, row)) } }
    val colorToMove = board.whoisToMove
    val columnsPlayable = Array(MAX_COL) {col -> ColumnsPlayableModel(col, col in board.getMoves())}
    val colorHasWon = if (!board.gameFinished()) Color.None else if (board.playerToMoveHasLost()) opponentColor(board.whoisToMove) else board.whoisToMove
    val gameFinished = board.gameFinished()
    val winningFields =  board.getWinningFields().map { f -> Coordinate(f.col, f.row) }.toList()
}

data class ColumnsPlayableModel(val col: Int, val enabled: Boolean)
data class FieldModel(val col: Int, val row: Int, val color: Color)

