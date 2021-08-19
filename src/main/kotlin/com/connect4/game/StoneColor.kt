package com.connect4.game

enum class StoneColor(val character: Char) {
    Black('x'), White('o');
}

val StoneColorCharacterSet = StoneColor.values().map { v -> v.character }.toSet()

fun charToStoneColor(ch:Char) : StoneColor {
    when (ch) {
        StoneColor.Black.character -> return StoneColor.Black
        StoneColor.White.character -> return StoneColor.White
    }
    throw Exception("Illegal stone character")
}

