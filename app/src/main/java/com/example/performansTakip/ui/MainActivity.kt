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
    
    private var seciliTarih = ""
    private var islemTurleri = listOf<IslemTuru>()
    private var bolumler = listOf<String>()
    private var tumCalisanlar = listOf<Calisan>()
    private var filtrelenmisCalısanlar = listOf<Calisan>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ActionBar başlığını gizle
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        
        viewlariBaslat()
        veritabaniniBaslat()
        dinleyicileriAyarla()
        verileriYukle()
        autoCompleteViewlariAyarla()
        firmaAdiniGoster()
    }
    
    private fun viewlariBaslat() {
        etTarih = findViewById(R.id.etTarih)
        actvBolum = findViewById(R.id.actvBolum)
        actvCalisan = findViewById(R.id.actvCalisan)
        actvIslemTuru = findViewById(R.id.actvIslemTuru)
        etMiktar = findViewById(R.id.etMiktar)
        etBirim = findViewById(R.id.etBirim)
        etAciklama = findViewById(R.id.etAciklama)
        btnWhatsAppGonder = findViewById(R.id.btnWhatsAppGonder)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
    }
    
    private fun firmaAdiniGoster() {
        val firmaAdi = sharedPreferences.getString("firma_ismi", "")
        if (!firmaAdi.isNullOrEmpty()) {
            val tvFirmaAdi = findViewById<TextView>(R.id.tvFirmaAdi)
            tvFirmaAdi.text = firmaAdi
        }
    }
    
    private fun veritabaniniBaslat() {
        veritabaniYardimcisi = VeritabaniYardimcisi(this)
    }
    
    private fun dinleyicileriAyarla() {
        etTarih.setOnClickListener {
            tarihSeciciGoster()
        }
        
        // Bölüm seçildiğinde çalışanları filtrele
        actvBolum.setOnItemClickListener { _, _, _, _ ->
            val seciliBolum = actvBolum.text.toString()
            if (seciliBolum.isNotEmpty()) {
                // Seçilen bölümü kaydet
                sharedPreferences.edit().putString("son_bolum", seciliBolum).apply()
                
                // Çalışanları filtrele
                filtrelenmisCalısanlar = tumCalisanlar.filter { it.bolum == seciliBolum }
                
                // Filtrelenmiş çalışanlar için adapter güncelle
                val calisanAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    filtrelenmisCalısanlar.map { it.ad }
                )
                actvCalisan.setAdapter(calisanAdapter)
                actvCalisan.setText("") // Çalışan seçimini sıfırla
            }
            klavyeyiGizle()
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
            whatsAppGonder()
        }
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_kayit -> {
                    // Zaten MainActivity'deyiz
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
                    finishAffinity() // Uygulamadan tamamen çıkış için
                    true
                }
                else -> false
            }
        }
    }
    
    private fun verileriYukle() {
        islemTurleri = veritabaniYardimcisi.tumIslemTurleriniGetir()
        tumCalisanlar = veritabaniYardimcisi.tumCalisanlariGetir()
        bolumler = veritabaniYardimcisi.tumBolumleriGetir().map { it.ad }
        
        // En son seçilen çalışanı ve bölümü otomatik olarak seç
        val sonCalisan = sharedPreferences.getString("son_calisan", "")
        val sonBolum = sharedPreferences.getString("son_bolum", "")
        
        if (!sonBolum.isNullOrEmpty()) {
            actvBolum.setText(sonBolum)
            // Bölüme göre çalışanları filtrele
            filtrelenmisCalısanlar = tumCalisanlar.filter { it.bolum == sonBolum }
        } else {
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
    }
    
    private fun autoCompleteViewlariAyarla() {
        // Bölümler için adapter
        val bolumAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            bolumler
        )
        actvBolum.setAdapter(bolumAdapter)
        
        // İşlem türleri için adapter
        val islemTuruAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            islemTurleri.map { it.ad }
        )
        actvIslemTuru.setAdapter(islemTuruAdapter)
        
        // Çalışanlar için adapter - filtrelenmiş liste kullan
        val calisanAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            filtrelenmisCalısanlar.map { it.ad }
        )
        actvCalisan.setAdapter(calisanAdapter)
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
        val calisanAdi = actvCalisan.text.toString().trim()
        val islemTuru = actvIslemTuru.text.toString().trim()
        val miktarMetni = etMiktar.text.toString().trim()
        val birim = etBirim.text.toString().trim()
        val aciklama = etAciklama.text.toString().trim()
        
        // Doğrulama
        if (seciliTarih.isEmpty() || calisanAdi.isEmpty() || islemTuru.isEmpty() || 
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
            
            val mesaj = "$seciliTarih | $calisanAdi | $islemTuru | $formatliMiktar | $birim"
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
        bottomNavigation.selectedItemId = R.id.nav_kayit
    }
}
