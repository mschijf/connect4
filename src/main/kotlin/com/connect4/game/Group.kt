package com.connect4.game

enum class GroupType {
    horizontal, vertical, diagonalSWNE, diagonalNWSE
}

class Group (val fields: List<Field>, val groupType: GroupType) {
    fun completeWithColor(color: Color) : Boolean = fields.all { f -> f.stone == color}
    fun empty() : Boolean = fields.all { f -> f.stone == Color.None}

    private var whiteCount = 0
    private var blackCount = 0

    fun fieldColorChanged(oldColor: Color, newColor: Color) {
        when(oldColor) {
            Color.White -> --whiteCount
            Color.Black -> --blackCount
        }
        when(newColor) {
            Color.White -> ++whiteCount
            Color.Black -> ++blackCount
        }
    }

    fun countOfColor(color: Color) : Int {
        if (color == Color.White)
            return whiteCount
        else
            return blackCount
    }



}