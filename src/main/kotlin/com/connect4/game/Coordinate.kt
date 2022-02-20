package com.connect4.game

data class Coordinate(val col: Int, val row: Int) {
    override fun toString(): String {
        return ('a' + col).toString() + ('1' + row).toString()
    }
}
