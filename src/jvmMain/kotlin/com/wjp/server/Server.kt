package com.wjp.server

import com.wjp.common.ShopItem
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.gzip
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.flow.MutableStateFlow

fun main() {
    embeddedServer(factory = Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }

        val shopItemList = mutableListOf(
                ShopItem("Apple", 12.0),
                ShopItem("Banna", 18.0),
                ShopItem("Orange", 23.0),
        )
        routing {
            // 将 index.html 作为一个 placeholder
            get("/"){
                call.respondText(contentType = ContentType.Text.Html) {
                    this::class.java.classLoader.getResource("index.html")!!.readText()
                }
            }
            // index.html需要引用这个该目录的生成的shopping.js
            static("/"){
                resources("")
            }
            route("/api") {
                get("/hello") {
                    call.respond("hello ktor")
                }
                get("/") {
                    call.respondRedirect("items")
                }
                route("items") {
                    get {
                        call.respond(shopItemList)
                    }
                    post {
                        shopItemList += call.receive<ShopItem>()
                        call.respond(HttpStatusCode.OK)
                    }
                    delete("/{id}") {
                        val id = call.parameters["id"]?.toInt() ?: error("invalid id ${call.parameters["id"]}")
                        shopItemList.removeIf { it.id == id }
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }

        }
    }.start(true)
}