@echo off
setlocal enabledelayedexpansion

echo ===== Performans Takip Versiyon Guncelleyici =====
echo.

:: Uygulama dizinini kontrol et
if not exist "app\build.gradle" (
    echo HATA: app\build.gradle dosyasi bulunamadi!
    echo Bu dosya ana proje klasorunden calistirilmalidir.
    echo.
    pause
    exit /b 1
)

:: Mevcut versiyonu al
set "versionLine="
for /f "tokens=* usebackq" %%a in (`powershell -Command "Select-String -Path app\build.gradle -Pattern 'versionName' | Select-Object -ExpandProperty Line"`) do (
    set "versionLine=%%a"
)

:: Versiyonu çıkar
for /f "tokens=2 delims='" %%a in ("!versionLine!") do (
    set "currentVersion=%%a"
)

echo Mevcut versiyon: !currentVersion!
echo.

:: Versiyon bileşenlerini ayır (örn: 1.0.0)
for /f "tokens=1-3 delims=." %%a in ("!currentVersion!") do (
    set "majorVersion=%%a"
    set "minorVersion=%%b"
    set "patchVersion=%%c"
)

:: Yükseltme tipi seçimi
echo Yukseltme tipini secin:
echo 1 - Major versiyon (x.0.0)
echo 2 - Minor versiyon (-.x.0)
echo 3 - Patch versiyon (-.-.x)
echo.

set /p upgradeType="Secim (1-3): "
set /p commitMsg="Degisiklik aciklamasi: "

:: Seçime göre versiyonu güncelle
if "%upgradeType%"=="1" (
    set /a majorVersion+=1
    set minorVersion=0
    set patchVersion=0
    set "updateType=Major"
) else if "%upgradeType%"=="2" (
    set /a minorVersion+=1
    set patchVersion=0
    set "updateType=Minor"
) else if "%upgradeType%"=="3" (
    set /a patchVersion+=1
    set "updateType=Patch"
) else (
    echo Gecersiz secim!
    exit /b 1
)

:: Yeni versiyonu oluştur
set "newVersion=!majorVersion!.!minorVersion!.!patchVersion!"

:: build.gradle dosyasını güncelle
powershell -Command "(Get-Content app\build.gradle) -replace 'versionName \"[0-9]*\.[0-9]*\.[0-9]*\"', 'versionName \"!newVersion!\"' | Set-Content app\build.gradle"

:: Version.txt dosyasına kaydet
echo %date% %time% - %updateType% guncelleme: !currentVersion! -^> !newVersion! - %commitMsg% >> version_history.txt

echo.
echo Versiyon guncellendi: !currentVersion! -^> !newVersion!
echo Aciklama: %commitMsg%
echo.
echo Bu bilgiler version_history.txt dosyasina kaydedildi.
echo.
pause
