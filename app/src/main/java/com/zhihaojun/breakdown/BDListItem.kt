package com.zhihaojun.breakdown

/**
 * Created by zhihaojun on 2017-06-22.
 */
interface BDListItem {
    fun id(): Long
    fun isChecked(): Boolean
    fun text(): String
    fun setText(text: String)
    fun getChildAt(idx: Int): BDListItem
    fun childs(): Iterator<BDListItem>
    fun parent(): BDListItem?
    fun setParent(item: BDListItem)
    fun add(item: BDListItem, pos: Int)
    fun add(item: BDListItem)
    fun remove(item: BDListItem)
    fun check(check: Boolean)
    fun indexOf(item: BDListItem): Int
}
