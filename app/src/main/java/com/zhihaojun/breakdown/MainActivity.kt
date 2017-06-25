package com.zhihaojun.breakdown

import android.database.DataSetObserver
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.BaseAdapter
import android.widget.ListView
import com.google.gson.Gson
import java.io.File
import java.util.concurrent.locks.ReentrantLock

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val STORE_FILE_LOCATION = "list.json"
    }
    private var rootItem: BDListItem? = null
    private var dataChanged: Boolean = false
    private var adapter: BaseAdapter? = null
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemsData()
        listView = findViewById(R.id.bd_list) as ListView

        adapter = BDListAdapter(listView, rootItem as BDListItem, this)
        (adapter as BDListAdapter).registerDataSetObserver(object: DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                dataChanged = true
            }
        })
        listView.adapter = adapter

        startAutosave()
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

    fun startAutosave() {
        Thread(object: Runnable{
            override fun run() {
                while (true) {
                    while (!dataChanged) {}
                    Log.i(TAG, "start to store data")
                    storeData()
                    dataChanged = false
                    Log.i(TAG, "data stored")
                    Thread.sleep(1000L)
                }
            }
        }).start()
    }

    fun storeData() {
        var f = fileAt(STORE_FILE_LOCATION)
        var serializer = BDJSONSerializer()
        try {
            var text = serializer.toJSON(rootItem!!)
            f.writeText(text)
        } catch (e: Exception) {
            Log.e(TAG, "store data failed")
            e.printStackTrace()
        }
    }
}
