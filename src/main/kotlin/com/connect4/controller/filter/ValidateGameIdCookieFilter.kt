package com.connect4.controller.filter

import com.connect4.controller.GameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import javax.servlet.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val COOKIE_NAME = "GAMEID"
const val ATTRIBUTE_NAME = "GAMEID"

@Component
class ValidateGameIdCookieFilter @Autowired constructor(private val gameService: GameService) : Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        var cookie = readGameCookie(request as HttpServletRequest)
        if (cookie == null) {
            println("No cookie yet, set new cookie")
            cookie = createGameCookie(response as HttpServletResponse, gameService.getNewGameId())
        } else if (isNotInteger(cookie.value)) {
            println("Unexpected value in cookie $COOKIE_NAME: '${cookie.value}'. Create new value")
            cookie = createGameCookie(response as HttpServletResponse, gameService.getNewGameId())
        }
        request.setAttribute(ATTRIBUTE_NAME, cookie.value.toInt())
        chain.doFilter(request, response)
    }

    private fun isNotInteger(s: String): Boolean {
        return try {
            s.toInt()
            false
        } catch (e: NumberFormatException) {
            true
        }
    }

    private fun readGameCookie(request: HttpServletRequest): Cookie? {
        if (request.cookies == null)
            return null
        return Arrays.stream(request.cookies)
            .filter { c -> c.name == COOKIE_NAME}
            .findAny()
            .orElse(null)
    }

    private fun createGameCookie(response:HttpServletResponse, newGameId: Int): Cookie {
        val newCookie = Cookie(COOKIE_NAME, newGameId.toString())
        newCookie.maxAge = 3600
        newCookie.path = "/"
        response.addCookie(newCookie)
        return newCookie
    }
}