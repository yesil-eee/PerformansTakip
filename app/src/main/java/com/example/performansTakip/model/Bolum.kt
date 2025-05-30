package com.example.performansTakip.model

data class Bolum(
    val id: Long = 0,
    val ad: String,
    val sorumluKisi: String
) {
    override fun toString(): String {
        return ad
    }
}
