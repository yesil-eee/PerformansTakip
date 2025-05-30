package com.example.performanstakip.model

data class Calisan(
    val id: Long = 0,
    val ad: String = "",
    val bolum: String = ""
) {
    override fun toString(): String {
        return ad
    }
}
