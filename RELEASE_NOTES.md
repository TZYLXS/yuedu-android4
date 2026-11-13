## 安卓 4.0 版 2.0.4（versionCode 20004）

Legado（阅读）`3.23.110211` 编译，**minSdk 19 / targetSdk 30**（Android 4.4+），`armeabi-v7a` 单 ABI。
应用名「阅读」，包名 `io.legado.api19`，与官方版、阅读Pro 可共存安装。
与 **阅读Pro（`io.legado.pro`）2.0.4** 同步发布、同一套版本号。

### 本版仅跟进文档，无代码改动

本轮巡检服务端 `docs/更新日志.md` 又新增 **2.0.5 / 2.0.6** 两段（2.0.3 / 2.0.4 段上一版已跟），
`docs/APP同步接口.md` **一字未改**（SHA256 `68e3f7954681`，29343 B）。

| 文档 | 变更 |
| --- | --- |
| `docs/更新日志.md` | **2.0.5**：加入书架后立即抓完整目录（强制 `refresh`、含分页），服务端后台补全并广播，按钮显示章数。**2.0.6**：加入书架立即抓完整目录，有手机在线优先走软总线（能过反爬），无设备或失败自动回落本机解析；按钮与提示显示章数和来源 |
| `docs/APP同步接口.md` | 无变化（`68e3f7954681`，29343 B，逐字节一致） |

与阅读Pro 2.0.4 同构，4.0 版自有差异原样保留：`openWebShelf` 拼 `/app/legado-web`、`rawServer` 判 `/app/`、
`negativeButton(requireContext().getString(R.string.cancel))`、`serverBase` 的 path 分支。

### 为什么客户端不用改

- 软总线 `toc` 走 `WebBook.getChapterListAwait`，每次现抓、无缓存，按 `nextTocUrl` / 页面「下一页」翻页
  （上限 40 页）整本抓完才回，天然满足服务端要的 `refresh`。
- 任务类型、payload、回传字段、超时（`JOB_TIMEOUT_TOC = 18000L`）全部未变。
- 「按钮显示章数 / 来源」是服务端网页端 UI，与客户端面板无关。

### 产物

| 文件 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_2.0.4.apk` | 19 906 629 B（18.98 MB） | `6b30d6405386b7a364904af867c89f1af47c6f07faf6382ae85c8acfe106e297` |

`aapt2 dump badging`：`io.legado.api19`、`versionCode 20004`、`versionName 2.0.4`、`sdkVersion 19`、
`targetSdkVersion 30`、`native-code 'armeabi-v7a'`、DEX 5 个（MultiDex）、v1 + v2 双签。

### 实测结果

- 编译：`BUILD SUCCESSFUL in 2m 4s`（77 tasks，20 executed）
- 应用内日志校验：解开 APK 读 `assets/updateLog.md`，首条为 **2026/11/14（2.0.4）**，
  2.0.3 / 2.0.2 / 1.11 … 历史段完整保留
- 代码零改动：`git diff` 只含 `app/build.gradle` 版本号、`assets/updateLog.md`、`RELEASE_NOTES.md`、
  README 与 docs（含 `prune-releases.json`）

### 安装

- 包名 `io.legado.api19`，应用名「阅读」，可与官方版、阅读Pro 同装
- 老平板 / 老手机直接装；签名与 1.x / 2.0.x 一致，可覆盖升级

### 已知限制

- 未在真机 Android 4.4 上实测（MuMu 最低 Android 12）
- 服务端按**标题**去重、客户端按 URL 去重，目录条数可能不同
