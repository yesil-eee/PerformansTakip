@echo off
setlocal enabledelayedexpansion

echo [GitHub Senkronizasyon Araci]
echo -------------------------------
echo.

REM Gerekli bilgileri sabit olarak ayarlayalım
set GIT_USER=yesil-eee
set GIT_REPO=PerformansTakip

REM Ana dal adini otomatik tespit et
FOR /F "tokens=*" %%i IN ('git symbolic-ref --short HEAD') DO set BRANCH_NAME=%%i

echo Ana dal: %BRANCH_NAME%

REM Kullanıcıdan commit mesajını al
set /p COMMIT_MSG="Commit Mesaji Giriniz: "

echo.
echo [1/4] Dosyalar sahneye aliniyor...
git add .

echo [2/4] Commit yapiliyor...
git commit -m "%COMMIT_MSG%"

echo [3/4] Uzak baglanti ayarlaniyor...
git remote remove origin 2>nul
git remote add origin git@github.com:%GIT_USER%/%GIT_REPO%.git

echo [4/4] Kod GitHub'a yukleniyor...
git push -u origin %BRANCH_NAME%

echo.
echo [ISLEM TAMAMLANDI]
pause
endlocal
