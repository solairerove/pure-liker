package com.github.solairerove.pureliker

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/*
Это код с блядского риакта браузерной верссии блядского аппа.
надо понять как синхронизировать время и как генерить хмак токен, чтобы ходить с токеном на апи
Далее дело воощбе за малым. Это основная проблема.

calculateAuthorizationHash(type, url, requestBody, userId) {
    // Хак, т.к. для истории чатов нужен hmac от get /me
    // Нужно будет добавить проброс авторизации извне
    if (url.includes(this.chatApiUrl)) {
      url = 'me'
      type = 'get'
      requestBody = null
    }

    const httpMethod = (type || 'get').toUpperCase();
    const serverTime = this.getServerTime();
    const body = (!requestBody || requestBody === '{}') ? '' : unescape(encodeURIComponent(requestBody));

    const hash = Hash.hmac(Hash.sha256, this.storage.get('sessionToken'))
      .update(`${
        httpMethod
      }+${
        (url[0] === '/') ? url : `/${url}`
      }+${
        body
      }+${
        serverTime
      }`)
      .digest('hex');
    return `hmac ${userId}:${serverTime}:${hash}`;
  }
 */
@SpringBootApplication
class PureLikerApplication : CommandLineRunner {

    override fun run(vararg args: String?) {
        println("hui")
    }
}

fun main(args: Array<String>) {
    runApplication<PureLikerApplication>(*args)
}
