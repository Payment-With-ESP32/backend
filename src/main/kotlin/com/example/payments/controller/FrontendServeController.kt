package com.example.payments.controller

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class FrontendServeController: ErrorController {
    companion object {
        const val PATH = "/error"
    }

    @RequestMapping(PATH)
    fun handleError(): String = "forward:/index.html"

    fun getErrorPath(): String = PATH
}