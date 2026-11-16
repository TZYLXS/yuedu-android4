## 安卓 4.0 版 2.6.18.1（versionCode 2061801）

Legado 移植版，**minSdk 19 / targetSdk 30**（Android 4.4–5.1），单包 `armeabi-v7a`、多 Dex，
应用名「阅读」，包名 `io.legado.api19`（与官方版 `io.legado.app` 冲突，安装前先卸官方版）。
与 **阅读Pro（`io.legado.pro`）2.6.18.1** 同步发布、同一套版本号；版号基线取自服务端 `legado-web` **v2.6.18**，本版为其上的 `0.0.0.1` 小更新。

### 本版变更一：软总线加权路由 + 封面/简介拆分（有真实代码改动）

服务端在 2.6.16 之后推进 2.6.17 / 2.6.18（`b639ec2..ecf040`），软总线升级为加权路由：

- **任务分三类**：`book`（search/toc/rank）、`content`（content/ebook）、`media`（cover/intro/bookinfo）
- **隐藏性能分（0–100，不对外暴露）**：初始 50，成功 +4、失败 −8、>8s 再 −2；高分（>=55）可领全部类、在途上限 3，
  低分（<55）只领 media、上限 2；无高分设备在线时自动放开全部任务
- **领取即锁定 30 秒**：结果幂等——重复回传 `duplicate`、非锁定回传 `stale`；权重计算独立成纯函数 `busweights.js`，异常降级
- **2.6.18 修复**：在途数按实际锁定任务重算，修掉 inflight 泄漏卡死设备；`/api/bus` 新增 `highScore`/`lowScore` 计数

本端落实（`FnReaderRemote.kt` + `FnReaderBridgeService.kt`）：

- 新增 `cover` / `intro` 任务，payload 与 bookinfo 相同 `{bookUrl, source, name, author}`：抽出 `fetchMediaBook()`，
  bookinfo/cover/intro 共用同一次 `WebBook.getBookInfoAwait`；`cover()` 只回 `{coverUrl}`，`intro()` 回简介与元数据，拿不到的字段省略
- `submit()` 识别幂等字符串态：`ok:"duplicate"`/`"stale"` 正常返回不重试，`ignored` 仍按调试设备失败抛出
- 队列路由：`bookinfo`/`cover`/`intro` 统一进 info 队列；本端保持低性能机口径（检索 2 线程、简介 1 线程），超时不变（18/18/23/23/9 秒）

### 本版变更二：E-ink 墨水屏显示优化（无厂商 SDK 依赖）

面向文石 / 墨案 / 海信 / 汉王等常见安卓墨水屏，复用「主题模式」里的 E-Ink 开关（themeMode 值 3），选上后：

- 新增 `utils/EInkUtils.kt`：递归遍历视图，关闭 RecyclerView 项动画、所有可滚动 View 的过度滚动（边缘回弹光晕）与滚动条淡入淡出，
  ListView 缓存底色固定为白；用 tag 去重，可在全局布局回调里反复调用
- 新增主题 `AppTheme.EInk` 与无动画窗口样式 `Animation.Activity.EInk`：窗口转场全部置空、白底、`colorControlHighlight` 透明（水波纹不可见）
- `BaseActivity` 固定该主题、注册全局布局监听并 `overridePendingTransition(0,0)`；`BaseDialogFragment` 关闭对话框窗口动画并同样递归处理
- 阅读页本就有独立的墨水屏白底黑字、无翻页动画配置，本版把书架/列表/对话框等全局界面也对齐到无残影口径

### 文档同步

- `docs/API19移植说明.md` 新增本版章节、`README.md` 版本号/下载表/能力清单更新
- `docs/app回传接受规范.md` 新增 §18（与 Pro 仓一字不差，三份副本同 SHA）
- 应用内 `assets/updateLog.md` 顶部新增 `**2026/11/17（2.6.18.1）**`，老段落保留

### 包信息

| 产物 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_2.6.18.1.apk` | 19 941 229 B（19.02 MB） | `E2E1A9EFF5067D580C76FF2E015CC8B53A227EA991C1883F2CEBFDE53F80B01A` |

- `aapt2 dump badging`：`package: name='io.legado.api19' versionCode='2061801' versionName='2.6.18.1'`，无 `application-debuggable`
- 包内 `assets/updateLog.md` 首条已核对为 `**2026/11/17（2.6.18.1）**`（`python zipfile` 解包读取）
- 签名沿用 `keystore/yuedu.jks`（v1 + v2），可直接覆盖安装旧版
- 编译期 D8 有一条非致命 WARNING（重写 `HandleFileActivity` 的 Kotlin metadata 时内部告警，不影响 dex 与运行）

### 安装方式

1. 下载 `yuedu_app_2.6.18.1.apk`（Android 4.4–5.1 老机型 / 安卓墨水屏，32 位）
2. 覆盖安装升级，书源与书架数据保留
3. 进入「我的 → 接入 FnReader 服务器」，填服务端地址与 `X-Api-Token`；墨水屏再到「主题模式」选「E-Ink 模式」

### 自测与限制

- 编译：Gradle `-Xmx2048m` + Kotlin 守护进程 `-Xmx1536m` + `--max-workers=2`，最终一次 2m29s，exit 0
- 未跑项：真机实派 cover/intro、构造 duplicate/stale、E-ink 真机残影观察，步骤见 `docs/app回传接受规范.md` §18.2
