package com.zhihaojun.breakdown

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.TextView
import android.text.Layout
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT






/**
 * Created by zhihaojun on 2017-06-22.
 */
class BDListAdapter(val mListView: ListView, val mRootItem: BDListItem, val mContext: Context): BaseAdapter() {
    companion object {
        const val ITEM_INDENT = 30
        const val TAG = "BDListAdapter"
    }

    private var mItemList: MutableList<BDListItem> = ArrayList<BDListItem>()
    private var mItemIndents: MutableList<Int> = ArrayList<Int>()

    init {
        build()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var item = mItemList[position]
        var v: View
        if (convertView == null) {
            v = View.inflate(mContext, R.layout.bd_list_item, null)
        } else {
            v = convertView
        }
        var holder: BDListItemHolder = BDListItemHolder(this, mListView, v, position, mItemIndents[position], mItemList[position], mContext)
        return v
    }

    override fun getItem(position: Int): Any {
        return mItemList[position]
    }

    override fun getItemId(position: Int): Long {
        return mItemList[position].id()
    }

    override fun getCount(): Int {
        return mItemList.size
    }

    fun build() {
        mItemList.clear()
        mItemIndents.clear()

        // dfs list items
        dfs(mRootItem, 0)
    }

    private fun dfs(item: BDListItem, depth: Int) {
        for (child in item.childs()) {
            mItemList.add(child)
            mItemIndents.add(depth)
            dfs(child, depth+1)
        }
    }

    fun focusOn(item: BDListItem) {
        var pos = mItemList.indexOf(item)
        focusOn(pos)
    }

    fun focusOn(idx: Int) {
        if (idx != -1) {
            var v = mListView.getChildAt(idx)
            var editText = v.findViewById(R.id.bd_item_str) as EditText
            editText.setSelection(editText.text.length)
            editText.requestFocus()

            // show up soft keyboard
            var imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0)
        }
    }
}
