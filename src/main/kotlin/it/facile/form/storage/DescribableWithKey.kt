package it.facile.form.storage

interface DescribableWithKey {
    val key: String
    val textDescription: String
    operator fun component1(): String = key
    operator fun component2() = textDescription
}

data class SimpleDescribableWithKey(override val key: String,
                                    override val textDescription: String) : DescribableWithKey

infix fun Int.keyTo(that: String): DescribableWithKey = SimpleDescribableWithKey(toString(), that)

infix fun String.keyTo(that: String): DescribableWithKey = SimpleDescribableWithKey(this, that)