# Config
$inputPath = "fastlane\metadata\android\ru-RU\images\phoneScreenshots"
$outputPath = "assets\rustore"
$targetWidth = 1080
$targetHeight = 1920

if (-not (Test-Path $outputPath)) {
    New-Item -ItemType Directory -Path $outputPath | Out-Null
    Write-Host "New folder: $outputPath" -ForegroundColor Green
}
else {
    Write-Host "Clean up the output folder..." -ForegroundColor Yellow
    
    Get-ChildItem $outputPath -Filter *.png | Remove-Item -Force
    
    Write-Host "Cleanup complete" -ForegroundColor Green
}

if (-not (Test-Path $inputPath)) {
    Write-Host "Error: Input folder not found: $inputPath" -ForegroundColor Red
    exit 1
}

Add-Type -AssemblyName System.Drawing

$processedCount = 0
$skippedCount = 0

Write-Host "Starting image processing..." -ForegroundColor Cyan

foreach ($file in Get-ChildItem $inputPath -Filter *.png) {
    try {
        $inputFile = $file.FullName
        $outputFile = Join-Path $outputPath $file.Name
        
        $image = [System.Drawing.Image]::FromFile($inputFile)
        
        $ratio = [Math]::Min($targetWidth / $image.Width, $targetHeight / $image.Height)
        $newWidth = [int]($image.Width * $ratio)
        $newHeight = [int]($image.Height * $ratio)
        
        $bitmap = New-Object System.Drawing.Bitmap $targetWidth, $targetHeight
        $bitmap.MakeTransparent()
        
        $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
        $graphics.Clear([System.Drawing.Color]::Transparent)
        
        $x = [int](($targetWidth - $newWidth) / 2)
        $y = [int](($targetHeight - $newHeight) / 2)
        
        $graphics.DrawImage($image, $x, $y, $newWidth, $newHeight)
        
        $bitmap.Save($outputFile, [System.Drawing.Imaging.ImageFormat]::Png)
        
        $image.Dispose()
        $graphics.Dispose()
        $bitmap.Dispose()
        
        $processedCount++
        Write-Host "  ✓ Processed: $($file.Name)" -ForegroundColor Green
    }
    catch {
        $skippedCount++
        Write-Host "  ✗ Error while processing $($file.Name): $_" -ForegroundColor Red
    }
}

Write-Host "`n=== COMPLETE ===" -ForegroundColor Cyan
Write-Host "Processed: $processedCount" -ForegroundColor Green
Write-Host "Skipped: $skippedCount" -ForegroundColor Yellow
Write-Host "Output folder: $outputPath" -ForegroundColor White

if ($Host.Name -eq "ConsoleHost") {
    Write-Host "`nPress any key to exit..."
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
}