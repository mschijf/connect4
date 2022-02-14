package com.connect4.searchengine.bfs

import com.connect4.game.*
import com.connect4.searchengine.CleverBoard
import com.connect4.searchengine.IGenius
import com.connect4.searchengine.SearchResult
import java.time.Duration
import java.time.Instant
import java.util.*

class GeniusAStar(): IGenius {

    data class InternalSearchResult (val moveSequence:String, val evaluationValue: Int)

    private var cleverBoard = CleverBoard()
    private val rootColor = cleverBoard.whoisToMove
    private val root = Node(0, -1, true)
    private var currentNode = root
    private val nodeStack = Stack<Node>()
    private var nodesVisited = 0

    override fun setBoard(board: Board) {
        cleverBoard = CleverBoard(board)
    }

    override fun computeMove(level:Int) : SearchResult {
        nodesVisited = 0
        val start = Instant.now()
        val result = aStarSearch()
        val timePassed = Duration.between(start, Instant.now()).toMillis()
        val moveList = result.moveSequence
            .split('-')
            .filter{s -> s.isNotEmpty()}
            .map{ s -> toCoordinate(s.toInt()) }
            .toList()
        return SearchResult(moveList, result.evaluationValue, nodesVisited, timePassed)
    }

    private fun aStarSearch(): InternalSearchResult {
        root.value = cleverBoard.evaluate()
        currentNode = root
        while (root.value > -1000 && root.value < 1000 && nodesVisited < 1000000) {
            gotoMostPromisingNode()
            expand()
            updateAncestors()
        }
        return InternalSearchResult(getMoveSequence(), root.value)
    }

    private fun gotoMostPromisingNode() {
        while (currentNode.child != null) {
            nodeStack.push(currentNode)
            currentNode = currentNode.getChildWithEqualValue()
            cleverBoard.doMove(currentNode.move)
        }
    }

    private fun expand() {
        var lastChild: Node? = null;
        val moves = cleverBoard.generateMoves();
        for (move in moves) {
            nodesVisited++
            cleverBoard.doMove(move);

            ///LET OP: BIJ END NODE MOET JE DE NODE ALS ENDNODE OF IETS MARKEREN!!!
            var eval = if (cleverBoard.gameFinished()) cleverBoard.endValue(0) else cleverBoard.evaluate()
            eval = if (rootColor == cleverBoard.whoisToMove) eval else -eval
            val newChild = Node(eval, move, !currentNode.maxNode)
            cleverBoard.undoMove();
            if (lastChild == null)
                currentNode.child = newChild
            else
                lastChild.sibling = newChild
            lastChild = newChild
        }
    }

    private fun updateAncestors() {
        var currentValue = currentNode.value
        currentNode.update()
        while (currentNode != root) {
            if (currentValue == currentNode.value)
                return
            currentNode = nodeStack.pop()
            cleverBoard.undoMove()
            currentValue = currentNode.value
            currentNode.update()
        }
    }

    private fun getMoveSequence() : String {
        var p = root
        var moveSequence = ""
        while (p.child != null) {
            p = p.getChildWithEqualValue()
            moveSequence = moveSequence + p.move.toString() + "-"
        }
        return moveSequence
    }
}
