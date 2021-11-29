package com.github.solairerove.pureliker

import org.apache.commons.codec.binary.Hex
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

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

/**
 * this.storage.get('sessionToken') cookie?
 * userId есть
 * serverTime проблема
 * hash вообще пиздец честно говоря
 */
@SpringBootApplication
class PureLikerApplication : CommandLineRunner {

    override fun run(vararg args: String?) {
        println("hui")
        println(createSignature("some data", "key"))
    }
}

fun main(args: Array<String>) {
    runApplication<PureLikerApplication>(*args)
}

fun createSignature( data: String, key: String): String {
    val sha256Hmac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA256")
    sha256Hmac.init(secretKey)

    return Hex.encodeHexString(sha256Hmac.doFinal(data.toByteArray()))

    // For base64
    // return Base64.getEncoder().encodeToString(sha256Hmac.doFinal(data.toByteArray()))
}