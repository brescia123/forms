package it.facile.form.viewmodel

sealed class FieldValueK {
    object Empty : FieldValueK()
    class Text(val text: String = "") : FieldValueK()
    class Bool(val bool: Boolean = false) : FieldValueK()
    class Object(val value: DescribableK? = null) : FieldValueK()

    override fun toString(): String = when (this) {
        is Text -> text.toString()
        is Bool -> bool.toString()
        is Object -> value?.describe() ?: NO_VALUE
        is Empty -> "Missing value"
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null) false
        else when(this) {
            is Empty -> true
            is Text -> other is Text && text.equals(other.text)
            is Bool -> other is Bool && bool == other.bool
            is Object -> other is Object && value?.equals(other.value) ?: other.value == null
        }
    }

    companion object {
        private val NO_VALUE: String = "No selected value"
    }
}