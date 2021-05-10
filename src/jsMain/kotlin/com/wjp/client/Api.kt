package com.wjp.client

import com.wjp.common.ShopItem
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.browser.window

val endpoint = window.location.origin

val jsonClient = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}

suspend fun items(): List<ShopItem> = jsonClient.get(endpoint + ShopItem.path)

suspend fun addItem(item: ShopItem) {
    jsonClient.post<Unit>(ShopItem.path) {
        body = item
    }
}

suspend fun deleteItem(item: ShopItem) {
    jsonClient.delete<Unit>(ShopItem.path + "/${item.id}")

}