package com.connect4.controller

import com.connect4.controller.model.BoardModel
import com.connect4.game.Board
import com.connect4.searchengine.Genius
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable

@Service
class GameService {
    private var board = Board()

    fun getBoard(gameId: Int): BoardModel {
        return BoardModel(board)
    }

    fun getNewBoard(gameId: Int): BoardModel {
        board = Board()
        return BoardModel(board)
    }

    fun doMove(gameId: Int, column: Int): BoardModel {
        board.doMoveByColumn(column)
        return BoardModel(board)
    }

    fun takeBackLastMove(gameId: Int): BoardModel {
        board.undoMove()
        return BoardModel(board)
    }

    fun computeAndExecuteNextMove(gameId: Int, @PathVariable(name = "level") level: Int): BoardModel {
        val searchResult = Genius(board).computeMove(level)
        if (searchResult.moveSequence.isEmpty()) {
            throw Exception("No move calculated")
        }
        board.doMoveByCoordinate(searchResult.moveSequence.first())
        return BoardModel(board, searchResult)
    }


}