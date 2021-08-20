package com.connect4.game

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.lang.Exception

internal class BoardTest {

    @Test
    fun testEmptyBoardString() {
        val board = Board()
        val result = board.toString()
        assertEquals(result, "______")
    }

    @Test
    fun testSeveralCorrectBoardString() {
        assertEquals(Board("______").toString(), "______")
        assertEquals(Board("x_x_o_x_o_o_o").toString(), "x_x_o_x_o_o_o")
        assertEquals(Board("xoxox_x_o_x_o__o").toString(), "xoxox_x_o_x_o__o")
    }

    @Test
    fun testWrongNumberOfColumns() {
        assertThrows<Exception> { Board("") }
        assertThrows<Exception> { Board("_______") }
    }

    @Test
    fun testTooManyStonesInColumn() {
        assertThrows<Exception> { Board("xoxoxox_x_x_o_o_o_x") }
    }

    @Test
    fun testWrongCharacters() {
        assertThrows<Exception> { Board("_______") }
        assertThrows<Exception> { Board("q_x_x_o_o_o_x") }
        assertThrows<Exception> { Board("x_X_x_o_o_o_x") }
        assertThrows<Exception> { Board("xoxoqox_x_x_o_o_o_x") }
        assertThrows<Exception> { Board("xoxo0ox_x_x_o_o_o_x") }
    }

    @Test
    fun testGetMoves() {
        assertEquals ( Board("______").getMoves(), listOf(0, 1, 2, 3, 4, 5, 6) )
        assertEquals ( Board("o______").getMoves(), listOf(0, 1, 2, 3, 4, 5, 6) )
        assertEquals ( Board("xo______").getMoves(), listOf(0, 1, 2, 3, 4, 5, 6) )
        assertEquals ( Board("xoxoxo______").getMoves(), listOf(1, 2, 3, 4, 5, 6) )
        assertEquals ( Board("xoxoxo_xoxoxo__xoxoxo_xoxoxo_xoxoxo_xoxoxo").getMoves(), listOf(2) )
        assertEquals ( Board("xoxoxo_x_xoxoxo_o_xoxoxo__xoxoxo").getMoves(), listOf(1,3,5) )
        assertEquals ( Board("xoxoxo_xoxoxo_xoxoxo_xoxoxo_xoxoxo_xoxoxo_xoxoxo").getMoves(), emptyList<Int>() )
    }

    @Test
    fun testDoMove() {
        val board = Board("______")
        board.doMove(0)
        board.doMove(1)
        board.doMove(0)
        board.doMove(0)
        board.doMove(6)
        assertEquals ( board.toString(),  "oox_x_____o")
    }

    @Test
    fun testIllegalMove() {
        val board = Board("oxoxox______")
        assertThrows<Exception> {board.doMove(-1)}
        assertThrows<Exception> {board.doMove(7)}
        assertThrows<Exception> {board.doMove(0)}
    }

    @Test
    fun testConnect4() {
        var board = Board("oooo_x_x_x___")
        assertTrue(board.playerToMoveHasLost())
        board = Board("xxxx_o_o_o__o_")
        assertTrue(board.playerToMoveHasLost())
        board = Board("__ox_ox_ox_o_")
        assertTrue(board.playerToMoveHasLost())
        board = Board("_o_xo_ox_oox_xoxx_")
        assertTrue(board.playerToMoveHasLost())
    }

}