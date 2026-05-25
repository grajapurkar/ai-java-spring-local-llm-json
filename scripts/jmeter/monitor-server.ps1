<#
Monitor the Java process owning port 8080 (Spring Boot) and log CPU/memory periodically to CSV.
Usage: .\monitor-server.ps1 -Port 8080 -Interval 5 -OutFile server-metrics.csv
#>
param(
    [int]$Port = 8080,
    [int]$Interval = 5,
    [string]$OutFile = "server-metrics.csv"
)

function Get-ProcessIdByPort {
    param($Port)
    try {
        $conn = Get-NetTCPConnection -LocalPort $Port -ErrorAction Stop | Select-Object -First 1
        return $conn.OwningProcess
    } catch {
        return $null
    }
}

$pid = Get-ProcessIdByPort -Port $Port
if (-not $pid) {
    Write-Host "No process found listening on port $Port. Please start your Spring Boot app and retry." -ForegroundColor Yellow
    exit 1
}

# Prepare CSV header
if (-not (Test-Path $OutFile)) {
    "Timestamp,ProcessId,ProcessName,CPUPercent,MemoryMB,PrivateMemoryMB,Threads,Handles" | Out-File -FilePath $OutFile -Encoding UTF8
}

$proc = Get-Process -Id $pid -ErrorAction Stop
$procName = $proc.ProcessName
$prevTotalCpu = $proc.CPU
$processors = (Get-WmiObject -Class Win32_ComputerSystem).NumberOfLogicalProcessors

Write-Host "Monitoring process $procName (PID $pid) every $Interval seconds. Writing to $OutFile"

while ($true) {
    Start-Sleep -Seconds $Interval
    try {
        $proc = Get-Process -Id $pid -ErrorAction Stop
    } catch {
        Write-Host "Process $pid has exited. Stopping monitor." -ForegroundColor Yellow
        break
    }
    $currTotalCpu = $proc.CPU
    $cpuDelta = $currTotalCpu - $prevTotalCpu
    $prevTotalCpu = $currTotalCpu
    # CPU percent = cpuDelta (seconds) / interval seconds * 100 / logical processors
    $cpuPercent = 0
    if ($Interval -gt 0 -and $processors -gt 0) {
        $cpuPercent = ($cpuDelta / $Interval) * 100 / $processors
        $cpuPercent = [math]::Round($cpuPercent,2)
    }
    $memMB = [math]::Round($proc.WorkingSet64 / 1MB,2)
    $privateMB = [math]::Round($proc.PrivateMemorySize64 / 1MB,2)
    $threads = $proc.Threads.Count
    $handles = $proc.HandleCount
    $ts = [DateTime]::UtcNow.ToString("o")
    "$ts,$pid,$procName,$cpuPercent,$memMB,$privateMB,$threads,$handles" | Out-File -FilePath $OutFile -Append -Encoding UTF8
}
# End

