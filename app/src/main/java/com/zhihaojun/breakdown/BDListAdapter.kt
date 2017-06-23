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
            v.findViewById(R.id.bd_item_str).requestFocus()
        }
    }
}

class BDListItemHolder(val mAdapter: BDListAdapter, val mListView: ListView, val mView: View, val mIdx: Int, val mIndent: Int, var mItem: BDListItem, val mContext: Context) : CompoundButton.OnCheckedChangeListener, TextView.OnEditorActionListener, View.OnKeyListener, View.OnClickListener {
    companion object {
        const val ITEM_INDENT = 30
        const val TAG = "BDListItemHolder"
    }

    var mEditText: EditText = mView.findViewById(R.id.bd_item_str) as EditText
    var mCheckbox: CheckBox = mView.findViewById(R.id.bd_item_checkbox) as CheckBox
    var mSpace: Space = mView.findViewById(R.id.bd_indent_space) as Space
    var mIndentBtn: ImageButton = mView.findViewById(R.id.bd_indent) as ImageButton
    var mUnindentBtn: ImageButton = mView.findViewById(R.id.bd_unindent) as ImageButton

    init {
        mCheckbox.setOnCheckedChangeListener(this)
        mSpace.layoutParams = LinearLayout.LayoutParams(dp2Px(mIndent * ITEM_INDENT), LinearLayout.LayoutParams.MATCH_PARENT)
        mEditText.text = SpannableStringBuilder(mItem.text())
        mEditText.setOnKeyListener(this)

        // indent and unindent arrow
        mIndentBtn.setOnClickListener(this)
        mUnindentBtn.setOnClickListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        mItem.check(isChecked)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        Log.i(TAG, "key code " + event?.keyCode)
        if (event?.action != KeyEvent.ACTION_DOWN) {
            return false
        }

        return false
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action != KeyEvent.ACTION_DOWN) {
            return false
        }
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            var edit: EditText = v as EditText
            if (edit.text.isEmpty()) {
                // delete current item
                var parent: BDListItem? = mItem.parent()
                if (parent != null) {
                    parent.remove(mItem)

                    // rebuild
                    mAdapter.build()
                    mAdapter.notifyDataSetChanged()

                    // auto focus current idx
                    mView.postDelayed(object : Runnable {
                        override fun run() {
                            if (mIdx > 0) {
                                // focus on last item
                                mAdapter.focusOn(mIdx - 1)
                            }
                        }
                    }, 200L)
                }

                // handle over
                return true
            }
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // save current item
            mItem.setText(mEditText.text.toString())

            // add in parent
            var parent: BDListItem? = mItem.parent()
            if (parent != null) {
                var idxInParent = parent.indexOf(mItem)

                // insert a new empty item
                var newItem = MemoryBDListItem(BDListItemID.next(), parent, false, "")
                parent.add(newItem, idxInParent+1)

                // rebuild
                mAdapter.build()
                mAdapter.notifyDataSetChanged()

                // auto focus
                mView.postDelayed(object: Runnable {
                    override fun run() {
                        mAdapter.focusOn(newItem)
                    }
                }, 200L)
            }
            return true
        }
        return false
    }

    override fun onClick(v: View?) {
        if (v == mIndentBtn) {
            Log.i(TAG, "indent clicked")
            // get item above
            var parent: BDListItem? = mItem.parent()
            if (parent != null) {
                var idxInParent = parent.indexOf(mItem)
                Log.i(TAG, "idxInParent " + idxInParent)
                // assert idxInParent >= 1
                var above = parent.getChildAt(idxInParent - 1)
                Log.i(TAG, "parent id " + parent.id())

                parent.remove(mItem)
                above.add(mItem)
                mItem.setParent(above)

                // rebuild
                mAdapter.build()
                mAdapter.notifyDataSetChanged()
            }
        } else if (v == mUnindentBtn) {
            Log.i(TAG, "unindent clicked")
            // get parent's parent
            var parent: BDListItem? = mItem.parent()
            if (parent != null) {
                var grandparent: BDListItem? = parent.parent()
                if (grandparent != null) {
                    // parent in grandparent idx
                    var parentIdxInGrandparent = grandparent.indexOf(parent)

                    grandparent.add(mItem, parentIdxInGrandparent + 1)
                    mItem.setParent(grandparent)
                    Log.i(TAG, "grandparent id " + grandparent.id())
                    parent.remove(mItem)
                    mAdapter.build()
                    mAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun dp2Px(dp: Int): Int {
        val displayMetrics = mContext.getResources().getDisplayMetrics()
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }
}
