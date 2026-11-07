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
