package com.github.solairerove.pureliker

import org.apache.commons.codec.binary.Hex
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
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

        val currTime = (Date.from(Instant.now()))
        val c = Calendar.getInstance()
        c.time = currTime
        c.add(Calendar.YEAR, 1)
        val exp = c.time

        val serverTime = "${currTime.time.toString().take(currTime.time.toString().length - 3)}.${currTime.time.toString().takeLast(3)}"
        val expTime = "${exp.time.toString().take(exp.time.toString().length - 3)}.${exp.time.toString().takeLast(3)}"
        println(serverTime)
        println(expTime)


        // GET+/search/feed/?city_id=524901&have_photo=true&is_around_city=true&is_online=true&lang=ru&looking_for=f&ordering=-is_online%2C-created_at&page=1&sexuality=h&start_at=1638286752.74++1638286752.804
        val meToEncode = "GET+/me++$serverTime"
        val feedToEncode = "GET+/search/feed/?city_id=524901&have_photo=true&is_around_city=true&is_online=true&lang=ru&looking_for=f&ordering=-is_online%2C-created_at&page=1&sexuality=h&start_at=$serverTime++$serverTime"
        val feed2ToEncode = "GET+/search/feed/?city_id=524901&have_photo=true&is_around_city=true&is_online=true&lang=ru&looking_for=f&ordering=-is_online%2C-created_at&page=2&sexuality=h&start_at=$serverTime++$serverTime"
        val like = "POST+/users/5fb7647831027f377be90d9c/reactions/sent/likes+{\"value\":\"liked\",\"createdTime\":1638531460.726,\"expiresTime\":1670067460.726}+1638531460.756"
        val like2 = "POST+/users/61a7dc4097843e17c0895f8d/reactions/sent/likes+{\"value\":\"liked\",\"createdTime\":$serverTime,\"expiresTime\":$expTime}+$serverTime"
        println("hmac 6019a921ae5703021800365c:$serverTime:${createSignature(meToEncode, "c5298114379096c4da2db73fb4f311db")}")
        println("hmac 6019a921ae5703021800365c:$serverTime:${createSignature(feedToEncode, "c5298114379096c4da2db73fb4f311db")}")
        println("hmac 6019a921ae5703021800365c:$serverTime:${createSignature(feed2ToEncode, "c5298114379096c4da2db73fb4f311db")}")
        println("hmac 6019a921ae5703021800365c:1638531460.756:${createSignature(like, "c5298114379096c4da2db73fb4f311db")}")
        println("hmac 6019a921ae5703021800365c:$serverTime:${createSignature(like2, "c5298114379096c4da2db73fb4f311db")}")

        val myfile = File("tni.txt")
        Files.write(myfile.toPath(), ("ele" + "\n").toByteArray(), StandardOpenOption.APPEND)
        Files.write(myfile.toPath(), ("ele2" + "\n").toByteArray(), StandardOpenOption.APPEND)
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