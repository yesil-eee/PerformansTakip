@echo off
setlocal enabledelayedexpansion

echo ===== GitHub'dan Proje Indirme Araci =====
echo.

:: Git'in yüklü olup olmadığını kontrol et
where git >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo HATA: Git bulunamadi. Lutfen Git'i yukleyin.
    echo.
    pause
    exit /b 1
)

:: Hedef klasör varsa sorma
set "HEDEF_KLASOR=%~dp0"
if exist "%HEDEF_KLASOR%\.git" (
    echo Mevcut Git deposu bulundu.
    echo Bu klasordeki tum degisiklikler kaybedilebilir!
    set /p DEVAM_ET="Mevcut depo uzerine yukleme yapmak istiyor musunuz? (E/H): "
    if /i "!DEVAM_ET!" neq "E" (
        echo.
        echo Islem iptal edildi.
        echo.
        pause
        exit /b 0
    )
)

:: GitHub Repository URL'sini sor
set /p REPO_URL="GitHub Repository URL'sini girin (ornek: https://github.com/kullanici/repo.git): "

if "!REPO_URL!" == "" (
    echo HATA: Repository URL'si bos olamaz.
    echo.
    pause
    exit /b 1
)

:: Dalı (branch) sor
set /p BRANCH="Hangi dali indirmek istiyorsunuz? (varsayilan: main): "
if "!BRANCH!" == "" set "BRANCH=main"

echo.
echo Indirme Bilgileri:
echo Repository: !REPO_URL!
echo Dal: !BRANCH!
echo Hedef Klasor: %HEDEF_KLASOR%
echo.
set /p ONAY="Bu bilgilerle devam etmek istiyor musunuz? (E/H): "

if /i "%ONAY%" neq "E" (
    echo.
    echo Islem iptal edildi.
    echo.
    pause
    exit /b 0
)

echo.
echo Proje indiriliyor...

:: Git deposu olup olmadığını kontrol et
if exist "%HEDEF_KLASOR%\.git" (
    :: Mevcut git reposunu güncelle
    cd "%HEDEF_KLASOR%"
    
    :: Değişiklikleri kaydet (varsa)
    echo Mevcut degisiklikler kaydediliyor...
    git add .
    git commit -m "Otomatik kayit: %date% %time%" > nul 2>&1
    
    echo Uzak depo ekleniyor...
    git remote remove origin > nul 2>&1
    git remote add origin %REPO_URL% > nul 2>&1
    
    echo Uzak depodan veriler aliniyor...
    git fetch origin
    
    echo !BRANCH! dalina geciliyor...
    git checkout !BRANCH! 2>nul || git checkout -b !BRANCH! origin/!BRANCH!
    
    echo Uzak depodaki degisiklikler yerel depoya uygulaniyor...
    git pull origin !BRANCH!
) else (
    :: Yeni bir git reposu oluştur
    echo Yeni git deposu olusturuluyor...
    git clone --branch !BRANCH! %REPO_URL% "%HEDEF_KLASOR%\_temp"
    
    :: Dosyaları taşı (gizli .git klasörü dahil)
    echo Dosyalar tasiniyor...
    xcopy "%HEDEF_KLASOR%\_temp\*" "%HEDEF_KLASOR%" /E /H /C /I /Y
    rd /s /q "%HEDEF_KLASOR%\_temp"
)

if %ERRORLEVEL% neq 0 (
    echo.
    echo HATA: Indirme sirasinda bir sorun olustu.
) else (
    echo.
    echo Proje basariyla indirildi.
)

echo.
pause
