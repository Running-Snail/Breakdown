package com.zhihaojun.breakdown

import android.util.Log
import com.google.gson.Gson
import java.io.File

/**
 * Created by zhihaojun on 2017-06-24.
 */
class BDJSONSerializer {
    companion object {
        const val TAG = "BDJSONSerializer"
    }

    fun toJSON(root: BDListItem): String {
        var gson = Gson()
        var data = toBDRootJSON(root)
        return gson.toJson(data)
    }

    fun fromJSON(text: String): BDListItem {
        var gson = Gson()
        var data = gson.fromJson(text, BDRootJSON::class.java)
        return toMemoryBDListItem(data)
    }

    fun fromJSON(file: File): BDListItem {
        if (!file.canRead()) {
            throw java.io.IOException("file cannot read")
        }
        return fromJSON(file.readText())
    }

    private fun toMemoryBDListItem(data: BDRootJSON): MemoryBDListItem {
        return toMemoryBDListItemDFS(data.root, null)
    }

    private fun toMemoryBDListItemDFS(itemData: BDItemJSON, parent: MemoryBDListItem?): MemoryBDListItem {
        var item = MemoryBDListItem(itemData.id, parent, itemData.checked, itemData.text)
        for (childItemData in itemData.children) {
            item.add(toMemoryBDListItemDFS(childItemData, item))
        }
        return item
    }

    private fun toBDRootJSON(rootItem: BDListItem): BDRootJSON {
        var root = toBDRootJSONDFS(rootItem, null)
        return BDRootJSON(root)
    }

    private fun toBDRootJSONDFS(item: BDListItem, parent: BDItemJSON?): BDItemJSON {
        var itemData = BDItemJSON(ArrayList<BDItemJSON>(), item.id(), item.text(), item.isChecked())
        for (child in item.childs()) {
            itemData.children.add(toBDRootJSONDFS(child, itemData))
        }
        return itemData
    }
}


data class BDRootJSON(var root: BDItemJSON) {

}

data class BDItemJSON(var children: MutableList<BDItemJSON>, var id: Long, var text: String, var checked: Boolean) {

}
