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
        assertEquals(result, "------")
    }

    @Test
    fun testSeveralCorrectBoardString() {
        assertEquals(Board("------").toString(), "------")
        assertEquals(Board("x-x-o-x-o-o-o").toString(), "x-x-o-x-o-o-o")
        assertEquals(Board("xoxox-x-o-x-o--o").toString(), "xoxox-x-o-x-o--o")
    }

    @Test
    fun testWrongNumberOfColumns() {
        assertThrows<Exception> { Board("") }
        assertThrows<Exception> { Board("-------") }
    }

    @Test
    fun testTooManyStonesInColumn() {
        assertThrows<Exception> { Board("xoxoxox-x-x-o-o-o-x") }
    }

    @Test
    fun testWrongCharacters() {
        assertThrows<Exception> { Board("-------") }
        assertThrows<Exception> { Board("q-x-x-o-o-o-x") }
        assertThrows<Exception> { Board("x-X-x-o-o-o-x") }
        assertThrows<Exception> { Board("xoxoqox-x-x-o-o-o-x") }
        assertThrows<Exception> { Board("xoxo0ox-x-x-o-o-o-x") }
    }

    @Test
    fun testGetMoves() {
        assertEquals ( Board("------").getMoves(), listOf(0, 1, 2, 3, 4, 5, 6) )
        assertEquals ( Board("o------").getMoves(), listOf(7, 1, 2, 3, 4, 5, 6) )
        assertEquals ( Board("xo------").getMoves(), listOf(14, 1, 2, 3, 4, 5, 6) )
        assertEquals ( Board("xoxoxo------").getMoves(), listOf(1, 2, 3, 4, 5, 6) )
        assertEquals ( Board("xoxoxo-oxoxox--xxxooo-oooxxx-xxxooo-oooxxx").getMoves(), listOf(2) )
        assertEquals ( Board("xoxoxo-x-xoxoxo-o-xoxoxo--xoxoxo").getMoves(), listOf(8,10,5) )
        assertEquals ( Board("xoxoxo-xoxoxo-xoxoxo-xoxoxo-xoxoxo-xoxoxo-xoxoxo").getMoves(), emptyList<Int>() )
    }

    @Test
    fun testDoMove() {
        val board = Board("------")
        board.doMoveByColumn(0)
        board.doMoveByColumn(1)
        board.doMoveByColumn(0)
        board.doMoveByColumn(0)
        board.doMoveByColumn(6)
        assertEquals ( board.toString(),  "oox-x-----o")
    }

    @Test
    fun testIllegalMove() {
        val board = Board("oxoxox------")
        assertThrows<Exception> {board.doMoveByColumn(-1)}
        assertThrows<Exception> {board.doMoveByColumn(7)}
        assertThrows<Exception> {board.doMoveByColumn(0)}
    }

    @Test
    fun testConnect4() {
        var board = Board("oooo-x-x-x---")
        assertTrue(board.playerToMoveHasLost())
        board = Board("xxxx-o-o-o--o-")
        assertTrue(board.playerToMoveHasLost())
        board = Board("--ox-ox-ox-o-")
        assertTrue(board.playerToMoveHasLost())
        board = Board("-o-xo-ox-oox-xoxx-")
        assertTrue(board.playerToMoveHasLost())
    }


    @Test
    fun testEmptyMoveString() {
        val board = Board("------|")
        val result = board.toString()
        assertEquals(result, "------")
    }

    @Test
    fun testWrongMoveStringFormat() {
        assertThrows<Exception> { Board("------|h") }
        assertThrows<Exception> { Board("------|0") }
        assertThrows<Exception> { Board("------|A") }
        assertThrows<Exception> { Board("------| a") }
    }

    @Test
    fun testLegalMoveString() {
        val board = Board("------|abcdefg")
        val result = board.toString()
        assertEquals(result, "o-x-o-x-o-x-o")
    }

    @Test
    fun testLegalMoveStringOneColumn() {
        val board = Board("------|aaaaaa")
        val result = board.toString()
        assertEquals(result, "oxoxox------")
    }

    @Test
    fun testTooMuchInOneColumn() {
        assertThrows<Exception> { Board("oxoxox------|a") }
    }

    @Test
    fun testBoardStatusString() {
        val board = Board("------")
        board.doMoveByColumn(0)
        board.doMoveByColumn(1)
        board.doMoveByColumn(0)
        board.doMoveByColumn(0)
        board.doMoveByColumn(6)
        assertEquals ( board.toBoardStatusString(),  "------|abaag")
    }

    @Test
    fun testBoardStatusStringWithNotEmptyBoard() {
        val board = Board("----ox--")
        board.doMoveByColumn(0)
        board.doMoveByColumn(1)
        board.doMoveByColumn(0)
        board.doMoveByColumn(0)
        board.doMoveByColumn(6)
        assertEquals ( board.toBoardStatusString(),  "----ox--|abaag")
    }

}