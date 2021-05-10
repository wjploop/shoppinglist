package com.wjp.client

import com.wjp.common.ShopItem
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.buttonInput
import kotlinx.html.js.onClickFunction
import react.RProps
import react.dom.*
import react.functionalComponent
import react.useEffect
import react.useState
import kotlin.random.Random

private val scope = MainScope()

val App = functionalComponent<RProps> {

    val (items, setItems) = useState(emptyList<ShopItem>())

    useEffect(dependencies = listOf()) {
        scope.launch {
            setItems(items())
        }
    }

    h1 {
        +"Full-Stack List"
    }
    ul {
        items.forEach { item ->
            li {
                attrs {
                    onClickFunction = {
                        scope.launch {
                            deleteItem(item)
                        }
                    }
                }
                +"name ${item.name}  price: ${item.price}"
            }
        }
    }
    p {
        +"hello"
    }

    button {
        p {
            +" Add Item"
        }
        attrs {
            onClickFunction = {
                scope.launch {
                    val newItem = ShopItem("Kotlin ", Random.nextDouble())
                    addItem(newItem)
                    setItems(items())
                }
            }
        }
    }

}