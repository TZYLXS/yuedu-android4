## 安卓 4.0 版 2.6.7.1（versionCode 2060701）

Legado（阅读）`3.23.110211` 编译，**minSdk 19 / targetSdk 30**（Android 4.4+），`armeabi-v7a` 单 ABI。
应用名「阅读」，包名 `io.legado.api19`，与官方版、阅读Pro 可共存安装。
与 **阅读Pro（`io.legado.pro`）2.6.7.1** 同步发布、同一套版本号；版号基线取自服务端 `legado-web` **v2.6.7**，本版为其上的 `0.0.0.1` 小更新。

### 本版做了什么

**仅跟进文档，无代码改动**——代码与 2.4.4.1 完全一致，本版把版本号一次对齐到服务端 2.6.7。

- 服务端 `docs/APP同步接口.md` **零改动**（33802 B，SHA256 `e04f34664bcd5e5bd02abb5342698d2e1219d0b849d76003211c589e223f9ea1`）
- `docs/更新日志.txt` 新增 2.6.6 / 2.6.7 两段（`.md` 自 2.6.0 起服务端只写 `.txt`，巡检两处都比对）
- **2.6.6**：净化规则（替换·屏蔽）对 `/api/m/content` 同样生效，App 正文与网页一致；返回字段 `title` / `paragraphs` 结构不变
- **2.6.7**：网页端新增「离线缓存」页、`GET /api/ebook/export`（整本 TXT 导出）、`GET /api/update/check`（启动检查更新）；App 侧无对应界面

4.0 版自有差异原样保留（`openWebShelf` 拼 `/app/legado-web`、`rawServer` 判 `/app/`、
`negativeButton(requireContext().getString(R.string.cancel))`、`serverBase` 的 path 分支）。

### 契约复核（逐项核对，全部未变）

| 维度 | 结论 |
| --- | --- |
| 回传字段 | `search` / `toc` / `content` / `rank` / `bookinfo` 上行字段未变；2.6.6 只改正文内容，不改结构 |
| 接口路径 | `/api/m/*` 未增删改；2.6.7 新增的四个接口全在网页端 |
| 鉴权 | `X-Api-Token`；传书 `GET /api/ebook/upload` 现取 + `X-Upload-Token` |
| 任务类型与超时 | `search` 18 s / `toc` 18 s / `content` 23 s / `rank` 23 s / `bookinfo` 9 s |
| 错误码 | 未变（`upload-token` 401、超限 413、multipart 400 语义照旧） |

### 实测结果（MuMu Android 12）

| 环节 | 结果 |
| --- | --- |
| 编译 | `:app:assembleAppRelease` `BUILD SUCCESSFUL in 1m 43s`（exit 0） |
| badging | `io.legado.api19`、`versionCode 2060701`、`versionName 2.6.7.1`、`sdkVersion 19`、`targetSdkVersion 30`、`native-code 'armeabi-v7a'`、无 `application-debuggable` |
| 应用内日志 | zipfile 解包读 `assets/updateLog.md`，首条 `2026/11/16（2.6.7.1）`（共 51 条） |
| MuMu 冒烟 | 覆盖安装启动正常，首启更新日志首条即本版；面板 ①–⑧ 与 Pro 同构（本版零 diff，沿用 2.4.4.1 实测） |

### 产物

| 文件 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_2.6.7.1.apk` | 19 934 850 B（19.01 MB） | `e626bd50a813ee43de5f31c63e4dc78338fea574f3d64f9ecc25c1b5cf1f2f7c` |

### 安装

- 包名 `io.legado.api19`，应用名「阅读」，与官方「阅读」、阅读Pro 共存
- 单 ABI `armeabi-v7a`，Android 4.4+（含 4.0 版系统定制机）可用
- 签名与 2.4.4.1 一致，可直接覆盖升级

### 已知限制

- 4.4 上 `ExoPlayer` 音频、扫码、libarchive 三类功能不可用（见 README「已知限制」）。
- 传书只做「本机文件 → 飞牛」，不做上传历史与断点续传；超 `maxMB` 由服务端 413 拒绝。
- 服务端「离线缓存」页与「更新提醒」只在网页端有，App 内没有对应入口。
