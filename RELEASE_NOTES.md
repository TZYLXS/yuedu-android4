## 安卓 4.0 版 3.1.4.1（versionCode 3010401）

Legado（阅读）`3.23.110211` 编译，**minSdk 19 / targetSdk 30**（Android 4.4–5.1），单包 **armeabi-v7a**，MultiDex。
应用名「阅读」，包名 `io.legado.api19`，与阅读Pro `io.legado.pro` 可共存安装。
与 **阅读Pro 3.1.4.1** 同步发布、同一套版本号；版号按服务端 FnReader **3.1.4 + 0.0.0.1** 对齐。

### 客户端修复：书源反向同步 404 不再永久跳过（与 Pro 同构）

之前 `FnReaderAutoSync.runSources` 拉取 `/api/m/sources` 遇到 404 会把 `fnReaderNoSourceSync` 永久写成 `"1"`，服务端升级后也不会再试。

- 404 改为 **6 小时时间戳退避**，到期自动重试，成功后清为 `"0"`
- 旧值 `"1"` 按时间戳解析为 1970 年，已过期，老设备升级后自动恢复同步
- 新增 `docs/客户端书源反向同步.md`，记录接口字段、5 分钟同步预期与验收步骤

### 同步服务端 FnReader v3.1.4

服务端 main/tag `v3.1.4` 为 `b230d8e6609e962481e6077bb01356c9528999c4`，相对 `v3.0.10` 共 12 个提交：

- **3.0.11–3.0.16**：空书架保留亚马逊导航；manifest 补 Node/Java 依赖；设置项改名「亚马逊出版物封面」；修复设置页 `ReferenceError`；`GET /api/books` 不再每次 `dedupeShelf`；Python 持久进程池补 `--worker`
- **3.1.0**：Java 目录引擎移出、TOC 规范化改 Python、进程池扩容、搜索 12 路并发
- **3.1.1**：Python 兼容 3.9；软总线回传目录改单遍解析，兼容直接数组、`{chapters:[...]}`、字符串化 JSON、`title|name`、`url|chapterUrl|fileUrl`，上限 20000 章
- **3.1.2**：发布包补 `vendor39/libatomic`
- **3.1.3**：电子书扫描跳过 `cache/covers/tmp`
- **3.1.4**：修复 `linkLocalEbooksToShelf` 在电子书索引为空时提前 `return`，导致失效 ebook 条目未被移除

客户端契约复核：

- `docs/APP同步接口.md` blob 仍为 `f4b8c9f957a1d609eb0a231123fadaa196ec5644`，未变
- `docs/AI构建指南.md` blob 仍为 `2937e4d4adfec9ae8d2e0c36920afac29ddfc97d`，未变
- `docs/软总线路由与权重.md` 仅新增服务端回传解析容错，没有新增客户端必填字段、路由或任务类型
- `/api/m/*` 路径集合新旧均为 30 个，无差异

### 包信息

| 产物 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_3.1.4.1.apk` | 19 954 940 B（19.03 MB） | `AE9E3481E123B5558E125B6E1B4C4F1E0B7A407D1E79BCF2EC1E04F091482AF3` |

- `aapt2 dump badging`：`versionCode='3010401' versionName='3.1.4.1'`，sdkVersion 19 / target 30，无 `application-debuggable`
- `apksigner verify`：v1 true / v2 true，证书 `CN=FnReader YueDu`
- 包内 `assets/updateLog.md` 首条为 `**2026/11/22（3.1.4.1）**`
- MuMu Android 12 覆盖安装，实际显示 3.1.4.1 更新日志并正常启动
- 签名沿用 `keystore/yuedu.jks`，可直接覆盖安装旧版

### 安装方式

1. 下载 `yuedu_app_3.1.4.1.apk`
2. 覆盖安装即可升级，书源与书架数据保留
3. 进入「我的 → 接入 FnReader 服务器」，填服务端地址与 `X-Api-Token`

### 说明

- 本版重点是客户端 404 退避修复；服务端 3.1.4 的契约没有新增客户端必填字段
- A4 的 URL / 菜单兼容差异保持不变，未引入 Pro 专属界面
- 应用内「检查更新」指向 GitHub `TZYLXS/yuedu-android4` 的 `releases/latest`；本 Release 发布后即指向 `v3.1.4.1`
