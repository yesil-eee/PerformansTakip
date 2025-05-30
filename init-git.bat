@echo off
REM Bu betik bulunduğu klasörde bir Git deposu başlatır

echo [INFO] Git deposu başlatılıyor...
git init

echo [INFO] Varsayılan .gitignore dosyası oluşturuluyor...
REM Basit bir .gitignore örneği (gerekiyorsa düzenleyebilirsiniz)
echo node_modules/ > .gitignore
echo *.log >> .gitignore
echo .env >> .gitignore

echo [INFO] Dosyalar versiyon kontrolüne ekleniyor...
git add .

echo [INFO] İlk commit oluşturuluyor...
git commit -m "İlk commit: Proje başlatıldı"

REM Dilerseniz aşağıdaki satırı açarak uzak repo adresi ekleyebilirsiniz
REM git remote add origin https://github.com/kullaniciadi/proje.git

echo [SUCCESS] Git deposu başarıyla başlatıldı.
pause
