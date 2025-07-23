package com.example.payments.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class FrontendServeController {
    @RequestMapping(value = ["/{path:^(?!api)(?!.*\\.).*$}"], method = [RequestMethod.GET])
    fun mainHtml() = "index.html"
}