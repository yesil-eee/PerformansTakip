# PowerShell script for initializing a Git repo for Android Kotlin project

Add-Type -AssemblyName Microsoft.VisualBasic
$remoteUrl = [Microsoft.VisualBasic.Interaction]::InputBox("GitHub repo URL’sini girin:", "Git Remote Adresi", "https://github.com/kullaniciadi/proje.git")

if ([string]::IsNullOrWhiteSpace($remoteUrl)) {
    Write-Host "[ERROR] Uzak repo adresi girilmedi. İşlem iptal edildi." -ForegroundColor Red
    pause
    exit
}

# Git yüklü mü kontrol et
if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Host "[ERROR] Git yüklü değil. https://git-scm.com adresinden yükleyin." -ForegroundColor Red
    pause
    exit
}

Write-Host "[INFO] Git deposu başlatılıyor..." -ForegroundColor Cyan
git init

Write-Host "[INFO] .gitignore oluşturuluyor..." -ForegroundColor Cyan

@"
# Gradle
/.gradle
/build
**/build

# Local config
local.properties

# IntelliJ / Android Studio
.idea/
*.iml

# Keystore
*.jks

# MacOS
.DS_Store

# Log
*.log

# Kotlin
*.class
*.jar

# Artifacts
/captures
/outputs
*.apk
*.ap_
"@ | Out-File -Encoding utf8 -FilePath ".gitignore"

Write-Host "[INFO] Dosyalar ekleniyor..." -ForegroundColor Cyan
git add .

Write-Host "[INFO] İlk commit oluşturuluyor..." -ForegroundColor Cyan
git commit -m "İlk commit: Android Kotlin projesi başlatıldı"

Write-Host "[INFO] Uzak repo ekleniyor..." -ForegroundColor Cyan
git remote add origin $remoteUrl

Write-Host "[INFO] Main branch ayarlanıyor ve push işlemi başlatılıyor..." -ForegroundColor Cyan
git branch -M main
git push -u origin main

Write-Host "[SUCCESS] Git kurulumu ve GitHub’a gönderim tamamlandı." -ForegroundColor Green
pause
