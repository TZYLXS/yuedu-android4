## 安卓 4.0 版 3.0.2.1（versionCode 3000201）

Legado（阅读）`3.23.110211` 编译，**minSdk 19 / targetSdk 30**（Android 4.4–5.1），单包 **armeabi-v7a**，MultiDex。
应用名「阅读」，包名 `io.legado.api19`，与阅读Pro `io.legado.pro` 可共存安装。
与 **阅读Pro 3.0.2.1** 同步发布、同一套版本号；版号按服务端 FnReader **3.0.2 + 0.0.0.1** 对齐。

### 按 3.0.x 客户端适配清单补齐自动回传（与 Pro 同构）

- **加书/改书自动回传**：`Book.save()` 后 2 秒防抖 `POST /api/m/books`；未配置服务器时内部跳过。
- **阅读进度自动回传**：`ReadBook.saveRead()` 后 3 秒防抖 `POST /api/m/progress`；服务层每轮按 `fnReaderProgressSyncedMs` 水位批量 `POST /api/m/progress/batch`。
- **独立封面上传**：新增 `POST /api/m/cover`，`{bookUrl,mime,data:"data:image/...;base64,..."}`，单张上限 8 MB；404 时自动停用。
- **书源双向同步补齐删除**：自动同步先读本机快照，本机删除的书源写入 `deleted` 数组；`lastUpdateTime<=0` 自动补当前时间戳。
- **自动补书**：本机有、服务端没有的书按书名归一化去重后回传，同名不同源跳过。
- **应用内更新兼容四段版本号**：`AppUpdateGitHub.versionCodeOf()` 支持四段，`v3.0.2.1` 正确换算为 `3000201`。

### 包信息

| 产物 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_3.0.2.1.apk` | 19 954 016 B（19.03 MB） | `DCEC0C2B0D45E8D779B4593802AE4C1A0B77330D3B7DB643BDD087483C209534` |

- `aapt2 dump badging`：`versionCode='3000201' versionName='3.0.2.1'`，`sdkVersion 19` / `targetSdkVersion 30`，无 `application-debuggable`
- `apksigner verify`：v1 true / v2 true，签名证书 `CN=FnReader YueDu`
- 包内 `assets/updateLog.md` 首条为 `**2026/11/22（3.0.2.1）**`

### 安装方式

1. 下载 `yuedu_app_3.0.2.1.apk`
2. 覆盖安装即可升级，书源与书架数据保留
3. 进入「我的 → 接入 FnReader 服务器」，填服务端地址与 `X-Api-Token`

### 说明

- 自动回传依赖「接入 FnReader 服务器」已开启且地址 / token 有效；未开启时钩子直接返回。
- 首次启用后进度水位从 0 开始，会把已有阅读记录补推一遍，之后只推增量。
- 封面接口只处理本机文件封面（PNG/JPEG/GIF/WebP），在线 `http(s)` 封面仍随 books 的 `coverUrl` 下发。
