<#
PowerShell script to generate a CSV file of sample questions for JMeter.
Usage: .\generate-questions.ps1 -Count 1000 -OutFile questions.csv
#>
param(
    [int]$Count = 1000,
    [string]$OutFile = "questions.csv"
)

$templates = @(
    'Does insurance cover {0} damage?',
    'What is the policy for {0} claims?',
    'How do I file a claim for {0}?',
    'Is {0} included in standard homeowner insurance?',
    'Does my policy exclude {0} events?',
    'What deductible applies to {0} losses?'
)

$subjects = @(
    'flood', 'fire', 'earthquake', 'wind', 'hail', 'theft', 'vandalism', 'water backup', 'collapse', 'liability',
    'mold', 'storm surge', 'sinkhole', 'wildfire', 'roof damage'
)

# If Count is greater than combinations, we'll cycle subjects and append an index for uniqueness
$out = New-Object System.Collections.Generic.List[string]
for ($i=1; $i -le $Count; $i++) {
    $template = $templates[ (Get-Random -Maximum $templates.Count) ]
    $subject = $subjects[ (($i - 1) % $subjects.Count) ]
    $question = [string]::Format($template, $subject)
    # Escape quotes and wrap in CSV safe format (no header, single column)
    $out.Add("\"$question\"")
}

Set-Content -Path $OutFile -Value $out -Encoding UTF8
Write-Host "Generated $Count questions -> $OutFile"

