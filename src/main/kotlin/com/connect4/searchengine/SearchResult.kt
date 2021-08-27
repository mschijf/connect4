package com.connect4.searchengine

import com.connect4.game.Coordinate

data class SearchResult (val moveSequence: List<Coordinate>, val evaluationValue: Int, val nodesVisited: Int, val durationMillis: Long)
