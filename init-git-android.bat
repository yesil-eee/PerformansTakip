@echo off
REM Android (Kotlin) proje klasöründe Git deposu başlatır

echo [INFO] Git deposu başlatılıyor...
git init

echo [INFO] .gitignore oluşturuluyor...

REM Android Studio için standart .gitignore içeriği
(
echo # Gradle
echo /.gradle
echo /build
echo **/build

echo # Local configuration file (sdk path, etc)
echo local.properties

echo # IntelliJ IDEA
echo .idea/
echo *.iml

echo # Keystore files
echo *.jks

echo # MacOS
echo .DS_Store

echo # Log files
echo *.log

echo # Kotlin compile
echo *.class
echo *.jar

echo # Others
echo /captures
echo /outputs
echo *.apk
echo *.ap_
) > .gitignore

echo [INFO] Dosyalar versiyon kontrolüne ekleniyor...
git add .

echo [INFO] İlk commit oluşturuluyor...
git commit -m "İlk commit: Android Kotlin projesi başlatıldı"

REM Dilerseniz aşağıdaki satırı kendi remote URL’nizle düzenleyin
REM git remote add origin https://github.com/kullaniciadi/proje-adi.git

echo [SUCCESS] Git kurulumu tamamlandı.
pause
