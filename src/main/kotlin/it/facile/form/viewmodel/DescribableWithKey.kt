package it.facile.form.viewmodel

interface DescribableWithKey {
    val key: Int
    fun describe(): String
}