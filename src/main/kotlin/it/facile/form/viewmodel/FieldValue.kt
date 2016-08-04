package it.facile.form.viewmodel

sealed class FieldValue {
    object Missing : FieldValue()
    class Text(val text: String = "") : FieldValue()
    class Bool(val bool: Boolean = false) : FieldValue()
    class Object(val value: Describable? = null) : FieldValue()

    override fun toString(): String = when (this) {
        is Text -> text.toString()
        is Bool -> bool.toString()
        is Object -> value?.describe() ?: NO_VALUE
        is Missing -> "Missing value"
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null) false
        else when(this) {
            is Missing -> true
            is Text -> other is Text && text.equals(other.text)
            is Bool -> other is Bool && bool == other.bool
            is Object -> other is Object && value?.equals(other.value) ?: other.value == null
        }
    }

    companion object {
        private val NO_VALUE: String = "No selected value"
    }
}