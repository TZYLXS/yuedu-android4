## 安卓 4.0 版 2.6.12.1（versionCode 2061201）

Legado 移植版，**minSdk 19 / targetSdk 30**（Android 4.4–5.1），单包 `armeabi-v7a`、多 Dex，
应用名「阅读」，包名 `io.legado.api19`（与官方版 `io.legado.app` 冲突，安装前先卸官方版）。
与 **阅读Pro（`io.legado.pro`）2.6.12.1** 同步发布、同一套版本号；版号基线取自服务端 `legado-web` **v2.6.12**，本版为其上的 `0.0.0.1` 小更新。

### 本版变更（首次不是「仅跟进文档」）

服务端 2.6.12（`b85fb31`，2026-11-17 02:41）在软总线加了「调试设备」判定：设备 ID 含 `bus-debug` / `debug`、或名称含「调试」即判为调试设备，
其回传**一律丢弃**，任务留给真机或超时；调试任务（`payload.debug === true`）只投给调试设备，普通手机不再收到。
接口侧两处新增：`GET /api/m/remote/pending` 响应多 `debug` 字段，`POST /api/m/remote/result` 对调试设备返回 `{ok:true, accepted:false, ignored:true, reason}`。

A4 端与 Pro 逐条同构落地：

- `FnReaderRemote.poll()`：返回类型 `List<Job>` → `Pending(jobs, debug)`，缺省 false，兼容 2.6.11 及更早服务端
- `FnReaderRemote.submit()`：响应 `ignored == true` 时抛 `RemoteException`（带 `reason` 与命中规则说明），不再静默当成功
- `FnReaderBridgeService`：轮询循环里 `pending.debug` 命中时通知栏**一次性**提示改设备 ID / 名称
- 默认设备标识不触碰规则：`deviceId = AppConst.androidId`、`deviceName = Build.MODEL`
- A4 自有差异原样保留：`openWebShelf` 拼 `/app/legado-web`、`rawServer` 判 `/app/`、`negativeButton(requireContext().getString(R.string.cancel))`、`serverBase` 的 path 分支

契约其余部分复核无变化：任务类型 `search` / `toc` / `content` / `rank` / `bookinfo`，超时 18 / 18 / 23 / 23 / 9 秒；鉴权仍为 `X-Api-Token` 与 `X-Upload-Token`（现取）。
字段表与自测清单见 `docs/app回传接受规范.md` §15。

### 文档同步

- `docs/API19移植说明.md`（本版章节改写为真实代码改动）、`README.md`（版本号与能力条目）、`docs/app回传接受规范.md`（新增 §15，与 Pro 仓一字不差）
- 应用内 `assets/updateLog.md` 顶部新增 `**2026/11/17（2.6.12.1）**`，老段落保留

### 包信息

| 产物 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_2.6.12.1.apk` | 19 936 867 B（19.01 MB） | `22182FC26951934F763A3DC19465429BB9C6BFAE8FF9464D360EEDBCC3606790` |

- `aapt2 dump badging`：`package: name='io.legado.api19' versionCode='2061201' versionName='2.6.12.1'`，`sdkVersion:'19'`，`targetSdkVersion:'30'`，`native-code: 'armeabi-v7a'`，无 `application-debuggable`
- 包内 `assets/updateLog.md` 首条已核对为 `**2026/11/17（2.6.12.1）**`（`python zipfile` 解包读取）
- 签名沿用 `keystore/yuedu.jks`（v1 + v2）；4.4 只认 v1，故两者都开

### 安装方式

1. 下载 `yuedu_app_2.6.12.1.apk`，覆盖安装即可升级，书源与书架保留
2. 首次安装若提示「应用未安装」，先卸载官方「阅读」（包名冲突）
3. 进入「我的 → 接入 FnReader 服务器」，填服务端地址与 `X-Api-Token`

### 自测与限制

- 编译：Gradle 侧 `-Xmx2048m` + Kotlin daemon `-Xmx1024m` + `--max-workers=2`，脱离会话脚本构建，用时约 5 分钟
- 本版**未做模拟器冒烟**：MuMu 当时 `emulator-5554 offline`，且按内存约束未起模拟器；编译与包内校验已通过
- 调试设备规则的真机自测步骤见 `docs/app回传接受规范.md` §15.2
