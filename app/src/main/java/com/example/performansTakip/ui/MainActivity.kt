package com.example.performansTakip.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.performansTakip.R
import com.example.performansTakip.model.Calisan
import com.example.performansTakip.model.IslemTuru
import com.example.performansTakip.model.PerformansKaydi
import com.example.performansTakip.util.VeritabaniYardimcisi
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {
    
    private lateinit var veritabaniYardimcisi: VeritabaniYardimcisi
    private lateinit var sharedPreferences: SharedPreferences
    
    private lateinit var etTarih: TextInputEditText
    private lateinit var actvBolum: AutoCompleteTextView
    private lateinit var actvCalisan: AutoCompleteTextView
    private lateinit var actvIslemTuru: AutoCompleteTextView
    private lateinit var etMiktar: TextInputEditText
    private lateinit var etBirim: TextInputEditText
    private lateinit var etAciklama: TextInputEditText
    private lateinit var btnWhatsAppGonder: MaterialButton
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var tvBolumSorumlusu: TextView
    
    private var seciliTarih = ""
    private var islemTurleri = listOf<IslemTuru>()
    private var bolumler = listOf<String>()
    private var tumCalisanlar = listOf<Calisan>()
    private var filtrelenmisCalısanlar = listOf<Calisan>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Aydınlık modu zorla - karanlık mod kapalı
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
        try {
            // Önce SharedPreferences'i başlat - her şeyden önce olmalı
            sharedPreferences = getSharedPreferences("performans_takip", Context.MODE_PRIVATE)
            
            // ActionBar başlığını gizle
            supportActionBar?.hide()
            
            // Layout'u ayarla
            setContentView(R.layout.activity_main)
            
            // Sırayla başlatma işlemlerini yap
            viewlariBaslat()
            veritabaniniBaslat()
            dinleyicileriAyarla()
            verileriYukle()
            autoCompleteViewlariAyarla()
            firmaAdiniGoster()
            
            // Başlarıyla başlatıldı - toast mesajı kaldırıldı
        } catch (e: Exception) {
            // Kritik bir hata oluşursa kullanıcıya bildir
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("MainActivity", "Başlatma hatası: ${e.message}", e)
        }
    }
    
    private fun viewlariBaslat() {
        try {
            // Tüm viewları başlat
            etTarih = findViewById(R.id.etTarih) ?: throw NullPointerException("etTarih bulunamadı")
            actvBolum = findViewById(R.id.actvBolum) ?: throw NullPointerException("actvBolum bulunamadı")
            actvCalisan = findViewById(R.id.actvCalisan) ?: throw NullPointerException("actvCalisan bulunamadı")
            actvIslemTuru = findViewById(R.id.actvIslemTuru) ?: throw NullPointerException("actvIslemTuru bulunamadı")
            etMiktar = findViewById(R.id.etMiktar) ?: throw NullPointerException("etMiktar bulunamadı")
            etBirim = findViewById(R.id.etBirim) ?: throw NullPointerException("etBirim bulunamadı")
            etAciklama = findViewById(R.id.etAciklama) ?: throw NullPointerException("etAciklama bulunamadı")
            btnWhatsAppGonder = findViewById(R.id.btnWhatsAppGonder) ?: throw NullPointerException("btnWhatsAppGonder bulunamadı")
            bottomNavigation = findViewById(R.id.bottomNavigation) ?: throw NullPointerException("bottomNavigation bulunamadı")
            tvBolumSorumlusu = findViewById(R.id.tvBolumSorumlusu) ?: throw NullPointerException("tvBolumSorumlusu bulunamadı")
            
            // Görünüm ayarları
            etTarih.isFocusable = false // Klavye açılmasını engelle
            
            android.util.Log.d("MainActivity", "Tüm viewlar başarıyla başlatıldı")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Viewlar başlatılırken hata: ${e.message}", e)
            throw e
        }
    }
    
    private fun firmaAdiniGoster() {
        try {
            val firmaAdi = sharedPreferences.getString("firma_ismi", "")
            if (!firmaAdi.isNullOrEmpty()) {
                val tvFirmaAdi = findViewById<TextView>(R.id.tvFirmaAdi)
                tvFirmaAdi.text = firmaAdi
                android.util.Log.d("MainActivity", "Firma adı gösterildi: $firmaAdi")
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Firma adı gösterilirken hata: ${e.message}")
        }
    }
    
    private fun veritabaniniBaslat() {
        try {
            veritabaniYardimcisi = VeritabaniYardimcisi(this)
            android.util.Log.d("MainActivity", "Veritabanı başarıyla başlatıldı")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Veritabanı başlatılırken hata: ${e.message}", e)
            // Sadece kritik hata olduğunda kullanıcıya göster
            throw e
        }
    }
    
    private fun dinleyicileriAyarla() {
        etTarih.setOnClickListener {
            tarihSeciciGoster()
        }
        
        // Bölüm seçildiğinde çalışanları filtrele
        actvBolum.setOnItemClickListener { _, _, position, _ ->
            val seciliBolum = actvBolum.adapter.getItem(position) as String
            
            // Bölüme göre çalışanları filtrele
            filtrelenmisCalısanlar = tumCalisanlar.filter { it.bolum == seciliBolum }
            
            // Çalışan adapterını güncelle
            val calisanAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                filtrelenmisCalısanlar.map { it.ad }
            )
            actvCalisan.setAdapter(calisanAdapter)
            
            // Bölüm sorumlusunu göster
            val bolumSorumlusu = veritabaniYardimcisi.bolumSorumlusuGetir(seciliBolum)
            if (bolumSorumlusu.isNotEmpty()) {
                tvBolumSorumlusu.text = "Sorumlu: $bolumSorumlusu"
                tvBolumSorumlusu.visibility = View.VISIBLE
            } else {
                tvBolumSorumlusu.visibility = View.GONE
            }
            
            // Son seçilen bölümü kaydet
            sharedPreferences.edit().putString("son_bolum", seciliBolum).apply()
            
            // Eğer önceden seçili bir çalışan varsa ve artık listede değilse, çalışan seçimini temizle
            val sonCalisan = sharedPreferences.getString("son_calisan", "")
            if (!sonCalisan.isNullOrEmpty() && filtrelenmisCalısanlar.none { it.ad == sonCalisan }) {
                actvCalisan.text.clear()
                sharedPreferences.edit().remove("son_calisan").apply()
            }
        }
        
        actvIslemTuru.setOnItemClickListener { _, _, _, _ ->
            val seciliIslemTuru = islemTurleri.find { it.ad == actvIslemTuru.text.toString() }
            seciliIslemTuru?.let {
                etBirim.setText(it.birim)
            }
            // Klavyeyi kapat
            klavyeyiGizle()
        }
        
        actvCalisan.setOnItemClickListener { _, _, _, _ ->
            // Seçilen çalışanı kaydet
            val seciliCalisan = actvCalisan.text.toString()
            if (seciliCalisan.isNotEmpty()) {
                sharedPreferences.edit().putString("son_calisan", seciliCalisan).apply()
            }
            // Klavyeyi kapat
            klavyeyiGizle()
        }
        
        etMiktar.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (etMiktar.text.toString().isNotEmpty() && actvIslemTuru.text.toString().isNotEmpty()) {
                    val seciliIslemTuru = islemTurleri.find { it.ad == actvIslemTuru.text.toString() }
                    seciliIslemTuru?.let {
                        etBirim.setText(it.birim)
                    }
                }
            }
        }
        
        btnWhatsAppGonder.setOnClickListener {
            klavyeyiGizle()
            whatsAppGonder()
        }
        
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_kayit -> {
                    // Zaten bu ekrandayız, bir şey yapmaya gerek yok
                    true
                }
                R.id.nav_ozet -> {
                    startActivity(Intent(this, OzetActivity::class.java))
                    true
                }
                R.id.nav_ayarlar -> {
                    startActivity(Intent(this, AyarlarActivity::class.java))
                    true
                }
                R.id.nav_cikis -> {
                    cikisOnayDiyaloguGoster()
                    true
                }
                else -> false
            }
        }
    }
    
    private fun verileriYukle() {
        try {
            android.util.Log.d("MainActivity", "verileriYukle başlıyor")
            
            // İşlem türlerini yükle
            islemTurleri = try {
                veritabaniYardimcisi.tumIslemTurleriniGetir().also {
                    android.util.Log.d("MainActivity", "${it.size} işlem türü yüklendi")
                }
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "İşlem türleri yüklenirken hata", e)
                listOf()
            }
            
            // Bölümleri yükle
            bolumler = try {
                veritabaniYardimcisi.tumBolumleriGetir().map { it.ad }.also {
                    android.util.Log.d("MainActivity", "${it.size} bölüm yüklendi")
                }
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Bölümler yüklenirken hata", e)
                listOf()
            }
            
            // Çalışanları yükle
            tumCalisanlar = try {
                veritabaniYardimcisi.tumCalisanlariGetir().also {
                    android.util.Log.d("MainActivity", "${it.size} çalışan yüklendi")
                }
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Çalışanlar yüklenirken hata", e)
                listOf()
            }
            
            // Son seçili çalışanı yükle
            val sonCalisan = sharedPreferences.getString("son_calisan", "")
            
            // Filtrelenmiş çalışanlar başlangıçta tüm çalışanlar olacak
            if (tumCalisanlar.isNotEmpty()) {
                filtrelenmisCalısanlar = tumCalisanlar
            }
            
            if (!sonCalisan.isNullOrEmpty()) {
                actvCalisan.setText(sonCalisan)
            }
            
            // Bugünün tarihini varsayılan olarak ayarla
            val bugun = Calendar.getInstance()
            val tarihFormati = SimpleDateFormat("dd/MM/yyyy", Locale("tr", "TR"))
            seciliTarih = tarihFormati.format(bugun.time)
            etTarih.setText(seciliTarih)
            
            android.util.Log.d("MainActivity", "verileriYukle tamamlandı")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Veriler yüklenirken genel hata", e)
            // Toast mesajı kaldırıldı
        }
    }
    
    private fun autoCompleteViewlariAyarla() {
        try {
            android.util.Log.d("MainActivity", "autoCompleteViewlariAyarla başlıyor")
            
            // Bölüm adapterı
            try {
                val bolumAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bolumler)
                actvBolum.setAdapter(bolumAdapter)
                actvBolum.inputType = 0 // Klavye açılmaması için
                actvBolum.isFocusable = false
                
                // Son kullanılan bölümü yükle (ilk kullanım değilse)
                val sonBolum = sharedPreferences.getString("son_bolum", "")
                if (!sonBolum.isNullOrEmpty() && bolumler.contains(sonBolum)) {
                    actvBolum.setText(sonBolum, false)
                    
                    // Seçilen bölümün çalışanlarını filtrele ve adapterı güncelle
                    filtrelenmisCalısanlar = tumCalisanlar.filter { it.bolum == sonBolum }
                    val calisanAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        filtrelenmisCalısanlar.map { it.ad }
                    )
                    actvCalisan.setAdapter(calisanAdapter)
                }
                
                android.util.Log.d("MainActivity", "Bölüm adapterı ayarlandı")
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Bölüm adapterı ayarlanırken hata", e)
            }
            
            // Çalışan adapterı - eğer bölüm seçili değilse boş olacak
            try {
                // Bölüm seçili değilse boş liste, seçili ise filtrelenmiş liste kullan
                val seciliBolum = actvBolum.text.toString()
                if (seciliBolum.isEmpty()) {
                    val emptyAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, emptyList<String>())
                    actvCalisan.setAdapter(emptyAdapter)
                }
                
                actvCalisan.inputType = 0 // Klavye açılmaması için
                actvCalisan.isFocusable = false
                
                // Son kullanılan çalışanı yükle (ilk kullanım değilse ve bölüm seçiliyse)
                if (seciliBolum.isNotEmpty()) {
                    val sonCalisan = sharedPreferences.getString("son_calisan", "")
                    if (!sonCalisan.isNullOrEmpty() && filtrelenmisCalısanlar.any { it.ad == sonCalisan }) {
                        actvCalisan.setText(sonCalisan, false)
                    }
                }
                
                android.util.Log.d("MainActivity", "Çalışan adapterı ayarlandı")
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Çalışan adapterı ayarlanırken hata", e)
            }
            
            // İşlem türü adapterı
            try {
                val islemTuruAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, islemTurleri.map { it.ad })
                actvIslemTuru.setAdapter(islemTuruAdapter)
                actvIslemTuru.inputType = 0 // Klavye açılmaması için
                actvIslemTuru.isFocusable = false
                
                // Son kullanılan işlem türünü yükle (ilk kullanım değilse)
                val sonIslemTuru = sharedPreferences.getString("son_islem_turu", "")
                if (!sonIslemTuru.isNullOrEmpty() && islemTurleri.any { it.ad == sonIslemTuru }) {
                    actvIslemTuru.setText(sonIslemTuru, false)
                }
                
                android.util.Log.d("MainActivity", "İşlem türü adapterı ayarlandı")
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "İşlem türü adapterı ayarlanırken hata", e)
            }
            
            // Bugünün tarihini ayarla (sadece boşsa)
            if (etTarih.text.toString().isEmpty()) {
                try {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val bugun = dateFormat.format(Date())
                    seciliTarih = bugun
                    etTarih.setText(bugun)
                    android.util.Log.d("MainActivity", "Tarih ayarlandı: $bugun")
                } catch (e: Exception) {
                    android.util.Log.e("MainActivity", "Tarih ayarlanırken hata", e)
                }
            }
            
            // Miktar alanını her zaman temizle (hatırlanmaması isteniyor)
            try {
                etMiktar.setText("") 
                android.util.Log.d("MainActivity", "Miktar alanı temizlendi")
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Miktar alanı temizlenirken hata", e)
            }
            
            android.util.Log.d("MainActivity", "autoCompleteViewlariAyarla tamamlandı")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "AutoComplete viewlar ayarlanırken genel hata", e)
        }
    }
    
    private fun tarihSeciciGoster() {
        val calendar = Calendar.getInstance()
        if (seciliTarih.isNotEmpty()) {
            try {
                val tarihFormati = SimpleDateFormat("dd/MM/yyyy", Locale("tr", "TR"))
                val tarih = tarihFormati.parse(seciliTarih)
                tarih?.let {
                    calendar.time = it
                }
            } catch (e: Exception) {
                // Eğer ayrıştırma başarısız olursa bugünün tarihini kullan
            }
        }
        
        val tarihSeciciDialog = DatePickerDialog(
            this,
            { _, yil, ay, gun ->
                val seciliCalendar = Calendar.getInstance()
                seciliCalendar.set(yil, ay, gun)
                
                val tarihFormati = SimpleDateFormat("dd/MM/yyyy", Locale("tr", "TR"))
                seciliTarih = tarihFormati.format(seciliCalendar.time)
                etTarih.setText(seciliTarih)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Türkçe gün isimleri ve ilk gün Pazartesi ayarı
        tarihSeciciDialog.datePicker.firstDayOfWeek = Calendar.MONDAY
        
        tarihSeciciDialog.show()
    }
    
    private fun whatsAppGonder() {
        // Değerleri oku
        val tarih = etTarih.text.toString()
        val bolum = actvBolum.text.toString()
        val calisanAdi = actvCalisan.text.toString()
        val islemTuru = actvIslemTuru.text.toString()
        val miktarMetni = etMiktar.text.toString()
        val birim = etBirim.text.toString()
        val aciklama = etAciklama.text.toString()
        
        // Son kullanılan değerleri kaydet (daha sonra hatırlanması için)
        sharedPreferences.edit().apply {
            putString("son_bolum", bolum)
            putString("son_calisan", calisanAdi)
            putString("son_islem_turu", islemTuru)
            // Miktar kaydedilmiyor - her seferinde elle girilecek
        }.apply()
        
        // Doğrulama
        if (seciliTarih.isEmpty() || bolum.isEmpty() || calisanAdi.isEmpty() || islemTuru.isEmpty() || 
            miktarMetni.isEmpty() || birim.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm gerekli alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }
        
        val miktar = try {
            miktarMetni.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Geçerli bir miktar girin", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Eğer yeniyse çalışanı kaydet
        if (!tumCalisanlar.any { it.ad == calisanAdi }) {
            val yeniCalisan = Calisan(ad = calisanAdi)
            veritabaniYardimcisi.calisanEkle(yeniCalisan)
            tumCalisanlar = veritabaniYardimcisi.tumCalisanlariGetir()
            autoCompleteViewlariAyarla()
        }
        
        // Son kullanılan çalışanı kaydet
        sharedPreferences.edit().putString("son_calisan", calisanAdi).apply()
        
        // Performans kaydı oluştur
        val kayit = PerformansKaydi(
            tarih = seciliTarih,
            calisanAdi = calisanAdi,
            islemTuru = islemTuru,
            miktar = miktar,
            birim = birim,
            aciklama = aciklama
        )
        
        // Veritabanına kaydet
        val kayitId = veritabaniYardimcisi.performansKaydiEkle(kayit)
        if (kayitId > 0) {
            Toast.makeText(this, "Kayıt başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
            
            // İstenen formata göre WhatsApp mesajı oluştur
            // Örnek: 03/05/2025 | Ali Çelik | Halı Yıkama | 150 | m2 | Açıklama
            val formatliMiktar = if (miktar % 1.0 == 0.0) {
                miktar.toInt().toString()
            } else {
                miktar.toString()
            }
            
            val mesaj = "$seciliTarih | $bolum | $calisanAdi | $islemTuru | $formatliMiktar | $birim"
            val aciklamaliMesaj = if (aciklama.isNotEmpty()) {
                "$mesaj | $aciklama"
            } else {
                mesaj
            }
            
            // WhatsApp'a gönder
            whatsAppMesajiGonder(aciklamaliMesaj)
            
            // Formu temizle
            formuTemizle()
        } else {
            Toast.makeText(this, "Kayıt kaydedilemedi", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun whatsAppMesajiGonder(mesaj: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage("com.whatsapp")
            intent.putExtra(Intent.EXTRA_TEXT, mesaj)
            startActivity(intent)
        } catch (e: Exception) {
            // WhatsApp yüklü değilse, jenerik paylaşım ile dene
            try {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, mesaj)
                intent.putExtra(Intent.EXTRA_SUBJECT, "Performans Kaydı")
                startActivity(Intent.createChooser(intent, "Paylaş"))
            } catch (ex: Exception) {
                Toast.makeText(this, "WhatsApp bulunamadı", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun formuTemizle() {
        actvIslemTuru.setText("")
        etMiktar.setText("")
        etBirim.setText("")
        etAciklama.setText("")
        
        // Tarih ve çalışanı kolaylık için sakla
    }
    
    private fun klavyeyiGizle() {
        val view = currentFocus ?: View(this)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    
    override fun onResume() {
        super.onResume()
        // Diğer aktivitelerden döndüğünde verileri yenile
        verileriYukle()
        autoCompleteViewlariAyarla()
        firmaAdiniGoster() // Firma adını her ekrana dönüşte güncelle
        bottomNavigation.selectedItemId = R.id.nav_kayit
    }
    
    private fun cikisOnayDiyaloguGoster() {
        AlertDialog.Builder(this)
            .setTitle("Uygulamadan Çıkış")
            .setMessage("Kapatmak istediğinize emin misiniz?")
            .setPositiveButton("Evet") { _, _ ->
                finishAffinity() // Tüm aktiviteleri kapat
            }
            .setNegativeButton("Hayır", null)
            .show()
    }
    
    override fun onBackPressed() {
        cikisOnayDiyaloguGoster()
    }
}
