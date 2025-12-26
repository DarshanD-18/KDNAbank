# E2E test for registration -> approval -> account creation
# Starts the Spring Boot app (background) and performs HTTP interactions

Set-Location -LiteralPath "$PSScriptRoot\..\banking-system"

Start-Process -FilePath ".\mvnw.cmd" -ArgumentList "spring-boot:run" -NoNewWindow -PassThru | Out-Null

$ready = $false
for ($i=0; $i -lt 60; $i++) {
    try {
        $r = Invoke-WebRequest -Uri 'http://localhost:8081/login' -UseBasicParsing -TimeoutSec 5
        if ($r.StatusCode -eq 200) { $ready = $true; break }
    } catch { }
    Start-Sleep -Seconds 1
}

if (-not $ready) {
    Write-Output 'ERROR: server-not-ready'
    exit 2
}

# Official session: register customer
$sess = New-Object Microsoft.PowerShell.Commands.WebRequestSession
Invoke-WebRequest -Uri 'http://localhost:8081/login' -WebSession $sess -UseBasicParsing | Out-Null
Invoke-WebRequest -Uri 'http://localhost:8081/login' -Method Post -Body @{username='official'; password='official123'} -WebSession $sess -UseBasicParsing | Out-Null
Invoke-WebRequest -Uri 'http://localhost:8081/official/customer/register' -Method Post -Body @{
    fullName='Auto Test User'
    dob='1990-01-01'
    gender='Other'
    mobileNumber='9998887771'
    email='autotest@example.com'
    address='Test Address'
    aadhaarNumber='111122223333'
    panNumber='AUTOTEST1P'
} -WebSession $sess -UseBasicParsing -MaximumRedirection 0 | Out-Null

# Manager session: approve the pending customer
$mgr = New-Object Microsoft.PowerShell.Commands.WebRequestSession
Invoke-WebRequest -Uri 'http://localhost:8081/login' -WebSession $mgr -UseBasicParsing | Out-Null
Invoke-WebRequest -Uri 'http://localhost:8081/login' -Method Post -Body @{username='manager'; password='manager123'} -WebSession $mgr -UseBasicParsing | Out-Null
$dash = Invoke-WebRequest -Uri 'http://localhost:8081/manager/dashboard' -WebSession $mgr -UseBasicParsing
[regex]$r = '/manager/customers/approve/([0-9]+)'
$m = $r.Match($dash.Content)
if ($m.Success) {
    $id = $m.Groups[1].Value
    Invoke-WebRequest -Uri ("http://localhost:8081/manager/customers/approve/" + $id) -Method Post -WebSession $mgr -UseBasicParsing | Out-Null
} else {
    Write-Output 'ERROR: no-pending-customer-found'
    exit 3
}

# Official: create account for approved customer
$createPage = Invoke-WebRequest -Uri 'http://localhost:8081/official/accounts/create' -WebSession $sess -UseBasicParsing
[regex]$r2 = 'name="customerId"[^>]*value="([0-9]+)"'
$m2 = $r2.Match($createPage.Content)
if ($m2.Success) { $cid = $m2.Groups[1].Value } else { Write-Output 'ERROR: no-approved-customer-in-dropdown'; exit 4 }
Invoke-WebRequest -Uri 'http://localhost:8081/official/accounts/create' -Method Post -Body @{customerId=$cid; accountType='SAVINGS'; balance='5000'} -WebSession $sess -UseBasicParsing -MaximumRedirection 0 | Out-Null

# Verify account list shows new account
$accounts = Invoke-WebRequest -Uri 'http://localhost:8081/official/accounts' -WebSession $sess -UseBasicParsing
if ($accounts.Content -match 'Auto Test User') { Write-Output 'SUCCESS: end-to-end verified'; exit 0 } else { Write-Output 'ERROR: account-not-found-after-creation'; exit 5 }
