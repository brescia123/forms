package it.facile.form.viewmodel

interface DescribableWithKey {
    fun describe(): String
    fun key(): Int
}