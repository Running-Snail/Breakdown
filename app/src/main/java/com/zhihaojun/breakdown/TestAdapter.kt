package com.zhihaojun.breakdown

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

/**
 * Created by zhihaojun on 2017-06-22.
 */
class TestAdapter(val list: ArrayList<String>, val context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View? {
        var holder: TestListItemHolder
        var v: View
        if (convertView == null) {
            v = View.inflate(context, R.layout.bd_list_item, null)
            holder = TestListItemHolder(v)
            v.tag = holder
        } else {
            v = convertView
            holder = v.tag as TestListItemHolder
        }
        holder.str.text = list[pos]
        return v
    }

    override fun getItem(pos: Int): Any? {
        return list.get(pos)
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }
}

class TestListItemHolder(var itemView: View) {
    var str: TextView = itemView.findViewById(R.id.bd_item_str) as TextView
}
