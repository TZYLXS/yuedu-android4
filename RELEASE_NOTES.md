## 阅读（安卓 4.0 版）1.9（versionCode 10900）

minSdk 19 移植版，包名 `io.legado.api19`，可与阅读Pro、官方版共存安装。
与 **阅读Pro 1.9** 同步发布、同一套版本号，同一份 FnReader 适配实现。

### 本版主线：跟进文档 §15，软总线新增 `rank` 代抓榜单

服务端 FnReader 已到 **1.11.2**。`docs/APP同步接口.md` 这一轮新增：

| 文档章节 | 变更 | 对客户端的影响 |
| --- | --- | --- |
| §15 每日推荐（`GET /api/daily`） | 榜单按「起点 → 纵横 → 17K」依次抓，6 小时缓存；起点有反爬（202 挑战页）时先让软总线上的在线手机抓（任务类型 `rank`） | 需要客户端支持 `rank` 任务 |
| §16 缓存同步 / §17 书源双向同步 | 措辞细化（32 MB 上限、重名建 `(2)`、LWW 与 `pull` / `pullDeleted`） | 1.8 已按此实现，本轮复核协议无差异，未改代码 |

`docs/更新日志.md` 另外补了 1.10.0 – 1.11.2 六段版本记录，只做记录，无接口变化。

### 代码改动（与阅读Pro 同构）

- `model/FnReaderRemote.kt`
  - `execute()` 增加 `"rank"` 分支（1.8 及以前收到 `rank` 直接回 `ok:false`）
  - 新增 `rank(payload)`：浏览器 UA 抓 `payload.url`，正文不足 10 KB 视为反爬挑战页直接报错；Jsoup 按三套结构解析
    （起点 `.book-img-text li` / `.rank-list li`、纵横 `.zh-modules-rank-book`、17K `a[href*="/book/"]` + `/book/<数字>.html`），
    按 url 去重、补 `rank` 序号，回传 `{source,url,books:[{rank,name,url,author,cover,score}]}`
  - 新增 `httpGetText()` 与 `String.toAbsolute()`
- `service/FnReaderBridgeService.kt`：`timeoutOf("rank")` = 23 秒
- `ui/config/FnReaderConfigDialog.kt`：菜单 ⑨ 文案补 `pull` / `pullDeleted` 说明
- 老平台差异全部保留：书源导入用 `openWebShelf` 拼 `/app/legado-web`、`rawServer` 判 `/app/`、对话框 `negativeButton` 走 `R.string.cancel`

回传字段、接口路径、鉴权方式都没有变，1.8 的推书 / 进度 / 封面 / 缓存 / 书源同步照旧。

### 实测结果

- 编译：`BUILD SUCCESSFUL in 1m 29s`
- 模拟器（MuMu，Android 12）装 1.9 包：首启更新日志首条为 **2026/11/09（1.9）**；「我的 → FnReader 服务器」九项菜单齐全
- 软总线：与阅读Pro 同一份代码，`rank` 领取 / 抓取 / 反爬判定 / `ok:false` 回传路径已在 Pro 端实测（起点 209 字节挑战页、纵横 HTTP 500，两种失败都回得干净）
- 未跑通的路径：成功解析 + 成功回传本轮没在真机复现（服务端 `daily.json` 仍在 6 小时缓存期内，未触发 `rank`）；解析选择器与回传结构按服务端 `Tre()` / `Dre()` / `yre()` 对齐

### 安装

- 包名 `io.legado.api19`，应用名「阅读」，与官方版 / Pro 版包名不同，可并存
- 只带 `armeabi-v7a` 原生库，Android 4.4 – 5.1（API 19–22）直接安装
- 签名与 1.0 – 1.8 一致（证书 SHA-256 `81ce1c10…09ef0f`），覆盖升级不丢数据

### 包信息

| APK | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_1.9.apk` | 18.67 MB（19 578 468 B） | `B16EDD12C40C98464C359E9D86E50ADF500146E8E641EBF1B3EE132BC5AB492B` |

校验：`aapt2 dump badging` → `io.legado.api19` / `versionCode 10900` / `versionName 1.9` / `sdkVersion 19` / `targetSdkVersion 30`；
`apksigner verify` v1 + v2 通过，证书 SHA-256 `81ce1c10476fa400ed962c4a18b54ae4497581320b1d940d896d62b6c609ef0f`。

### 升级方式

装了 1.7 / 1.8 的直接覆盖安装；服务端地址、令牌、书源、书架、进度都保留，无需重新导入书源或重建缓存。
