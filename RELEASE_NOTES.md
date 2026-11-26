# 阅读 3.4.2.2（Android 4.0 版）

修复应用内「检查更新」永久提示「已是最新版本」。两仓共用版号 `3.4.2.2` / versionCode `3400202`，服务端仍为 FnReader `3.4.2`（接口契约未变）。

## 问题现象
- 3.2.0.1、3.3.5.1 等版本点「检查更新」恒提示「已是最新版本」，而 GitHub 最新 Release 已是 `v3.4.2.1`
- 已在 MuMu 上用 3.2.0.1 原包复现，清空应用数据后仍复现

## 根因
`AppUpdateGitHub.versionCodeOf(tag)` 次版本按 `*10000` 计算，`app/build.gradle` 的 versionCode 次版本按 `*100000`：`v3.4.2.1` 解析成 `3040201`（真实 `3400201`），`v3.2.0.1` 解析成 `3020001`（真实 `3200001`），新版本恒小于当前值 → 永远判「已是最新」。

## 修复
- 公式统一为 `大版本*1000000 + 次版本*100000 + 修订*100 + 构建`，与 `build.gradle` 同口径
- A4 与 Pro 同步改；A4 自有差异（`openWebShelf` 的 `/app/legado-web`、`rawServer` 判 `/app/`、`serverBase` 的 path 分支、`negativeButton` 文案）未动
- 验证：json-path 2.8.0 复算 `versionCodeOf("v3.4.2.1") = 3400201`，大于 3.3.5.1 的 `3300501`，可正确提示更新

## 注意
已装 3.4.2.1 及更早版本的机器需手动下载一次安装包才能升到本版；从本版起应用内自更新恢复正常。

## 产物
- `yuedu_app_3.4.2.2.apk`（armeabi-v7a，v1 + v2 双签）

包名 `io.legado.api19`，minSdk 19 / targetSdk 30，正式签名，无 debuggable。
