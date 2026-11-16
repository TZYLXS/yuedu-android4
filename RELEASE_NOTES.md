## 安卓 4.0 版 2.6.18.2（versionCode 2061802）

Legado 移植版，**minSdk 19 / targetSdk 30**（Android 4.4–5.1），单包 `armeabi-v7a`、多 Dex，
应用名「阅读」，包名 `io.legado.api19`（与官方版 `io.legado.app` 冲突，安装前先卸官方版）。
与 **阅读Pro（`io.legado.pro`）2.6.18.2** 同步发布、同一套版本号；版号基线取自服务端 `legado-web` **v2.6.18**。

### 本版仅跟进文档，无代码改动

上游 `ecf0408..c3922a3` 有 2 个提交、2 个文件变化：

- `6bfdbdc6`：`AGENTS.md` 新增上游环境专用的工具调用约定（+11），与安卓客户端接口无关
- `c3922a35`：`docs/软总线调试APK.md` 新增 §6.3（+29），补充 `GET /api/bus` 在线设备字段与历史实测快照

新增说明的 `devices[]` 字段为 `id`、`name`、`model`、`version`、`lastSeen`、`handled`、`enabled`、`debug`、`online`、`idle`，
顶层另有 `pending`、`recent`、`usage`。这些都是面板⑦的只读信息，客户端现有 `GET /api/bus` 调用不需要修改。

三份重点文档 blob 与 2.6.18 基线一致：`APP同步接口.md` = `6b9b701f…`、`更新日志.md` = `84c99abc…`、
`更新日志.txt` = `f1099dcb…`；compare 不含 `remote.js` / `server.js`，所以 `/api/m/*` 路径、字段、鉴权、任务类型与超时均无变化。

**文档口径校正**：`cover` / `intro` 在 `timeoutOf()` 中没有独立分支，当前实际使用 default **20 秒**；
`bookinfo` 独立分支仍为 9 秒。本版只把 README 与接入文档中的 cover/intro 超时改为 20 秒，没有修改客户端代码。
A4 的 2 条检索线程、1 条简介线程与 E-ink 优化保持原样。

### 文档同步

- `README.md`、`docs/API19移植说明.md`、`docs/飞牛书库接入.md` 更新到本版并补充 `/api/bus` 字段表
- `docs/app回传接受规范.md` 新增 §19，本地正本与两仓 docs 三份副本 SHA256 一致
- 应用内 `assets/updateLog.md` 顶部新增 `**2026/11/17（2.6.18.2）**`，旧段落保留

### 包信息

| 产物 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_2.6.18.2.apk` | 19 941 506 B（19.02 MB） | `1092B8E74538EC78D3548901F2E0C95EBE1D21D45DE5C3DD5A5AFB9CF91B01AE` |

- `aapt2 dump badging`：`versionCode='2061802' versionName='2.6.18.2'`，无 `application-debuggable`
- 包内 `assets/updateLog.md` 首条已用 Python zipfile 核对为 `**2026/11/17（2.6.18.2）**`
- 签名沿用 `keystore/yuedu.jks`（v1 + v2），可直接覆盖安装旧版

### 安装方式

1. 下载 `yuedu_app_2.6.18.2.apk`（Android 4.4–5.1 老机型 / 安卓墨水屏，32 位）
2. 覆盖安装升级，书源与书架数据保留
3. 进入「我的 → 接入 FnReader 服务器」，填服务端地址与 `X-Api-Token`；墨水屏可到「主题模式」选「E-Ink 模式」

### 自测与限制

- 编译：Gradle `-Xmx2048m` + Kotlin `-Xmx1536m` + `--max-workers=2`，**2m41s**，exit 0
- MuMu 模拟器：覆盖安装成功，`dumpsys` 显示 2061802 / 2.6.18.2，首启更新日志显示本版
- 本版未改代码，未重复执行全量书架同步；真机实派 cover/intro、构造 duplicate/stale 与 E-ink 真机残影观察仍是既有待测项
