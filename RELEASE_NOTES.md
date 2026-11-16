## 安卓 4.0 版 2.6.10.1（versionCode 2061001）

Legado 移植版，**minSdk 19 / targetSdk 30**（Android 4.4–5.1），单包 `armeabi-v7a`、多 Dex，
应用名「阅读」，包名 `io.legado.api19`（与官方版 `io.legado.app` 冲突，安装前先卸官方版）。
与 **阅读Pro（`io.legado.pro`）2.6.10.1** 同步发布、同一套版本号；版号基线取自服务端 `legado-web` **v2.6.10**，
本版为其上的 `0.0.0.1` 小更新。

### 本版做了什么

**仅跟进文档，无代码改动**。服务端 v2.6.9、v2.6.10 只改网页设置页的地址展示，没有一条触及 App 契约；
本版代码与 2.6.8.1（即 2.4.4.1 同一份）逐字一致，4.0 版自有差异原样保留
（`openWebShelf` 拼 `/app/legado-web`、`rawServer` 判 `/app/`、
`negativeButton(requireContext().getString(R.string.cancel))`、`serverBase` 的 path 分支）。

- `docs/APP同步接口.md` **零改动**：33802 B（SHA256 `e04f34664bcd5e5bd02abb5342698d2e1219d0b849d76003211c589e223f9ea1`）
- `docs/更新日志.md` 未变，`docs/更新日志.txt` 新增 2.6.9 / 2.6.10 两段
- **2.6.9**：网页「其它地址」IPv4 与 IPv6 分行显示，复制按钮同时复制两类
- **2.6.10**：IPv6 单独一行 + 独立复制按钮，IPv4 复制只复制 IPv4，并过滤 `[::1]` 回环
- 改动面只有 `web/app.js`（+26/−4）、`index.html`、`manifest`；`server.js` 与 `/api/m/*` 未动

### 契约复核（逐项核对，全部未变）

| 维度 | 结论 |
| --- | --- |
| 回传字段 | `search` / `toc` / `content` / `rank` / `bookinfo` 上行字段与 2.6.8.1 一致 |
| 接口路径 | `/api/m/*` 未增删改（`/api/m/info` 的 `ipv6Lans` 语义未变） |
| 鉴权 | `X-Api-Token`；传书 `X-Upload-Token` 每次现取，401 `upload-token` 重取一次 |
| 任务类型与超时 | `search` 18 s / `toc` 18 s / `content` 23 s / `rank` 23 s / `bookinfo` 9 s |
| 错误码 | 未变 |
| 封面 / 书源通道 | `/api/m/cover`、`/api/m/sources`、`/api/m/sources/sync`、`/api/m/cache` 未变 |

### 实测结果（MuMu Android 12）

| 环节 | 结果 |
| --- | --- |
| 文档比对 | 远端 `1b0bb3f → 4d9d373`（含 `16ff8a1 v2.6.9`）；`APP同步接口.md` / `更新日志.md` sha 未变，只有 `更新日志.txt` 变化（`e16ced11…`） |
| 源码 diff | `更新日志.txt` +10、`web/app.js` +26/−4、`index.html` 6、`manifest` 2 |
| 编译 | `:app:assembleAppRelease` `BUILD SUCCESSFUL in 1m 49s`（exit 0） |
| badging | `io.legado.api19`、`versionCode 2061001`、`versionName 2.6.10.1`、`armeabi-v7a`、无 `application-debuggable` |
| 应用内日志 | zipfile 解包读 `assets/updateLog.md`，首条 `2026/11/17（2.6.10.1）`（共 53 条） |
| MuMu 冒烟 | 覆盖安装启动正常，首启更新日志首条即本版；菜单 ①–⑧ 与 `buildMethods()` 一致 |

### 产物

| 文件 | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_2.6.10.1.apk` | 19 935 347 B（19.01 MB） | `bd228755d3fe7a58aefc675993ef049eb42e1aba749ebb9d61983b31f19e462a` |

### 安装

- 先卸载官方「阅读」（包名 `io.legado.app` 与官方版相同），再装本包
- v1 + v2 双签，可直接覆盖 2.6.8.1 升级，数据不丢
- 仅 `armeabi-v7a`，64 位设备也能跑（系统带 32 位兼容层即可）

### 已知限制

- 传书只做「本机 → 飞牛」，失败重来点 ⑧ 再选一次；不做断点续传。
- 服务端 `ebook-parse` 的 toc-only 分支仍休眠（payload 不带 `tocOnly`）。
- 服务端「离线缓存」「更新提醒」只在网页端，App 无对应入口。
- 面板填 IPv6 字面量地址（`http://[240e:…]:1122`）会弹一次「请用 https」的既有提醒，只提示不拦截。
