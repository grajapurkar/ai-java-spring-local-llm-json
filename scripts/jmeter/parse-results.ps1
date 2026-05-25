<#
Parse a JMeter .jtl results file (CSV) and print a concise summary: total requests, duration, throughput, avg/median/90/95/99 latencies, success rate.
Usage: .\parse-results.ps1 -JtlFile results/results_50.jtl
#>
param(
    [string]$JtlFile
)

if (-not (Test-Path $JtlFile)) {
    Write-Host "File not found: $JtlFile" -ForegroundColor Red
    exit 1
}

# Import CSV and determine which timing column exists
$rows = Import-Csv -Path $JtlFile -Delimiter ","
if ($rows.Count -eq 0) {
    Write-Host "No rows found in $JtlFile" -ForegroundColor Yellow
    exit 0
}

# Determine elapsed column name (common names: elapsed, time, Latency, timeStamp)
$first = $rows[0]
$elapsedCol = $null
foreach ($c in $first.PSObject.Properties.Name) {
    if ($c -match 'elapsed|Elapsed|time|Time$|latency|Latency') {
        $elapsedCol = $c
        break
    }
}
if (-not $elapsedCol) {
    Write-Host "Could not find elapsed/latency column in JTL headers. Headers: $($first.PSObject.Properties.Name -join ',')" -ForegroundColor Yellow
    exit 1
}

# Determine timestamp column
$tsCol = $null
foreach ($c in $first.PSObject.Properties.Name) {
    if ($c -match 'timeStamp|timeStamp$|timestamp|time') {
        # prefer timeStamp if exists
        if ($c -match 'timeStamp|timestamp') { $tsCol = $c; break }
        if (-not $tsCol) { $tsCol = $c }
    }
}

# Success column
$successCol = ($first.PSObject.Properties.Name | Where-Object { $_ -match 'success|Success' }) | Select-Object -First 1

# Extract elapsed as numbers
$elapsedList = @()
$successCount = 0
$minTs = [long]::MaxValue
$maxTs = 0
foreach ($r in $rows) {
    $val = $r.$elapsedCol
    $num = 0
    if ([string]::IsNullOrEmpty($val)) { continue }
    if ([int]::TryParse($val, [ref]$num)) {
        $elapsedList += $num
    } elseif ([double]::TryParse($val, [ref]$num)) {
        $elapsedList += [math]::Round($num)
    }
    if ($successCol) {
        $s = $r.$successCol
        if ($s -eq 'true' -or $s -eq 'True' -or $s -eq '1') { $successCount++ }
    }
    if ($tsCol) {
        $tsVal = $r.$tsCol
        # JMeter timestamp often in milliseconds since epoch
        [long]$tsnum = 0
        if ([long]::TryParse($tsVal, [ref]$tsnum)) {
            if ($tsnum -lt $minTs) { $minTs = $tsnum }
            if ($tsnum -gt $maxTs) { $maxTs = $tsnum }
        }
    }
}

$total = $elapsedList.Count
if ($total -eq 0) { Write-Host "No timing data found"; exit 0 }

$avg = [math]::Round(($elapsedList | Measure-Object -Average).Average,2)
$sorted = $elapsedList | Sort-Object
$median = if ($total % 2 -eq 1) { $sorted[([int]($total/2))] } else { ([int](($sorted[$total/2 -1] + $sorted[$total/2]) / 2)) }
function Percentile([array]$arr, [double]$p) {
    if ($arr.Length -eq 0) { return 0 }
    $idx = [math]::Ceiling(($p/100) * $arr.Length) - 1
    if ($idx -lt 0) { $idx = 0 }
    if ($idx -ge $arr.Length) { $idx = $arr.Length - 1 }
    return $arr[$idx]
}
$p90 = Percentile $sorted 90
$p95 = Percentile $sorted 95
$p99 = Percentile $sorted 99

$durationSec = 0
if ($minTs -ne [long]::MaxValue -and $maxTs -gt $minTs) {
    $durationSec = ([double]($maxTs - $minTs)) / 1000.0
}
$throughput = 0
if ($durationSec -gt 0) { $throughput = [math]::Round($total / $durationSec, 2) }
$successRate = 100
if ($successCol) { $successRate = [math]::Round(($successCount / $total) * 100,2) }

Write-Host "JTL Summary: $JtlFile"
Write-Host "Total Requests: $total"
if ($durationSec -gt 0) { Write-Host "Test Duration (s): $([math]::Round($durationSec,2))" }
Write-Host "Throughput (req/s): $throughput"
Write-Host "Avg (ms): $avg"
Write-Host "Median (ms): $median"
Write-Host "90th (ms): $p90"
Write-Host "95th (ms): $p95"
Write-Host "99th (ms): $p99"
Write-Host "Success Rate (%): $successRate"

# Optionally output CSV summary
$summary = [PSCustomObject]@{
    File = $JtlFile
    TotalRequests = $total
    DurationSec = [math]::Round($durationSec,2)
    Throughput = $throughput
    AvgMs = $avg
    MedianMs = $median
    P90Ms = $p90
    P95Ms = $p95
    P99Ms = $p99
    SuccessRatePercent = $successRate
}
$summary | ConvertTo-Csv -NoTypeInformation

