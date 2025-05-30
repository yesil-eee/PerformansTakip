package com.example.performansTakip.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.performansTakip.R
import com.example.performansTakip.model.PerformansKaydi
import com.example.performansTakip.util.VeritabaniYardimcisi
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
class OzetActivity : AppCompatActivity() {
    
    private lateinit var veritabaniYardimcisi: VeritabaniYardimcisi
    
    private lateinit var tvAktifCalisanlar: TextView
    private lateinit var tvIslemTurleri: TextView
    private lateinit var tvBirimler: TextView
    private lateinit var tvKayitlar: TextView
    private lateinit var rvSonKayitlar: RecyclerView
    private lateinit var tvKayitYok: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var islemTurleriLauncher: ActivityResultLauncher<Intent>
    private lateinit var tvGelistiriciAdi: TextView


    private lateinit var kayitlarAdapter: SonKayitlarAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ozet)
        tvGelistiriciAdi = findViewById(R.id.tvGelistiriciAdi)
        
        // Geliştirici adını ayarla
        val sharedPreferences = getSharedPreferences("ayarlar", MODE_PRIVATE)
        val gelistiriciAdi = sharedPreferences.getString("gelistirici_adi", "İlyas Yeşil") ?: "İlyas Yeşil"
        tvGelistiriciAdi.text = gelistiriciAdi
        
        viewlariBaslat()
        veritabaniniBaslat()
        recyclerViewAyarla()
        dinleyicileriAyarla()
        ozetVerileriniYukle()
    }
    
    private fun viewlariBaslat() {
        tvAktifCalisanlar = findViewById(R.id.tvAktifCalisanlar)
        tvIslemTurleri = findViewById(R.id.tvIslemTurleri)
        tvBirimler = findViewById(R.id.tvBirimler)
        tvKayitlar = findViewById(R.id.tvKayitlar)
        rvSonKayitlar = findViewById(R.id.rvSonKayitlar)
        tvKayitYok = findViewById(R.id.tvKayitYok)
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }
    
    private fun veritabaniniBaslat() {
        veritabaniYardimcisi = VeritabaniYardimcisi(this)
    }
    
    private fun recyclerViewAyarla() {
        kayitlarAdapter = SonKayitlarAdapter()
        rvSonKayitlar.apply {
            layoutManager = LinearLayoutManager(this@OzetActivity)
            adapter = kayitlarAdapter
        }
    }
    
    private fun dinleyicileriAyarla() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_kayit -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_ozet -> {
                    // Zaten OzetActivity'deyiz
                    true
                }
                R.id.nav_ayarlar -> {
                    startActivity(Intent(this, AyarlarActivity::class.java))
                    true
                }
                R.id.nav_cikis -> {
                    finish()
                    true
                }
                else -> false
            }
        }
    }
    
    private fun ozetVerileriniYukle() {
        // İstatistikleri yükle
        val aktifCalisanSayisi = veritabaniYardimcisi.aktifCalisanSayisi()
        val islemTuruSayisi = veritabaniYardimcisi.islemTuruSayisi()
        val birimSayisi = veritabaniYardimcisi.birimSayisi()
        val kayitSayisi = veritabaniYardimcisi.toplamKayitSayisi()
        
        tvAktifCalisanlar.text = aktifCalisanSayisi.toString()
        tvIslemTurleri.text = islemTuruSayisi.toString()
        tvBirimler.text = birimSayisi.toString()
        tvKayitlar.text = kayitSayisi.toString()
        
        // Son kayıtları yükle
        val sonKayitlar = veritabaniYardimcisi.sonKayitlariGetir(5)
        if (sonKayitlar.isEmpty()) {
            tvKayitYok.visibility = View.VISIBLE
            rvSonKayitlar.visibility = View.GONE
        } else {
            tvKayitYok.visibility = View.GONE
            rvSonKayitlar.visibility = View.VISIBLE
            kayitlarAdapter.kayitlariGuncelle(sonKayitlar)
        }
    }
    
    override fun onResume() {
        super.onResume()
        ozetVerileriniYukle()
        bottomNavigation.selectedItemId = R.id.nav_ozet
    }
    
    private inner class SonKayitlarAdapter : RecyclerView.Adapter<SonKayitlarAdapter.KayitViewHolder>() {
        
        private var kayitlar = listOf<PerformansKaydi>()
        
        fun kayitlariGuncelle(yeniKayitlar: List<PerformansKaydi>) {
            kayitlar = yeniKayitlar
            notifyDataSetChanged()
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KayitViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                android.R.layout.simple_list_item_2, parent, false
            )
            return KayitViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: KayitViewHolder, position: Int) {
            holder.bind(kayitlar[position])
        }
        
        override fun getItemCount() = kayitlar.size
        
        inner class KayitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val text1: TextView = itemView.findViewById(android.R.id.text1)
            private val text2: TextView = itemView.findViewById(android.R.id.text2)
            
            fun bind(kayit: PerformansKaydi) {
                text1.text = "${kayit.calisanAdi} - ${kayit.islemTuru}"
                
                val formatliMiktar = if (kayit.miktar % 1.0 == 0.0) {
                    kayit.miktar.toInt().toString()
                } else {
                    kayit.miktar.toString()
                }
                
                text2.text = "${kayit.tarih} | $formatliMiktar ${kayit.birim}"
            }
        }
    }
}
