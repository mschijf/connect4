package com.connect4.game

class Group (private val fields: List<Field>) {
    fun completeWithColor(color: Color) : Boolean = fields.all { f -> f.stone == color}
    fun empty() : Boolean = fields.all { f -> f.stone == Color.None}

    fun countOfColor(color: Color) : Int {
        if (fields.any { f -> f.stone == opponentColor(color)})
            return 0
        return fields.count { f -> f.stone == color }
    }
}