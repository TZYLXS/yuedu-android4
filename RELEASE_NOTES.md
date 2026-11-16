## 安卓 4.0 版 2.6.16.1（versionCode 2061601）

Legado 移植版，**minSdk 19 / targetSdk 30**（Android 4.4–5.1），单包 `armeabi-v7a`、多 Dex，
应用名「阅读」，包名 `io.legado.api19`（与官方版 `io.legado.app` 冲突，安装前先卸官方版）。
与 **阅读Pro（`io.legado.pro`）2.6.16.1** 同步发布、同一套版本号；版号基线取自服务端 `legado-web` **v2.6.16**，本版为其上的 `0.0.0.1` 小更新。

### 本版变更：仅跟进文档，无代码改动

服务端在 2.6.13 之后连续推进三版（`737cd6e..b639ec2`），全部是服务端内部改动，**客户端契约零变化**：

- **2.6.14**：开启「优先手机解析」后，章节正文也优先走软总线；`/api/bus` 新增 `usage` 统计
  （`phoneSearch` / `phoneToc` / `phoneContent` / `localToc` / `localContent`）
- **2.6.15**：修掉「开了手机代理仍优先本地」——搜索先问手机再跑本机，正文提前到「本机无书源」之前尝试手机
- **2.6.16**：规则引擎加递归保护，书源规则自我引用最多 200 层，超过返回 502「书源规则递归过深，已中止解析」

核对结论：

- 对比 `737cd6e...b639ec2` 仅 5 个文件，**`remote.js` 未改**
- `docs/APP同步接口.md` 与 `docs/更新日志.md` 本轮零改动
- `/api/m/*` 路径、字段、令牌、任务类型、超时（18 / 18 / 23 / 23 / 9 秒）全部照旧
- 客户端代码零改动，只同步版本号、updateLog 与文档；A4 的 2 条解析线程 / 1 条简介线程 / 1 条电子书线程设定不受影响

### 文档同步

- `docs/API19移植说明.md` 新增本版章节、`README.md` 版本号与下载表更新
- `docs/app回传接受规范.md` 更新 §17（与 Pro 仓一字不差，三份副本同 SHA）
- 应用内 `assets/updateLog.md` 顶部新增 `**2026/11/17（2.6.16.1）**`，老段落保留

### 包信息

| 产物 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_2.6.16.1.apk` | 19 939 204 B（19.02 MB） | `85F08FBB76537D488A12D41591A81205F188BB3829F130CC8BED3EA1F0CFFC79` |

- `aapt2 dump badging`：`package: name='io.legado.api19' versionCode='2061601' versionName='2.6.16.1'`，无 `application-debuggable`
- 包内 `assets/updateLog.md` 首条已核对为 `**2026/11/17（2.6.16.1）**`（`python zipfile` 解包读取）
- 签名沿用 `keystore/yuedu.jks`（v1 + v2），可直接覆盖安装旧版

### 安装方式

1. 下载 `yuedu_app_2.6.16.1.apk`（Android 4.4–5.1 老机型，32 位）
2. 覆盖安装升级，书源与书架数据保留
3. 进入「我的 → 接入 FnReader 服务器」，填服务端地址与 `X-Api-Token`

### 自测与限制

- 编译：Gradle `-Xmx2048m` + Kotlin 守护进程 `-Xmx1536m` + `--max-workers=2`，单包约 3 分钟，exit 0
- 模拟器冒烟：MuMu 装本包后连上 `http://10.5.4.70:1122`，服务端在线，菜单与同步项正常
- 未跑项：真机观察 `usage.phoneContent` 增长、递归保护 502 的客户端表现，步骤见 `docs/app回传接受规范.md` §17.2
