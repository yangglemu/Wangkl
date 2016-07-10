package com.soft.wangkl

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

/**
 * Created by 123456 on 2016/6/24.
 */
class SaleFLAdapter(context: Context, sqlite: SQLiteDatabase, start: Date, end: Date) :
        DataAdapter(context, sqlite, start, end) {

    override fun initData() {
        val s = dateFormatter.format(start)
        val e = dateFormatter.format(end)
        var sb = StringBuilder()
        sb.append("select sale_mx.tm,sum(sale_mx.sl) as sl,sum(sale_mx.je) as je from sale_mx join goods on ")
        sb.append("(sale_mx.tm=goods.tm) where date(rq)>='$s' and date(rq)<='$e' ")
        sb.append("group by sale_mx.tm order by goods.sj asc")
        val c = db.rawQuery(sb.toString(), null)

        var id = 0
        while (c.moveToNext()) {
            val map = HashMap<String, String>()
            map["id"] = (++id).toString()
            map["tm"] = c.getString(0) + ".00"
            map["sl"] = c.getString(1)
            map["je"] = decimalFormatter.format(c.getInt(2))
            mData.add(map)
        }
        compute()
        c.close()
    }

    override fun compute() {
        val sl = mData.sumBy { it["sl"]!!.toInt() }
        val je = mData.sumBy { decimalFormatter.parseObject(it["je"]!!).toString().toInt() }
        val map = HashMap<String, String>()
        map["id"] = "合计"
        map["tm"] = ""
        map["sl"] = sl.toString()
        map["je"] = decimalFormatter.format(je)
        mData.add(map)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var v: View
        var vh: ViewHolder
        if (convertView == null) {
            v = mInflater.inflate(R.layout.sale_fl_item, null)
            vh = ViewHolder(v)
            v.tag = vh
        } else {
            v = convertView
            vh = v.tag as ViewHolder
        }
        val map = mData[position]
        vh.id.text = map["id"]
        vh.tm.text = map["tm"]
        vh.sl.text = map["sl"]
        vh.je.text = map["je"]
        return v
    }

    private class ViewHolder(v: View) {
        val id = v.findViewById(R.id.sale_fl_id) as TextView
        val tm = v.findViewById(R.id.sale_fl_tm) as TextView
        val sl = v.findViewById(R.id.sale_fl_sl) as TextView
        val je = v.findViewById(R.id.sale_fl_je) as TextView
    }
}