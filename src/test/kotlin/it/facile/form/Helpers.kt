package it.facile.form

import org.mockito.Mockito
import java.util.*

fun <T> anyKObject(): T {
    Mockito.anyObject<T>()
    return null as T
}

fun <T> List<T>.randomElement() = if (lastIndex == 0) get(0) else get(Random().nextInt(lastIndex))

fun <T> List<T>.randomIndex() = if (lastIndex == 0) 0 else Random().nextInt(lastIndex)
