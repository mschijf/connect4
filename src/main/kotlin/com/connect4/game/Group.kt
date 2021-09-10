package com.connect4.game

enum class GroupType {
    Horizontal, Vertical, DiagonalSWNE, DiagonalNWSE
}

class Group (val fields: List<Field>, val groupType: GroupType) {
    private var whiteCount = 0
    private var blackCount = 0

    fun completeWithColor(color: Color) : Boolean = countOfColor(color) == fields.size

    fun fieldColorChanged(oldColor: Color, newColor: Color) {
        when(oldColor) {
            Color.White -> --whiteCount
            Color.Black -> --blackCount
            else -> {}
        }
        when(newColor) {
            Color.White -> ++whiteCount
            Color.Black -> ++blackCount
            else -> {}
        }
    }

    fun countOfColor(color: Color) = if (color == Color.White) whiteCount else blackCount
    fun getFirstEmptyField(): Field = fields.first { f -> f.stone == Color.None }


}