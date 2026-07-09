$ErrorActionPreference = "Stop"

# ---------- Config ----------
$ProjectDir = "D:\SPRING BOOT\modern-rest-service"
$BaseUrl = "http://localhost:8080"
$DbHost = "localhost"
$DbPort = 55432
$DbUser = "postgres"
$DbPass = "password"

$Username = "admin"
$Password = "admin123"

$SampleFilePath = "$env:TEMP\sample-upload.txt"
$DownloadedFilePath = "$env:TEMP\downloaded-file.txt"
$AppLogPath = Join-Path $ProjectDir "quick-smoke-app.log"

# ---------- Helpers ----------
function Wait-TcpPort {
    param([string]$Host,[int]$Port,[int]$TimeoutSec = 90)
    $deadline = (Get-Date).AddSeconds($TimeoutSec)
    while ((Get-Date) -lt $deadline) {
        try {
            $tcp = New-Object System.Net.Sockets.TcpClient
            $iar = $tcp.BeginConnect($Host, $Port, $null, $null)
            $ok = $iar.AsyncWaitHandle.WaitOne(1000, $false)
            if ($ok -and $tcp.Connected) {
                $tcp.EndConnect($iar) | Out-Null
                $tcp.Close()
                return $true
            }
            $tcp.Close()
        } catch {}
        Start-Sleep -Seconds 2
    }
    return $false
}

function Wait-Health {
    param([string]$Url,[int]$TimeoutSec = 180)
    $deadline = (Get-Date).AddSeconds($TimeoutSec)
    while ((Get-Date) -lt $deadline) {
        try {
            $resp = Invoke-RestMethod -Method Get -Uri "$Url/actuator/health" -TimeoutSec 5
            if ($resp.status -eq "UP") { return $true }
        } catch {}
        Start-Sleep -Seconds 3
    }
    return $false
}

# ---------- Main ----------
$appProc = $null
Push-Location $ProjectDir
try {
    Write-Host "== 1) Start Docker DB =="
    docker compose up -d db | Out-Host

    Write-Host "Waiting for DB on $DbHost:$DbPort ..."
    if (-not (Wait-TcpPort -Host $DbHost -Port $DbPort -TimeoutSec 120)) {
        throw "DB did not become reachable on $DbHost:$DbPort"
    }
    Write-Host "DB is reachable."

    Write-Host "`n== 2) Start app in background =="
    if (Test-Path $AppLogPath) { Remove-Item $AppLogPath -Force }

    $appProc = Start-Process `
        -FilePath "cmd.exe" `
        -ArgumentList "/c set DB_USERNAME=$DbUser && set DB_PASSWORD=$DbPass && mvnw.cmd spring-boot:run" `
        -WorkingDirectory $ProjectDir `
        -RedirectStandardOutput $AppLogPath `
        -RedirectStandardError $AppLogPath `
        -PassThru

    Write-Host "App PID: $($appProc.Id)"
    Write-Host "App log: $AppLogPath"

    Write-Host "Waiting for app health..."
    if (-not (Wait-Health -Url $BaseUrl -TimeoutSec 240)) {
        Write-Host "---- Last app log lines ----"
        if (Test-Path $AppLogPath) { Get-Content $AppLogPath -Tail 80 }
        throw "App did not become healthy at $BaseUrl/actuator/health"
    }
    Write-Host "App is healthy."

    Write-Host "`n== 3) Prepare sample file =="
    "hello from quick smoke cleanup $(Get-Date -Format o)" | Out-File -FilePath $SampleFilePath -Encoding utf8

    Write-Host "`n== 4) Login and get JWT =="
    $loginBody = @{ username = $Username; password = $Password } | ConvertTo-Json
    $loginResp = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/v1/auth/login" -ContentType "application/json" -Body $loginBody
    $token = $loginResp.token
    if (-not $token) { throw "Token not found in login response." }
    $authHeaders = @{ Authorization = "Bearer $token" }

    Write-Host "`n== 5) Async report endpoint =="
    Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/v1/ops/reports/users" -Headers $authHeaders | ConvertTo-Json -Depth 5 | Out-Host

    Write-Host "`n== 6) Messaging endpoint =="
    $msgBody = @{ message = "hello from api quick-smoke-cleanup" } | ConvertTo-Json
    Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/v1/ops/messages" -Headers $authHeaders -ContentType "application/json" -Body $msgBody | ConvertTo-Json -Depth 5 | Out-Host

    Write-Host "`n== 7) File upload =="
    $uploadResp = curl.exe -s -X POST "$BaseUrl/api/v1/files/upload" `
      -H "Authorization: Bearer $token" `
      -F "file=@$SampleFilePath"

    if (-not $uploadResp) { throw "Upload response empty." }
    $uploadObj = $uploadResp | ConvertFrom-Json
    $downloadUrl = $uploadObj.downloadUrl
    if (-not $downloadUrl) { throw "downloadUrl missing in upload response." }

    Write-Host "`n== 8) File download =="
    curl.exe -s -L -X GET "$BaseUrl$downloadUrl" `
      -H "Authorization: Bearer $token" `
      -o "$DownloadedFilePath"

    if (-not (Test-Path $DownloadedFilePath)) { throw "Downloaded file not found." }

    Write-Host "`n== 9) Thymeleaf home page =="
    $homeResp = Invoke-WebRequest -Method Get -Uri "$BaseUrl/"
    Write-Host "Home status code: $($homeResp.StatusCode)"
    Write-Host "Home contains title? " ($homeResp.Content -like "*Modern REST Service*")

    Write-Host "`n✅ Full smoke test completed successfully."
}
finally {
    Write-Host "`n== Cleanup =="
    if ($appProc -and -not $appProc.HasExited) {
        Write-Host "Stopping app PID $($appProc.Id) ..."
        Stop-Process -Id $appProc.Id -Force -ErrorAction SilentlyContinue
    } else {
        Write-Host "App process already stopped."
    }

    Write-Host "Stopping Docker compose services ..."
    try {
        docker compose down | Out-Host
    } catch {
        Write-Host "docker compose down failed: $($_.Exception.Message)"
    }

    Pop-Location
    Write-Host "Cleanup complete."
}
