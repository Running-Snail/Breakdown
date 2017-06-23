package com.zhihaojun.breakdown

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.BaseAdapter
import android.widget.ListView
import com.google.gson.Gson
import java.io.File

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val STORE_FILE_LOCATION = "list.json"
    }
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

    private fun fileAt(path: String): File {
        return filesDir.resolve(path)
    }

    private fun buildFromJSON(data: BDStoreJSON): BDListItem {
        var root = MemoryBDListItem(data.root.id, null, data.root.checked, data.root.text)

        buildDFS(data.root, root)

        return root
    }

    private fun buildDFS(itemData: BDStoreItemJSON, parent: BDListItem?) {
        var item = MemoryBDListItem(itemData.id, parent, itemData.checked, itemData.text)
        parent?.add(item)
        for (childData in itemData.children) {
            buildDFS(childData, item)
        }
    }

    fun itemsData() {
        var f = fileAt(STORE_FILE_LOCATION)
        Log.i(TAG, f.absolutePath)
        if (f.canRead()) {
            var content = f.readText()
            var gson: Gson = Gson()
            var data: BDStoreJSON = gson.fromJson(content, BDStoreJSON::class.java) as BDStoreJSON
            rootItem = buildFromJSON(data)
        } else {
            // create empty file
            f.createNewFile()

            rootItem = MemoryBDListItem(BDListItemID.next(), null, false, "root")
            rootItem?.add(MemoryBDListItem(BDListItemID.next(), rootItem, false, "Hi, There"))

        }
    }
}

data class BDStoreJSON(var root: BDStoreItemJSON) {

}

data class BDStoreItemJSON(var children: List<BDStoreItemJSON>, var id: Long, var text: String, var checked: Boolean) {

}
