package com.example.performansTakip.ui

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.performansTakip.R

import com.example.performansTakip.model.Bolum
import com.example.performansTakip.model.Calisan
import com.example.performansTakip.model.IslemTuru
import com.example.performansTakip.util.CSVYardimcisi
import com.example.performansTakip.util.VeritabaniYardimcisi
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlin.reflect.KProperty

class AyarlarActivity : AppCompatActivity() {
    
    private companion object {
        const val VARSAYILAN_SIFRE = "admin123"
    }
    
    private lateinit var veritabaniYardimcisi: VeritabaniYardimcisi
    private lateinit var csvYardimcisi: CSVYardimcisi
    private lateinit var sharedPreferences: SharedPreferences
    
    // ActivityResult işleyicileri
    private lateinit var bolumleriLauncher: ActivityResultLauncher<Intent>
    private lateinit var calisanlarLauncher: ActivityResultLauncher<Intent>
    private lateinit var islemTurleriLauncher: ActivityResultLauncher<Intent>


    // Giriş görünümleri
    private lateinit var loginCard: View
    private lateinit var etSifre: TextInputEditText
    private lateinit var btnGiris: MaterialButton
    
    // Şifre değiştirme görünümleri
    private lateinit var etYeniSifre: TextInputEditText
    private lateinit var etYeniSifreTekrar: TextInputEditText
    private lateinit var btnSifreDegistir: MaterialButton
    
    // Yönetici paneli görünümleri
    private lateinit var adminPanel: View
    private lateinit var etBolumAdi: TextInputEditText
    private lateinit var etSorumluKisi: TextInputEditText
    private lateinit var btnBolumEkle: MaterialButton
    private lateinit var rvBolumler: RecyclerView
    
    private lateinit var etIslemTuruAdi: TextInputEditText
    private lateinit var etIslemBirimi: TextInputEditText
    private lateinit var btnIslemTuruEkle: MaterialButton
    private lateinit var rvIslemTurleri: RecyclerView
    
    // Firma ayarları görünümleri
    private lateinit var etFirmaIsmi: TextInputEditText
    private lateinit var btnFirmaIsmiKaydet: MaterialButton
    
    // Çalışan ekleme için görünümler
    private lateinit var etCalisanAdi: TextInputEditText
    private lateinit var actvCalisanBolumu: AutoCompleteTextView
    private lateinit var btnCalisanEkle: MaterialButton
    private lateinit var rvCalisanlar: RecyclerView
    
    private lateinit var btnCSVDisaAktar: MaterialButton
    private lateinit var btnCSVIceAktar: MaterialButton
    private lateinit var btnTumVerileriSil: MaterialButton
    
    private lateinit var bottomNavigation: BottomNavigationView
    
    private lateinit var bolumlerAdapter: BolumlerAdapter
    private lateinit var islemTurleriAdapter: IslemTurleriAdapter
    private lateinit var calisanlarAdapter: CalisanlarAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Aydınlık modu zorla - karanlık mod kapalı
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
        // Önce SharedPreferences'i başlat (her şeyden önce yapılmalı)
        sharedPreferences = getSharedPreferences("performans_takip", MODE_PRIVATE)
        
        // ActivityResult işleyicilerini oluştur
        islemTurleriLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    handleIslemTurleriResult(uri)
                }
            }
        }
        
        calisanlarLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    handleCalisanlarResult(uri)
                }
            }
        }
        
        // Bölümler için ActivityResult işleyicisi
        bolumleriLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    handleBolumlerResult(uri)
                }
            }
        }
        
        // Layout'u ayarla
        setContentView(R.layout.activity_ayarlar)
        
        // Sırasıyla işlemleri yap, ama güvenli şekilde
        try {
            // Önce tüm view'ları başlat
            viewlariBaslat()
            
            // Sonra veritabanını başlat
            veritabaniniBaslat()
            
            // Dinleyicileri ekle
            dinleyicileriAyarla()
            
            // Veritabanı hazır olduktan sonra adapterleri ayarla
            adapterleriAyarla()
            
            // Giriş kontrolünü yap
            girisKontrolEt()
        } catch (e: Exception) {
            // Hata olursa kullanıcıya bildir ve log'a yaz
            Log.e("AyarlarActivity", "Başlatma hatası", e)
            Toast.makeText(this, "Ayarlar yüklenirken bir hata oluştu: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun viewlariBaslat() {
        try {
            // Giriş paneli
            loginCard = findViewById(R.id.loginCard) ?: throw NullPointerException("loginCard bulunamadı")
            etSifre = findViewById(R.id.etSifre) ?: throw NullPointerException("etSifre bulunamadı")
            btnGiris = findViewById(R.id.btnGiris) ?: throw NullPointerException("btnGiris bulunamadı")
            
            // Şifre değiştirme
            etYeniSifre = findViewById(R.id.etYeniSifre) ?: throw NullPointerException("etYeniSifre bulunamadı")
            etYeniSifreTekrar = findViewById(R.id.etYeniSifreTekrar) ?: throw NullPointerException("etYeniSifreTekrar bulunamadı")
            btnSifreDegistir = findViewById(R.id.btnSifreDegistir) ?: throw NullPointerException("btnSifreDegistir bulunamadı")
            
            // Yönetici paneli
            adminPanel = findViewById(R.id.adminPanel) ?: throw NullPointerException("adminPanel bulunamadı")
            etBolumAdi = findViewById(R.id.etBolumAdi) ?: throw NullPointerException("etBolumAdi bulunamadı")
            etSorumluKisi = findViewById(R.id.etSorumluKisi) ?: throw NullPointerException("etSorumluKisi bulunamadı")
            btnBolumEkle = findViewById(R.id.btnBolumEkle) ?: throw NullPointerException("btnBolumEkle bulunamadı")
            rvBolumler = findViewById(R.id.rvBolumler) ?: throw NullPointerException("rvBolumler bulunamadı")
            
            etIslemTuruAdi = findViewById(R.id.etIslemTuruAdi) ?: throw NullPointerException("etIslemTuruAdi bulunamadı")
            etIslemBirimi = findViewById(R.id.etIslemBirimi) ?: throw NullPointerException("etIslemBirimi bulunamadı")
            btnIslemTuruEkle = findViewById(R.id.btnIslemTuruEkle) ?: throw NullPointerException("btnIslemTuruEkle bulunamadı")
            rvIslemTurleri = findViewById(R.id.rvIslemTurleri) ?: throw NullPointerException("rvIslemTurleri bulunamadı")
            
            // Çalışan görünümleri
            etCalisanAdi = findViewById(R.id.etCalisanAdi) ?: throw NullPointerException("etCalisanAdi bulunamadı")
            actvCalisanBolumu = findViewById(R.id.actvCalisanBolumu) ?: throw NullPointerException("actvCalisanBolumu bulunamadı")
            btnCalisanEkle = findViewById(R.id.btnCalisanEkle) ?: throw NullPointerException("btnCalisanEkle bulunamadı")
            rvCalisanlar = findViewById(R.id.rvCalisanlar) ?: throw NullPointerException("rvCalisanlar bulunamadı")
            
            // CSV işlemleri için görünümler
            btnCSVDisaAktar = findViewById(R.id.btnCSVDisaAktar) ?: throw NullPointerException("btnCSVDisaAktar bulunamadı")
            btnCSVIceAktar = findViewById(R.id.btnCSVIceAktar) ?: throw NullPointerException("btnCSVIceAktar bulunamadı")
            
            // Firma ayarları için görünümler
            etFirmaIsmi = findViewById(R.id.etFirmaIsmi) ?: throw NullPointerException("etFirmaIsmi bulunamadı")
            btnFirmaIsmiKaydet = findViewById(R.id.btnFirmaIsmiKaydet) ?: throw NullPointerException("btnFirmaIsmiKaydet bulunamadı")
            
            // Tüm verileri silme butonu
            btnTumVerileriSil = findViewById(R.id.btnTumVerileriSil) ?: throw NullPointerException("btnTumVerileriSil bulunamadı")
            
            // Alt gezinme çubuğu
            bottomNavigation = findViewById(R.id.bottomNavigation) ?: throw NullPointerException("bottomNavigation bulunamadı")
            
            // CSV yardımcısını başlat
            csvYardimcisi = CSVYardimcisi(this)
            
            // RecyclerView'ları hazırla
            rvBolumler.layoutManager = LinearLayoutManager(this)
            rvIslemTurleri.layoutManager = LinearLayoutManager(this)
            rvCalisanlar.layoutManager = LinearLayoutManager(this)
            
            Log.d("AyarlarActivity", "Tüm viewlar başarıyla başlatıldı")
        } catch (e: Exception) {
            Log.e("AyarlarActivity", "Viewlar başlatılırken hata: ${e.message}")
            throw e  // Yeniden fırlat ki üst metodda yakalansın
        }
    }
    
    private fun veritabaniniBaslat() {
        try {
            veritabaniYardimcisi = VeritabaniYardimcisi(this)
            Log.d("AyarlarActivity", "Veritabanı yardımcısı başarıyla başlatıldı")
        } catch (e: Exception) {
            Log.e("AyarlarActivity", "Veritabanı başlatılırken hata", e)
            Toast.makeText(this, "Veritabanı başlatılırken hata oluştu", Toast.LENGTH_SHORT).show()
            throw e
        }
    }
    
    private fun adapterleriAyarla() {
        Log.d("AyarlarActivity", "adapterleriAyarla başlıyor")
        
        // Bölüm dropdown için adapter
        try {
            val bolumAdlari = veritabaniYardimcisi.tumBolumleriGetir().map { it.ad }.toTypedArray()
            val bolumAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bolumAdlari)
            actvCalisanBolumu.setAdapter(bolumAdapter)
            actvCalisanBolumu.inputType = 0 // Klavye açılmaması için
            actvCalisanBolumu.isFocusable = false
            Log.d("AyarlarActivity", "Bölüm dropdown adapteri başarıyla ayarlandı")
        } catch (e: Exception) {
            Log.e("AyarlarActivity", "Bölüm dropdown adapteri ayarlanırken hata", e)
            Toast.makeText(this, "Bölüm listesi yüklenirken bir hata oluştu", Toast.LENGTH_SHORT).show()
        }
        
        // Bölümler RecyclerView adapteri
        try {
            bolumlerAdapter = BolumlerAdapter { bolum ->
                silmeOnayDialoguGoster {
                    veritabaniYardimcisi.bolumSil(bolum.id)
                    bolumleriYukle()
                }
            }
            
            rvBolumler.adapter = bolumlerAdapter
            Log.d("AyarlarActivity", "Bölümler adapteri başarıyla ayarlandı")
        } catch (e: Exception) {
            Log.e("AyarlarActivity", "Bölümler adapteri ayarlanırken hata", e)
            Toast.makeText(this, "Bölümler listesi yüklenirken bir hata oluştu", Toast.LENGTH_SHORT).show()
        }
        
        // İşlem türleri RecyclerView adapteri
        try {
            islemTurleriAdapter = IslemTurleriAdapter { islemTuru ->
                silmeOnayDialoguGoster {
                    veritabaniYardimcisi.islemTuruSil(islemTuru.id)
                    islemTurleriniYukle()
                }
            }
            
            rvIslemTurleri.adapter = islemTurleriAdapter
            Log.d("AyarlarActivity", "İşlem türleri adapteri başarıyla ayarlandı")
        } catch (e: Exception) {
            Log.e("AyarlarActivity", "İşlem türleri adapteri ayarlanırken hata", e)
            Toast.makeText(this, "İşlem türleri listesi yüklenirken bir hata oluştu", Toast.LENGTH_SHORT).show()
        }
        
        // Çalışanlar RecyclerView adapteri
        try {
            calisanlarAdapter = CalisanlarAdapter(mutableListOf()) { calisan ->
                silmeCalisanOnayDialoguGoster {
                    veritabaniYardimcisi.calisanSil(calisan.id)
                    calisanlariYukle()
                }
            }
            
            rvCalisanlar.adapter = calisanlarAdapter
            Log.d("AyarlarActivity", "Çalışanlar adapteri başarıyla ayarlandı")
        } catch (e: Exception) {
            Log.e("AyarlarActivity", "Çalışanlar adapteri ayarlanırken hata", e)
            Toast.makeText(this, "Çalışanlar listesi yüklenirken bir hata oluştu", Toast.LENGTH_SHORT).show()
        }
        
        // Verileri yükle - her birini ayrı try-catch içinde yap
        try {
            bolumleriYukle()
        } catch (e: Exception) {
            Log.e("AyarlarActivity", "Bölümleri yüklerken hata", e)
        }
        
        try {
            islemTurleriniYukle()
        } catch (e: Exception) {
            Log.e("AyarlarActivity", "İşlem türlerini yüklerken hata", e)
        }
        
        try {
            calisanlariYukle()
        } catch (e: Exception) {
            Log.e("AyarlarActivity", "Çalışanları yüklerken hata", e)
        }
        
        Log.d("AyarlarActivity", "adapterleriAyarla tamamlandı")
    }
    
    private fun dinleyicileriAyarla() {
        btnGiris.setOnClickListener {
            val sifre = etSifre.text.toString()
            val kaydedilmisSifre = sharedPreferences.getString("admin_sifre", VARSAYILAN_SIFRE)
            
            if (sifre == kaydedilmisSifre) {
                // Doğru şifre, yönetici panelini göster
                sharedPreferences.edit().putBoolean("giris_yapildi", true).apply()
                yoneticiPaneliniGoster()
            } else {
                // Yanlış şifre, hata mesajı göster
                Toast.makeText(this, "Hatalı şifre!", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Şifre değiştirme butonu
        btnSifreDegistir.setOnClickListener {
            sifreDegistir()
        }
        
        // Firma ismi kaydet butonuna tıklandığında
        btnFirmaIsmiKaydet.setOnClickListener {
            firmaIsmiKaydet()
        }
        
        btnBolumEkle.setOnClickListener {
            bolumEkle()
        }
        
        btnIslemTuruEkle.setOnClickListener {
            islemTuruEkle()
        }
        
        btnCalisanEkle.setOnClickListener {
            calisanEkle()
        }
        
        btnCSVDisaAktar.setOnClickListener {
            val secenekler = arrayOf("Bölümler", "İşlem Türleri", "Çalışanlar")
            AlertDialog.Builder(this)
                .setTitle("CSV Dışa Aktar")
                .setItems(secenekler) { _, which ->
                    when (which) {
                        0 -> bolumleriDisaAktar()
                        1 -> islemTurleriniDisaAktar()
                        2 -> calisanlariDisaAktar()
                    }
                }
                .show()
        }
        
        btnCSVIceAktar.setOnClickListener {
            val secenekler = arrayOf("Bölümler", "İşlem Türleri", "Çalışanlar")
            AlertDialog.Builder(this)
                .setTitle("CSV İçe Aktar")
                .setItems(secenekler) { _, which ->
                    when (which) {
                        0 -> bolumleriIceAktar()
                        1 -> islemTurleriniIceAktar()
                        2 -> calisanlariIceAktar()
                    }
                }
                .show()
        }
        
        btnTumVerileriSil.setOnClickListener {
            tumVerileriSilOnayDialoguGoster()
        }
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_kayit -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_ozet -> {
                    startActivity(Intent(this, OzetActivity::class.java))
                    true
                }
                R.id.nav_ayarlar -> {
                    // Zaten AyarlarActivity'deyiz
                    true
                }
                R.id.nav_cikis -> {
                    // Çıkış butonuna basıldığında uygulamadan çık
                    finishAffinity()
                    true
                }
                else -> false
            }
        }
    }
    
    private fun girisKontrolEt() {
        val girisYapildi = sharedPreferences.getBoolean("giris_yapildi", false)
        if (girisYapildi) {
            yoneticiPaneliniGoster()
        } else {
            girisFormuGoster()
        }
    }
    
    private fun girisFormuGoster() {
        loginCard.visibility = View.VISIBLE
        adminPanel.visibility = View.GONE
    }
    
    private fun yoneticiPaneliniGoster() {
        loginCard.visibility = View.GONE
        adminPanel.visibility = View.VISIBLE
        bolumleriYukle()
        islemTurleriniYukle()
        calisanlariYukle()
    }
    
    private fun bolumEkle() {
        val bolumAdi = etBolumAdi.text.toString().trim()
        val sorumluKisi = etSorumluKisi.text.toString().trim()
        
        if (bolumAdi.isEmpty() || sorumluKisi.isEmpty()) {
            Toast.makeText(this, "Tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }
        
        val bolum = Bolum(ad = bolumAdi, sorumluKisi = sorumluKisi)
        val eklenenId = veritabaniYardimcisi.bolumEkle(bolum)
        
        if (eklenenId > 0) {
            Toast.makeText(this, "Bölüm eklendi: $bolumAdi", Toast.LENGTH_SHORT).show()
            bolumleriYukle()
            
            // Alanları temizle
            etBolumAdi.text?.clear()
            etSorumluKisi.text?.clear()
        } else {
            Toast.makeText(this, "Bölüm eklenirken hata oluştu", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun islemTuruEkle() {
        val islemTuruAdi = etIslemTuruAdi.text.toString().trim()
        val birim = etIslemBirimi.text.toString().trim()
        
        if (islemTuruAdi.isEmpty() || birim.isEmpty()) {
            Toast.makeText(this, "Tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }
        
        val islemTuru = IslemTuru(ad = islemTuruAdi, birim = birim)
        val eklenenId = veritabaniYardimcisi.islemTuruEkle(islemTuru)
        
        if (eklenenId > 0) {
            Toast.makeText(this, "İşlem türü eklendi: $islemTuruAdi", Toast.LENGTH_SHORT).show()
            islemTurleriniYukle()
            
            // Alanları temizle
            etIslemTuruAdi.text?.clear()
            etIslemBirimi.text?.clear()
        } else {
            Toast.makeText(this, "İşlem türü eklenirken hata oluştu", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun calisanEkle() {
        val calisanAdi = etCalisanAdi.text.toString().trim()
        val calisanBolumu = actvCalisanBolumu.text.toString().trim()
        
        if (calisanAdi.isEmpty() || calisanBolumu.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }
        
        val calisan = Calisan(ad = calisanAdi, bolum = calisanBolumu)
        val eklenenId = veritabaniYardimcisi.calisanEkle(calisan)
        
        if (eklenenId > 0) {
            Toast.makeText(this, "Çalışan başarıyla eklendi", Toast.LENGTH_SHORT).show()
            etCalisanAdi.setText("")
            actvCalisanBolumu.setText("")
            // Alanları temizle
            etCalisanAdi.text?.clear()
            actvCalisanBolumu.setText("")
        } else {
            Toast.makeText(this, "Çalışan eklenirken hata oluştu", Toast.LENGTH_SHORT).show()
        }
        calisanlarDropdownGuncelle() // Dropdown listesini güncelle
    }
    
    private fun bolumleriYukle() {
        val bolumler = veritabaniYardimcisi.tumBolumleriGetir()
        bolumlerAdapter.bolumleriGuncelle(bolumler)
    }
    
    private fun islemTurleriniYukle() {
        val islemTurleri = veritabaniYardimcisi.tumIslemTurleriniGetir()
        islemTurleriAdapter.islemTurleriniGuncelle(islemTurleri)
    }
    
    private fun calisanlariYukle() {
        val calisanlar = veritabaniYardimcisi.tumCalisanlariGetir()
        if (!::calisanlarAdapter.isInitialized) {
            calisanlarAdapter = CalisanlarAdapter(calisanlar.toMutableList()) { calisan ->
                silmeCalisanOnayDialoguGoster {
                    veritabaniYardimcisi.calisanSil(calisan.id)
                    calisanlariYukle()
                    calisanlarDropdownGuncelle()
                }
            }
            rvCalisanlar.adapter = calisanlarAdapter
            rvCalisanlar.layoutManager = LinearLayoutManager(this)
        } else {
            calisanlarAdapter.calisanlariGuncelle(calisanlar)
        }
        calisanlarDropdownGuncelle() // Dropdown listesini güncelle
    }
    
    private fun calisanlarDropdownGuncelle() {
        try {
            val bolumIsimleri = veritabaniYardimcisi.tumBolumleriGetir().map { it.ad }.toTypedArray()
            if (bolumIsimleri.isNotEmpty()) {
                val bolumlerListAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bolumIsimleri)
                actvCalisanBolumu.setAdapter(bolumlerListAdapter)
                actvCalisanBolumu.inputType = 0 // Klavye açılmaması için
                actvCalisanBolumu.isFocusable = false
                Log.d("AyarlarActivity", "Dropdown güncellendi, ${bolumIsimleri.size} bölüm yüklendi")
            } else {
                Log.d("AyarlarActivity", "Dropdown güncellenirken dikkat: Bölüm listesi boş")
                Toast.makeText(this, "Bölüm listesi boş, lütfen önce bölüm ekleyin", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("AyarlarActivity", "Bölüm dropdown'u güncellenirken hata", e)
            Toast.makeText(this, "Bölüm listesi güncellenirken hata oluştu", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun bolumleriDisaAktar() {
        val dosya = csvYardimcisi.bolumleriDisaAktar(veritabaniYardimcisi.tumBolumleriGetir())
        dosya?.let {
            csvYardimcisi.csvDosyasiniPaylas(it)
            Toast.makeText(this, "Bölümler CSV dosyası oluşturuldu", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(this, "CSV oluşturulurken hata", Toast.LENGTH_SHORT).show()
    }
    
    private fun islemTurleriniDisaAktar() {
        val dosya = csvYardimcisi.islemTurleriniDisaAktar(veritabaniYardimcisi.tumIslemTurleriniGetir())
        dosya?.let {
            csvYardimcisi.csvDosyasiniPaylas(it)
            Toast.makeText(this, "İşlem türleri CSV dosyası oluşturuldu", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(this, "CSV oluşturulurken hata", Toast.LENGTH_SHORT).show()
    }
    
    private fun calisanlariDisaAktar() {
        val dosya = csvYardimcisi.calisanlariDisaAktar(veritabaniYardimcisi.tumCalisanlariGetir())
        dosya?.let {
            csvYardimcisi.csvDosyasiniPaylas(it)
            Toast.makeText(this, "Çalışanlar CSV dosyası oluşturuldu", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(this, "CSV oluşturulurken hata", Toast.LENGTH_SHORT).show()
    }
    
    private fun bolumleriIceAktar() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/*"
        }
        bolumleriLauncher.launch(intent)
    }
    
    private fun islemTurleriniIceAktar() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/*"
        }
        islemTurleriLauncher.launch(intent)
    }
    
    private fun calisanlariIceAktar() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/*"
        }
        calisanlarLauncher.launch(intent)
    }
    
    private fun handleIslemTurleriResult(uri: Uri) {
        val islemTurleri = csvYardimcisi.islemTurleriniIceAktar(uri)
        if (islemTurleri.isNotEmpty()) {
            islemTurleri.forEach { islemTuru ->
                veritabaniYardimcisi.islemTuruEkle(islemTuru)
            }
            Toast.makeText(this, "${islemTurleri.size} işlem türü içe aktarıldı", Toast.LENGTH_SHORT).show()
            islemTurleriniYukle()
        } else {
            Toast.makeText(this, "CSV içe aktarılırken hata oluştu veya boş dosya", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun handleBolumlerResult(uri: Uri) {
        try {
            val bolumler = csvYardimcisi.bolumleriIceAktar(uri)
            if (bolumler.isNotEmpty()) {
                var eklenenSayisi = 0
                
                // Önceki tüm bölümleri silelim
                veritabaniYardimcisi.tumBolumleriSil()
                
                // Yeni bölümleri ekleyelim
                bolumler.forEach { bolum ->
                    val id = veritabaniYardimcisi.bolumEkle(bolum)
                    if (id > 0) eklenenSayisi++
                }
                
                Toast.makeText(this, "$eklenenSayisi bölüm içe aktarıldı", Toast.LENGTH_SHORT).show()
                bolumleriYukle() // Listeyi güncelle
            } else {
                Toast.makeText(this, "CSV içe aktarılırken hata oluştu veya boş dosya", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("CSV_IMPORT", "CSV içe aktarma hatası", e)
        }
    }
    
    private fun handleCalisanlarResult(uri: Uri) {
        try {
            val calisanlar = csvYardimcisi.calisanlariIceAktar(uri)
            if (calisanlar.isNotEmpty()) {
                var eklenenSayisi = 0
                
                // Önceki tüm çalışanları silelim
                veritabaniYardimcisi.tumCalisanlariSil()
                
                // Yeni çalışanları ekleyelim
                calisanlar.forEach { calisan ->
                    val id = veritabaniYardimcisi.calisanEkle(calisan)
                    if (id > 0) eklenenSayisi++
                }
                
                Toast.makeText(this, "$eklenenSayisi çalışan içe aktarıldı", Toast.LENGTH_SHORT).show()
                calisanlariYukle() // Listeyi güncelle
                calisanlarDropdownGuncelle() // Dropdown listesini güncelle
            } else {
                Toast.makeText(this, "CSV içe aktarılırken hata oluştu veya boş dosya", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("CSV_IMPORT", "CSV içe aktarma hatası", e)
        }
    }
    
    private fun tumVerileriSilOnayDialoguGoster() {
        AlertDialog.Builder(this)
            .setTitle("Tüm Verileri Sil")
            .setMessage("Bu işlem tüm verileri kalıcı olarak silecektir. Devam etmek istiyor musunuz?")
            .setPositiveButton("Evet") { _, _ ->
                tumVerileriSil()
            }
            .setNegativeButton("İptal", null)
            .show()
    }
    
    private fun sifreDegistir() {
        val yeniSifre = etYeniSifre.text.toString().trim()
        val yeniSifreTekrar = etYeniSifreTekrar.text.toString().trim()
        
        if (yeniSifre.isEmpty() || yeniSifreTekrar.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (yeniSifre != yeniSifreTekrar) {
            Toast.makeText(this, "Şifreler eşleşmiyor", Toast.LENGTH_SHORT).show()
            return
        }
        
        sharedPreferences.edit().putString("admin_sifre", yeniSifre).apply()
        Toast.makeText(this, "Şifre başarıyla değiştirildi", Toast.LENGTH_SHORT).show()
        
        // Alanları temizle
        etYeniSifre.text?.clear()
        etYeniSifreTekrar.text?.clear()
    }
    
    private fun firmaIsmiKaydet() {
        val firmaIsmi = etFirmaIsmi.text.toString().trim()
        
        if (firmaIsmi.isEmpty()) {
            Toast.makeText(this, "Lütfen firma ismini girin", Toast.LENGTH_SHORT).show()
            return
        }
        
        sharedPreferences.edit().putString("firma_ismi", firmaIsmi).apply()
        Toast.makeText(this, "Firma ismi kaydedildi", Toast.LENGTH_SHORT).show()
        etFirmaIsmi.clearFocus()
        
        // Ana ekran, firma adını görüntüleyecek
    }
    
    private fun tumVerileriSil() {
        // Tüm verileri veritabanından sil
        veritabaniYardimcisi.tumBolumleriSil()
        veritabaniYardimcisi.tumIslemTurleriniSil()
        veritabaniYardimcisi.tumCalisanlariSil()
        veritabaniYardimcisi.tumKayitlariSil()
        
        // Verilerin başarıyla silinme mesajını göster
        Toast.makeText(this, "Tüm veriler silindi", Toast.LENGTH_SHORT).show()
        
        // Listeleri güncelle
        bolumleriYukle()
        islemTurleriniYukle()
        calisanlariYukle()
    }
    
    private fun silmeOnayDialoguGoster(onaylandi: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Silme Onayı")
            .setMessage("Bu öğeyi silmek istediğinizden emin misiniz?")
            .setPositiveButton("Evet") { _, _ -> onaylandi() }
            .setNegativeButton("Hayır", null)
            .show()
    }
    
    private fun silmeCalisanOnayDialoguGoster(onConfirm: () -> Unit) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Çalışanı Sil")
        builder.setMessage("Bu çalışanı silmek istediğinizden emin misiniz?")
        builder.setPositiveButton("Evet") { _, _ -> onConfirm() }
        builder.setNegativeButton("Hayır", null)
        builder.show()
    }
    
    override fun onResume() {
        super.onResume()
        bottomNavigation.selectedItemId = R.id.nav_ayarlar
    }
    
    // Firma adı sadece ana ekranda gösterilecek
    
    override fun onPause() {
        super.onPause()
        // Güvenlik için aktiviteden ayrılırken çıkış yap
        sharedPreferences.edit().putBoolean("giris_yapildi", false).apply()
    }
    
    // Adapterler
    private inner class CalisanlarAdapter(private val calisanListesi: MutableList<Calisan>, private val onDeleteClick: (Calisan) -> Unit) : RecyclerView.Adapter<CalisanlarAdapter.CalisanViewHolder>() {

        fun calisanlariGuncelle(yeniCalisanlar: List<Calisan>) {
            calisanListesi.clear()
            calisanListesi.addAll(yeniCalisanlar)
            // NotifyDataSetChanged metodunun tam yolunu belirtin
            (this as androidx.recyclerview.widget.RecyclerView.Adapter<*>).notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalisanViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
            return CalisanViewHolder(view)
        }

        override fun onBindViewHolder(holder: CalisanViewHolder, position: Int) {
            val calisan = calisanListesi[position]
            holder.bind(calisan)
        }
        
        override fun getItemCount(): Int = calisanListesi.size
        
        inner class CalisanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvAd: TextView = itemView.findViewById(android.R.id.text1)
            private val tvBolum: TextView = itemView.findViewById(android.R.id.text2)
            
            fun bind(calisan: Calisan) {
                tvAd.text = calisan.ad
                tvBolum.text = calisan.bolum
                itemView.setOnLongClickListener {
                    onDeleteClick(calisan)
                    true
                }
            }
        }
    }
    
    private inner class BolumlerAdapter(
        private val silmeTiklamasi: (Bolum) -> Unit
    ) : RecyclerView.Adapter<BolumlerAdapter.BolumViewHolder>() {
        
        private var bolumler = listOf<Bolum>()
        
        fun bolumleriGuncelle(yeniBolumler: List<Bolum>) {
            bolumler = yeniBolumler
            notifyDataSetChanged()
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BolumViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                android.R.layout.simple_list_item_2, parent, false
            )
            return BolumViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: BolumViewHolder, position: Int) {
            holder.bind(bolumler[position])
        }
        
        override fun getItemCount() = bolumler.size
        
        inner class BolumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val text1: TextView = itemView.findViewById(android.R.id.text1)
            private val text2: TextView = itemView.findViewById(android.R.id.text2)
            
            fun bind(bolum: Bolum) {
                text1.text = bolum.ad
                text2.text = bolum.sorumluKisi
                
                itemView.setOnLongClickListener {
                    silmeTiklamasi(bolum)
                    true
                }
            }
        }
    }
    
    private inner class IslemTurleriAdapter(
        private val silmeTiklamasi: (IslemTuru) -> Unit
    ) : RecyclerView.Adapter<IslemTurleriAdapter.IslemTuruViewHolder>() {
        
        private var islemTurleri = listOf<IslemTuru>()
        
        fun islemTurleriniGuncelle(yeniIslemTurleri: List<IslemTuru>) {
            islemTurleri = yeniIslemTurleri
            notifyDataSetChanged()
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IslemTuruViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                android.R.layout.simple_list_item_2, parent, false
            )
            return IslemTuruViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: IslemTuruViewHolder, position: Int) {
            holder.bind(islemTurleri[position])
        }
        
        override fun getItemCount() = islemTurleri.size
        
        inner class IslemTuruViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val text1: TextView = itemView.findViewById(android.R.id.text1)
            private val text2: TextView = itemView.findViewById(android.R.id.text2)
            
            fun bind(islemTuru: IslemTuru) {
                text1.text = islemTuru.ad
                text2.text = islemTuru.birim
                
                itemView.setOnLongClickListener {
                    silmeTiklamasi(islemTuru)
                    true
                }
            }
        }
    }
}
