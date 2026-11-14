## 安卓 4.0 版 2.4.2（versionCode 20402）

Legado（阅读）`3.23.110211` 编译，**minSdk 19 / targetSdk 30**（Android 4.4+），`armeabi-v7a` 单 ABI。
应用名「阅读」，包名 `io.legado.api19`，与官方版、阅读Pro 可共存安装。
与 **阅读Pro（`io.legado.pro`）2.4.2** 同步发布、同一套版本号，并与服务端 `legado-web` **v2.4.2** 对齐。

### 本版新增

- **目录先行（toc-only）**：解析电子书时只回传目录分片，正文不在首轮全量上传，按章按需补齐。
- **按章回传**：服务端下发 `ebook-chapter` 任务时，客户端用本地原文件取出该章，推给 `/api/m/ebook/chapter`，再把服务端 ack 作为任务结果回传。

本版把 Pro 的 `FnReaderEbook.kt`（新增）/ `FnReaderRemote.kt` / `FnReaderBridgeService.kt` 移植到 API19 基线，
`minSdk 19` 下 Kotlin 编译通过；4.0 版自有差异（`openWebShelf` 拼 `/app/legado-web`、`rawServer` 判 `/app/`、
`negativeButton(requireContext().getString(R.string.cancel))`、`serverBase` 的 path 分支）原样保留。

### 实测结果（测试端 `10.5.4.70:1123`）

| 环节 | 结果 |
| --- | --- |
| 上传 → 派发 → 手机解析 | mobi 上传后约 6 s 变 `complete`，`chapters=3`、`parts=2/2` |
| 按章回传 | `ebook-chapter` 任务 0 s 回 `{"chapters":1,"indexes":[1]}`，`/api/content?index=1` 可读回正文 |
| 编译 | `BUILD SUCCESSFUL`，`:app:compileAppReleaseKotlin` 通过（日志 `a4-compile.log`）、release 打包 2m1s |

### 产物

| 文件 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_2.4.2.apk` | 19 928 139 B（19.00 MB） | `86a5fe66dc90c4c0ed99df0af33558d37ff6566f2db2e4e2e20d3b8a03bbaece` |

`aapt2 dump badging`：`io.legado.api19`、`versionCode 20402`、`versionName 2.4.2`、`sdkVersion 19`、
`targetSdkVersion 30`、`native-code 'armeabi-v7a'`、`application-debuggable` 不存在（发版包无 debug 能力）。

### 安装

- 包名 `io.legado.api19`，应用名「阅读」，可与官方版、阅读Pro 同装
- 老平板 / 老手机直接装；签名与 1.x / 2.x 一致，可覆盖升级

### 已知限制

- 未在真机 Android 4.4 上实测（MuMu 最低 Android 12）
- **toc-only 目前是休眠分支**：服务端 `ebook/bus.js` 派 `ebook-parse` 时不带 `tocOnly` / `mode:toc`，上传仍走完整分片解析
- PDF 原页模式（`k:"img"`）客户端尚无渲染能力
