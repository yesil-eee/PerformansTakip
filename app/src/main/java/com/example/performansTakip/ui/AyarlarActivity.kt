package com.example.performanstakip.ui

import android.app.Activity
import android.content.Intent
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
import com.example.performanstakip.R

import com.example.performanstakip.model.Bolum
import com.example.performanstakip.model.Calisan
import com.example.performanstakip.model.IslemTuru
import com.example.performanstakip.util.CSVYardimcisi
import com.example.performanstakip.util.VeritabaniYardimcisi
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlin.reflect.KProperty

class AyarlarActivity : AppCompatActivity() {
    
    private companion object {
        const val VARSAYILAN_SIFRE = "admin123"
        const val GELISTIRICI_ADI = "Mehmet KIRAN"
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
    private lateinit var tvGelistiriciAdi: TextView
    
    private lateinit var etIslemTuruAdi: TextInputEditText
    private lateinit var etIslemBirimi: TextInputEditText
    private lateinit var btnIslemTuruEkle: MaterialButton
    private lateinit var rvIslemTurleri: RecyclerView
    
    // Firma ayarları görünümleri
    private lateinit var etFirmaIsmi: TextInputEditText
    private lateinit var btnFirmaIsmiKaydet: MaterialButton
    
    // Çalışan ekleme için görünümler
    private lateinit var actvCalisanAdi: AutoCompleteTextView
    private lateinit var etCalisanBolumu: TextInputEditText
    private lateinit var btnCalisanEkle: MaterialButton
    private lateinit var rvCalisanlar: RecyclerView
    private lateinit var calisanlarListAdapter: ArrayAdapter<String>
    
    private lateinit var btnCSVDisaAktar: MaterialButton
    private lateinit var btnCSVIceAktar: MaterialButton
    private lateinit var btnTumVerileriSil: MaterialButton
    
    private lateinit var bottomNavigation: BottomNavigationView
    
    private lateinit var bolumlerAdapter: BolumlerAdapter
    private lateinit var islemTurleriAdapter: IslemTurleriAdapter
    private lateinit var calisanlarAdapter: CalisanlarAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
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
        setContentView(R.layout.activity_ayarlar)
        tvGelistiriciAdi = findViewById(R.id.tvGelistiriciAdi)




        viewlariBaslat()
        veritabaniniBaslat()
        adapterleriAyarla()
        dinleyicileriAyarla()
        girisKontrolEt()
    }
    
    private fun viewlariBaslat() {
        // Giriş görünümleri
        loginCard = findViewById(R.id.loginCard)
        etSifre = findViewById(R.id.etSifre)
        btnGiris = findViewById(R.id.btnGiris)
        
        // Yönetici paneli görünümleri
        adminPanel = findViewById(R.id.adminPanel)
        etBolumAdi = findViewById(R.id.etBolumAdi)
        etSorumluKisi = findViewById(R.id.etSorumluKisi)
        btnBolumEkle = findViewById(R.id.btnBolumEkle)
        rvBolumler = findViewById(R.id.rvBolumler)
        
        etIslemTuruAdi = findViewById(R.id.etIslemTuruAdi)
        etIslemBirimi = findViewById(R.id.etIslemBirimi)
        btnIslemTuruEkle = findViewById(R.id.btnIslemTuruEkle)
        rvIslemTurleri = findViewById(R.id.rvIslemTurleri)
        
        // Firma ayarları görünümleri
        etFirmaIsmi = findViewById(R.id.etFirmaIsmi)
        btnFirmaIsmiKaydet = findViewById(R.id.btnFirmaIsmiKaydet)
        tvGelistiriciAdi = findViewById(R.id.tvGelistiriciAdi)
        
        // Şifre değiştirme görünümleri
        etYeniSifre = findViewById(R.id.etYeniSifre)
        etYeniSifreTekrar = findViewById(R.id.etYeniSifreTekrar)
        btnSifreDegistir = findViewById(R.id.btnSifreDegistir)
        
        actvCalisanAdi = findViewById(R.id.actvCalisanAdi)
        etCalisanBolumu = findViewById(R.id.etCalisanBolumu)
        btnCalisanEkle = findViewById(R.id.btnCalisanEkle)
        rvCalisanlar = findViewById(R.id.rvCalisanlar)
        
        btnCSVDisaAktar = findViewById(R.id.btnCSVDisaAktar)
        btnCSVIceAktar = findViewById(R.id.btnCSVIceAktar)
        btnTumVerileriSil = findViewById(R.id.btnTumVerileriSil)
        
        bottomNavigation = findViewById(R.id.bottomNavigation)
        
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        
        // Geliştirici adını ayarla
        tvGelistiriciAdi.text = GELISTIRICI_ADI
        
        // Kaydedilmiş firma ismini yükle
        val firmaIsmi = sharedPreferences.getString("firma_ismi", "")
        if (!firmaIsmi.isNullOrEmpty()) {
            etFirmaIsmi.setText(firmaIsmi)
        }
    }
    
    private fun veritabaniniBaslat() {
        veritabaniYardimcisi = VeritabaniYardimcisi(this)
        csvYardimcisi = CSVYardimcisi(this)
    }
    
    private fun adapterleriAyarla() {
        // Çalışan adı için dropdown adapter
        val calisanIsimleri = veritabaniYardimcisi.tumCalisanlariGetir().map { it.ad }.toTypedArray()
        calisanlarListAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, calisanIsimleri)
        actvCalisanAdi.setAdapter(calisanlarListAdapter)
        
        bolumlerAdapter = BolumlerAdapter { bolum ->
            silmeOnayDialoguGoster {
                veritabaniYardimcisi.bolumSil(bolum.id)
                bolumleriYukle()
            }
        }
        
        rvBolumler.apply {
            adapter = bolumlerAdapter
            layoutManager = LinearLayoutManager(this@AyarlarActivity)
        }
        
        islemTurleriAdapter = IslemTurleriAdapter { islemTuru ->
            silmeOnayDialoguGoster {
                veritabaniYardimcisi.islemTuruSil(islemTuru.id)
                islemTurleriniYukle()
            }
        }
        
        rvIslemTurleri.apply {
            adapter = islemTurleriAdapter
            layoutManager = LinearLayoutManager(this@AyarlarActivity)
        }
        
        calisanlariYukle()
        
        bolumleriYukle()
        islemTurleriniYukle()
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
        val calisanAdi = actvCalisanAdi.text.toString().trim()
        val bolum = etCalisanBolumu.text.toString().trim()
        
        if (calisanAdi.isEmpty()) {
            Toast.makeText(this, "Çalışan adı boş olamaz", Toast.LENGTH_SHORT).show()
            return
        }
        
        val calisan = Calisan(ad = calisanAdi, bolum = bolum)
        val eklenenId = veritabaniYardimcisi.calisanEkle(calisan)
        
        if (eklenenId > 0) {
            Toast.makeText(this, "Çalışan eklendi: $calisanAdi", Toast.LENGTH_SHORT).show()
            calisanlariYukle()
            calisanlarDropdownGuncelle() // Dropdown listesini güncelle
            
            // Alanları temizle
            actvCalisanAdi.setText("") 
            etCalisanBolumu.text?.clear()
        } else {
            Toast.makeText(this, "Çalışan eklenirken hata oluştu", Toast.LENGTH_SHORT).show()
        }
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
        // Dropdown için çalışan adlarını güncelle
        val calisanIsimleri = veritabaniYardimcisi.tumCalisanlariGetir().map { it.ad }.toTypedArray()
        calisanlarListAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, calisanIsimleri)
        actvCalisanAdi.setAdapter(calisanlarListAdapter)
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
        
        // Firma ismini SharedPreferences'a kaydet
        sharedPreferences.edit().putString("firma_ismi", firmaIsmi).apply()
        Toast.makeText(this, "Firma ismi kaydedildi: $firmaIsmi", Toast.LENGTH_SHORT).show()
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
