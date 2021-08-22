package com.connect4.controller

import com.connect4.controller.model.BoardModel
import com.connect4.game.Board
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.github.jknack.handlebars.io.TemplateLoader
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Connect4Controller {

    var board = Board()

    @GetMapping("/")
    fun index(): String {
        val loader: TemplateLoader = ClassPathTemplateLoader("/handlebars", ".hbs")
        val handlebars = Handlebars(loader)
        val template: Template = handlebars.compile("connect4")
        return template.apply(BoardModel(board))
    }

    @GetMapping("/board/")
    fun getBoard(): BoardModel {
        return BoardModel(board)
    }

    @PostMapping("/board/")
    fun newBoard(): BoardModel {
        board = Board()
        return BoardModel(board)
    }

    @PostMapping("/move/{column}")
    fun doMove(@PathVariable(name = "column") column: Int): BoardModel {
        board.doMove(column)
        return BoardModel(board)
    }

    @PostMapping("/move/compute")
    fun computeAndExecuteNextMove(): BoardModel {
        val mvp = board.computeMove()
        board.doMove(mvp.column)
        return BoardModel(board)
    }
}
