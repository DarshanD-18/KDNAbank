# Find a pending customer and approve, then verify official account-create dropdown contains the customer
$mgr = New-Object Microsoft.PowerShell.Commands.WebRequestSession
Invoke-WebRequest 'http://localhost:8081/login' -WebSession $mgr -UseBasicParsing | Out-Null
Invoke-WebRequest -Uri 'http://localhost:8081/login' -Method Post -Body @{username='manager'; password='manager123'} -WebSession $mgr -UseBasicParsing | Out-Null
$dash = Invoke-WebRequest -Uri 'http://localhost:8081/manager/dashboard' -WebSession $mgr -UseBasicParsing
[regex]$r = '/manager/customers/approve/([0-9]+)'
$m = $r.Match($dash.Content)
if ($m.Success) {
    $id = $m.Groups[1].Value
    Write-Output "Approving id: $id"
    Invoke-WebRequest -Uri ("http://localhost:8081/manager/customers/approve/" + $id) -Method Post -WebSession $mgr -UseBasicParsing | Out-Null
} else {
    Write-Output 'No pending customer to approve'
    exit 2
}

# Now login as official and check account create
$sess = New-Object Microsoft.PowerShell.Commands.WebRequestSession
Invoke-WebRequest 'http://localhost:8081/login' -WebSession $sess -UseBasicParsing | Out-Null
Invoke-WebRequest -Uri 'http://localhost:8081/login' -Method Post -Body @{username='official'; password='official123'} -WebSession $sess -UseBasicParsing | Out-Null
$createPage = Invoke-WebRequest -Uri 'http://localhost:8081/official/accounts/create' -WebSession $sess -UseBasicParsing
$createPage.Content | Out-File approved_dropdown.html -Encoding utf8
if ($createPage.Content -match 'Auto Test User' -or $createPage.Content -match 'Diag User') { Write-Output 'Approved customer appears in account create dropdown' } else { Write-Output 'Approved customer NOT found in dropdown' ; exit 3 }
