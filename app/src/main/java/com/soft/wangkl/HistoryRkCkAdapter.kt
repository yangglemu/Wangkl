package com.soft.wangkl

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * Created by 123456 on 2016/7/16.
 */

class HistoryRkCkAdapter(activity: MainActivity, db: SQLiteDatabase, start: Date?, end: Date?)
: DataAdapter(activity, db, start, end) {

    override fun initData() {
        val s = dateFormatter.format(start)
        val e = dateFormatter.format(end)
        val sql = "select date(rq) as rq,tm,sl from rk where date(rq)>='$s' and date(rq)<='$e"
        val c = db.rawQuery(sql, null)
        var id = 0
        while (c.moveToNext()) {
            val tm = c.getString(1).toInt()
            val sl = c.getString(2).toInt()
            val map = HashMap<String, String>()
            map["id"] = (++id).toString()
            map["rq"] = c.getString(0)
            map["tm"] = tm.toString()
            map["sl"] = sl.toString()
            map["je"] = (tm * sl).toString()
            mData.add(map)
        }
        c.close()
    }

    override fun compute() {
        val sum_sl = mData.sumBy { it["sl"]!!.toInt() }
        val sum_je = mData.sumBy { it["je"]!!.toString().toInt() }
        val m = HashMap<String, String>()
        m["id"] = "合计"
        m["rq"] = "汇总天数:${mData.size}"
        m["sl"] = sum_sl.toString()
        m["je"] = decimalFormatter.format(sum_je)
        mData.add(m)
    }

    override fun setSort(v: View) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}