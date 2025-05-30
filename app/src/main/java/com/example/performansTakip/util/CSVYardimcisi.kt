package com.example.performanstakip.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.performanstakip.model.Bolum
import com.example.performanstakip.model.Calisan
import com.example.performanstakip.model.IslemTuru
import com.example.performanstakip.model.PerformansKaydi
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class CSVYardimcisi(private val context: Context) {
    
    fun performansKayitlariniDisaAktar(kayitlar: List<PerformansKaydi>): File? {
        return try {
            val tarihFormati = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val zaman = tarihFormati.format(Date())
            val dosyaAdi = "performans_kayitlari_$zaman.csv"
            
            val dosya = File(context.getExternalFilesDir(null), dosyaAdi)
            val yazici = FileWriter(dosya)
            
            // Başlık yaz
            yazici.append("Tarih;Çalışan;İş Türü;Miktar;Birim;Açıklama\n")
            
            // Kayıtları yaz
            kayitlar.forEach { kayit ->
                yazici.append(kayit.csvDizgisi())
                yazici.append("\n")
            }
            
            yazici.close()
            dosya
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun islemTurleriniDisaAktar(islemTurleri: List<IslemTuru>): File? {
        return try {
            val tarihFormati = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val zaman = tarihFormati.format(Date())
            val dosyaAdi = "islem_turleri_$zaman.csv"
            
            val dosya = File(context.getExternalFilesDir(null), dosyaAdi)
            val yazici = FileWriter(dosya)
            
            // Başlık yaz
            yazici.append("İş Türü;Birim\n")
            
            // İşlem türlerini yaz
            islemTurleri.forEach { islemTuru ->
                yazici.append("${islemTuru.ad};${islemTuru.birim}\n")
            }
            
            yazici.close()
            dosya
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun calisanlariDisaAktar(calisanlar: List<Calisan>): File? {
        return try {
            val tarihFormati = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val zaman = tarihFormati.format(Date())
            val dosyaAdi = "calisanlar_$zaman.csv"
            
            val dosya = File(context.getExternalFilesDir(null), dosyaAdi)
            val yazici = FileWriter(dosya)
            
            // Başlık yaz
            yazici.append("Çalışan;Bölüm\n")
            
            // Çalışanları yaz
            calisanlar.forEach { calisan ->
                yazici.append("${calisan.ad};${calisan.bolum}\n")
            }
            
            yazici.close()
            dosya
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun bolumleriDisaAktar(bolumler: List<Bolum>): File? {
        return try {
            val tarihFormati = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val zaman = tarihFormati.format(Date())
            val dosyaAdi = "bolumler_$zaman.csv"
            
            val dosya = File(context.getExternalFilesDir(null), dosyaAdi)
            val yazici = FileWriter(dosya)
            
            // Başlık yaz
            yazici.append("Bölüm Adı;Sorumlu Kişi\n")
            
            // Bölümleri yaz
            bolumler.forEach { bolum ->
                yazici.append("${bolum.ad};${bolum.sorumluKisi}\n")
            }
            
            yazici.close()
            dosya
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun performansKayitlariniIceAktar(uri: Uri): List<PerformansKaydi> {
        val kayitlar = mutableListOf<PerformansKaydi>()
        
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val okuyucu = BufferedReader(InputStreamReader(inputStream))
            
            var satir: String?
            var ilkSatir = true
            
            while (okuyucu.readLine().also { satir = it } != null) {
                // Başlık satırını atla
                if (ilkSatir) {
                    ilkSatir = false
                    continue
                }
                
                val kayit = PerformansKaydi.csvDizgisindenOlustur(satir!!)
                if (kayit != null) {
                    kayitlar.add(kayit)
                }
            }
            
            okuyucu.close()
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return kayitlar
    }
    
    fun islemTurleriniIceAktar(uri: Uri): List<IslemTuru> {
        val islemTurleri = mutableListOf<IslemTuru>()
        
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val okuyucu = BufferedReader(InputStreamReader(inputStream))
            
            var satir: String?
            var ilkSatir = true
            
            while (okuyucu.readLine().also { satir = it } != null) {
                // Başlık satırını atla
                if (ilkSatir) {
                    ilkSatir = false
                    continue
                }
                
                val parcalar = satir!!.split(";")
                if (parcalar.size >= 2) {
                    islemTurleri.add(IslemTuru(ad = parcalar[0], birim = parcalar[1]))
                }
            }
            
            okuyucu.close()
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return islemTurleri
    }
    
    fun calisanlariIceAktar(uri: Uri): List<Calisan> {
        val calisanlar = mutableListOf<Calisan>()
        
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val okuyucu = BufferedReader(InputStreamReader(inputStream))
            
            var satir: String?
            var ilkSatir = true
            
            while (okuyucu.readLine().also { satir = it } != null) {
                // Başlık satırını atla
                if (ilkSatir) {
                    ilkSatir = false
                    continue
                }
                
                val parcalar = satir!!.split(";")
                if (parcalar.size >= 1) {
                    calisanlar.add(Calisan(
                        ad = parcalar[0],
                        bolum = if (parcalar.size > 1) parcalar[1] else ""
                    ))
                }
            }
            
            okuyucu.close()
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return calisanlar
    }
    
    fun bolumleriIceAktar(uri: Uri): List<Bolum> {
        val bolumler = mutableListOf<Bolum>()
        
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val okuyucu = BufferedReader(InputStreamReader(inputStream))
            
            var satir: String?
            var ilkSatir = true
            
            while (okuyucu.readLine().also { satir = it } != null) {
                // Başlık satırını atla
                if (ilkSatir) {
                    ilkSatir = false
                    continue
                }
                
                val parcalar = satir!!.split(";")
                if (parcalar.size >= 2) {
                    bolumler.add(Bolum(
                        ad = parcalar[0],
                        sorumluKisi = parcalar[1]
                    ))
                }
            }
            
            okuyucu.close()
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return bolumler
    }
    
    fun csvDosyasiniPaylas(dosya: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            dosya
        )
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "CSV Dosyasını Paylaş"))
    }
}
