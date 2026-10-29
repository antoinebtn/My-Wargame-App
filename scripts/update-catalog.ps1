# Refreshes the paint catalogue from https://github.com/Arcturus5404/miniature-paints (MIT).
# Pantone and RAL are colour references rather than hobby paints, so they are skipped.
$ErrorActionPreference = 'Stop'
$repo = 'https://raw.githubusercontent.com/Arcturus5404/miniature-paints/main'
$dest = Join-Path $PSScriptRoot '..\backend\src\main\resources\catalog'
New-Item -ItemType Directory -Force $dest | Out-Null

$files = Invoke-RestMethod 'https://api.github.com/repos/Arcturus5404/miniature-paints/contents/paints'
foreach ($f in $files) {
    if ($f.name -in @('Pantone.md', 'RAL.md')) { continue }
    Write-Host "Downloading $($f.name)"
    Invoke-WebRequest "$repo/paints/$($f.name)" -OutFile (Join-Path $dest $f.name)
}
Invoke-WebRequest "$repo/LICENSE" -OutFile (Join-Path $dest 'LICENSE')
Write-Host 'Catalogue updated. Wipe the paint table (or the DB volume) to re-import.'
