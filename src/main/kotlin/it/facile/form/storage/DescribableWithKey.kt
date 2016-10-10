package it.facile.form.storage

interface DescribableWithKey {
    val key: String
    val textDescription: String
    operator fun component1(): String = key
    operator fun component2() = textDescription
}

infix fun Int.keyTo(that: String): DescribableWithKey {
    val keyInt = this
    return object : DescribableWithKey {
        override val key: String = keyInt.toString()
        override val textDescription: String = that
    }
}

infix fun String.keyTo(that: String): DescribableWithKey {
    val keyString = this
    return object : DescribableWithKey {
        override val key: String = keyString
        override val textDescription: String = that
    }
}