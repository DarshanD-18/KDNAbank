$sess = New-Object Microsoft.PowerShell.Commands.WebRequestSession
Invoke-WebRequest 'http://localhost:8081/login' -WebSession $sess -UseBasicParsing | Out-Null
try {
    $r = Invoke-WebRequest -Uri 'http://localhost:8081/login' -Method Post -Body @{username='official'; password='official123'} -WebSession $sess -UseBasicParsing
    Write-Output "LOGIN-STATUS: $($r.StatusCode)"
} catch {
    Write-Output "LOGIN-ERROR: $_"
}

try {
    $reg = Invoke-WebRequest -Uri 'http://localhost:8081/official/customer/register' -Method Post -Body @{fullName='Diag User'; dob='1990-01-01'; gender='Other'; mobileNumber='9998887772'; email='diag@example.com'; address='Diag Addr'; aadhaarNumber='111122223333'; panNumber='DIAG1234P'} -WebSession $sess -UseBasicParsing
    Write-Output "REGISTER-STATUS: $($reg.StatusCode)"
} catch {
    Write-Output "REG-ERROR: $_"
}

# Manager check
$mgr = New-Object Microsoft.PowerShell.Commands.WebRequestSession
Invoke-WebRequest 'http://localhost:8081/login' -WebSession $mgr -UseBasicParsing | Out-Null
try {
    $rm = Invoke-WebRequest -Uri 'http://localhost:8081/login' -Method Post -Body @{username='manager'; password='manager123'} -WebSession $mgr -UseBasicParsing
    Write-Output "MANAGER-LOGIN-STATUS: $($rm.StatusCode)"
} catch {
    Write-Output "MAN-LOGIN-ERROR: $_"
}
try {
    $dash = Invoke-WebRequest -Uri 'http://localhost:8081/manager/dashboard' -WebSession $mgr -UseBasicParsing
    Write-Output "MANAGER-DASH-LEN: $($dash.Content.Length)"
    $snippet = $dash.Content.Substring(0, [math]::Min(800, $dash.Content.Length))
    Write-Output "MANAGER-DASH-SNIPPET:\n$snippet"
} catch {
    Write-Output "DASH-ERROR: $_"
}
if ($dash -and $dash.Content -match 'Diag User') { Write-Output 'MANAGER-SEES-DIAG: true' } else { Write-Output 'MANAGER-SEES-DIAG: false' }
