## 安卓 4.0 版 3.0.10.1（versionCode 3001001）

Legado（阅读）`3.23.110211` 编译，**minSdk 19 / targetSdk 30**（Android 4.4–5.1），单包 **armeabi-v7a**，MultiDex。
应用名「阅读」，包名 `io.legado.api19`，与阅读Pro `io.legado.pro` 可共存安装。
与 **阅读Pro 3.0.10.1** 同步发布、同一套版本号；版号按服务端 FnReader **3.0.10 + 0.0.0.1** 对齐。

### 仅同步服务端 3.0.10，无客户端代码改动（与 Pro 同构）

服务端从 `3.0.8` 连发到 `v3.0.10`（tag `255c51c`），本版只同步版号，客户端代码零改动：

- **3.0.9**：TXT 导出改用一次性独立 Python 进程，60 秒硬超时，超过 4000 章回 JS；stdin EPIPE 不再触发 `uncaughtException`
- **3.0.10**：启动脚本依次探测 `nodejs_v22/v24/v20/v26` 与系统 node，逐个 `node -v` 校验，跳过损坏运行时，修复全新安装启动失败
- `docs/APP同步接口.md` blob 仍为 `f4b8c9f957a1d609eb0a231123fadaa196ec5644`，`docs/软总线路由与权重.md`、`docs/AI构建指南.md` 未变；`/api/m/*` 契约未变
- A4 的 6 类回传、软总线、封面/书源/缓存通道沿用 3.0.2.1

### 包信息

| 产物 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_3.0.10.1.apk` | 19 954 512 B（19.03 MB） | `0F2C2928F134924F3775DACBC135EC049042A64EC1188D5057DF6E10AD7ECA92` |

- `aapt2 dump badging`：`versionCode='3001001' versionName='3.0.10.1'`，sdkVersion 19 / target 30，无 `application-debuggable`
- `apksigner verify`：v1 true / v2 true，证书 `CN=FnReader YueDu`
- 包内 `assets/updateLog.md` 首条为 `**2026/11/22（3.0.10.1）**`

### 安装方式

1. 下载 `yuedu_app_3.0.10.1.apk`
2. 覆盖安装即可升级，书源与书架数据保留
3. 进入「我的 → 接入 FnReader 服务器」，填服务端地址与 `X-Api-Token`

### 说明

- 本版无客户端代码改动，未重复做真机回传验证；3.0.2.1 的静态校验与真机遗留项保持不变。
- 应用内「检查更新」指向 GitHub `TZYLXS/yuedu-android4` 的 `releases/latest`；本 Release 发布后即指向 `v3.0.10.1`。
