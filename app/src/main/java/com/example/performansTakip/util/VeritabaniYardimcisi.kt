package com.example.performansTakip.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.performansTakip.model.Bolum
import com.example.performansTakip.model.Calisan
import com.example.performansTakip.model.IslemTuru
import com.example.performansTakip.model.PerformansKaydi

class VeritabaniYardimcisi(context: Context) : SQLiteOpenHelper(context, VERITABANI_ADI, null, VERITABANI_VERSIYONU) {
    
    companion object {
        private const val VERITABANI_ADI = "performans_takip.db"
        private const val VERITABANI_VERSIYONU = 1
        
        // Performans Kayıtları Tablosu
        private const val TABLO_KAYITLAR = "performans_kayitlari"
        private const val KOLON_ID = "id"
        private const val KOLON_TARIH = "tarih"
        private const val KOLON_CALISAN = "calisan_adi"
        private const val KOLON_ISLEM_TURU = "islem_turu"
        private const val KOLON_MIKTAR = "miktar"
        private const val KOLON_BIRIM = "birim"
        private const val KOLON_ACIKLAMA = "aciklama"
        
        // İşlem Türleri Tablosu
        private const val TABLO_ISLEM_TURLERI = "islem_turleri"
        private const val KOLON_ISLEM_ADI = "ad"
        private const val KOLON_ISLEM_BIRIMI = "birim"
        
        // Çalışanlar Tablosu
        private const val TABLO_CALISANLAR = "calisanlar"
        private const val KOLON_CALISAN_ADI = "ad"
        private const val KOLON_CALISAN_BOLUMU = "bolum"
        
        // Bölümler Tablosu
        private const val TABLO_BOLUMLER = "bolumler"
        private const val KOLON_BOLUM_ADI = "ad"
        private const val KOLON_SORUMLU_KISI = "sorumlu_kisi"
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        // Performans kayıtları tablosunu oluştur
        val kayitlarTablosuOlustur = """
            CREATE TABLE $TABLO_KAYITLAR (
                $KOLON_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KOLON_TARIH TEXT NOT NULL,
                $KOLON_CALISAN TEXT NOT NULL,
                $KOLON_ISLEM_TURU TEXT NOT NULL,
                $KOLON_MIKTAR REAL NOT NULL,
                $KOLON_BIRIM TEXT NOT NULL,
                $KOLON_ACIKLAMA TEXT
            )
        """.trimIndent()
        
        // İşlem türleri tablosunu oluştur
        val islemTurleriTablosuOlustur = """
            CREATE TABLE $TABLO_ISLEM_TURLERI (
                $KOLON_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KOLON_ISLEM_ADI TEXT NOT NULL UNIQUE,
                $KOLON_ISLEM_BIRIMI TEXT NOT NULL
            )
        """.trimIndent()
        
        // Çalışanlar tablosunu oluştur
        val calisanlarTablosuOlustur = """
            CREATE TABLE $TABLO_CALISANLAR (
                $KOLON_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KOLON_CALISAN_ADI TEXT NOT NULL UNIQUE,
                $KOLON_CALISAN_BOLUMU TEXT
            )
        """.trimIndent()
        
        // Bölümler tablosunu oluştur
        val bolumlerTablosuOlustur = """
            CREATE TABLE $TABLO_BOLUMLER (
                $KOLON_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KOLON_BOLUM_ADI TEXT NOT NULL UNIQUE,
                $KOLON_SORUMLU_KISI TEXT NOT NULL
            )
        """.trimIndent()
        
        db.execSQL(kayitlarTablosuOlustur)
        db.execSQL(islemTurleriTablosuOlustur)
        db.execSQL(calisanlarTablosuOlustur)
        db.execSQL(bolumlerTablosuOlustur)
        
        // Varsayılan verileri ekle
        varsayilanVerileriEkle(db)
    }
    
    override fun onUpgrade(db: SQLiteDatabase, eskiVersiyon: Int, yeniVersiyon: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLO_KAYITLAR")
        db.execSQL("DROP TABLE IF EXISTS $TABLO_ISLEM_TURLERI")
        db.execSQL("DROP TABLE IF EXISTS $TABLO_CALISANLAR")
        db.execSQL("DROP TABLE IF EXISTS $TABLO_BOLUMLER")
        onCreate(db)
    }
    
    private fun varsayilanVerileriEkle(db: SQLiteDatabase) {
        // Varsayılan bölümler
        val bolumler = listOf(
            Bolum(ad = "Bobin Aktarma", sorumluKisi = "Kadriye Ersöz"),
            Bolum(ad = "Halı Tamir", sorumluKisi = "Naciye Göl"),
            Bolum(ad = "Tamir-Temizlik", sorumluKisi = "Nurcan Karahan"),
            Bolum(ad = "Merkez Çıkış", sorumluKisi = "Hatice Bircan"),
            Bolum(ad = "Overlok", sorumluKisi = "Zeliha Akarsu"),
            Bolum(ad = "Halı Yıkama", sorumluKisi = "Süleyman Bostancı"),
            Bolum(ad = "Küçük Tıraş", sorumluKisi = "Hüseyin Beki"),
            Bolum(ad = "Çakım Hazırlık", sorumluKisi = "Nazif Karataş"),
            Bolum(ad = "Çakım", sorumluKisi = "Halil Aktürk"),
            Bolum(ad = "Paketleme", sorumluKisi = "Emine Yılmaz")
        )
        
        // Varsayılan işlem türleri
        val islemTurleri = listOf(
            IslemTuru(ad = "Çakım", birim = "m²"),
            IslemTuru(ad = "Halı Tamir", birim = "m²"),
            IslemTuru(ad = "Bobin Aktarma", birim = "kg"),
            IslemTuru(ad = "Merkez Çıkış", birim = "m²"),
            IslemTuru(ad = "Paketleme", birim = "m²"),
            IslemTuru(ad = "Tamir-Temizlik", birim = "m²"),
            IslemTuru(ad = "Overlok", birim = "m²"),
            IslemTuru(ad = "Yıkama1 (Normal, Velvet)", birim = "m²"),
            IslemTuru(ad = "Yıkama2 (Depo,Diğer)", birim = "m²"),
            IslemTuru(ad = "Çakım Hazırlık", birim = "m²"),
            IslemTuru(ad = "Küçük Tıraş", birim = "m²"),
            IslemTuru(ad = "Tamir-Temizlik", birim = "m²")

        )
        
        // Varsayılan çalışanlar
        val calisanlar = listOf(
            Calisan(ad = "Emine Yılmaz", bolum = "Paketleme"),
            Calisan(ad = "Halil Aktürk", bolum = "Çakım"),
            Calisan(ad = "Hatice Bircan", bolum = "Merkez Çıkış"),
            Calisan(ad = "Hüseyin Beki", bolum = "Küçük Tıraş"),
            Calisan(ad = "Kadriye Ersöz", bolum = "Bobin Aktarma"),
            Calisan(ad = "Naciye Göl", bolum = "Halı Tamir"),
            Calisan(ad = "Nazif Karataş", bolum = "Çakım Hazırlık"),
            Calisan(ad = "Nurcan Karahan", bolum = "Tamir-Temizlik"),
            Calisan(ad = "Süleyman Bostancı", bolum = "Halı Yıkama"),
            Calisan(ad = "Zeliha Akarsu", bolum = "Overlok"),
            Calisan(ad = "İsmail Karaca", bolum = "Overlok")

        )
        
        // Varsayılan verileri veritabanına ekle
        bolumler.forEach { bolum ->
            val values = ContentValues().apply {
                put(KOLON_BOLUM_ADI, bolum.ad)
                put(KOLON_SORUMLU_KISI, bolum.sorumluKisi)
            }
            db.insert(TABLO_BOLUMLER, null, values)
        }
        
        islemTurleri.forEach { islemTuru ->
            val values = ContentValues().apply {
                put(KOLON_ISLEM_ADI, islemTuru.ad)
                put(KOLON_ISLEM_BIRIMI, islemTuru.birim)
            }
            db.insert(TABLO_ISLEM_TURLERI, null, values)
        }
        
        calisanlar.forEach { calisan ->
            val values = ContentValues().apply {
                put(KOLON_CALISAN_ADI, calisan.ad)
                put(KOLON_CALISAN_BOLUMU, calisan.bolum)
            }
            db.insert(TABLO_CALISANLAR, null, values)
        }
    }
    
    // Performans Kayıtları İşlemleri
    fun performansKaydiEkle(kayit: PerformansKaydi): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KOLON_TARIH, kayit.tarih)
            put(KOLON_CALISAN, kayit.calisanAdi)
            put(KOLON_ISLEM_TURU, kayit.islemTuru)
            put(KOLON_MIKTAR, kayit.miktar)
            put(KOLON_BIRIM, kayit.birim)
            put(KOLON_ACIKLAMA, kayit.aciklama)
        }
        return db.insert(TABLO_KAYITLAR, null, values)
    }
    
    fun tumPerformansKayitlariniGetir(): List<PerformansKaydi> {
        val kayitlar = mutableListOf<PerformansKaydi>()
        val db = readableDatabase
        val cursor = db.query(
            TABLO_KAYITLAR,
            null,
            null,
            null,
            null,
            null,

            "$KOLON_TARIH DESC"
        )
        
        with(cursor) {
            while (moveToNext()) {
                val kayit = PerformansKaydi(
                    id = getLong(getColumnIndexOrThrow(KOLON_ID)),
                    tarih = getString(getColumnIndexOrThrow(KOLON_TARIH)),
                    calisanAdi = getString(getColumnIndexOrThrow(KOLON_CALISAN)),
                    islemTuru = getString(getColumnIndexOrThrow(KOLON_ISLEM_TURU)),
                    miktar = getDouble(getColumnIndexOrThrow(KOLON_MIKTAR)),
                    birim = getString(getColumnIndexOrThrow(KOLON_BIRIM)),
                    aciklama = getString(getColumnIndexOrThrow(KOLON_ACIKLAMA))
                )
                kayitlar.add(kayit)
            }
        }
        cursor.close()
        return kayitlar
    }
    
    fun sonKayitlariGetir(limit: Int = 10): List<PerformansKaydi> {
        val kayitlar = mutableListOf<PerformansKaydi>()
        val db = readableDatabase
        val cursor = db.query(
            TABLO_KAYITLAR,
            null,
            null,
            null,
            null,
            null,

            "$KOLON_ID DESC",
            limit.toString()
        )
        
        with(cursor) {
            while (moveToNext()) {
                val kayit = PerformansKaydi(
                    id = getLong(getColumnIndexOrThrow(KOLON_ID)),
                    tarih = getString(getColumnIndexOrThrow(KOLON_TARIH)),
                    calisanAdi = getString(getColumnIndexOrThrow(KOLON_CALISAN)),
                    islemTuru = getString(getColumnIndexOrThrow(KOLON_ISLEM_TURU)),
                    miktar = getDouble(getColumnIndexOrThrow(KOLON_MIKTAR)),
                    birim = getString(getColumnIndexOrThrow(KOLON_BIRIM)),
                    aciklama = getString(getColumnIndexOrThrow(KOLON_ACIKLAMA))
                )
                kayitlar.add(kayit)
            }
        }
        cursor.close()
        return kayitlar
    }
    
    fun tumKayitlariSil() {
        val db = writableDatabase
        db.delete(TABLO_KAYITLAR, null, null)
    }
    
    // İşlem Türleri İşlemleri
    fun tumIslemTurleriniGetir(): List<IslemTuru> {
        val islemTurleri = mutableListOf<IslemTuru>()
        val db = readableDatabase
        val cursor = db.query(
            TABLO_ISLEM_TURLERI,
            null,
            null,
            null,
            null,
            null,

            KOLON_ISLEM_ADI
        )
        
        with(cursor) {
            while (moveToNext()) {
                val islemTuru = IslemTuru(
                    id = getLong(getColumnIndexOrThrow(KOLON_ID)),
                    ad = getString(getColumnIndexOrThrow(KOLON_ISLEM_ADI)),
                    birim = getString(getColumnIndexOrThrow(KOLON_ISLEM_BIRIMI))
                )
                islemTurleri.add(islemTuru)
            }
        }
        cursor.close()
        return islemTurleri
    }
    
    fun islemTuruEkle(islemTuru: IslemTuru): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KOLON_ISLEM_ADI, islemTuru.ad)
            put(KOLON_ISLEM_BIRIMI, islemTuru.birim)
        }
        return db.insertWithOnConflict(TABLO_ISLEM_TURLERI, null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }
    
    fun islemTuruSil(id: Long) {
        val db = writableDatabase
        db.delete(TABLO_ISLEM_TURLERI, "$KOLON_ID = ?", arrayOf(id.toString()))
    }
    
    fun tumIslemTurleriniSil() {
        val db = writableDatabase
        db.delete(TABLO_ISLEM_TURLERI, null, null)
    }
    
    fun islemTuruIcinBirimGetir(islemTuruAdi: String): String? {
        val db = readableDatabase
        val cursor = db.query(
            TABLO_ISLEM_TURLERI,  // Tablo adı
            arrayOf(KOLON_ISLEM_BIRIMI),  // Döndürülecek sütunlar
            "$KOLON_ISLEM_ADI = ?",  // WHERE koşulu
            arrayOf(islemTuruAdi),  // WHERE parametreleri
            null,  // GROUP BY
            null,  // HAVING
            null  // ORDER BY
        )
        
        return if (cursor.moveToFirst()) {
            val birim = cursor.getString(cursor.getColumnIndexOrThrow(KOLON_ISLEM_BIRIMI))
            cursor.close()
            birim
        } else {
            cursor.close()
            null
        }
    }
    
    // Çalışanlar İşlemleri
    fun tumCalisanlariGetir(): List<Calisan> {
        val calisanlar = mutableListOf<Calisan>()
        val db = readableDatabase
        val cursor = db.query(
            TABLO_CALISANLAR,
            null,
            null,
            null,
            null,
            null,

            KOLON_CALISAN_ADI
        )
        
        with(cursor) {
            while (moveToNext()) {
                val calisan = Calisan(
                    id = getLong(getColumnIndexOrThrow(KOLON_ID)),
                    ad = getString(getColumnIndexOrThrow(KOLON_CALISAN_ADI)),
                    bolum = getString(getColumnIndexOrThrow(KOLON_CALISAN_BOLUMU)) ?: ""
                )
                calisanlar.add(calisan)
            }
        }
        cursor.close()
        return calisanlar
    }
    
    fun calisanEkle(calisan: Calisan): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KOLON_CALISAN_ADI, calisan.ad)
            put(KOLON_CALISAN_BOLUMU, calisan.bolum)
        }
        return db.insertWithOnConflict(TABLO_CALISANLAR, null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }
    
    fun calisanSil(id: Long) {
        val db = writableDatabase
        db.delete(TABLO_CALISANLAR, "$KOLON_ID = ?", arrayOf(id.toString()))
    }
    
    fun tumCalisanlariSil() {
        val db = writableDatabase
        db.delete(TABLO_CALISANLAR, null, null)
    }
    
    // Bölümler İşlemleri
    fun tumBolumleriGetir(): List<Bolum> {
        val bolumler = mutableListOf<Bolum>()
        val db = readableDatabase
        val cursor = db.query(
            TABLO_BOLUMLER,
            null,
            null,
            null,
            null,
            null,

            KOLON_BOLUM_ADI
        )
        
        with(cursor) {
            while (moveToNext()) {
                val bolum = Bolum(
                    id = getLong(getColumnIndexOrThrow(KOLON_ID)),
                    ad = getString(getColumnIndexOrThrow(KOLON_BOLUM_ADI)),
                    sorumluKisi = getString(getColumnIndexOrThrow(KOLON_SORUMLU_KISI))
                )
                bolumler.add(bolum)
            }
        }
        cursor.close()
        return bolumler
    }
    
    fun bolumEkle(bolum: Bolum): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KOLON_BOLUM_ADI, bolum.ad)
            put(KOLON_SORUMLU_KISI, bolum.sorumluKisi)
        }
        return db.insert(TABLO_BOLUMLER, null, values)
    }
    
    fun bolumSil(id: Long) {
        val db = writableDatabase
        db.delete(TABLO_BOLUMLER, "$KOLON_ID = ?", arrayOf(id.toString()))
    }
    
    fun tumBolumleriSil() {
        val db = writableDatabase
        db.delete(TABLO_BOLUMLER, null, null)
    }
    
    // İstatistikler
    fun toplamKayitSayisi(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLO_KAYITLAR", null)
        cursor.moveToFirst()
        val sayi = cursor.getInt(0)
        cursor.close()
        return sayi
    }
    
    fun aktifCalisanSayisi(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(DISTINCT $KOLON_CALISAN) FROM $TABLO_KAYITLAR", null)
        cursor.moveToFirst()
        val sayi = cursor.getInt(0)
        cursor.close()
        return sayi
    }
    
    fun islemTuruSayisi(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLO_ISLEM_TURLERI", null)
        cursor.moveToFirst()
        val sayi = cursor.getInt(0)
        cursor.close()
        return sayi
    }
    
    fun birimSayisi(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(DISTINCT $KOLON_ISLEM_BIRIMI) FROM $TABLO_ISLEM_TURLERI", null)
        cursor.moveToFirst()
        val sayi = cursor.getInt(0)
        cursor.close()
        return sayi
    }
}
