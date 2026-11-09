## 阅读（安卓 4.0 版）1.10（versionCode 11000）

Legado `3.23.110211` 降到 **minSdk 21 → 19 / targetSdk 30** 的移植版，单包覆盖 Android 4.4 – 5.1（API 19–22），
armeabi-v7a，v1 + v2 双签。应用名「阅读」，包名 `io.legado.api19`。
与 **阅读Pro 1.10** 同步发布、同一套版本号、同一份 FnReader 适配实现。

### 本版主线：跟进服务端 1.11.3，**无代码改动**

本轮巡检命中 FnReader `docs/更新日志.md` 新增 **1.11.3（2026-11-10）**：

> 修复 legado-py 缺陷清单：补 applyReplaceRules（正文净化）、formatJs 结果生效、脚本/测试目录名统一、日志写入 docs、README 版本自动同步。

| 文档 | 本轮状态 | 对客户端的影响 |
| --- | --- | --- |
| `docs/更新日志.md` | 新增 1.11.3 一段（16 438 → 16 649 B） | 全部是服务端 Python 侧（legado-py）修复 |
| `docs/APP同步接口.md` | **未变**（27 458 B / SHA256 `7b46d6a625031f31…`） | 接口路径、回传字段、鉴权、软总线任务类型与超时都没有变化 |

本版**仅跟进文档与版本号**，`FnReaderRemote.kt` / `FnReaderBridgeService.kt` / `FnReaderConfigDialog.kt`
与 1.9 完全一致；4.0 版自有差异（`openWebShelf` 拼 `/app/legado-web`、`rawServer` 判 `/app/`、
`negativeButton` 用 `getString(R.string.cancel)`、`serverBase` 的 path 分支）原样保留。

正文净化 `applyReplaceRules` 与 `formatJs` 都在服务端执行，客户端不再叠一层。

### 实测结果

- 编译：`BUILD SUCCESSFUL`（1m 27s，77 tasks）
- 服务端实测：`GET /api/m/info` → `{"app":"阅读","version":"1.11.3"}`
- 模拟器（MuMu，Android 12）装 1.10 包：
  - 首启更新日志首条为 **2026/11/10（1.10）**（截图为证）
  - 「我的 → FnReader 服务器」九项菜单齐全，⑤ 手机解析待命显示「已关闭（点此开启）」
  - 面板地址栏保留 `http://10.5.4.70:1122`，令牌正常，「测试连接」回显服务端 1.11.3
- `POST /api/remote/job {"type":"rank"}` 在 1.11.3 上仍返回 `400 {"error":"不支持的任务类型"}`，
  `rank` 只能由服务端 `/api/daily` 内部触发（与 Pro 版结论一致）

### 安装

- 包名 `io.legado.api19`，应用名「阅读」，可与官方「阅读」、阅读Pro 共存
- 仅 armeabi-v7a；4.4 / 5.0 / 5.1 直接装
- 签名与 1.0 – 1.9 一致（证书 SHA-256 `81ce1c10…09ef0f`），可覆盖升级

### 包信息

| APK | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_1.10.apk` | 18.67 MB（19 578 987 B） | `1EF73ED4843DDCB6B4B470B02451B4DA2169D70F37553C7D1E38CA5496862BA2` |

校验：`aapt2 dump badging` → `io.legado.api19` / `versionCode 11000` / `versionName 1.10` / `sdkVersion 19` /
`targetSdkVersion 30` / `native-code 'armeabi-v7a'`；`apksigner verify --print-certs` 证书 SHA-256
`81ce1c10476fa400ed962c4a18b54ae4497581320b1d940d896d62b6c609ef0f`。

### 升级方式

装了 1.0 – 1.9 的直接覆盖安装即可；服务端地址、令牌、书源、书架、进度都保留，无需重新导入书源。
