## 阅读 1.0.26 · API19 版接入 FnReader 同步（versionCode 19002）

Legado（阅读）API19 移植版的第二次发版：**新包名** + **FnReader 服务器同步**，minSdk 19 / targetSdk 30，单包覆盖 Android 4.4–5.1（API 19–22）。

### 这一版改了什么

- **新包名 `io.legado.api19`**：可以和官方「阅读」同时装着，不再互相顶掉，也不用手动卸载
- **我的 → 书源管理 下面一行新增「FnReader 服务器」**：按 FnReader「App 同步接口」重新设计，一集菜单收齐所有接法
- 顶部「同步」= `GET /api/m/sync`，直接显示服务器版本、服务器时间、书架数、进度条数、书源总数与启用数
- 一集菜单七条：①导入书源（`sources.json?all=1`）②复制同步地址 ③回传阅读进度（`progress/batch`）④WebDAV（`/dav/backup/`、`/dav/books/`）⑤HTTP API 接口表 ⑥网页书架 ⑦接入手册（`APP同步接口.md` / `APK接入.md` / `手机接入API.md`）
- **地址自动纠正**：填飞牛网页地址（`http://IP:5666/app/legado-web`）或只填 IP，会依次试 `填的地址 → 同主机:1122 → 加 /app/legado-web`，哪个通就把哪个写回输入框
- **失败原因说人话**：令牌不对 / 地址是网页 / 服务没起来 都有中文提示，并附「试过：<最后尝试的URL>」
- **修好了「导入书源一条都进不来」**：之前点①会直接退出（网络导入页只认 `legado://import/bookSource?src=…`），现在能一次把服务器上 1000+ 个书源全导进来
- 应用内「更新日志」写进本版内容；「检查更新」指向本仓库 Release，按 tag 里的 versionCode 比较

### 安装

1. 直接安装 `yuedu_app_1.0.26.110806.apk`（新包名，可与官方版共存，无需卸载任何东西）
2. 打开应用 → 我的 → **FnReader 服务器**
3. 服务器地址填 `http://<NAS_IP>:1122`（FnReader 手机接口端口；填 5666 的网页地址也会被自动纠正），访问令牌填 FnReader「设置 → 手机接入」里那串
4. 点「同步」确认连上；再点「① 导入书源」把服务器书源全量导入

### 产物信息

| 项 | 值 |
| --- | --- |
| 包名 | `io.legado.api19` |
| versionCode / versionName | 19002 / 1.0.26.110806 |
| minSdk / targetSdk | 19 / 30 |
| 签名 | v1 (JAR) + v2，`apksigner verify` 通过（证书 `CN=FnReader YueDu`） |
| ABI | armeabi-v7a |
| DEX | MultiDex |
| 体积 | 约 18.6 MB |

### 验证情况

- MuMu 模拟器（Android 12，SDK 32）安装启动、隐私协议、我的页入口位置、`/api/m/sync` 真实服务器连通（FnReader 1.7.25，书架 3 / 进度 3 / 书源 1062 启用 840）均实测通过
- 地址回退实测：填 `http://10.5.4.70:5666/app/legado-web` 点同步，自动纠正为 `http://10.5.4.70:1122` 并同步成功
- 导入书源实测：导入页列出 1062 个书源，确认后书源管理显示 1062 条

### 已知限制

- 4.4 真机上这些功能可能不可用（三方库要求 API21+，不影响启动和阅读）：ExoPlayer 音频播放、扫码导入书源、zip/epub 的 libarchive 路径
- 模拟器为 Android 12，未在 Android 4.4 真机上复测（老机器不在手边）
