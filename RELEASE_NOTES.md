# 阅读 3.4.2.1（Android 4.0 版）

同步 FnReader `v3.4.1`（main `fa03949`，线上服务端实际运行 `3.4.2`）。两仓共用版号 `3.4.2.1` / versionCode `3400201`。

## 跟进的协议变更（§17.2.2 本地小说文件绑定，3.4.1+）
- `/api/m/books` 与 `/api/m/progress/batch` 接受 `localFile=true` 与 `fileName`，按 `fileName || name` 查 ebook 索引
- 命中 → 绑 `ebook://<sha1>`；未命中 → 登记 `local://<normBookName>` pending，扫描完成后自动升级并保留进度
- 响应带 `bound`/`pending` 计数

## 客户端改动
- `FnReaderAutoSync.pushBooks` / `pushProgress` 返回 `[added|updated, bound, pending]`，摘要显示「已绑定 N 本、待扫描 M 本」，并补上「本地书 N 本」行
- 老服务端缺省 0，原逻辑不变；`bookUrl` 继续用 `file://`，服务端幂等重绑
- A4 自有差异未动：`openWebShelf` 走 `/app/legado-web`、`rawServer` 判 `/app/`、`serverBase` 的 path 分支

## 服务端 3.4.0/3.4.2 同期变化
- 内置 Node v22.12.0（去 `nodejs_v26` 依赖）、正文替换误伤保护、封面客户端优先/死链过滤、书源同步分页与类型规范化、软总线负载均衡

## 产物
- `yuedu_app_3.4.2.1.apk`（armeabi-v7a，v1 + v2 双签）

包名 `io.legado.api19`，minSdk 19 / targetSdk 30，正式签名，无 debuggable。
