# API 19 移植说明

底座：`LegadoTeam/legado` tag `3.23.110211`（commit `56c3301dc441c23aaa75bac973943f7469c202be`）。
上游该 tag 是 `minSdk 21 / targetSdk 33 / AGP 8.1.2 / Kotlin 1.9.20 / Gradle 8.3`，本身不能直接降到 19，
下面 21 处改动就是全部差异，**业务逻辑代码没有动**。

## 一、构建脚本

| 文件 | 改动 | 原因 |
| --- | --- | --- |
| `app/build.gradle` | 去掉 `com.google.gms.google-services` 插件 | Firebase 移除后无 `google-services.json` 处理 |
| 同上 | 去掉 `apply from: 'download.gradle'` | 该脚本只在打包时下载 Cronet（见第三节） |
| 同上 | `def name = "legado"` → `def appName = "yuedu"`，`version = "1.0." + releaseTime()` | 产物命名 `yuedu_app_*`，版本号前缀区分移植版 |
| 同上 | 删除 `gitCommits = 'git rev-list HEAD --count'` | CI / 无 `.git` 环境下会直接构建失败 |
| 同上 | `versionCode 10000 + gitCommits` → `19001` | 同上；19001 也标明了 minSdk |
| 同上 | `signingConfigs` 固定为 `yuedu`（`keystore/yuedu.jks`），`v1SigningEnabled/v2SigningEnabled = true`，关闭 v3/v4 | **4.4 只认 v1 JAR 签名**，缺 v1 装不上 |
| 同上 | `minSdk 21` → `19`，`targetSdk 33` → `30` | 目标设备 4.4–5.1 |
| 同上 | `multiDexEnabled true` + `abiFilters 'armeabi-v7a'` | 老设备是 armv7；5 万方法数超 64K |
| 同上 | `applicationIdSuffix '.release' / '.debug'` 删除 | 老设备的包名要与其他机器上安装的一致 |
| 同上 | `minifyEnabled/shrinkResources` → `false` | 4.4 上混淆 + 资源压缩会带来额外的运行期风险，先关掉保稳定 |
| 同上 | `lint { abortOnError false; checkReleaseBuilds false }` | NewApi 只做报告，逐条按报告修 |
| 同上 | `archivesBaseName` / `outputFileName` 改用 `appName` | 与上面的重命名对齐 |
| 同上 | 注释掉 `cronetlib` fileTree、`protobuf-javalite` | Cronet 移除 |
| 同上 | 注释掉 `firebase-bom / analytics-ktx / perf-ktx` | Firebase 要求 API 21+，老设备无 GMS |
| 同上 | 新增 `androidx.multidex:multidex:2.0.1` | 4.4 没有原生 multidex |
| `modules/book/build.gradle` | `minSdk 21` → `19`，`targetSdk 33` → `30` | 否则清单合并直接报 `minSdkVersion 19 cannot be smaller than 21` |
| `modules/rhino1.7.3/build.gradle` | 同上 | 同上 |

## 二、源码

| 文件 | 改动 | 原因 |
| --- | --- | --- |
| `App.kt` | `Application` → `MultiDexApplication` | 4.4 上先装 multidex 再启动 |
| 删除 `app/src/main/java/io/legado/app/lib/cronet/`（11 个文件）与 `assets/cronet.json` | 整包移除 | Cronet 152 需要 API21+；`help/http/Cronet.kt` 是 `Class.forName` 反射 + `runCatching`，类不存在自然回退 OkHttp，无需改代码 |
| 新增 `help/http/Tls12SocketFactory.kt` | 包装 `SSLSocketFactory`，`SDK_INT < 21` 时把 `TLSv1.2` 补进 `enabledProtocols` | **4.4 支持 TLSv1.2 但默认不在启用列表**，OkHttp 4.x 不会自动打开，不处理则大部分 https 书源握手失败 |
| `help/http/HttpHelper.kt` | `.sslSocketFactory(Tls12SocketFactory.wrap(...), ...)` | 挂在 OkHttp 上 |
| `help/http/SSLHelper.kt` | `SSLContext.getInstance("SSL")` → `"TLS"` | 4.4 上 `SSL` 实例默认不带 TLS1.2 |
| `help/TTS.kt` | 新增 `TextToSpeech.speakCompat()`：21+ 走四参 `speak(text, mode, null, id)`，19 走三参 `speak(String, mode, HashMap)` + `KEY_PARAM_UTTERANCE_ID`；两处调用改成 `speakCompat` | 四参 `speak` 是 API21 新增，4.4 上会 `NoSuchMethodError` |
| `ui/file/HandleFileActivity.kt` | `HandleFileContract.DIR` 分支：`SDK_INT < 21` 时直接走内置 `FilePickerDialog`，否则用 SAF `selectDocTree.launch()` | `ACTION_OPEN_DOCUMENT_TREE` 是 API21 才有，4.4 上无法选目录 |
| `modules/book/.../PfdHelper.java` | `android.system.Os`（lseek/fstat/read）拆成 `@RequiresApi(21)` 方法，19 走 `FileInputStream.getChannel()`（position/size/read） | `android.system.Os` 21 才存在；这段是 epub 用 PFD 读取的路径 |
| `res/values/strings.xml` | `app_name` → `阅读` | 应用显示名 |
| `app/src/main/AndroidManifest.xml` | `<application>` 加 `extractNativeLibs="true"` / `usesCleartextTraffic="true"` | 4.4 不会自动解压 so；书源站点大量 http，需允许明文 |
| 同上 | 新增 `<uses-sdk tools:overrideLibrary="com.king.zxing,com.king.camera.scan,com.king.view.viewfinderview,androidx.media3.datasource.okhttp,me.zhanghai.android.libarchive" />` | 这 5 个库清单写 `minSdk 21`，不 override 清单合并过不了；它们只在扫码 / 音频 / 压缩包功能里用到，不影响启动与阅读 |

## 三、验证结果（本机构建）

```
BUILD SUCCESSFUL
app/build/outputs/apk/app/release/yuedu_app_1.0.26.110803.apk   18.6 MB

aapt2 dump badging:
  package: name='io.legado.app' versionCode='19001' versionName='1.0.26.110803'
  sdkVersion:'19'  targetSdkVersion:'30'
  application-label:'阅读'
  native-code: 'armeabi-v7a'

apksigner verify --verbose:
  Verifies
  Verified using v1 scheme (JAR signing): true
  Verified using v2 scheme (APK Signature Scheme v2): true

dex: classes.dex / classes2.dex / classes3.dex / classes4.dex / classes5.dex（5 个）
so : lib/armeabi-v7a/{libarchive-jni, libimage_processing_util_jni, librenderscript-toolkit}.so

lint -appRelease:
  [NewApi] 从 15 条（全部集中在 PfdHelper）修到 0 条
```

> 说明：`lintAppRelease` 在修完 PfdHelper 后复跑时会撞到 lint 自身的一个
> `InvalidProtocolBufferException`（Kotlin metadata 解析 bug，与代码无关），
> 因此最终以 `:modules:book:lintRelease` 的报告为准（`[NewApi]` 0 条）。

## 四、还没做的

- 没有真机复测（手上设备 adb 掉线）。`scripts/verify_device.bat` 可直接在接上 USB 的机器上跑安装 + 启动 + 抓日志。
- API 21 主题属性（`?attr/colorSurface` 之类）没有单独挪 `values-v21`：本 tag 的资源里不存在这类属性，无需处理。
- 4.4 上 `ExoPlayer` 音频、扫码、libarchive 三类功能不可用（见 README「已知限制」）。

## 2.0.x（versionCode 20200 / 2.0.2）：书架去重、删除墓碑、软总线拆两条队列

与阅读Pro 2.0.2 同构，4.0 版自有差异原样保留（`openWebShelf` 拼 `/app/legado-web`、`rawServer` 判 `/app/`、
`negativeButton(requireContext().getString(R.string.cancel))`、`serverBase` 的 path 分支）。

- **2.0.0 – 2.0.1**：书架同名去重（优先留本地有 `.nb` 的那本）、删除墓碑（本地删掉的书不被服务端推回）、
  ⑧⑨ 缓存上传与书源同步转后台自动（每 5 分钟一轮）、启动页换「飞牛阅读」竖版毛笔字
- **2.0.2**：跟进 `docs/APP同步接口.md` §18「简介补全」（`bookinfo`）——`bookinfo` 走独立 `infoQueue` 单并发，
  `search` / `toc` / `content` / `rank` 走 `searchQueue` 2 并发；`bookinfo` 超时 23 s → **9 s**
  （对齐 §18.2「超时 10 秒」）；`FnReaderRemote.bookInfo()` 回传字段与 §18.1 逐项一致
- 实测（MuMu Android 12）：首启日志首条 `2026/11/10（2.0.2）`，面板 ①–⑤ 齐全、无 ⑧⑨ 残留

## 1.11（versionCode 20200）：lean LEDE 更新日志订阅 + 给 lean 许愿（GitHub）

与阅读Pro 1.11 同构的两项新能力（4.0 版自有差异原样保留：`openWebShelf` 拼 `/app/legado-web`、
`rawServer` 判 `/app/`、`negativeButton` 用 `getString(R.string.cancel)`、`serverBase` 的 path 分支）：

- **订阅**：`coolsnowwolf/lede` 没有 CHANGELOG，更新日志就是 master 提交历史；
  内置 `https://github.com/coolsnowwolf/lede/commits/master.atom`（分组「固件」、`customOrder=6`），
  `DefaultData.ensureBuiltinRssSources()` 只补缺失、不动用户增删
- **Atom 解析**：`RssParserDefault.kt` 补 `<entry>` 条目态、`<link href>`（`rel="alternate"`）、
  `<summary>`/`<content>`、`<updated>`/`<published>` 的 ISO 8601 → 本地 `yyyy-MM-dd HH:mm`、标题 `oneLine()`
- **许愿**：`model/GithubWish.kt` 四步（`/user` → fork（缺则建并轮询）→ 建分支写
  `wishlist/<时间>-<账号>.md` → `/pulls`），兜底 `/issues`；
  UI 是 `ui/config/GithubWishDialog.kt` + `dialog_github_wish.xml`，入口在「我的」页
- token 只存本机，不进备份；classic 勾 `public_repo`，细粒度需 `Contents` + `Pull requests` 读写，
  自动 fork 还要 `Repository creation`

## 1.10（versionCode 11000）：跟进服务端 1.11.3，无代码改动

- `docs/更新日志.md` 新增 **1.11.3（2026-11-10）**：服务端补 `applyReplaceRules`（正文净化）、`formatJs` 结果生效、脚本/测试目录名统一、日志写入 `docs`、README 版本自动同步，全是服务端 legado-py 侧修复
- `docs/APP同步接口.md` 本轮未变（27 458 B / SHA256 `7b46d6a625031f31…`），接口路径、回传字段、鉴权、软总线任务类型与超时都没动
- 因此 4.0 版与 1.9 代码完全一致，只改版本号（1.10 / 11000）与文档；4.0 自有差异（`openWebShelf` 拼 `/app/legado-web`、`rawServer` 判 `/app/`、`negativeButton` 用 `getString(R.string.cancel)`、`serverBase` 的 path 分支）原样保留
- 实测（MuMu + FnReader 1.11.3）：首启日志首条 `2026/11/10（1.10）`，面板九项齐全，测试连接回显 1.11.3

## 1.9（versionCode 10900）：软总线支持 rank 代抓榜单

- 跟进 `docs/APP同步接口.md` §15 每日推荐（`GET /api/daily`）与 `docs/更新日志.md`（服务端 1.11.2）
- `model/FnReaderRemote.kt` 增加 `"rank"` 任务：抓 `payload.url` 榜单页，用 Jsoup 按起点 / 纵横 / 17K 三套选择器解析，
  回传 `{source,url,books:[…]}`；反爬页或 0 本回 `ok:false`，服务端自动降级
- `service/FnReaderBridgeService.kt` 的 `timeoutOf("rank")` = 23 秒
- §16 缓存同步、§17 书源双向同步（1.8 已实现）本版复核协议无差异，未改代码
- 版本号 1.9 / 10900，minSdk 19 不变；与阅读Pro 1.9 同一份实现

## 1.8（versionCode 10800）：origin / 缓存原文 / 书源时间戳修复

- 推书 `origin` 填书源真实 `bookSourceUrl`、`originName` 填源名；缺 origin 时按 `bookUrl` 的 host 匹配同宿主书源，再退按源名匹配，都不中才留空
- 搜索页 / 临时跳转不算详情页，跳过不推
- ⑧ 缓存补 `layout:"legado"` + `chapters[].file` + `chapters[].raw`（`.nb` 原文）+ `origin`
- 封面本机没有、或服务端封面仍是源站外链时就现下补推（Referer 先不带再退图片域名 / 书源地址，压过 gzip 的解一层）转 base64 一起推
- ⑨ 改/删书源都带 `lastUpdateTime`，本机删源推 `deleted`，服务端更进的版本 `pull` 回本地覆盖
- 版本号 1.8 / 10800，minSdk 19 不变；与阅读Pro 1.8 同一份实现

## 1.7（versionCode 10700）：与阅读Pro 同步的 FnReader 1.11.0 适配

4.0 版与 Pro 同构改造，差异只在原有分支上：

- 新增菜单 ⑧ 上传本机缓存到飞牛（`POST /api/m/cache`，传 `.nb` 原文，单本 > 30 MB 跳过）
- 新增菜单 ⑨ 书源双向同步（首次 `POST /api/m/sources` 合并，之后 `POST/GET /api/m/sources/sync` 按 `lastUpdateTime` 增量）
- `request()` 加可选读超时（缓存 60s / 书源合并 180s / 增量 120s）
- 4.0 版自有差异保持不动：`openWebShelf` 拼 `/app/legado-web`、`rawServer` 判 `/app/`、
  `negativeButton` 用 `requireContext().getString(R.string.cancel)`、`serverBase` 的 path 分支
