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

    fun itemsData() {
        var f = fileAt(STORE_FILE_LOCATION)
        Log.i(TAG, "data stored at " + f.absolutePath)
        var serializer = BDJSONSerializer()
        try {
            rootItem = serializer.fromJSON(f)
        } catch (e: Exception) {
            Log.e(TAG, "serialize failed")
            e.printStackTrace()

            // put default file
            var defaultList = resources.openRawResource(R.raw.default_list)
            var defaultContent = defaultList.bufferedReader().use { it.readText() }
            f.writeText(defaultContent)

            rootItem = serializer.fromJSON(defaultContent)
        }
    }
}
