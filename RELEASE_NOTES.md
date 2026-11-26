# 阅读 3.3.5.1（Android 4.0 版）

同步 FnReader `v3.3.5`（main `7adf7fe`，2026-11-22）。两仓共用版号 `3.3.5.1` / versionCode `3300501`。

## 跟进的协议变更
- `docs/APP同步接口.md` 新增 §17.2.1「同步细节（0.1.50+）」：时间戳相等不回传、`pull` 只回 `lastUpdateTime > since` 且单次最多 500 条、响应带 `nextSince`/`truncated`、`bookSourceType` 统一 0/1/2/3、布尔字段规范化、坏源过滤
- `docs/软总线路由与权重.md`：高/低分设备都能领 book/content/media，仅并发与在途额度不同
- `/api/m/*` 无新增客户端必填字段

## 客户端改动
- 书源同步改分页循环：`since` 起拉，`truncated=true` 用 `nextSince` 继续，GET 与 POST 两条路径都拉到 `truncated=false`
- `pull` 落库改字段级合并：源定义取服务端，启用开关/分组/排序/响应时间保留本地，`lastUpdateTime` 取双方较大值
- 本地书回传：导入的 txt/epub/pdf 不再被过滤，推送带 `localFile=true` 与 `fileName`，由服务端扫 ebook 目录绑定；老服务端忽略这类条目
- 配置菜单、E-Ink 崩溃修复等 A4 自有差异保持不变（`openWebShelf` 的 `/app/legado-web`、`rawServer` 判 `/app/`、`serverBase` 的 path 分支、`negativeButton` 文案）

## 服务端同期变化（3.3.0–3.3.5）
- 封面链路重构、书源同步加固、软总线低分设备分担、正文抽取兜底（过短回退通用抽取，失败明确报错）

## 产物
- `yuedu_app_3.3.5.1.apk`（armeabi-v7a）

包名 `io.legado.api19`，minSdk 19 / targetSdk 30，v1 + v2 双签，无 debuggable。
