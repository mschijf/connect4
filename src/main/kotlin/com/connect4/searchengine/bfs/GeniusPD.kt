package com.connect4.searchengine.bfs

import com.connect4.game.Board
import com.connect4.game.Coordinate
import com.connect4.game.DEFAULT_BOARD
import com.connect4.game.toCoordinate
import com.connect4.searchengine.CleverBoard
import com.connect4.searchengine.IGenius
import com.connect4.searchengine.SearchResult
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class GeniusPD : IGenius, Runnable {

    data class InternalSearchResult (val moveSequence:String, val evaluationValue: Int)

    private fun internalResultToMoveList(internalResult: InternalSearchResult): List<Coordinate> {
        return internalResult.moveSequence
            .split('-')
            .filter{s -> s.isNotEmpty()}
            .map{ s -> toCoordinate(s.toInt()) }
            .toList()
    }


    private val MAX_NODES_IN_MEMORY = 500_000_000
    private val MAX_NODES_PER_LEVEL =    500_000

    private var cleverBoard = CleverBoard(DEFAULT_BOARD)
    private var maxNodeColor = cleverBoard.whoisToMove
    private var root = Node(0, -1, true)
    private val nodeStack = Stack<Node>()

    private var newNodesCreated = 0
    private var totalNodesCreated = 0

    init {
        initSearchTree()
    }

    private fun initSearchTree() {
        maxNodeColor = cleverBoard.whoisToMove
        nodeStack.clear()
        root = Node(0, -1, true)
        totalNodesCreated = 1
    }

    private fun setBoard(board: Board) {
        stopThreadThinking()

        val newRoot = findNodeByBoardString(board.toString())
        if (newRoot == null) {
            root = Node(0, -1, true)
            cleverBoard = CleverBoard(board.toBoardStatusString())
            maxNodeColor = cleverBoard.whoisToMove
            totalNodesCreated = 1
        } else {
            if (newRoot != root) {
                removeParentAndSiblingsOfRootChild(newRoot)
            }
            root = newRoot
        }
        nodeStack.clear()
    }

    override fun computeMove(board: Board, level: Int): SearchResult {
        setBoard(board)
        return computeMove(level)
    }

    private fun computeMove(level: Int) : SearchResult {
        newNodesCreated = 0
        val start = Instant.now()
        val result = principalDeepeningSearch(level)
        val timePassed = Duration.between(start, Instant.now()).toMillis()
        val moveList = internalResultToMoveList(result)

        if (root.child != null)
            removeParentAndSiblingsOfRootChild(root.getChildWithEqualValue()!!)

        startThreadThinking()

        return SearchResult(moveList, result.evaluationValue, newNodesCreated, timePassed)
    }

    private fun principalDeepeningSearch(level: Int): InternalSearchResult {
        var currentNode = root
        while (root.value > -7000 && root.value < 7000 && newNodesCreated < level*MAX_NODES_PER_LEVEL && totalNodesCreated < MAX_NODES_IN_MEMORY) {
            currentNode = gotoMostPromisingNode(currentNode)
            expand(currentNode)
            currentNode = updateAncestors(currentNode)
        }
        backToRoot()
        return InternalSearchResult(getMoveSequence(), root.value)
    }

    private fun goDown(parent: Node, child: Node): Node {
        nodeStack.push(parent)
        cleverBoard.doMove(child.move)
        return child
    }

    private fun goUp(): Node {
        cleverBoard.undoMove()
        return nodeStack.pop()
    }

    private fun backToRoot() {
        while (!nodeStack.isEmpty()) {
            cleverBoard.undoMove()
            nodeStack.pop()
        }
    }

    private fun gotoMostPromisingNode(currentNode: Node): Node {
        var p = currentNode
        while (p.child != null) {
            p = goDown(p, p.getChildWithEqualValue()!!)
        }
        return p
    }

    private fun expand(currentNode: Node) {
        var lastChild: Node? = null
        val moves = cleverBoard.generateMoves()
        for (move in moves) {
            cleverBoard.doMove(move)

            val eval = cleverBoard.evaluateFromColorPerspective(maxNodeColor)
            val newChild = Node(eval, move, !currentNode.maxNode)
            ++newNodesCreated
            ++totalNodesCreated

            cleverBoard.undoMove()
            if (lastChild == null)
                currentNode.child = newChild
            else
                lastChild.sibling = newChild
            lastChild = newChild
        }
    }

    private fun updateAncestors(currentNode: Node): Node {
        var p = currentNode
        var currentValue = p.value
        p.update()
        while (p != root && currentValue != p.value) {
            p = goUp()
            currentValue = p.value
            p.update()
        }
        return p
    }

    private fun getMoveSequence() : String {
        var p = root
        var moveSequence = ""
        while (p.child != null) {
            val bestChild =
                if ((p.maxNode && p.value <= -8000) || (!p.maxNode && p.value >= 8000)) {
                    p.getChildWithEqualValueAndBiggestSubTree()
                } else if ((p.maxNode && p.value >= 8000) || (!p.maxNode && p.value <= -8000)) {
                    p.getChildWithEqualValueAndSmallestSubTree()
                } else {
                    p.getChildWithEqualValue()
                }
            moveSequence = moveSequence + bestChild?.move.toString() + "-"
            p = bestChild!!
        }
        return moveSequence
    }

    //==================================================================================================================

    private fun removeParentAndSiblingsOfRootChild(child: Node) {
        --totalNodesCreated
        var p = root.child
        while (p != null && p != child) {
            val prev = p
            p = p.sibling
            val removed = removeSubTree(prev)
            totalNodesCreated -= removed
        }
        if (p == null) {
            throw Exception("can't find child")
        }

        p = p.sibling
        while (p != null) {
            val prev = p
            p = p.sibling
            val removed = removeSubTree(prev)
            totalNodesCreated -= removed
        }

        cleverBoard.doMove(child.move)
        root.child = null
        root = child
        root.sibling = null
    }

    private fun removeSubTree(subTreeRoot: Node): Int {
        var removed = 1
        subTreeRoot.sibling = null
        var p = subTreeRoot.child
        subTreeRoot.child = null
        while (p != null) {
            val next = p.sibling
            p.sibling = null
            removed += removeSubTree(p)
            p = next
        }
        return removed
    }

    private fun findNodeByBoardString(boardString: String): Node? {
        if (cleverBoard.toString() == boardString)
            return root

        var p = root.child
        while (p != null) {
            cleverBoard.doMove(p.move)
            if (cleverBoard.toString() == boardString) {
                cleverBoard.undoMove()
                return p
            }
            cleverBoard.undoMove()
            p = p.sibling
        }
        return null
    }

    //==================================================================================================================

    private var worker: Thread? = null
    private val running = AtomicBoolean(false)
    private val threadStillRunning = AtomicBoolean(false)

    private fun startThreadThinking() {
        worker = Thread(this)
        worker!!.start()
    }

    private fun stopThreadThinking() {
        running.set(false)
        while(threadStillRunning.get()) {
            Thread.sleep(1)
        }
    }

    override fun run() {
        threadStillRunning.set(true)
        running.set(true)
        var currentNode = root
        while (running.get() && root.value > -7000 && root.value < 7000 && totalNodesCreated < MAX_NODES_IN_MEMORY) {
            try {
                currentNode = gotoMostPromisingNode(currentNode)
                expand(currentNode)
                currentNode = updateAncestors(currentNode)
            } catch (e: InterruptedException){
                Thread.currentThread().interrupt()
                println("Thread was interrupted, Failed to complete operation")
            }
        }
        backToRoot()
        threadStillRunning.set(false)
    }
}
