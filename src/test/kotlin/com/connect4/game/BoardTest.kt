package com.connect4.game

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.lang.Exception

internal class BoardTest {

    @Test
    fun testEmptyBoardString() {
        val board = Board()
        val result = board.toString()
        assertEquals(result, "0_0_0_0_0_0_0")
    }

    @Test
    fun testSeveralCorrectBoardString() {
        assertEquals(Board("0_0_0_0_0_0_0").toString(), "0_0_0_0_0_0_0")
        assertEquals(Board("x_x_o_x_o_o_o").toString(), "x_x_o_x_o_o_o")
        assertEquals(Board("xoxox_x_o_x_o_0_o").toString(), "xoxox_x_o_x_o_0_o")
    }

    @Test
    fun testWrongNumberOfColumns() {
        assertThrows<Exception> { Board("") }
        assertThrows<Exception> { Board("0") }
        assertThrows<Exception> { Board("0_0_0_0_0_0_0_0") }
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
        assertEquals ( Board("0_0_0_0_0_0_0").getMoves(), listOf(0, 1, 2, 3, 4, 5, 6) )
        assertEquals ( Board("o_0_0_0_0_0_0").getMoves(), listOf(0, 1, 2, 3, 4, 5, 6) )
        assertEquals ( Board("xo_0_0_0_0_0_0").getMoves(), listOf(0, 1, 2, 3, 4, 5, 6) )
        assertEquals ( Board("xoxoxo_0_0_0_0_0_0").getMoves(), listOf(1, 2, 3, 4, 5, 6) )
        assertEquals ( Board("xoxoxo_xoxoxo_0_xoxoxo_xoxoxo_xoxoxo_xoxoxo").getMoves(), listOf(2) )
        assertEquals ( Board("xoxoxo_x_xoxoxo_o_xoxoxo_0_xoxoxo").getMoves(), listOf(1,3,5) )
        assertEquals ( Board("xoxoxo_xoxoxo_xoxoxo_xoxoxo_xoxoxo_xoxoxo_xoxoxo").getMoves(), emptyList<Int>() )
    }

    @Test
    fun testDoMove() {
        val board = Board("0_0_0_0_0_0_0")
        board.doMove(0)
        board.doMove(1)
        board.doMove(0)
        board.doMove(0)
        board.doMove(6)
        assertEquals ( board.toString(),  "oox_x_0_0_0_0_o")
    }
    @Test
    fun testIllegalMove() {
        val board = Board("oxoxox_0_0_0_0_0_0")
        assertThrows<Exception> {board.doMove(-1)}
        assertThrows<Exception> {board.doMove(7)}
        assertThrows<Exception> {board.doMove(0)}
    }
}