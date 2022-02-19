package com.connect4.searchengine.bfs

import com.connect4.game.*
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
    private val MAX_NODES_PER_LEVEL =     500_000

    private var cleverBoard = CleverBoard(DEFAULT_BOARD)
    private var maxNodeColor = cleverBoard.whoisToMove
    private var root = Node(0, -1, true)
    private val nodeStack = Stack<Node>()

    private var newNodesCreated = 0
    private var totalNodesInTree = 0

    init {
        initTree(DEFAULT_BOARD)
    }

    private fun initTree(boardStatusString: String) {
        cleverBoard = CleverBoard(boardStatusString)
        maxNodeColor = cleverBoard.whoisToMove
        nodeStack.clear()
        root = Node(0, -1, true)
        totalNodesInTree = 1
    }

    private fun findRootChildNodeByBoardString(boardString: String): Node? {
        if (cleverBoard.toString() == boardString)
            return root

        var p = root.child
        while (p != null) {
            cleverBoard.doMove(p.move)
            val sameBoard = cleverBoard.toString() == boardString
            cleverBoard.undoMove()
            if (sameBoard) {
                return p
            }
            p = p.sibling
        }
        return null
    }

    private fun determineNewRoot(board: Board) {
        val newRoot = findRootChildNodeByBoardString(board.toString())
        if (newRoot == null) {
            initTree(board.toBoardStatusString())
        } else if (newRoot != root) {
            setRootToChild(newRoot)
        } else { //newRoot == root
            root = newRoot
            nodeStack.clear()
        }
    }

    //==================================================================================================================

    override fun computeMove(board: Board, level: Int): SearchResult {
        stopThreadThinking()
        determineNewRoot(board)
        val result = computeMove(level)
        startThreadThinking()
        return result
    }

    private fun computeMove(level: Int) : SearchResult {
        val start = Instant.now()
        val result = principalDeepeningSearch(level)
        val timePassed = Duration.between(start, Instant.now()).toMillis()
        val moveList = internalResultToMoveList(result)
        val value = if (root.maxNode) result.evaluationValue else -result.evaluationValue
        if (moveList.isNotEmpty()) {
            setRootToChild(root.getChildWithMove(toFieldIndex(moveList[0]))!!)
        }
        return SearchResult(moveList, value, newNodesCreated, timePassed)
    }

    private fun principalDeepeningSearch(level: Int): InternalSearchResult {
        newNodesCreated = 0
        var currentNode = root
        while (root.value > -7000 && root.value < 7000 && newNodesCreated < level*MAX_NODES_PER_LEVEL && totalNodesInTree < MAX_NODES_IN_MEMORY) {
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
            cleverBoard.undoMove()

            val newChild = Node(eval, move, !currentNode.maxNode)
            ++newNodesCreated
            ++totalNodesInTree
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

    private fun setRootToChild(rootChild: Node) {
        --totalNodesInTree
        var p = root.child
        while (p != null && p != rootChild) {
            val prev = p
            p = p.sibling
            val removed = removeSubTree(prev)
            totalNodesInTree -= removed
        }
        if (p == null) {
            throw Exception("can't find child")
        }

        p = p.sibling
        while (p != null) {
            val prev = p
            p = p.sibling
            val removed = removeSubTree(prev)
            totalNodesInTree -= removed
        }

        cleverBoard.doMove(rootChild.move)
        root.child = null
        root = rootChild
        root.sibling = null
        nodeStack.clear()
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
        while (running.get() && root.value > -7000 && root.value < 7000 && totalNodesInTree < MAX_NODES_IN_MEMORY) {
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
