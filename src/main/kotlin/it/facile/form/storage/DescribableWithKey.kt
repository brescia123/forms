package it.facile.form.storage

interface DescribableWithKey {
    val key: Int
    fun describe(): String
}