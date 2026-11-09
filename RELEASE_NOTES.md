## 阅读（安卓 4.0 版）2.0.3（versionCode 20300）

Legado `3.23.110211` 降到 **minSdk 19 / targetSdk 30** 的移植版，单包覆盖 Android 4.4 – 5.1（API 19–22），
armeabi-v7a，v1 + v2 双签。应用名「阅读」，包名 `io.legado.api19`。
与 **阅读Pro（`io.legado.pro`）2.0.3** 同步发布、同一套版本号。

### 本版仅跟进文档，无代码改动

本轮巡检只有服务端 `docs/更新日志.md` 变了（新增 2.0.3 / 2.0.4 两段），`docs/APP同步接口.md` **一字未改**，
客户端 `service/FnReaderBridgeService.kt`、`model/FnReaderRemote.kt`、`ui/config/FnReaderConfigDialog.kt`
全部保持 2.0.2 的状态，一行没动；4.0 版自有差异照旧保留（`openWebShelf` 拼 `/app/legado-web`、
`rawServer` 判 `/app/`、`serverBase` 的 path 分支、`negativeButton(getString(R.string.cancel))`）。

| 文档 | 变更 |
| --- | --- |
| `docs/更新日志.md` | **2.0.3**：目录引擎分页上限 5 → 40 页，带循环检测；没写 `nextTocUrl` 时按页面「下一页」链接兜底，没有新章节即停。**2.0.4**：Python 解析引擎的目录同样支持分页，桥接层按 `nextTocUrl` / 页面「下一页」循环抓取（上限 40 页、去重、无新增即停） |
| `docs/APP同步接口.md` | 无变化（SHA256 与上一轮基线一致，`68e3f795…`） |

分页与去重都在服务端做，客户端 `toc` 任务与回传格式不变、超时不变（手机 18 秒，服务端等 20 秒）。

### 实测：单书源从「只回 50 章」变成整本目录一次回完

服务端 `GET /api/m/info` → `{"app":"阅读","version":"2.0.4"}`；`GET /api/toc?url=…dingdians…/index/11010898/&refresh=1`
返回 191 253 B、chapters 1610 条（服务端按标题去重），首条「群星之子逃婚录 第1章」、末条「第3139章 白骨王座（三更）」。
同一本在 2.0.2 服务端只回 50 章，本版服务端侧问题解除。

### 产物

| 文件 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_2.0.3.apk` | 19 906 270 B（18.98 MB） | `d0791e3d51f512bb3877cf2d5894b36ce02fd6a3350352ba60f3e93947f0f4a1` |

`aapt2 dump badging`：`io.legado.api19`、`versionCode 20003`、`versionName 2.0.3`、`sdkVersion 19`、
`targetSdkVersion 30`、`native-code 'armeabi-v7a'`，v1 + v2 双签。

### 实测结果

- 编译：`BUILD SUCCESSFUL in 1m 21s`（77 tasks）
- 应用内日志校验：解开 APK 读 `assets/updateLog.md`，首条为 **2026/11/10（2.0.3）**，2.0.2 及更早段落完整保留
- 代码零改动：`git diff` 只含 `app/build.gradle` 版本号、`assets/updateLog.md`、`RELEASE_NOTES.md`、README 与 docs

### 安装

- 包名 `io.legado.api19`，可与阅读Pro、官方「阅读」共存
- Android 4.4 – 5.1（API 19–22）用这个包；Android 7.0+ 用阅读Pro
- 签名与 1.x / 2.0.x 一致，可直接覆盖升级

### 已知限制

- 服务端按**标题**去重、客户端按 URL 去重，两者章数可能不同（本书 1610 vs 3139）
- 本版仍未在真机 Android 4.4 上实测（MuMu 最低 Android 12，只能验到编译与日志）
