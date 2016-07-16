package com.soft.wangkl

import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

/**
 * Created by 123456 on 2016/7/16.
 */

class HistoryRkAdapter(activity: MainActivity, db: SQLiteDatabase, start: Date?, end: Date?)
: DataAdapter(activity, db, start, end) {
    override fun initData() {
        val s = dateFormatter.format(start)
        val e = dateFormatter.format(end)
        var sql = "select date(rq) as rq,tm,sl from rk "
        sql += "where date(rq)>='$s' and date(rq)<='$e'"
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
            map["je"] = decimalFormatter.format(tm * sl)
            mData.add(map)
        }
        c.close()
    }

    override fun compute() {
        val sum_sl = mData.sumBy { it["sl"]!!.toInt() }
        val sum_je = mData.sumBy { decimalFormatter.parse(it["je"]).toInt() }
        val m = HashMap<String, String>()
        m["id"] = "合计"
        m["rq"] = "汇总天数: ${mData.size}"
        m["sl"] = sum_sl.toString()
        m["je"] = decimalFormatter.format(sum_je)
        mData.add(m)
    }

    override fun setSort(v: View) {
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View
        val vh: ViewHolder
        if (convertView == null) {
            v = mInflater.inflate(R.layout.history_item, null)
            vh = ViewHolder(v)
            v.tag = vh
        } else {
            v = convertView
            vh = v.tag as ViewHolder
        }
        val m = mData[position]
        vh.id.text = m["id"]
        vh.rq.text = m["rq"]
        vh.tm.text = m["tm"]
        vh.sl.text = m["sl"]
        vh.je.text = m["je"]
        return v
    }

    private class ViewHolder(v: View) {
        val id = v.findViewById(R.id.history_rk_ck_item_id) as TextView
        val rq = v.findViewById(R.id.history_rk_ck_item_rq) as TextView
        val tm = v.findViewById(R.id.history_rk_ck_item_tm) as TextView
        val sl = v.findViewById(R.id.history_rk_ck_item_sl) as TextView
        val je = v.findViewById(R.id.history_rk_ck_item_je) as TextView
    }

}