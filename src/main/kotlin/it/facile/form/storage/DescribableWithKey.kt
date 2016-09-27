package it.facile.form.storage

interface DescribableWithKey {
    val key: Int
    val text: String
    operator fun component1(): Int = key
    operator fun component2() = text
}

infix fun Int.keyTo(that: String): DescribableWithKey {
    val keyInt = this
    return object : DescribableWithKey {
        override val key: Int = keyInt
        override val text: String = that
    }
}