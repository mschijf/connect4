package com.connect4.controller

import com.connect4.controller.model.BoardModel
import com.connect4.game.Board
import com.connect4.searchengine.IGenius
import com.connect4.searchengine.bfs.GeniusPN
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable

@Service
class GameService {
    var genius : IGenius = GeniusPN()

    fun getBoard(boardStatusString: String): Pair<BoardModel, String> {
        val board = Board(boardStatusString)
        return BoardModel(board) to board.toBoardStatusString()
    }

    fun getNewBoard(): Pair<BoardModel, String> {
        val board = Board()
        return BoardModel(board) to board.toBoardStatusString()
    }

    fun doMove(boardStatusString: String, column: Int): Pair<BoardModel, String> {
        val board = Board(boardStatusString)
        board.doMoveByColumn(column)
        return BoardModel(board) to board.toBoardStatusString()
    }

    fun takeBackLastMove(boardStatusString: String): Pair<BoardModel, String> {
        val board = Board(boardStatusString)
        board.undoMove()
        return BoardModel(board) to board.toBoardStatusString()
    }

    fun computeAndExecuteNextMove(boardStatusString: String, @PathVariable(name = "level") level: Int): Pair<BoardModel, String> {
        val board = Board(boardStatusString)

        val searchResult = genius.computeMove(board, level)
        if (searchResult.moveSequence.isEmpty()) {
            throw Exception("No move calculated")
        }
        board.doMoveByCoordinate(searchResult.moveSequence.first())
        return BoardModel(board, searchResult) to board.toBoardStatusString()
    }
}