package com.connect4.searchengine

import com.connect4.game.Coordinate

data class SearchResult (val moveSequence: List<Coordinate>, val evaluationValue: Int, val nodesVisited: Int, val durationMillis: Long) {
    val nodesPerSecond = if (durationMillis <= 0) null else ((1.0*nodesVisited / durationMillis) * 1000).toInt()
}
