# Test flow script for the banking app
# Usage: Start the app (mvn spring-boot:run) in one terminal,
# then run this script in another PowerShell terminal:
# powershell -ExecutionPolicy Bypass -File .\scripts\test_flow.ps1

$base = "http://localhost:8081"

Write-Host "Waiting for $base to be available..."
for ($i=0; $i -lt 60; $i++) {
    try {
        $r = Invoke-WebRequest -Uri "$base/login" -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop
        if ($r.StatusCode -eq 200) { break }
    } catch { Start-Sleep -Seconds 1 }
}

if ($null -eq $r) { Write-Error "Server not reachable"; exit 1 }

# 1) Submit an official request
$testUser = "testofficial"
$testPass = "testpass"
Write-Host "Submitting official request: $testUser"
Invoke-WebRequest -Uri "$base/official/request" -Method Post -Body @{ username=$testUser; password=$testPass } -UseBasicParsing -TimeoutSec 10
Write-Host "Request submitted."

# 2) Login as manager and keep session
Write-Host "Logging in as manager..."
$mgrSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
Invoke-WebRequest -Uri "$base/login" -Method Post -Body @{ username='manager'; password='manager123' } -WebSession $mgrSession -UseBasicParsing -TimeoutSec 10
Write-Host "Manager logged in. Fetching pending requests..."

$reqPage = Invoke-WebRequest -Uri "$base/manager/requests" -WebSession $mgrSession -UseBasicParsing -TimeoutSec 10
$html = $reqPage.Content
if ($html -match "/manager/approve/([0-9]+)") {
    $id = $matches[1]
    Write-Host "Found pending request id: $id"
    Write-Host "Approving request $id"
    Invoke-WebRequest -Uri "$base/manager/approve/$id" -Method Post -WebSession $mgrSession -UseBasicParsing -TimeoutSec 10
    Write-Host "Approved."
} else {
    Write-Host "No pending request found in manager page."
}

# 3) Try login as approved official
Write-Host "Attempting login as official: $testUser"
$offSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$resp = Invoke-WebRequest -Uri "$base/login" -Method Post -Body @{ username=$testUser; password=$testPass } -WebSession $offSession -UseBasicParsing -TimeoutSec 10 -ErrorAction SilentlyContinue

# Check landing page
$home = Invoke-WebRequest -Uri "$base/official/dashboard" -WebSession $offSession -UseBasicParsing -TimeoutSec 10 -ErrorAction SilentlyContinue
if ($home -and $home.Content -match "Official Dashboard") {
    Write-Host "SUCCESS: Official login and dashboard accessible."
} else {
    Write-Host "FAILED: Official could not access dashboard. Check server logs."
}

Write-Host "Test flow complete."