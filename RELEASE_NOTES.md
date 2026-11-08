## 阅读（安卓 4.0 版）1.8（versionCode 10800）

minSdk 19 移植版，包名 `io.legado.api19`，可与阅读Pro、官方版共存安装。
与 **阅读Pro 1.8** 同步发布、同一套版本号。

### 本版主线：修「同步上去的书在网页端没有书源」

1.7 同步的书在飞牛网页端打开提示「没有书源」、目录空、正文读不出——客户端加书时 `origin` 传空、
`originName` 写占位「手机端」，服务端拿不到书源地址就取不了目录和正文。本版与阅读Pro 1.8 同一份实现：

- 推书 `origin` 填书源真实地址（`bookSourceUrl`），`originName` 填源名；缺 origin 时按 `bookUrl` 的 host 匹配同宿主书源，再退按源名唯一匹配，都不中才留空并报「无匹配书源」
- 搜索页 / 临时跳转不算详情页，跳过并报「非详情页跳过」
- ⑧ 缓存按 §16 补 `layout:"legado"` + `chapters[].file` + `chapters[].raw`（`.nb` 原文）+ `origin`
- 封面本机没有就现下（Referer 先用图片域名、403 再退书源地址），转 base64 一起推，网页端不再白图
- ⑨ 每次改/删书源都带 `lastUpdateTime`，本机删源推 `deleted`，服务端更进的版本 `pull` 回本地覆盖

### 实测

- 与阅读Pro 1.8 同源实现、同一台 MuMu 模拟器 + FnReader 1.11.1 / 1.11.2：9 本书 `origin` 全部等于书源 `bookSourceUrl`，6 本目录与正文可读，⑧ 落盘 `meta.json`（`layout: legado`、`raw` 原文），⑨ 改名双向生效
- 4.0 版自有差异（网页地址拼 `/app/legado-web`、`negativeButton` 取 `R.string.cancel`）保持不动
- 编译产物 1.8 / 10800，minSdk 19 不变

### 安装

- 直接用 `yuedu_app_1.8.apk`（armeabi-v7a 通用，老设备兼容）
- 覆盖安装保留书架、书源、阅读进度与令牌；首次启动会弹 1.8 更新日志

### 1.7 内容（上一版）

- 适配 FnReader 1.11.0：⑧ 缓存上传（`.nb` 原文）、⑨ 书源双向同步（首次合并上传 + 之后增量）
- 修掉「全量推 `/api/m/sources/sync` 导致服务端删源」的坑；菜单扩到 ①–⑨

