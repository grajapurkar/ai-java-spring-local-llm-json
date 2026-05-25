<#
PowerShell script to run JMeter tests in non-GUI mode for multiple user counts and generate HTML reports.
Prerequisites:
 - Apache JMeter installed and 'jmeter' is on PATH (or update $jmeterCmd to full path to jmeter.bat)
 - Run your Spring Boot app (e.g., mvn spring-boot:run) so that http://localhost:8080 is reachable

Usage: .\run-tests.ps1 -UserCounts @(5,10,50) -Iterations 1 -Ramp 5
#>
param(
    [int[]]$UserCounts = @(5,10,50,100,500,1000),
    [int]$Iterations = 1,
    [int]$Ramp = 5,
    [string]$CsvFile = "questions.csv",
    [string]$TestPlan = "test-plan.jmx",
    [string]$ResultsDir = "results",
    [string]$JMeterCmd = "jmeter"  # or full path to jmeter.bat
)

# Ensure results directory exists
if (-not (Test-Path $ResultsDir)) { New-Item -ItemType Directory -Path $ResultsDir | Out-Null }

foreach ($threads in $UserCounts) {
    $resultsFile = Join-Path $ResultsDir "results_${threads}.jtl"
    $reportDir = Join-Path $ResultsDir "report_${threads}"
    if (Test-Path $reportDir) { Remove-Item -Recurse -Force $reportDir }

    $cmd = "$JMeterCmd -n -t $TestPlan -Jthreads=$threads -Jramp=$Ramp -Jiterations=$Iterations -JcsvFile=$CsvFile -JresultsFile=$resultsFile -l $resultsFile -j $ResultsDir/jmeter_${threads}.log"
    Write-Host "Running: $cmd"
    & $JMeterCmd -n -t $TestPlan -Jthreads=$threads -Jramp=$Ramp -Jiterations=$Iterations -JcsvFile=$CsvFile -JresultsFile=$resultsFile -l $resultsFile -j "$ResultsDir/jmeter_${threads}.log"

    # Generate dashboard report (requires CSV/aggregate data from .jtl)
    Write-Host "Generating HTML report for $threads users -> $reportDir"
    & $JMeterCmd -g $resultsFile -o $reportDir

    Write-Host "Completed run for $threads threads. Results: $resultsFile, report: $reportDir"
}

Write-Host "All runs complete. Results stored in: $ResultsDir"

