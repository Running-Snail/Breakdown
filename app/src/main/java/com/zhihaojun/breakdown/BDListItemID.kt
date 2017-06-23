package com.zhihaojun.breakdown

/**
 * Created by zhihaojun on 2017-06-23.
 */
class BDListItemID private constructor() {
    companion object {
        var ID = 0L

        fun next(): Long {
            ID ++
            return ID
        }
    }
}
