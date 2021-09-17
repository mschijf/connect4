package com.connect4.controller

import com.connect4.controller.model.BoardModel
import com.connect4.game.Board
import com.connect4.game.DEFAULT_BOARD
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
class Connect4HTMLController @Autowired constructor(private val gameService: GameService) {

    @GetMapping("/")
    fun index(): String {
        val loader: TemplateLoader = ClassPathTemplateLoader("/handlebars", ".hbs")
        val handlebars = Handlebars(loader)
        val template: Template = handlebars.compile("connect4")
        return template.apply(BoardModel(Board())) //todo: apply with smaller object: only board.fields and nrOfRows or something like that
    }

}


