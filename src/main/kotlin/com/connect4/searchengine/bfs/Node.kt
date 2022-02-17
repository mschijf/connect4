package com.connect4.searchengine.bfs

class Node(var value: Int, val move: Int, val maxNode: Boolean) {
    var child: Node? = null
    var sibling: Node? = null

    fun update() {
        this.value = if (maxNode) maxValueChildren() else minValueChildren()
    }

    private fun maxValueChildren() : Int {
        var maxValue = -999999
        var childNode = child
        while (childNode != null) {
            if (childNode.value > maxValue)
                maxValue = childNode.value
            childNode = childNode.sibling
        }
        return maxValue
    }

    private fun minValueChildren() : Int {
        var minValue = 999999
        var childNode = child
        while (childNode != null) {
            if (childNode.value < minValue)
                minValue = childNode.value
            childNode = childNode.sibling
        }
        return minValue
    }

    fun getChildWithEqualValue(): Node? {
        var currentNode = child
        while (currentNode != null && currentNode.value != this.value)
            currentNode = currentNode.sibling
        return currentNode
    }

    private fun getAllChildsWithEqualValue(): List<Node> {
        val resultList = mutableListOf<Node>()
        var currentNode = child
        while (currentNode != null) {
            if (currentNode.value == this.value)
                resultList += currentNode
            currentNode = currentNode.sibling
        }
        return resultList
    }

    fun getChildWithEqualValueAndBiggestSubTree(): Node? {
        var bestChild = child
        var max = -1
        for (child in getAllChildsWithEqualValue()) {
            val childTreeCount = nodeCount(child)
            if (childTreeCount > max) {
                max = childTreeCount
                bestChild = child
            }
        }
        return bestChild
    }

    fun getChildWithEqualValueAndSmallestSubTree(): Node? {
        var bestChild = child
        var min = 999_999_999
        for (child in getAllChildsWithEqualValue()) {
            val childTreeCount = nodeCount(child)
            if (childTreeCount < min) {
                min = childTreeCount
                bestChild = child
            }
        }
        return bestChild
    }

    private fun nodeCount(root: Node): Int {
        var count = 1
        var p = root.child
        while (p != null) {
            count += nodeCount(p)
            p = p.sibling
        }
        return count
    }



}