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

const val BOARD_COOKIE = "BOARDSTATUS"

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
    fun getBoard(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse,
                 @CookieValue(value = BOARD_COOKIE, defaultValue = DEFAULT_BOARD) boardStatusString: String): BoardModel {
        val (model, persistanceString) = gameService.getBoard(boardStatusString)
        httpServletResponse.addCookie(getNewCookie(persistanceString))
        return model
    }

    @PostMapping("/board/")
    fun newBoard(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse): BoardModel {
        val (model, persistanceString) =  gameService.getNewBoard()
        httpServletResponse.addCookie(getNewCookie(persistanceString))
        return model
    }

    @PostMapping("/move/{column}")
    fun doMove(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse,
               @CookieValue(value = BOARD_COOKIE, defaultValue = DEFAULT_BOARD) boardStatusString: String,
               @PathVariable(name = "column") column: Int): BoardModel {
        val (model, persistanceString) =  gameService.doMove(boardStatusString, column)
        httpServletResponse.addCookie(getNewCookie(persistanceString))
        return model
    }

    @PostMapping("/move/takeback/")
    fun takeBackLastMove(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse,
                         @CookieValue(value = BOARD_COOKIE, defaultValue = DEFAULT_BOARD) boardStatusString: String): BoardModel {
        val (model, persistanceString) =  gameService.takeBackLastMove(boardStatusString)
        httpServletResponse.addCookie(getNewCookie(persistanceString))
        return model
    }

    @PostMapping("/move/compute/{level}")
    fun computeAndExecuteNextMove(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse,
                                  @CookieValue(value = BOARD_COOKIE, defaultValue = DEFAULT_BOARD) boardStatusString: String,
                                  @PathVariable(name = "level") level: Int): BoardModel {
        val (model, persistanceString) =  gameService.computeAndExecuteNextMove(boardStatusString, level)
        httpServletResponse.addCookie(getNewCookie(persistanceString))
        return model
    }

    private fun getNewCookie(persistanceString: String): Cookie {
        val cookie = Cookie(BOARD_COOKIE, persistanceString)
        cookie.maxAge = 3600*24*365
        cookie.path = "/"
        return cookie
    }
}


