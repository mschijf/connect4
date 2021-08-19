package com.connect4

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class Connect4Application

fun main(args: Array<String>) {
    runApplication<Connect4Application>(*args)
}

