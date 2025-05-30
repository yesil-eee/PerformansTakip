package com.example.performansTakip.model

data class PerformansKaydi(
    val id: Long = 0,
    val tarih: String,
    val calisanAdi: String,
    val islemTuru: String,
    val miktar: Double,
    val birim: String,
    val aciklama: String = ""
) {
    fun whatsAppMesajiOlustur(): String {
        // Sayısal değer tam sayı ise, virgüllü gösterme
        val formatliMiktar = if (miktar % 1.0 == 0.0) {
            miktar.toInt().toString()
        } else {
            miktar.toString()
        }
        
        return if (aciklama.isNotEmpty()) {
            "$tarih | $calisanAdi | $islemTuru | $formatliMiktar | $birim | $aciklama"
        } else {
            "$tarih | $calisanAdi | $islemTuru | $formatliMiktar | $birim"
        }
    }
    
    fun csvDizgisi(): String {
        return "$tarih;$calisanAdi;$islemTuru;$miktar;$birim;$aciklama"
    }
    
    companion object {
        fun csvDizgisindenOlustur(csvDizgisi: String): PerformansKaydi? {
            return try {
                val parcalar = csvDizgisi.split(";")
                if (parcalar.size >= 5) {
                    PerformansKaydi(
                        tarih = parcalar[0],
                        calisanAdi = parcalar[1],
                        islemTuru = parcalar[2],
                        miktar = parcalar[3].toDouble(),
                        birim = parcalar[4],
                        aciklama = if (parcalar.size > 5) parcalar[5] else ""
                    )
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}
