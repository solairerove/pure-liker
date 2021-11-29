package com.github.solairerove.pureliker

import org.apache.commons.codec.binary.Hex
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.Instant
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/*
Это код с блядского риакта браузерной верссии блядского аппа.
надо понять как синхронизировать время и как генерить хмак токен, чтобы ходить с токеном на апи
Далее дело воощбе за малым. Это основная проблема.

webpack:///Api.js
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

/*
next problem is get correct server time

  getServerTime(time) {
    const clientTime = time * 1000 || Date.now();
    return (clientTime + Number(this.storage.get('deltaTime'))) / 1000;
  }

  synchroniseTime(responseBody) {
    if (_.get(responseBody, 'additionalInfo.serverTime')) {
      const serverTime = parseInt((responseBody.additionalInfo.serverTime * 1000).toString(), 10);
      const clientTime = Date.now();
      this.storage.set('deltaTime', serverTime - clientTime);
    }
  }
 */

/**
 * this.storage.get('sessionToken') cookie?
 * userId есть
 * serverTime проблема
 * hash вообще пиздец честно говоря (больше не пиздец)
 */
@SpringBootApplication
class PureLikerApplication : CommandLineRunner {

    override fun run(vararg args: String?) {
        println("hui")

        val currTime = (Date.from(Instant.now()).time).toString()
        val serverTime = "${currTime.take(currTime.length - 3)}.${currTime.takeLast(3)}"
        println(serverTime)

        println(createSignature("GET+/me++1638229151.213", "6d30252367eaa0630c4ba38481a33a84"))

        val sToEncode = "GET+/me++$serverTime"
        println("hmac 6019a921ae5703021800365c:$serverTime:${createSignature(sToEncode, "6d30252367eaa0630c4ba38481a33a84")}")
    }
}

fun main(args: Array<String>) {
    runApplication<PureLikerApplication>(*args)
}

// https://www.freeformatter.com/hmac-generator.html#ad-output
// example of necessary hmac
// hmac 6019a921ae5703021800365c:1638229151.213:aa5cd4111be3900e494fef91cb92e896405f5acf4de31aff0f62b9966b0d1103
// userId - 6019a921ae5703021800365c
// serverTime - 1638226516.123
// hash 1d5a9b7c6f4b23293aaa51ed2262e87815b2e7f406d4e195c32b610fc8c396e1
fun createSignature(data: String, key: String): String {
    val sha256Hmac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(key.toByteArray(), "HmacSHA256")
    sha256Hmac.init(secretKey)

    return Hex.encodeHexString(sha256Hmac.doFinal(data.toByteArray()))
}

fun getServerTime() {

}