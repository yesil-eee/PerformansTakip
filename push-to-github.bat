@echo off
SET REMOTE_URL=https://github.com/yesil-dev01/PersonelTakip.git

echo [INFO] Git deposu başlatılıyor...
git init

echo [INFO] .gitignore oluşturuluyor...

(
echo # Gradle
echo /.gradle
echo /build
echo **/build
echo local.properties
echo .idea/
echo *.iml
echo *.jks
echo .DS_Store
echo *.log
echo *.class
echo *.jar
echo /captures
echo /outputs
echo *.apk
echo *.ap_
) > .gitignore

echo [INFO] Dosyalar ekleniyor...
git add .

echo [INFO] İlk commit yapılıyor...
git commit -m "İlk commit: Android Kotlin proje aktarıldı"

echo [INFO] Uzak repo bağlanıyor...
git remote add origin %REMOTE_URL%

echo [INFO] Main branch ayarlanıyor ve gönderiliyor...
git branch -M main
git push -u origin main

echo [SUCCESS] Proje GitHub’a gönderildi.
pause
