package com.connect4.searchengine.bfs

class Node(var value: Int, val move: Int, val maxNode: Boolean) {
    var child: Node? = null;
    var sibling: Node? = null;

    fun update() {
        this.value = if (maxNode) maxValueChildren() else minValueChildren()
    }

    private fun maxValueChildren() : Int {
        var maxValue = -1000000;
        var childNode = child
        while (childNode != null) {
            if (childNode.value > maxValue)
                maxValue = childNode.value
            childNode = childNode.sibling
        }
        return maxValue;
    }

    private fun minValueChildren() : Int {
        var minValue = 1000000;
        var childNode = child
        while (childNode != null) {
            if (childNode.value < minValue)
                minValue = childNode.value
            childNode = childNode.sibling
        }
        return minValue;
    }

    fun getChildWithEqualValue(): Node {
        var currentNode = child!!
        while (currentNode.sibling != null && currentNode.value != this.value)
            currentNode = currentNode.sibling!!
        return currentNode!!
    }
}