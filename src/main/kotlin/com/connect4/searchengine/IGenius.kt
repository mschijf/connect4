package com.connect4.searchengine

import com.connect4.game.Board

interface IGenius {
    fun computeMove(board: Board, level: Int): SearchResult
}