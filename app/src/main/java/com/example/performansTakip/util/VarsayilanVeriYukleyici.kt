package com.example.performansTakip.util

import android.content.Context
import com.example.performansTakip.model.Calisan
import com.example.performansTakip.model.IslemTuru
import com.example.performansTakip.util.VeritabaniYardimcisi
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Assets klasöründeki CSV dosyalarından varsayılan verileri yüklemek için kullanılan yardımcı sınıf.
 */
class VarsayilanVeriYukleyici(private val context: Context) {
    
    private val veritabaniYardimcisi = VeritabaniYardimcisi(context)
    
    /**
     * Assets klasöründeki iş türleri CSV dosyasını okur ve veritabanına ekler.
     * @return Eklenen kayıt sayısı
     */
    fun islemTurleriniYukle(): Int {
        val islemTurleri = mutableListOf<IslemTuru>()
        
        try {
            val inputStream = context.assets.open("islem_turleri_import.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            var line: String?
            var firstLine = true
            
            while (reader.readLine().also { line = it } != null) {
                // Başlık satırını atla
                if (firstLine) {
                    firstLine = false
                    continue
                }
                
                val parts = line!!.split(";")
                if (parts.size >= 2) {
                    islemTurleri.add(IslemTuru(ad = parts[0], birim = parts[1]))
                }
            }
            
            reader.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
        
        // Veritabanına ekle
        var eklenenKayitSayisi = 0
        islemTurleri.forEach<IslemTuru> { islemTuru ->
            val id = veritabaniYardimcisi.islemTuruEkle(islemTuru)
            if (id > 0) eklenenKayitSayisi++
        }
        
        return eklenenKayitSayisi
    }
    
    /**
     * Assets klasöründeki çalışanlar CSV dosyasını okur ve veritabanına ekler.
     * @return Eklenen kayıt sayısı
     */
    fun calisanlariYukle(): Int {
        val calisanlar = mutableListOf<Calisan>()
        
        try {
            val inputStream = context.assets.open("calisanlar_import.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            var line: String?
            var firstLine = true
            
            while (reader.readLine().also { line = it } != null) {
                // Başlık satırını atla
                if (firstLine) {
                    firstLine = false
                    continue
                }
                
                val parts = line!!.split(";")
                if (parts.size >= 2) {
                    calisanlar.add(Calisan(
                        ad = parts[0],
                        bolum = parts[1]
                    ))
                }
            }
            
            reader.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
        
        // Veritabanına ekle
        var eklenenKayitSayisi = 0
        calisanlar.forEach<Calisan> { calisan ->
            val id = veritabaniYardimcisi.calisanEkle(calisan)
            if (id > 0) eklenenKayitSayisi++
        }
        
        return eklenenKayitSayisi
    }
    
    /**
     * Tüm varsayılan verileri yükler.
     * @return Map olarak eklenen kayıt sayıları
     */
    fun tumVerileriYukle(): Map<String, Int> {
        val sonuclar = mutableMapOf<String, Int>()
        
        sonuclar["islemTurleri"] = islemTurleriniYukle()
        sonuclar["calisanlar"] = calisanlariYukle()
        
        return sonuclar as Map<String, Int>
    }
}
