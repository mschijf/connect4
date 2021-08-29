package com.connect4.game

enum class Color {
    White, Black, None
}

fun opponentColor(color: Color) = when (color) {
    Color.White -> Color.Black
    Color.Black -> Color.White
    else -> Color.None
}