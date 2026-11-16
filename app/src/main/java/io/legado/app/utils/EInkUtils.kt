package io.legado.app.utils

import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import io.legado.app.R

/**
 * E-Ink 电子墨水屏通用优化（不依赖任何厂商 SDK）。
 *
 * 思路：墨水屏没有彩色、刷新慢，任何动画/渐变/水波纹都会留下残影，
 * 因此把列表项动画、滚动条淡入淡出、边缘回弹光晕、过度滚动全部关掉。
 * 用 View 上的 tag 保证同一个 View 只配置一次，可在全局布局回调里反复调用。
 */
// R.id 不是编译期常量，不能用 const val，普通顶层 val 即可
private val TAG_EINK_CONFIGURED = R.id.tag_eink_configured

fun View.applyEInkRecursively() {
    // 自身只配置一次，但无论是否已配置都要继续向下遍历，
    // 否则后加进来的子 View（如搜索结果、弹窗列表）会被漏掉
    if (getTag(TAG_EINK_CONFIGURED) != true) {
        setTag(TAG_EINK_CONFIGURED, true)
        applyEInkSelf()
    }
    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            getChildAt(i).applyEInkRecursively()
        }
    }
}

private fun View.applyEInkSelf() {
    // 所有可滚动 View：关闭过度滚动（边缘回弹光晕），滚动条不再淡入淡出
    overScrollMode = View.OVER_SCROLL_NEVER
    isScrollbarFadingEnabled = false
    when (this) {
        is RecyclerView -> {
            // 列表增删改不再播放默认动画，避免墨水屏残影
            itemAnimator = null
        }
        is AbsListView -> {
            // 滚动时列表项缓存底色固定为白，避免出现深色闪烁
            cacheColorHint = android.graphics.Color.WHITE
        }
    }
}
