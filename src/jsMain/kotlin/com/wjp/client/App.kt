package com.wjp.client

import com.wjp.common.ShopItem
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import react.RProps
import react.dom.h1
import react.dom.li
import react.dom.ul
import react.functionalComponent
import react.useEffect
import react.useState

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
        items.sortedBy { it.id }.forEach { item ->
            li {
                key = item.id.toString()
                "name ${item.name}  price: ${item.price}"
            }
        }
    }

}