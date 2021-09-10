package com.connect4.controller

import com.connect4.controller.filter.ATTRIBUTE_NAME
import com.connect4.controller.model.BoardModel
import com.connect4.game.Board
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.github.jknack.handlebars.io.TemplateLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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
    fun getBoard(httpServletRequest: HttpServletRequest): BoardModel {
        val gameId = httpServletRequest.getAttribute(ATTRIBUTE_NAME) as Int
        return gameService.getBoard(gameId)
    }

    @PostMapping("/board/")
    fun newBoard(httpServletRequest: HttpServletRequest): BoardModel {
        val gameId = httpServletRequest.getAttribute(ATTRIBUTE_NAME) as Int
        return gameService.getNewBoard(gameId)
    }

    @PostMapping("/move/{column}")
    fun doMove(httpServletRequest: HttpServletRequest, @PathVariable(name = "column") column: Int): BoardModel {
        val gameId = httpServletRequest.getAttribute(ATTRIBUTE_NAME) as Int
        return gameService.doMove(gameId, column)
    }

    @PostMapping("/move/takeback/")
    fun takeBackLastMove(httpServletRequest: HttpServletRequest): BoardModel {
        val gameId = httpServletRequest.getAttribute(ATTRIBUTE_NAME) as Int
        return gameService.takeBackLastMove(gameId)
    }

    @PostMapping("/move/compute/{level}")
    fun computeAndExecuteNextMove(httpServletRequest: HttpServletRequest, @PathVariable(name = "level") level: Int): BoardModel {
        val gameId = httpServletRequest.getAttribute(ATTRIBUTE_NAME) as Int
        return gameService.computeAndExecuteNextMove(gameId, level)
    }
}


