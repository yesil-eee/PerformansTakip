@echo off
REM Android (Kotlin) projesi için dinamik Git kurulumu

set /p REMOTE_URL=GitHub repo URL'sini girin (örnek: https://github.com/kullaniciadi/proje.git): 

IF "%REMOTE_URL%"=="" (
    echo [ERROR] Uzak repo adresi girilmedi. Betik iptal edildi.
    pause
    exit /b
)

echo [INFO] Git deposu baslatiliyor...
git init

echo [INFO] .gitignore olusturuluyor...

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

echo [INFO] Ilk commit yapiliyor...
git commit -m "Ilk commit: Android Kotlin projesi baslatildi"

echo [INFO] Uzak repo baglaniyor...
git remote add origin %REMOTE_URL%

echo [INFO] Branch ayarlaniyor ve push islemi yapiliyor...
git branch -M main
git push -u origin main

echo [SUCCESS] GitHub’a push islemi tamamlandi.
pause
