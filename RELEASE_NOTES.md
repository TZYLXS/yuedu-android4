## 阅读（安卓 4.0 版）2.0.2（versionCode 20200）

Legado `3.23.110211` 降到 **minSdk 19 / targetSdk 30** 的移植版，单包覆盖 Android 4.4 – 5.1（API 19–22），
armeabi-v7a，v1 + v2 双签。应用名「阅读」，包名 `io.legado.api19`。
与 **阅读Pro 2.0.2** 同步发布、同一套版本号、同一份 FnReader 适配实现。

### 本版主线：软总线拆两条队列，`bookinfo` 对齐文档 §18

本轮巡检命中 FnReader 两份文档同时更新：

| 文档 | 变更 |
| --- | --- |
| `docs/APP同步接口.md` | 新增 **§18 简介补全：让手机把书籍详情带回来**——任务 `bookinfo`，payload `{bookUrl, source, name, author}`，回传 `intro / author / coverUrl / kind / wordCount / lastChapter / totalChapterNum`（全可选，服务端只补空字段）；**超时 10 秒**，成功带 `"via": "phone"` |
| `docs/更新日志.md` | 新增 **2.0.2 / 2.0.1 / 2.0.0 / 1.11.4** 四段 |

客户端改动（与 Pro 逐行同构，4.0 版自有差异原样保留：`openWebShelf` 拼 `/app/legado-web`、`rawServer` 判 `/app/`、
`negativeButton(requireContext().getString(R.string.cancel))`、`serverBase` 的 path 分支）：

- **两条队列**：`bookinfo` 进独立 `infoQueue`（单并发），`search` / `toc` / `content` / `rank` 进
  `searchQueue`（`SEARCH_CONCURRENCY = 2`）
- **`bookinfo` 超时 23 s → 9 s**（`JOB_TIMEOUT_BOOKINFO = 9000L`），对齐 §18.2「超时 10 秒」
- `FnReaderRemote.kt` 未改：`bookInfo()` 字段、`matchSource()`、`plainText()` 与文档一致

### 产物

| 文件 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_2.0.2.apk` | 19 905 841 B（18.98 MB） | `da3d234021e97d460503d5cda432aced070c4ffbc644cc5f64d971d902e29165` |

`aapt2 dump badging`：`io.legado.api19`、`versionCode 20002`、`versionName 2.0.2`、`sdkVersion 19`、
`targetSdkVersion 30`、`application-label '阅读'`、`native-code 'armeabi-v7a'`。

### 实测结果

- 编译：`BUILD SUCCESSFUL in 1m 45s`（77 tasks）
- 应用内日志校验：APK 内 `assets/updateLog.md` 首条为 **2026/11/10（2.0.2）**
- 服务端 `GET /api/m/info` 回显 2.0.2；软总线 ①–⑤ 面板与自动同步行为与 Pro 一致
- 4.0 版未做真机（Android 4.4）运行验证：本机只有 MuMu（Android 12），接口层与 Pro 同构，风险集中在系统 API，
  近三版无 4.4 专属代码改动

### 安装

- 包名 `io.legado.api19`，应用名「阅读」，可与 Pro 版共存
- 仅 armeabi-v7a；Android 4.4 – 5.1 直接安装，Android 7+ 也能跑
