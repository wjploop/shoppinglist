package com.wjp.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class ShopItem(
        val name: String,
        val price: Double
) {
    val id: Int = name.hashCode()

//    companion object {
//        const val path = "/items"
//    }
}
