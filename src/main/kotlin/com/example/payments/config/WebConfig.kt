package com.example.payments.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

//@Configuration
//class WebConfig: WebMvcConfigurer {
//    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
//        registry.addResourceHandler("/**/*")
//            .addResourceLocations("classpath:/static/")
//            .resourceChain(true)
//    }
//
//    override fun addViewControllers(registry: ViewControllerRegistry) {
//        registry.addViewController("/{spring:\\w+}")
//            .setViewName("forward:/index.html");
//        registry.addViewController("/**/{spring:\\w+}")
//            .setViewName("forward:/index.html");
//        registry.addViewController("/{spring:\\w+}/**{spring:?!(\\.js|\\.css)$}")
//            .setViewName("forward:/index.html");
//        super.addViewControllers(registry)
//    }
//}