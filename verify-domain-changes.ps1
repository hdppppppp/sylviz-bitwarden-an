# 验证域名修改脚本
# 检查是否还有遗漏的官方 Bitwarden 域名

Write-Host "正在检查代码中是否还有官方 Bitwarden 域名..." -ForegroundColor Cyan
Write-Host ""

$patterns = @(
    "api\.bitwarden\.com",
    "vault\.bitwarden\.com", 
    "identity\.bitwarden\.com",
    "icons\.bitwarden\.net",
    "notifications\.bitwarden\.com",
    "events\.bitwarden\.com",
    "send\.bitwarden\.com"
)

$excludePaths = @(
    "*/test/*",
    "*/androidTest/*",
    "*Test.kt",
    "*.md",
    "*/values-*/strings.xml",
    "build/*",
    ".git/*"
)

$foundIssues = $false

foreach ($pattern in $patterns) {
    Write-Host "检查: $pattern" -ForegroundColor Yellow
    
    $results = Select-String -Path . -Pattern $pattern -Recurse -Exclude $excludePaths -ErrorAction SilentlyContinue
    
    if ($results) {
        $foundIssues = $true
        Write-Host "  ❌ 发现匹配:" -ForegroundColor Red
        $results | ForEach-Object {
            Write-Host "     $($_.Path):$($_.LineNumber)" -ForegroundColor Gray
        }
    } else {
        Write-Host "  ✅ 未发现" -ForegroundColor Green
    }
}

Write-Host ""
if (-not $foundIssues) {
    Write-Host "✅ 所有关键域名已成功替换为 key.sylviz.cn！" -ForegroundColor Green
} else {
    Write-Host "⚠️  发现一些遗漏的域名引用，请检查上述文件" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "已修改的关键配置:" -ForegroundColor Cyan
Write-Host "  • 默认环境 URL 常量" -ForegroundColor White
Write-Host "  • Retrofit 基础 URL" -ForegroundColor White
Write-Host "  • AndroidManifest 深度链接" -ForegroundColor White
Write-Host "  • 网络安全配置" -ForegroundColor White
Write-Host "  • SSO/Duo/WebAuth 回调验证" -ForegroundColor White
Write-Host "  • Digital Asset Links" -ForegroundColor White
Write-Host ""
Write-Host "下一步操作:" -ForegroundColor Cyan
Write-Host "  1. 运行: ./gradlew clean" -ForegroundColor White
Write-Host "  2. 运行: ./gradlew assembleRelease" -ForegroundColor White
Write-Host "  3. 卸载旧版本应用" -ForegroundColor White
Write-Host "  4. 安装新编译的 APK" -ForegroundColor White
