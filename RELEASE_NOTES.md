## 安卓 4.0 版 2.4.4.1（versionCode 2040401）

Legado（阅读）`3.23.110211` 编译，**minSdk 19 / targetSdk 30**（Android 4.4+），`armeabi-v7a` 单 ABI。
应用名「阅读」，包名 `io.legado.api19`，与官方版、阅读Pro 可共存安装。
与 **阅读Pro（`io.legado.pro`）2.4.4.1** 同步发布、同一套版本号；版号基线取自服务端 `legado-web` **v2.4.4**，本版为其上的 `0.0.0.1` 小更新。

### 本版新增

- **传书到飞牛**：设置面板新增「⑧ 传书到飞牛」，直接选本机电子书（pdf / mobi / prc / azw / azw3 / epub / txt）推给飞牛，不必再挂 WebDAV 或用 curl。
  - 上传令牌**每次现取**：上传前先 `GET /api/ebook/upload` 取当前 `token`，再 `POST /api/m/ebook/upload?name=<文件名>`，头带 `X-Upload-Token`、body 为裸 `application/octet-stream`（非 multipart）。
  - 令牌被服务端重置/轮换也无需改配置：命中 `401` 且 `code=upload-token` 时自动重新取一次令牌续传（最多重试一次）。
  - 服务端关闭传书（`enabled=false`）、超限（`maxMB`）、格式不支持时，面板直接显示服务端给出的原因文案。

本版把 Pro 的 `FnReaderRemote.kt`（新增传书段）/ `FnReaderConfigDialog.kt`（新增 ⑧ 项与文件选择）
移植到 API19 基线，`minSdk 19` 下 Kotlin 编译通过；4.0 版自有差异（`openWebShelf` 拼 `/app/legado-web`、
`rawServer` 判 `/app/`、无 `import android.content.Intent`、注释口径）原样保留。

### 实测结果

| 环节 | 结果 |
| --- | --- |
| 编译 | `:app:compileAppReleaseKotlin` 通过（日志 `a4-build.log`），release 打包 `BUILD SUCCESSFUL` |
| 传书链路 | 同一套代码在 Pro 2.4.4.1 上完成端到端实测（mobi 去重、txt 入库 `complete`、令牌轮换后仍成功），4.0 版为同源实现 |
| 装机 | 4.0 版未在真机 Android 4.4 上实测（MuMu 最低 Android 12） |

### 产物

| 文件 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_2.4.4.1.apk` | 19 934 470 B（19.01 MB） | `77dba672cb93b4592e352215da4a259b85ae85936c9532eb55dc6fe4ebf29544` |

`aapt2 dump badging`：`io.legado.api19`、`versionCode 2040401`、`versionName 2.4.4.1`、`sdkVersion 19`、
`targetSdkVersion 30`、`native-code 'armeabi-v7a'`、`application-debuggable` 不存在（发版包无 debug 能力）。

### 安装

- 包名 `io.legado.api19`，应用名「阅读」，可与官方版、阅读Pro 同装
- 老平板 / 老手机直接装；签名与 2.4.3 及更早版本一致，可覆盖升级

### 已知限制

- 未在真机 Android 4.4 上实测（MuMu 最低 Android 12）
- 传书只做「本机文件 → 飞牛」，无上传历史与重试队列；分片断点续传未做
- **toc-only 目前是休眠分支**：服务端 `ebook/bus.js` 派 `ebook-parse` 时不带 `tocOnly` / `mode:toc`
- PDF 原页模式（`k:"img"`）客户端尚无渲染能力
