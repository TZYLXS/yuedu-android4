## 安卓4.0 版 1.4（versionCode 10400）

Legado（阅读）移植到 **minSdk 19 / targetSdk 30**（Android 4.4+），单包 `yuedu_app_1.4.apk`，包名 `io.legado.api19`，可与 Pro 版、官方版共存安装。

### 本版新增：书源桥 + 连接保活

FnReader `1.7.30` 在 docs 里给了「给『阅读』APK 用的书源桥」：服务端把书架、书源、缓存打包成**一个**书源（`bookSourceName = 飞牛书库`），搜索 / 详情 / 目录 / 正文全部由飞牛执行。这一版把入口和保活做进客户端。

**⑨ 接入书源桥**（「FnReader 服务器」弹窗新增项）

- 拉 `GET /api/m/booksource?token=…` 拿服务端实时生成的那份 Legado 书源，走标准 `legado://import/bookSource?src=…` 导入
- 导入后是一条「飞牛书库」，本机不再需要几百条书源规则：
  - 搜索 `GET /api/m/legado/search?token=…&key={{key}}&page={{page}}`，规则 `$.books[*]`
  - 详情 `GET /api/m/legado/book?u=&s=&n=`，不在书架时服务端自动 `upsertBook`（`addedBy:"apk"`）
  - 目录 `GET /api/m/legado/toc?u=`，章节 url 直接指向 `/api/m/content?url=…&index=i`
  - 正文 `GET /api/m/content`，规则 `$.paragraphs[*]`
- 换服务器地址 / 换令牌后，重新点一次 ⑨ 覆盖导入即可

**连接保活**

- 手机解析待命连上后每 **60 秒**心跳一次 `GET /api/m/info`，通知栏显示「**已连接 FnReader 1.7.30　待命中**」，干完活自动回到这个状态
- 心跳同时校验令牌与地址：令牌被重置、服务没起来、换网段，立刻显示「连不上服务器，重试中」
- 掉线 3 秒重试，长轮询 `wait=15000`，派单不漏
- 新增 `RECEIVE_BOOT_COMPLETED` 与 `FnReaderBridgeBootReceiver`：开关开着时**开机自动拉起前台服务**

**顺带修掉**

- 「① 导入书源」统一为标准 `legado://import/bookSource?src=…` 导入链接（原来直接传 http 地址，`OnLineImportActivity` 读不到 `src` 会静默退出）

### 与 1.3 的关系

1.3 的手机解析待命（⑧）原样保留：⑨ 让手机不装规则也能读飞牛，⑧ 让飞牛跑不动的源交给手机跑，两者不冲突。

### 从 1.3 升级

同签名，直接覆盖安装（versionCode `10400` > `10300`）。书架、书源、进度都在。

### 版本规则

安卓 4.0 版（`TZYLXS/yuedu-android4`）与阅读Pro（`TZYLXS/yuedu-pro`）共用版本号，每次迭代 **+0.1**，满 10 进 1。
本版两端同为 **1.4 / 10400**，tag `v1.4`。

### 安装

我的 → FnReader 服务器 → 填地址与令牌 → 测试连接/同步 → ⑨ 接入书源桥（或 ① 导入全部书源）→ 需要手机代跑时再开 ⑧。

### 产物信息

| 项 | 值 |
| --- | --- |
| 文件 | `yuedu_app_1.4.apk` |
| 体积 | 19,551,010 B（18.65 MB） |
| SHA256 | `5E7A2FAB4BFA60A446DF5D3304DBFF256B15B64F977BBB4C14535D4FB8D7D4E3` |

公共参数：包名 `io.legado.api19`、versionName `1.4`、versionCode `10400`、minSdk `19`、targetSdk `30`、
签名证书 `CN=FnReader YueDu`（SHA-256 `81ce1c10476fa400ed962c4a18b54ae4497581320b1d940d896d62b6c609ef0f`），v1+v2 双签。

### 验证情况（MuMu，Android 12，对着线上 1.7.30）

- ⑨ 点下去弹「导入书源」列表：「飞牛书库 新增」，确认导入成功
- `dumpsys notification` 通知正文「已连接 FnReader 1.7.30　待命中」，服务 `isForeground=true foregroundId=108`
- 服务端接口侧同一份书源：`/api/m/legado/search` → `/book`（自动入架）→ `/toc`（章节 url 指向 `/api/m/content`）闭环通过

### 已知限制

- 书源桥依赖飞牛在线；`/api/m/content` 对个别源服务端自身会卡住，本章会一直转圈
- API19 机型系统较老，前台服务被某些 ROM 清理后仍需重进一次「FnReader 服务器」才会自动拉起（开机自启已在 manifest 里备好）

### 关于旧版本

Release 列表只保留当前版本，`v1.4` 之前的版本在下次触发 prune 后会清理。
