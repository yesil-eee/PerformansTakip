@echo off
echo [INFO] Yerel Git deposu başlatılıyor...
git init

echo [INFO] .gitignore dosyası oluşturuluyor...

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
git commit -m "İlk commit: Yerel Git deposu oluşturuldu"

echo [SUCCESS] Yerel Git deposu başarıyla oluşturuldu.
pause
