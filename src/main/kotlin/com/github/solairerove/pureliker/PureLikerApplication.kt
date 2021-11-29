package com.github.solairerove.pureliker

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PureLikerApplication : CommandLineRunner {

    override fun run(vararg args: String?) {
        println("hui")
    }
}

fun main(args: Array<String>) {
    runApplication<PureLikerApplication>(*args)
}
