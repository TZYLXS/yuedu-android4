## 安卓 4.0 版 2.6.13.1（versionCode 2061301）

Legado 移植版，**minSdk 19 / targetSdk 30**（Android 4.4–5.1），单包 `armeabi-v7a`、多 Dex，
应用名「阅读」，包名 `io.legado.api19`（与官方版 `io.legado.app` 冲突，安装前先卸官方版）。
与 **阅读Pro（`io.legado.pro`）2.6.13.1** 同步发布、同一套版本号；版号基线取自服务端 `legado-web` **v2.6.13**，本版为其上的 `0.0.0.1` 小更新。

### 一、跟进服务端 2.6.13（调试设备识别扩展）

服务端 2.6.13（`737cd6e`）把调试设备判定放宽：ID 追加 `busmon` / `monitor` / `mock` / `fake` / `e2e`，
名称追加「监控」「测试机」；并修掉调试工具把真实书籍目录写成 `http://debug.local/...` 的问题。
接口形状、任务类型、超时、令牌均未变，A4 端与 Pro 逐条同构，契约层面**不动逻辑**，只对齐提示文案：

- `FnReaderRemote.submit()` 的失败原因改为「ID 含 bus-debug/busmon/monitor/mock/fake/e2e/debug，或名称含「调试」「监控」「测试机」」
- `FnReaderRemote.Pending` 的 KDoc 同步写成 2.6.12 → 2.6.13 的演进
- `FnReaderBridgeService` 通知文案保持不变（只说改设备 ID / 名称）
- A4 自有差异原样保留：`openWebShelf` 拼 `/app/legado-web`、`rawServer` 判 `/app/`、
  `negativeButton(requireContext().getString(R.string.cancel))`、`serverBase` 的 path 分支

### 二、手机解析改多线程（本版性能改动）

A4 面向低性能机（典型 **4 核 A53**），不做核数自适应，固定 **2 条解析线程**——再多只会互相抢 CPU 并触发站点限流：

| 线程池 | 装什么任务 | 线程数 |
| --- | --- | --- |
| `parseDispatcher` | search / toc / content / rank | 固定 **2** 条 |
| `infoDispatcher` | bookinfo（**封面与简介**） | 固定 **1** 条，独占一池，不排在检索任务后面 |
| `ebookDispatcher` | ebook-parse / ebook-chapter | **1** 条，整本解析独占 |

- 线程池在服务启动时创建（守护线程 `fnreader-parse` / `fnreader-info` / `fnreader-ebook`），`onDestroy` 一起 `close()`
- 超时、回传格式、令牌、错误码全部不变；解析失败仍回 `ok:false` + 原因

### 三、文档同步

- `docs/API19移植说明.md` 新增本版章节（含线程数与 A4 自有差异说明）、`README.md` 更新能力条目
- `docs/飞牛书库接入.md` 队列段落改写为线程池口径
- `docs/app回传接受规范.md` 更新 §16（与 Pro 仓一字不差，三份副本同 SHA）
- 应用内 `assets/updateLog.md` 顶部新增 `**2026/11/17（2.6.13.1）**`，老段落保留

### 包信息

| 产物 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_2.6.13.1.apk` | 19 938 764 B（19.02 MB） | `54C0A51A99D83B846C633DCC57139B482401544915A19EB181463E3916048021` |

- `aapt2 dump badging`：`package: name='io.legado.api19' versionCode='2061301' versionName='2.6.13.1'`，无 `application-debuggable`
- 包内 `assets/updateLog.md` 首条已核对为 `**2026/11/17（2.6.13.1）**`（`python zipfile` 解包读取）
- 签名沿用 `keystore/yuedu.jks`（v1 + v2），可直接覆盖安装旧版

### 安装方式

1. 下载 `yuedu_app_2.6.13.1.apk`（Android 4.4–5.1 老机型，32 位）
2. 覆盖安装升级，书源与书架数据保留
3. 进入「我的 → 接入 FnReader 服务器」，填服务端地址与 `X-Api-Token`

### 自测与限制

- 编译：Gradle `-Xmx2048m` + Kotlin 守护进程 `-Xmx1536m` + `--max-workers=2`，单包约 3 分钟
- 静态校验：三条线程池的创建/关闭与任务分流已逐行核对；A4 检索线程数固定 2，与 Pro 的自适应逻辑不共用常量
- **未做真机 / 模拟器冒烟**：MuMu 长期 `offline`；低配机并发表现（2 条线程 + 电子书解析同时跑）留待真机，
  步骤见 `docs/app回传接受规范.md` §16.2 未勾选项
