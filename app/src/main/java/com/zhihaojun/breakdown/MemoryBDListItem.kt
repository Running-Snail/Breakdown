package com.zhihaojun.breakdown

import java.util.*

/**
 * Created by zhihaojun on 2017-06-22.
 */
class MemoryBDListItem(val mId: Long, var mParent: BDListItem?, var mChecked: Boolean, var mText: String): BDListItem {
    companion object {
        const val TAG = "MemoryBDListItem"
    }

    private val mChildItems: MutableList<BDListItem> = LinkedList<BDListItem>()

    override fun id(): Long {
        return mId
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun text(): String {
        return mText
    }

    override fun setText(text: String) {
        mText = text
    }

    override fun getChildAt(idx: Int): BDListItem {
        return mChildItems.get(idx)
    }

    override fun childs(): Iterator<BDListItem> {
        return mChildItems.iterator()
    }

    override fun parent(): BDListItem? {
        return mParent
    }

    override fun setParent(item: BDListItem) {
        mParent = item
    }

    override fun add(item: BDListItem) {
        mChildItems.add(item)
    }

    override fun add(item: BDListItem, pos: Int) {
        mChildItems.add(pos, item)
    }

    override fun remove(item: BDListItem) {
        mChildItems.remove(item)
    }

    override fun check(check: Boolean) {
        mChecked = check
    }

    override fun indexOf(item: BDListItem): Int {
        return mChildItems.indexOf(item)
    }

}
