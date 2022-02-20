package com.connect4.controller

import com.connect4.controller.model.BoardModel
import com.connect4.controller.model.ComputeStatusInfo
import com.connect4.game.DEFAULT_BOARD
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

const val BOARD_COOKIE = "BOARDSTATUS"
const val REQUESTPATH_BASE = "c4api/v1"

@RestController
@RequestMapping(REQUESTPATH_BASE)
class Connect4Controller @Autowired constructor(private val gameService: GameService) {

    @GetMapping("/board/")
    fun getBoard(@CookieValue(value = BOARD_COOKIE, defaultValue = DEFAULT_BOARD) boardStatusString: String): BoardModel {
        val (model, _) = gameService.getBoard(boardStatusString)
        return model
    }

    @PostMapping("/board/")
    fun newBoard(httpServletResponse: HttpServletResponse): BoardModel {
        val (model, persistanceString) =  gameService.getNewBoard()
        httpServletResponse.addCookie(getNewCookie(persistanceString))
        return model
    }

    @PostMapping("/move/{column}")
    fun doMove(httpServletResponse: HttpServletResponse,
               @CookieValue(value = BOARD_COOKIE, defaultValue = DEFAULT_BOARD) boardStatusString: String,
               @PathVariable(name = "column") column: Int): BoardModel {
        val (model, persistanceString) =  gameService.doMove(boardStatusString, column)
        httpServletResponse.addCookie(getNewCookie(persistanceString))
        return model
    }

    @PostMapping("/move/takeback/")
    fun takeBackLastMove(httpServletResponse: HttpServletResponse,
                         @CookieValue(value = BOARD_COOKIE, defaultValue = DEFAULT_BOARD) boardStatusString: String): BoardModel {
        val (model, persistanceString) =  gameService.takeBackLastMove(boardStatusString)
        httpServletResponse.addCookie(getNewCookie(persistanceString))
        return model
    }

    @PostMapping("/move/compute/{level}")
    fun computeAndExecuteNextMove(httpServletResponse: HttpServletResponse,
                                  @CookieValue(value = BOARD_COOKIE, defaultValue = DEFAULT_BOARD) boardStatusString: String,
                                  @PathVariable(name = "level") level: Int): BoardModel {
        val (model, persistanceString) =  gameService.computeAndExecuteNextMove(boardStatusString, level)
        httpServletResponse.addCookie(getNewCookie(persistanceString))
        return model
    }

    @GetMapping("/compute/info/")
    fun getComputeStatusInfo(@CookieValue(value = BOARD_COOKIE, defaultValue = DEFAULT_BOARD) boardStatusString: String): ComputeStatusInfo {
        return gameService.getComputeStatusInfo(boardStatusString)
    }


    private fun getNewCookie(persistanceString: String): Cookie {
        val cookie = Cookie(BOARD_COOKIE, persistanceString)
        cookie.maxAge = 3600*24*365
        cookie.path = "/"
        return cookie
    }
}


