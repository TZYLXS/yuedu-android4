## 阅读（安卓 4.0 版）1.7（versionCode 10700）

minSdk 19 移植版，包名 `io.legado.api19`，可与阅读Pro、官方版共存安装。
与 **阅读Pro 1.7** 同步发布、同一套版本号。

### 本版主线：适配 FnReader 1.11.0 —— §16 缓存同步（⑧）与 §17 书源双向同步（⑨）

服务端 `docs/APP同步接口.md` 这轮新增三章（§15 每日推荐 / §16 缓存同步 / §17 书源双向同步），
本版把后两章接进客户端，菜单从 ①–⑦ 扩到 ①–⑨，与阅读Pro 1.7 同一份实现。

**⑧ 上传本机缓存到飞牛（网页离线读正文）**

- 扫 `book_cache/<书名目录>/` 下的 `.nb`，按 §16 整本一次推 `layout / book / coverData+coverMime / chapters[{index,title,file,raw}] / images[]`
- `raw` 传缓存文件原文（不去标签、不补缩进），转码由服务端做
- 章节名取本地章节表，取不到退回文件名；章节图片 1 B–2 MB 转 base64；单次请求超 **30 MB** 的本子跳过
- 本机没有已缓存章节时提示「先在阅读里下载几章」

**⑨ 书源双向同步（最后一次修改为准）**

- 首次接入走一次性合并接口 `POST /api/m/sources`（按 `bookSourceUrl` 合并，幂等），只把服务端 `serverTime` 记成下次 `since`
- 之后本地有 `lastUpdateTime > since` 的改动才 POST 增量；没有改动只 GET 增量，不再每次重推全库
- `pull` 覆盖本地、`pullDeleted` 删本地；`truncated: true` 提示「再点一次继续」
- **为什么不直接用 `POST /api/m/sources/sync` 推全量**：实测「本机全量 + `since: 0`」会被服务端当成整库快照，删掉本机没有的源（1253 → 995，丢 258 条），靠备份 + `POST /api/m/sources` 补回。结论写进 `docs/app回传接受规范.md` §9

**其它**

- `request()` 增加可选读超时：缓存上传 60s、书源合并上传 180s、书源增量 120s，其余保持 12s
- 菜单 ①–⑦ 文案与行为不变；4.0 版自有差异（网页地址拼 `/app/legado-web`、`negativeButton` 用 `requireContext().getString(R.string.cancel)`）保持不动
- 文档同步：`docs/app回传接受规范.md` 扩到 9 章，自测清单加两条

### 实测

- 与阅读Pro 1.7 同源实现，同一台 MuMu 模拟器、同一 FnReader 1.11.0 服务端验证：⑧ 上传 4 本 45 章，NAS `cache/<书名>/` 落 `meta.json` + `0001 …txt` + `raw/`；⑨ 首次合并上传不掉源（服务端 1253 条不变），再点一次走增量「推送 0 / 拉取 0」
- 首次只合并上传 + 记 `serverTime`，不拉服务端全量（要全量导入用 ① 导入书源）
- 编译产物 1.7 / 10700，minSdk 19 保持不变

### 安装

- 直接用 `yuedu_app_1.7.apk`（armeabi-v7a 通用，老设备兼容）
- 覆盖安装保留书架、书源、阅读进度与令牌；首次启动会弹 1.7 更新日志
