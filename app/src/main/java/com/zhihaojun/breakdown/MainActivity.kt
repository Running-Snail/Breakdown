package com.zhihaojun.breakdown

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.BaseAdapter
import android.widget.ListView

class MainActivity : AppCompatActivity() {
    private var rootItem: BDListItem? = null
    private var adapater: BaseAdapter? = null
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemsData()
        listView = findViewById(R.id.bd_list) as ListView

        adapater = BDListAdapter(listView, rootItem as BDListItem, this)
        listView.adapter = adapater
    }

    fun itemsData() {
        var root = MemoryBDListItem(0L, null, false, "root") as BDListItem
        var hello = MemoryBDListItem(1L, root, false, "Hello")
        var world = MemoryBDListItem(2L, root, false, "World")
        root.add(hello, 0)
        root.add(world, 1)
        var hi = MemoryBDListItem(3L, hello, false, "功不唐捐")
        var ha = MemoryBDListItem(4L, hello, false, "玉汝于成")
        hello.add(hi, 0)
        hello.add(ha, 1)
        rootItem = root
    }
}
