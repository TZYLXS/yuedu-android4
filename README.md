# 阅读 · Legado Android 4.4–5.1 移植版

> 交流群：778213624/339426057/1063542188

把 [Legado（阅读）](https://github.com/gedoor/legado) 降到老安卓设备能用的版本：
**minSdk 19 / targetSdk 30，单包覆盖 Android 4.4 – 5.1（API 19–22）**，
保留主体功能（书架、书源、搜索、阅读、TTS、备份、WebDAV），并可直接接飞牛 fnOS 的
[FnReader](https://github.com/TZYLXS/FnReader) 书库。

- 底座：`LegadoTeam/legado` tag `3.23.110211`（commit `56c3301dc441c23aaa75bac973943f7469c202be`）
- 应用名：**阅读**（包名仍为 `io.legado.app`，会和官方版冲突，安装前先卸载官方版）
- 产物：`dist/yuedu_app_<version>.apk`（当前 `yuedu_app_2.0.2.apk`），v1 + v2 双签，armeabi-v7a，MultiDex
- 用途：老平板 / 老手机当纯阅读器

## 兼容性

| 项 | 值 |
| --- | --- |
| minSdkVersion | 19（Android 4.4） |
| targetSdkVersion | 30 |
| compileSdkVersion | 34 |
| versionCode | 20200（2.0.2） |
| versionName | `2.0.2` |
| 签名 | v1 (JAR) + v2，`apksigner verify` 通过 |
| ABI | armeabi-v7a |
| DEX | 5 个（`multiDexEnabled true` + `MultiDexApplication`） |

已构建产物的校验结果：

```
package: name='io.legado.api19' versionCode='20200' versionName='2.0.2'   # 见下文本版体积
sdkVersion:'19'
targetSdkVersion:'30'
application-label:'阅读'
native-code: 'armeabi-v7a'
Verifies / v1: true / v2: true
```

## 2.0 主线：书架去重、删除墓碑、软总线两条队列

- **书架同名去重**：同名（不同书源）只留一本，优先保留本地已有 `.nb` 缓存的那本，别的副本进度先并过去再删
- **删除墓碑**：手动删掉的书不会被自动同步补回来，服务端再推同一本直接跳过
- **⑧⑨ 转后台**：缓存上传与书源双向同步连上服务端就每 5 分钟自动跑一轮，面板里不再有这两个按钮
- **软总线两条队列**（2.0.2）：`bookinfo`（简介/封面补全）单并发独立跑，search/toc/content/rank 走检索队列 2 并发
- **`bookinfo` 超时 9 秒**（2.0.2）：对齐服务端 `docs/APP同步接口.md` §18.2 的「超时 10 秒」
- 启动页换成「飞牛阅读」竖版毛笔字；版本号与阅读Pro 同步（2.0.2 / 20200）

## lean LEDE 更新日志与许愿（1.11 引入，仍在）

`coolsnowwolf/lede` 没有独立 CHANGELOG，它的「更新日志」就是 master 的提交历史，本版把这条源内置成订阅：

- 订阅 `https://github.com/coolsnowwolf/lede/commits/master.atom`（分组「固件」），
  「订阅」里直接看最近提交；「我的 → lean LEDE 更新日志」是快捷入口
- 内置 RSS 解析器补 Atom 支持（`<entry>` / `<link href>` / `<summary>` / `<updated>` ISO 时间），
  否则这种源解析出 0 条
- 「我的 → 给 lean 许愿（GitHub）」：输入框 + 提交，登录后一键 fork lede、建分支、写
  `wishlist/<时间>-<账号>.md` 并向上游开 PR；也可「改发 Issue」
- token 只存本机 SharedPreferences，不进备份；classic token 勾 `public_repo`，
  细粒度 token 需 `Contents` + `Pull requests` 读写，自动 fork 还要 `Repository creation`

## 接入 FnReader（我的 → FnReader 服务器，①–⑤）

服务端 FnReader 2.0.x 起，面板五项与阅读Pro 一致；缓存上传、书源同步已改自动，不再占按钮：

| 项 | 干什么 | 接口 |
| --- | --- | --- |
| ① | 导入书源 | `GET /api/m/sources.json` |
| ② | 全部同步到飞牛书架（新书自动建、重名跳过、封面一起回传） | `POST /api/m/books` + `/api/m/progress/batch` + `/api/m/cover` |
| ③ | 打开飞牛阅读网页 | 内置浏览器（4.0 版地址拼 `/app/legado-web`） |
| ④ | 接入手册 | 内置三份文档 |
| ⑤ | 手机解析待命 | `/api/m/remote/pending` + `/api/m/remote/result` |

后台自动动作（连上即开启，每 5 分钟一轮）：

- **缓存上传**：`POST /api/m/cache`，传 `.nb` 原文，服务端转码落 `cache/<书名>/`；单次上限 30 MB
- **书源双向同步**：首次 `POST /api/m/sources` 合并，之后按 `lastUpdateTime` 增量；不推「全量 + since=0」
  （服务端会当整库快照删源，见 `docs/app回传接受规范.md` §9）

软总线任务类型与超时（手机侧执行）：

| 类型 | 干什么 | 超时 |
| --- | --- | --- |
| `search` | 本机书源搜索，限流时用本机书架同源同名兜底 | 18 s |
| `toc` | 本机书源抓目录 | 18 s |
| `content` | 本机书源抓正文 | 23 s |
| `rank` | 本机书源抓榜单 | 23 s |
| `bookinfo` | 详情规则补简介/封面/作者/分类/字数/最新章节/章数 | 9 s（服务端等 10 s） |

## 与上游的差异

改动都是「降级适配」，业务代码零改动。逐条说明见 [docs/API19移植说明.md](docs/API19移植说明.md)，概览：

- **构建**：`minSdk 19`、`targetSdk 30`、去掉 `versionCode` 对 git 的依赖、固定 v1+v2 签名、限定 32 位 ABI、lint 不阻断构建、`modules/*` 的 minSdk 同步下调
- **裁剪**：移除 Cronet（`lib/cronet/` 整包 + `cronet.json` + `download.gradle`）、移除 Firebase
- **网络**：新增 `Tls12SocketFactory`（4.4 支持 TLSv1.2 但默认不启用，OkHttp 3.x 不会自动打开），`SSLContext` 由 `SSL` 改 `TLS`，Cronet 缺失自动回退 OkHttp
- **框架 API**：TTS 四参 `speak` → `speakCompat`；`ACTION_OPEN_DOCUMENT_TREE` 在 4.4 回退内置文件夹选择器；`android.system.Os`（epub 的 PFD 读取）加 `FileInputStream` 回退
- **清单**：补 `extractNativeLibs` / `usesCleartextTraffic`；对 5 个「清单写 21、实际可用」的三方库做 `tools:overrideLibrary`

## 构建

需要 JDK 17、Android SDK（platform 34 + build-tools 34.0.0）、Gradle 8.3（wrapper 自动下载）。

```bash
# 1) 指向本机 SDK
echo "sdk.dir=/path/to/android-sdk" > local.properties

# 2) 生成自己的签名（仓库不含私钥）
keytool -genkeypair -keystore keystore/yuedu.jks -alias yuedu \
  -keyalg RSA -keysize 2048 -validity 36500 \
  -storepass yuedu2026 -keypass yuedu2026 \
  -dname "CN=FnReader YueDu, OU=Reader, O=FnReader, L=CN, ST=CN, C=CN" -storetype JKS

# 3) 打包
./gradlew :app:assembleAppRelease
# 产物：app/build/outputs/apk/app/release/yuedu_app_<version>.apk
```

签名密码写在 `app/build.gradle` 的 `signingConfigs.yuedu` 里（默认 `yuedu2026`）。

国内网络拉不到 `services.gradle.org` 时，把 `gradle/wrapper/gradle-wrapper.properties` 的
`distributionUrl` 换成 `https\://mirrors.cloud.tencent.com/gradle/gradle-8.3-bin.zip` 即可。

## 装上以后

1. 安装：`adb install -r dist/yuedu_app_*.apk`
2. 导入书源：我的 → 书源管理 → 网络导入（或直接打开 `legado://import/...` 链接）
3. 接飞牛书库：见 [docs/飞牛书库接入.md](docs/飞牛书库接入.md)

## 已知限制（4.4 上可能用不了的功能）

下面这些功能的三方库最低要求 API 21，已用 `tools:overrideLibrary` 放行，
**不影响启动和阅读**，只有真的用到时才会失败：

| 功能 | 原因 |
| --- | --- |
| 用 ExoPlayer 播放音频 / 朗读音频文件 | media3 1.1.1 要求 21+ |
| 扫二维码导入书源 | zxing-lite / camera-scan 要求 21+ |
| zip / epub 压缩包解析（libarchive 路径） | `me.zhanghai.android.libarchive` 要求 21+ |

普通书源阅读（网页 / JSON 书源）、单文件 TXT / EPUB、备份恢复、WebDAV、系统 TTS 朗读均可用。

## 免责

本项目只为让旧设备继续读书，不内置、不捆绑任何书源；书源由使用者自行导入，内容与版权责任自负。
上游项目 [gedoor/legado](https://github.com/gedoor/legado)，遵循其 GPL-3.0 许可；上游 README 见
[docs/上游README.md](docs/上游README.md)。
