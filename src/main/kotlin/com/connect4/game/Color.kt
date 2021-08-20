package com.connect4.game

enum class Color {
    White, Black, None
}

fun opponentColor(color: Color) = if (color == Color.White) Color.Black else if (color == Color.Black) Color.White else Color.None