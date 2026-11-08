## 阅读 1.3 · API19 版同步支持「手机解析待命」

Legado（阅读）API19 移植版，包名 `io.legado.api19`，可与官方版、阅读Pro 共存。**minSdk 19 / targetSdk 30**，单包覆盖 Android 4.4–5.1（API 19–22）。本版与阅读Pro 同为 **1.3 / 10300**，tag `v1.3`。

### 本版新增：让手机替飞牛解析书源

FnReader `1.7.26` 起支持把「飞牛跑不动的书源」派给手机：要 WebView、要浏览器环境、接口加密的那些源，由手机用本地书源规则跑一遍再回传。

- 「FnReader 服务器」弹窗新增第 ⑧ 项 **「手机解析待命」**，点一下开启，通知栏常驻「FnReader 手机解析」
- 前台服务长轮询 `/api/m/remote/pending`，领到任务后调用本机 WebBook：
  - `search` → `searchBookAwait` → `{books:[…]}`
  - `toc` → `getChapterListAwait` → `{chapters:[{index,title,url}]}`
  - `content` → `getContentAwait` → `{paragraphs:[…], title}`
- 结果 POST 回 `/api/m/remote/result`，飞牛网页搜索直接采用手机解析结果（`via:'phone'`）
- **单任务 45 秒上限 + 每任务独立协程**：卡死的书源只让这一个任务失败（回「解析超时」），不会把待命整条堵死
- 书源地址归一化（去协议、尾斜杠、大小写），飞牛存的 `http://xxx.com/` 与本机存的 `http://xxx.com` 也能对上
- 开关开着时，重进设置会自动把服务拉起来（进程被杀也不用重新点）

用法：手机开 ⑧ → 回飞牛网页正常搜书，本机解析不了的源自动落到手机上。

### 上一版（1.2）的东西都还在

新包名共存安装、七项接法菜单（导入书源 / 复制同步地址 / 回传进度 / WebDAV / HTTP API / 网页书架 / 接入手册）、同步真正落库、地址自动纠正、中文报错。

### 安装

1. 直接安装 `yuedu_app_1.3.apk`（覆盖装即可，同签名）
2. 打开应用 → 我的 → **FnReader 服务器**
3. 填 `http://<NAS_IP>:1122` 与令牌 → 同步 → ① 导入书源 → ⑧ 开启手机解析待命

### 产物信息

| 项 | 值 |
| --- | --- |
| 文件 | `yuedu_app_1.3.apk` |
| 包名 | `io.legado.api19` |
| versionCode / versionName | 10300 / 1.3 |
| minSdk / targetSdk | 19 / 30 |
| ABI | armeabi-v7a |
| DEX | MultiDex |
| 体积 | 19,548,706 B（18.64 MB） |
| SHA256 | `2A76836ECE454A5A47AA827BF95F9F575C0F94B03BA85CEA6A579963A47F0884` |
| 签名 | v1 (JAR) + v2，`apksigner verify` 通过（证书 `CN=FnReader YueDu`） |

### 验证情况（MuMu，Android 12）

- 安装启动、⑧ 开启后 `dumpsys` 显示 `isForeground=true foregroundId=108`
- 导入 1062 个书源后，用 `POST /api/remote/job` 打真实任务：
  - `search`（爪机书屋，凡人修仙传）→ 命中书页 `https://www.zjsw.org/read/841/`
  - `toc` → 2451 章，首章「第一章 山边小村」
  - `content` → 37 段真实正文
- 卡住的书源（夜伴书屋某章）实测按 45 秒超时回「解析超时（45 秒没出结果），这个书源本机也跑不动」，紧接着的下一单任务正常返回，验证单任务隔离有效

### 已知限制

- 4.4 真机上部分功能不可用（三方库要求 API21+，不影响启动与阅读）：ExoPlayer 音频播放、扫码导入书源、zip/epub 的 libarchive 路径
- 手机解析待命依赖进程存活；被系统清理后进设置页会自动拉起
- 模拟器为 Android 12，未在 4.4 真机复测

### 版本规则

安卓 4.0 版（本仓库）与阅读Pro（`TZYLXS/yuedu-pro`）共用版本号：当前 **1.3**（versionCode `10300`），每次迭代 **+0.1**，满 10 进 1；tag 与 versionCode 同口径（`v1.3` → 10300）。

### 关于旧版本

Release 列表只保留当前版本，`v1.3` 之前的版本在下次触发 prune 后会清理。
