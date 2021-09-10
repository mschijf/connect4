package com.connect4.controller

import com.connect4.controller.model.BoardModel
import com.connect4.game.Board
import com.connect4.searchengine.Genius
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

const val MAX_GAME_ID = 1000000000

@Service
class GameService {
    private val boardMapper: MutableMap<Int, Board> = mutableMapOf()

    fun getNewGameId (): Int {
        var gameId: Int
        do {
            gameId = Random().nextInt(MAX_GAME_ID)
        } while (gameId in boardMapper.keys)
        return gameId
    }

    fun getBoard(gameId: Int): BoardModel {
        val board = getBoardFromMapper(gameId)
        return BoardModel(board)
    }

    fun getNewBoard(gameId: Int): BoardModel {
        val board = createNewBoardInMapper(gameId)
        return BoardModel(board)
    }

    fun doMove(gameId: Int, column: Int): BoardModel {
        val board = getBoardFromMapper(gameId)
        board.doMoveByColumn(column)
        return BoardModel(board)
    }

    fun takeBackLastMove(gameId: Int): BoardModel {
        val board = getBoardFromMapper(gameId)
        board.undoMove()
        return BoardModel(board)
    }

    fun computeAndExecuteNextMove(gameId: Int, @PathVariable(name = "level") level: Int): BoardModel {
        val board = getBoardFromMapper(gameId)
        val searchResult = Genius(board).computeMove(level)
        if (searchResult.moveSequence.isEmpty()) {
            throw Exception("No move calculated")
        }
        board.doMoveByCoordinate(searchResult.moveSequence.first())
        return BoardModel(board, searchResult)
    }

    private fun getBoardFromMapper(gameId: Int): Board {
        return boardMapper[gameId] ?: createNewBoardInMapper(gameId)
    }

    private fun createNewBoardInMapper(gameId: Int): Board {
        val board = Board()
        boardMapper[gameId] = board
        return board
    }

}