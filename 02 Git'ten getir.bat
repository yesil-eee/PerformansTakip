@echo off
setlocal enabledelayedexpansion

echo ===== Performans Takip Versiyon Geri Yukleme Araci =====
echo.

:: Git'in yüklü olup olmadığını kontrol et
where git >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo HATA: Git bulunamadi. Lutfen Git'i yukleyin.
    echo.
    pause
    exit /b 1
)

:: Git repo kontrolü
if not exist ".git" (
    echo HATA: Bu klasor bir Git deposu degil.
    echo.
    pause
    exit /b 1
)

echo Son 10 commit listeleniyor...
echo.

:: Geçici dosya oluştur
set "tempFile=%TEMP%\git_commits_temp.txt"
git log -n 10 --pretty=format:"%%h %%ad %%s" --date=short > "%tempFile%"

:: Commit bilgilerini okuyup listelemek için
set "counter=0"
for /f "tokens=1-3*" %%a in (%tempFile%) do (
    set /a counter+=1
    set "commit_!counter!_hash=%%a"
    set "commit_!counter!_date=%%b"
    set "commit_!counter!_msg=%%c %%d"
    
    echo !counter!. [%%b] %%c %%d - (%%a)
)
echo.

:: Kullanıcıdan seçim iste
set /p selection="Geri donmek istediginiz versiyon numarasini seciniz (1-%counter%): "

:: Seçimi kontrol et
if %selection% lss 1 (
    echo Gecersiz secim.
    pause
    exit /b 1
)

if %selection% gtr %counter% (
    echo Gecersiz secim.
    pause
    exit /b 1
)

:: Seçilen commit hash'ini al
set "selected_hash=!commit_%selection%_hash!"
set "selected_date=!commit_%selection%_date!"
set "selected_msg=!commit_%selection%_msg!"

echo.
echo Secilen versiyon: !selected_date! - !selected_msg!
echo.

:: Kullanıcıya geri dönüş yöntemini sorma
echo Geri donme yontemi seciniz:
echo 1 - Gecici olarak bu versiyona don (git checkout) - Daha guvenli
echo 2 - Kalici olarak bu versiyona don (git reset) - Dikkatli kullanin!
echo.

set /p restore_method="Secim (1-2): "

if "%restore_method%"=="1" (
    echo.
    echo Gecici olarak "%selected_hash%" commit'ine donuluyor...
    echo Mevcut degisiklikler kaydedilsin mi?
    set /p save_changes="Kaydet? (E/H): "
    
    if /i "%save_changes%"=="E" (
        set commit_msg=Otomatik kayit: %date% %time%
        git add .
        git commit -m "!commit_msg!"
        echo Degisiklikler kaydedildi.
    )
    
    git checkout %selected_hash%
    
    if %ERRORLEVEL% equ 0 (
        echo.
        echo Basariyla "%selected_hash%" commit'ine geri donuldu.
        echo.
        echo NOT: Ana dala geri donmek icin "git checkout main" veya "git checkout master" komutunu kullanabilirsiniz.
    ) else (
        echo.
        echo HATA: Geri donme islemi sirasinda bir sorun olustu.
    )
) else if "%restore_method%"=="2" (
    echo.
    echo UYARI: Bu islem mevcut degisiklikleri ve secilen commit'ten sonraki tum commitleri kalici olarak silecektir.
    echo Devam etmek istediginizden emin misiniz?
    set /p confirm="Devam? (EVET/HAYIR): "
    
    if /i "%confirm%"=="EVET" (
        echo.
        echo "%selected_hash%" commit'ine kalici olarak donuluyor...
        git reset --hard %selected_hash%
        
        if %ERRORLEVEL% equ 0 (
            echo.
            echo Basariyla "%selected_hash%" commit'ine kalici olarak geri donuldu.
        ) else (
            echo.
            echo HATA: Geri donme islemi sirasinda bir sorun olustu.
        )
    ) else (
        echo.
        echo Islem iptal edildi.
    )
) else (
    echo.
    echo Gecersiz secim.
)

:: Geçici dosyayı temizle
del "%tempFile%" >nul 2>nul

echo.
pause
