# 阅读 · Legado Android 4.4–5.1 移植版

> 交流群：778213624/339426057/1063542188

把 [Legado（阅读）](https://github.com/gedoor/legado) 降到老安卓设备能用的版本：
**minSdk 19 / targetSdk 30，单包覆盖 Android 4.4 – 5.1（API 19–22）**，
保留主体功能（书架、书源、搜索、阅读、TTS、备份、WebDAV），并可直接接飞牛 fnOS 的
[FnReader](https://github.com/TZYLXS/FnReader) 书库。

- 底座：`LegadoTeam/legado` tag `3.23.110211`（commit `56c3301dc441c23aaa75bac973943f7469c202be`）
- 应用名：**阅读**（包名仍为 `io.legado.app`，会和官方版冲突，安装前先卸载官方版）
- 产物：`dist/yuedu_app_<version>.apk`（当前 `yuedu_app_1.9.apk`），v1 + v2 双签，armeabi-v7a，MultiDex
- 用途：老平板 / 老手机当纯阅读器

## 兼容性

| 项 | 值 |
| --- | --- |
| minSdkVersion | 19（Android 4.4） |
| targetSdkVersion | 30 |
| compileSdkVersion | 34 |
| versionCode | 10900（1.9） |
| versionName | `1.9` |
| 签名 | v1 (JAR) + v2，`apksigner verify` 通过 |
| ABI | armeabi-v7a |
| DEX | 5 个（`multiDexEnabled true` + `MultiDexApplication`） |

已构建产物的校验结果：

```
package: name='io.legado.api19' versionCode='10900' versionName='1.9'   # 18.67 MB
sdkVersion:'19'
targetSdkVersion:'30'
application-label:'阅读'
native-code: 'armeabi-v7a'
Verifies / v1: true / v2: true
```

## 接入 FnReader（我的 → FnReader 服务器，①–⑨）

服务端 FnReader 1.11.0 起，面板里九项功能与阅读Pro 1.9 完全一致：

| 项 | 干什么 | 接口 |
| --- | --- | --- |
| ① | 导入书源 | `GET /api/m/sources.json` |
| ② | 全部同步到飞牛书架（新书自动建、重名跳过、封面一起回传） | `POST /api/m/books` + `/api/m/progress/batch` + `/api/m/cover` |
| ③ | 打开飞牛阅读网页 | 内置浏览器（4.0 版地址拼 `/app/legado-web`） |
| ④ | 接入手册 | 内置三份文档 |
| ⑤ | 手机解析待命 | `/api/m/remote/pending` + `/api/m/remote/result` |
| ⑥ | 接入书源桥 | 服务端生成的「飞牛书库」源 |
| ⑦ | 查看软总线 | `GET /api/bus` |
| ⑧ | 上传本机缓存到飞牛（网页离线读正文，§16） | `POST /api/m/cache`，传 `.nb` 原文，服务端转码落 `cache/<书名>/` |
| ⑨ | 书源双向同步（最后一次修改为准，§17） | 首次 `POST /api/m/sources` 合并；之后 `POST/GET /api/m/sources/sync` 增量 |

⑧ 单次请求上限 30 MB；⑨ 首次不推「全量 + since=0」（服务端会当整库快照删源，见 `docs/app回传接受规范.md` §9）。

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
