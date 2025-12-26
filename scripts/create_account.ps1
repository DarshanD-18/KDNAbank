# Create account for first approved customer in approved_dropdown.html
$content = Get-Content -Path 'approved_dropdown.html' -Raw
[regex]$r = 'value="([0-9]+)"'
$m = $r.Match($content)
if (-not $m.Success) { Write-Output 'ERROR: could not parse customer id from approved_dropdown.html'; exit 2 }
$cid = $m.Groups[1].Value
Write-Output "Using customer id: $cid"

# Official session
$sess = New-Object Microsoft.PowerShell.Commands.WebRequestSession
Invoke-WebRequest 'http://localhost:8081/login' -WebSession $sess -UseBasicParsing | Out-Null
Invoke-WebRequest -Uri 'http://localhost:8081/login' -Method Post -Body @{username='official'; password='official123'} -WebSession $sess -UseBasicParsing | Out-Null
Invoke-WebRequest -Uri 'http://localhost:8081/official/accounts/create' -Method Post -Body @{customerId=$cid; accountType='SAVINGS'; balance='7500'} -WebSession $sess -UseBasicParsing | Out-Null
$accounts = Invoke-WebRequest -Uri 'http://localhost:8081/official/accounts' -WebSession $sess -UseBasicParsing
$accounts.Content | Out-File created_accounts.html -Encoding utf8
if ($accounts.Content -match 'Diag User' -or $accounts.Content -match 'Auto Test User') { Write-Output 'SUCCESS: account created and visible in accounts list' } else { Write-Output 'ERROR: account not found in accounts list' ; exit 3 }
