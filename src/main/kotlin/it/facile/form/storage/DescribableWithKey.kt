package it.facile.form.storage

interface DescribableWithKey {
    val key: Int
    val textDescription: String
    operator fun component1(): Int = key
    operator fun component2() = textDescription
}

infix fun Int.keyTo(that: String): DescribableWithKey {
    val keyInt = this
    return object : DescribableWithKey {
        override val key: Int = keyInt
        override val textDescription: String = that
    }
}