package com.zhihaojun.breakdown

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*

/**
 * Created by zhihaojun on 2017-06-26.
 */

class BDListItemHolder(val mAdapter: BDListAdapter, val mListView: ListView, val mView: View, val mIdx: Int, val mIndent: Int, var mItem: BDListItem, val mContext: Context) : CompoundButton.OnCheckedChangeListener, View.OnKeyListener, View.OnClickListener, View.OnFocusChangeListener {
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

        if (mItem.isChecked()) {
            mCheckbox.isChecked = true
        }

        mSpace.layoutParams = LinearLayout.LayoutParams(dp2Px(mIndent * ITEM_INDENT), LinearLayout.LayoutParams.MATCH_PARENT)
        mEditText.text = SpannableStringBuilder(mItem.text())
        mEditText.setOnKeyListener(this)
        mEditText.onFocusChangeListener = this

        // indent and unindent arrow
        mIndentBtn.setOnClickListener(this)
        mUnindentBtn.setOnClickListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        mItem.check(isChecked)
        mAdapter.notifyDataSetChanged()
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
            // save text
            mItem.setText(mEditText.text.toString())

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
            // save text
            mItem.setText(mEditText.text.toString())

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

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        Log.i(TAG, "focus changed $v focus: $hasFocus")
        if (!hasFocus) {
            mItem.setText(mEditText.text.toString())
            mAdapter.notifyDataSetChanged()
        }
    }

    private fun dp2Px(dp: Int): Int {
        val displayMetrics = mContext.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }
}
