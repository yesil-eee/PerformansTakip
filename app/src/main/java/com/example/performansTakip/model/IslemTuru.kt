package com.example.performansTakip.model

data class IslemTuru(
    val id: Long = 0,
    val ad: String,
    val birim: String
) {
    override fun toString(): String {
        return ad
    }
}
