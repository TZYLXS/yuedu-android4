## 安卓 4.0 版 3.2.0.1（versionCode 3200001）

Legado（阅读）`3.23.110211` 编译，**minSdk 19 / targetSdk 30**（Android 4.4–5.1），单包 **armeabi-v7a**，MultiDex。
应用名「阅读」，包名 `io.legado.api19`，与阅读Pro `io.legado.pro` 可共存安装。
与 **阅读Pro 3.2.0.1** 同步发布、同一套版本号；版号按服务端 FnReader **3.2.0 + 0.0.0.1** 对齐。

### 同步服务端 FnReader v3.2.0

服务端 main/tag `v3.2.0` 为 `43920411e3af2cd3c0b8529ae467fb85dce5298d`：

- **多客户端 / 多书源缓存合并**：同书按 `workId` 归并，章节取并集；同章不同正文保留多版本，8 源并发不分裂
- **「书籍」页与本地 `.nb` 缓存清单**：每本显示缓存章数、同步章数、来源数、变体数
- **设置页重排**：IPv6 地址独立成行，书源导入链接改明码并修复缺斜杠
- **引擎与导出优化**：增量哈希、导出流式写、Python 池扩到核数，修复阅读接入详情 404

`docs/APP同步接口.md` 仅新增 0.1.25+ 多源缓存合并说明，`/api/m/*` 无新增客户端必填字段；`docs/软总线路由与权重.md`、`docs/AI构建指南.md` 未变化。

### 客户端本版

- FnReader 同步通知分道、新增同步记录页、空书源过滤（T1–T5）
- FnReader 配置菜单改为分组列表：服务器地址/访问令牌整行点击弹输入框；手机解析待命、TOC 目录先行、TRE 代理解析改为滑动开关；每个动作独立一行，同步书架/立即同步/测试连接/传书点击先弹进度框
- **A4 崩溃修复**：E-Ink 优化不再调用 `isScrollbarFadingEnabled = false`，改为直接关闭垂直/水平滚动条，避免无 drawable 的 `ScrollCache` 在首绘时对 null `ScrollBarDrawable` 调 `mutate()` 崩溃
- A4 保留 `openWebShelf` 的 `/app/legado-web`、`rawServer` 判 `/app/`、`serverBase` 的 path 分支与 `negativeButton` 文案

### 三包

| APK | 体积 | SHA256 |
| --- | --- | --- |
| `yuedu_app_3.2.0.1.apk` | 19 973 951 B（19.05 MB） | `1988D25062DC8C764F9593A71750760A16143CFC631A76C30F1F68130570474F` |

### 校验

- `aapt2 dump badging`：`package='io.legado.api19' versionCode='3200001' versionName='3.2.0.1'`，sdkVersion 19 / target 30，无 `application-debuggable`
- `apksigner verify`：v1/v2 均 true
- 包内 `assets/updateLog.md` 首条为 `**2026/11/23（3.2.0.1）**`
- MuMu Android 12 覆盖安装：启动不崩，进入阅读页、返回书籍详情并滚动均正常，`logcat -b crash` 无 `ScrollBarDrawable` NPE
- 菜单打开、服务器/令牌输入框、三个开关、测试连接进度框均正常

### 说明

- 本版服务端契约未新增客户端必填字段；多源缓存合并与「书籍」页由服务端/Web 侧生效
- 应用内「检查更新」指向 GitHub `TZYLXS/yuedu-android4` 的 `releases/latest`；本 Release 发布后即指向 `v3.2.0.1`
