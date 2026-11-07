## 阅读 1.0 · Android 4.4–5.1 移植首版

Legado（阅读）`3.23.110211` 的 API19 移植版，**minSdk 19 / targetSdk 30**，单包覆盖 Android 4.4–5.1（API 19–22）。

### 安装

1. 先卸载官方「阅读」（包名同为 `io.legado.app`，否则签名冲突装不上）
2. 安装 `yuedu_app_1.0.26.110803.apk`
3. 打开应用 → 我的 → 书源管理 → 网络导入，粘贴书源链接即可

### 产物信息

| 项 | 值 |
| --- | --- |
| 包名 | `io.legado.app` |
| versionCode / versionName | 19001 / 1.0.26.110803 |
| minSdk / targetSdk | 19 / 30 |
| 签名 | v1 (JAR) + v2，`apksigner verify` 通过 |
| ABI | armeabi-v7a |
| DEX | 5 个（MultiDex） |
| 体积 | 18.6 MB |
| SHA256 | `08DE73E02B33C39F7ECB511EF443155CAF77B771E8A826E491D5A2D926EA3BCA` |

### 主要改动

- 构建：minSdk 19 / targetSdk 30、去掉 `versionCode` 对 git 的依赖、固定 v1+v2 双签、限 32 位 ABI、lint 不阻断、`modules/*` minSdk 同步下调
- 裁剪：移除 Cronet（整包 11 文件 + `cronet.json` + `download.gradle`）、移除 Firebase
- 网络：新增 `Tls12SocketFactory`（4.4 支持 TLSv1.2 但默认不启用），`SSLContext` 用 `TLS`
- 框架 API：TTS 四参 `speak` → `speakCompat`；`ACTION_OPEN_DOCUMENT_TREE` 4.4 回退内置文件夹选择器；`android.system.Os`（epub PFD）加 `FileInputStream` 回退
- 清单：补 `extractNativeLibs` / `usesCleartextTraffic`，5 个三方库 `overrideLibrary`
- lint `[NewApi]`：15 → 0

### 接飞牛书库

配合 [FnReader](https://github.com/TZYLXS/FnReader) 1.7.1+：在飞牛「阅读」设置页 →「阅读 APK 接入」复制书源链接，
在本 APK 里网络导入即可搜书、读正文。详见 [docs/飞牛书库接入.md](docs/飞牛书库接入.md)。

### 已知限制

4.4 上这些功能可能不可用（三方库要求 API21+，不影响启动和阅读）：
ExoPlayer 音频播放、扫码导入书源、zip/epub 的 libarchive 路径。

### 未验证项

本版未做真机复测（手上测试机 adb 掉线）。接上 USB 后可跑 `scripts/verify_device.bat` 安装并抓启动日志。
