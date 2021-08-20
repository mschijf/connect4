package com.connect4.controller

import com.connect4.game.Board
import com.connect4.game.Color
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.github.jknack.handlebars.io.TemplateLoader
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageResource {

    val board = Board()

    @GetMapping("/")
    fun index(): String {
        val loader: TemplateLoader = ClassPathTemplateLoader("/handlebars", ".hbs")
        val handlebars = Handlebars(loader)
        val template: Template = handlebars.compile("connect4")

        val columns = listOf(Rows(0), Rows(1), Rows(2), Rows(3), Rows(4), Rows(5), Rows(6))
        val s = template.apply(Columns(columns))
        return s
    }

    @PostMapping("/move/{column}")
    fun doMove(@PathVariable(name = "column") column: Int): MovePlayed {
        val mvp = board.doMove(column)
        return MovePlayed(mvp.column, mvp.row, if (mvp.color == Color.White) "blue" else "red")
    }
}

data class Columns(val columns: List<Rows>)
class Rows(val col:Int) {
    val rows = Array<Int>(6)  {i -> col * 10 + (5-i) }
}

data class MovePlayed(val column: Int, val row: Int, val color: String)