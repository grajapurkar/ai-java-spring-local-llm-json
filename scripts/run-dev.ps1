<#
  run-dev.ps1

  Builds the project, starts the Spring Boot app using the 'dev' profile, waits for
  a startup marker in the log and performs a sample POST to /api/chat.

  Usage:
    ./scripts/run-dev.ps1

  Notes:
  - Requires Maven in PATH.
  - Uses a run.log file in the project root. If your shell environment differs,
    adapt redirection or use an IDE runner.
#>

$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
Push-Location $ProjectRoot

Write-Host "Building project (skip tests)..."
mvn clean package -DskipTests

$LogFile = Join-Path $ProjectRoot 'run.log'
if (Test-Path $LogFile) { Remove-Item $LogFile -Force }

Write-Host "Starting Spring Boot (profile=dev). Output -> $LogFile"

# Start Spring Boot in background and redirect stdout/stderr to run.log
Start-Process mvn -ArgumentList 'spring-boot:run','-Dspring-boot.run.profiles=dev' -WorkingDirectory $ProjectRoot -NoNewWindow -RedirectStandardOutput $LogFile -RedirectStandardError $LogFile -PassThru | Out-Null

# Wait for a log marker that indicates the app (and loader) progressed.
# We look for either the Spring Boot "Started <Application>" marker or the loader marker.
$LoaderMarker = 'Policies loaded into vector store'
$SpringMarker = 'Started LocalLlmRagApplication'
Write-Host "Waiting for startup marker: $SpringMarker or $LoaderMarker"
while (-not (Test-Path $LogFile)) { Start-Sleep -Milliseconds 200 }

$found = $false
for ($i = 0; $i -lt 300; $i++) {
    if (Select-String -Path $LogFile -Pattern $SpringMarker -Quiet) {
        Write-Host "Spring application started (marker found)."
        $found = $true
        break
    }
    if (Select-String -Path $LogFile -Pattern $LoaderMarker -Quiet) {
        Write-Host "Loader finished (marker found)."
        $found = $true
        break
    }
    Start-Sleep -Seconds 1
}

if (-not $found) {
    Write-Host "Warning: startup marker not found within timeout. Proceeding to show logs and attempt request."
}

Write-Host "Tailing last 80 lines of run.log:";
Get-Content $LogFile -Tail 80 | ForEach-Object { Write-Host $_ }

Write-Host "Sending sample POST to /api/chat..."
try {
    $Body = '{"question":"Does insurance cover flood damage?"}'
    $Response = Invoke-WebRequest -Uri 'http://localhost:8080/api/chat' -Method POST -ContentType 'application/json' -Body $Body -UseBasicParsing -ErrorAction Stop
    Write-Host "Status: $($Response.StatusCode)"
    Write-Host "Response body:`n$($Response.Content)"
} catch {
    Write-Host "Request failed: $_"
}

Pop-Location

