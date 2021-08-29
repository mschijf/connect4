package com.connect4.controller

import com.connect4.controller.model.BoardModel
import com.connect4.game.Board
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.github.jknack.handlebars.io.TemplateLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Connect4Controller @Autowired constructor(private val gameService: GameService) {

    @GetMapping("/")
    fun index(): String {
        val loader: TemplateLoader = ClassPathTemplateLoader("/handlebars", ".hbs")
        val handlebars = Handlebars(loader)
        val template: Template = handlebars.compile("connect4")
        return template.apply(BoardModel(Board())) //todo: apply with smaller object: only board.fields and nrOfRows or something like that
    }

    @GetMapping("/board/")
    fun getBoard(): BoardModel {
        return gameService.getBoard(1)
    }

    @PostMapping("/board/")
    fun newBoard(): BoardModel {
        return gameService.getNewBoard(1)
    }

    @PostMapping("/move/{column}")
    fun doMove(@PathVariable(name = "column") column: Int): BoardModel {
        return gameService.doMove(1, column)
    }

    @PostMapping("/move/takeback/")
    fun takeBackLastMove(): BoardModel {
        return gameService.takeBackLastMove(1)
    }

    @PostMapping("/move/compute/{level}")
    fun computeAndExecuteNextMove(@PathVariable(name = "level") level: Int): BoardModel {
        return gameService.computeAndExecuteNextMove(1, level)
    }
}


