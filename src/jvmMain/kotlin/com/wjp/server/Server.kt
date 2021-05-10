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
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

val client = KMongo.createClient().coroutine //use coroutine extension
val database = client.getDatabase("shop_db")
val collection = database.getCollection<ShopItem>()

fun main() {
    embeddedServer(factory = Netty,  port = 8080, host = "localhost") {
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

//        val shopItemList = mutableListOf(
//                ShopItem("Apple", 12.0),
//                ShopItem("Banna", 18.0),
//                ShopItem("Orange", 23.0),
//        )

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

            route(ShopItem.path) {
                get {
//                        call.respond(shopItemList)
                    call.respond(collection.find().toList())
                }
                post {
                    val item =  call.receive<ShopItem>()
//                        shopItemList += item
                    collection.insertOne(item)
                    call.respond(HttpStatusCode.OK)
                }
                delete("/{id}") {
                    val id = call.parameters["id"]?.toInt() ?: error("invalid id ${call.parameters["id"]}")
//                        shopItemList.removeIf { it.id == id }
                    collection.deleteOneById(ShopItem::id eq id)
                    call.respond(HttpStatusCode.OK)
                }
            }
            get("hello") {
                call.respond("hello ktor")
            }

        }
    }.start(true)
}