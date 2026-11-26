# 阅读 3.4.0.1（Android 4.0 版）

同步 FnReader `v3.4.0`（main `d2e059f`，2026-11-27）。两仓共用版号 `3.4.0.1` / versionCode `3400001`。

## 本版性质
仅同步服务端版本，**无客户端代码改动**：`docs/APP同步接口.md`、`docs/软总线路由与权重.md`、`docs/AI构建指南.md` 与 `3.3.5` 完全一致，`/api/m/*` 契约未变。

## 服务端 3.4.0 变化
- 随包内置 Node 运行时 v22.12.0（按 `bin/node` 布局优先使用），manifest 去掉 `nodejs_v26` 依赖
- 正文兜底与替换规则误伤保护（替换后正文缩水 <55% 回退原文）
- 封面链路客户端优先、死链过滤
- 书源同步分页与类型规范化
- 软总线设备计数与负载均衡

## 客户端沿用能力（3.3.5.1 已落地）
- 书源同步分页循环（`nextSince`/`truncated`）与 `pull` 字段级合并
- 本地书 `localFile`/`fileName` 回传，服务端扫 ebook 绑定
- A4 自有差异未动：`openWebShelf` 走 `/app/legado-web`、`rawServer` 判 `/app/`、`serverBase` 的 path 分支

## 产物
- `yuedu_app_3.4.0.1.apk`（armeabi-v7a，v1 + v2 双签）

包名 `io.legado.api19`，minSdk 19 / targetSdk 30，正式签名，无 debuggable。
